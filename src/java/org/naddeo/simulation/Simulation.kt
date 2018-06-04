package org.naddeo.simulation

import javafx.scene.control.Label
import javafx.scene.paint.Color
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import tornadofx.runLater

/**
 * The different commands that can be used to manipulate
 * the simulation.
 */
sealed class SimulationCommands {

    /**
     * Start a simulation. This starts a new simulation if
     * one is already running.
     */
    class Start(
            val grid: Grid,
            val healthyLabel: Label,
            val infectedLabel: Label,
            val numberOfManagers: Int = 10,
            val agentsPerManager: Int = 50,
            val stepDelay: Int) : SimulationCommands()

    /**
     * Stop the current simulation if there is one.
     */
    class Stop : SimulationCommands()

    /**
     * Resume a stopped simulation if there is one.
     */
    class Resume : SimulationCommands()

    /**
     * Perform a single turn of the simulation if there is one.
     */
    class Turn : SimulationCommands()
}

/**
 * Kick off turns of the simulation with a delay between each step
 * until the job is cancelled.
 */
private fun simulate(simulation: Simulation, wait: Int): Job = launch {
    while (isActive) {
        simulation.turn()
        delay(wait)
    }
}

/**
 * Actor that can be used to interface with the simulation.
 * Sending commands to this actor is the only way to change the state of
 * the simulation.
 */
private fun createActor() = actor<SimulationCommands> {

    var simulation: Simulation? = null
    var stepDelay: Int = 1000
    var job: Job? = null

    for (cmd in channel) {
        when (cmd) {
            is SimulationCommands.Start -> {
                job?.cancel()
                simulation?.clearPrevious()
                simulation = Simulation(cmd.grid, cmd.healthyLabel, cmd.infectedLabel, cmd.numberOfManagers, cmd.agentsPerManager)
                stepDelay = cmd.stepDelay
                job = simulate(simulation, stepDelay)
            }

            is SimulationCommands.Stop -> {
                job?.cancel()
                job = null
            }

            is SimulationCommands.Resume -> {
                if (simulation != null && job == null) {
                    job = simulate(simulation, stepDelay)
                }
            }

            is SimulationCommands.Turn -> {
                simulation?.turn()
            }
        }
    }

}

/**
 * This contains the core logic of the simulation, which is made up of
 * actions performed over turns.
 */
private class Simulation(val grid: Grid, val healthyLabel: Label, val infectedLabel: Label, numberOfManagers: Int, agentsPerManager: Int) {

    private var previousAgentLocations: Map<GridUnitPoint, List<Agent>> = mapOf()

    val agentManagers: List<AgentManager> = (1..numberOfManagers).map {
        AgentManager(agentsPerManager)
    }

    /**
     * Clear out the sections of the board that previously had agents on them.
     */
    fun clearPrevious() {
        previousAgentLocations.forEach { gridUnitPoint, _ ->
            grid.getGridUnitForPoint(gridUnitPoint)?.clear()
        }
    }

    fun turn() {

        // Move all of the agents and find out what grid unit they end up on
        val currentLocations = agentManagers.map(AgentManager::moveAgents)
                .flatMap(AgentManager::getGridUnitPairings)
                .groupBy({ it.first }, { it.second })

        clearPrevious()
        currentLocations.forEach { gridUnitPoint, agents ->
            // Test for infection spreading within the grid unit
            // This is O(n^2) where n is the number of agents on the grid unit.
            // Typically there will be 2 or 3 on a unit.
            agents.forEach { agent1 ->
                agents.forEach { agent2 ->
                    if ((agent1.infected || agent2.infected) && agent1.isNear(agent2)) {
                        agent1.infected = true
                        agent2.infected = true
                    }
                }
            }

            // Mark the places that are occupied by agents
            val gridUnit = grid.getGridUnitForPoint(gridUnitPoint)
            agents.forEach {
                gridUnit?.markColor(Point(it.x, it.y), if (it.infected) Color.RED else Color.BLACK)
            }
        }

        previousAgentLocations = currentLocations

        val infectedAgents = agentManagers.map(AgentManager::infected).reduce(Int::plus)
        val healthyAgents = agentManagers.map(AgentManager::size).reduce(Int::plus) - infectedAgents

        runLater {
            infectedLabel.text = infectedAgents.toString()
            healthyLabel.text = healthyAgents.toString()
        }
    }
}


// public instance of the actor
val simulation by lazy {
    createActor()
}