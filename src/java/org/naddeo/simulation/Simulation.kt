package org.naddeo.simulation

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch


sealed class SimulationCommands {
    class Start(val grid: Grid, val numberOfManagers: Int = 10, val agentsPerManager: Int = 50, val stepDelay: Int = 1000) : SimulationCommands()
    class Stop : SimulationCommands()
    class Resume : SimulationCommands()
    class Turn : SimulationCommands()
}


private fun simulate(simulation: Simulation, wait: Int): Job = launch {
    while (isActive) {
        simulation.turn()
        delay(wait)
    }
}

private fun createActor() = actor<SimulationCommands> {

    var simulation: Simulation? = null
    var job: Job? = null

    for (cmd in channel) {
        when (cmd) {
            is SimulationCommands.Start -> {
                job?.cancel()
                simulation?.clearPrevious()
                simulation = Simulation(cmd.grid, cmd.numberOfManagers, cmd.agentsPerManager)
                job = simulate(simulation, cmd.stepDelay)
            }

            is SimulationCommands.Stop -> {
                job?.cancel()
                job = null
            }

            is SimulationCommands.Resume -> {
                if (simulation != null && job == null) {
                    job = simulate(simulation, 1000)
                }
            }

            is SimulationCommands.Turn -> {
                simulation?.turn()
            }
        }
    }

}


private class Simulation(val grid: Grid, numberOfManagers: Int, agentsPerManager: Int) {

    private var previousAgentLocations: Map<GridUnitPoint, List<Agent>> = mapOf()

    val agentManagers: List<AgentManager> = (1..numberOfManagers).map {
        AgentManager(agentsPerManager)
    }

    fun clearPrevious() {
        previousAgentLocations.forEach { gridUnitPoint, _ ->
            grid.getGridUnitForPoint(gridUnitPoint)?.clear()
        }
    }

    fun turn() {

        val currentLocations = agentManagers.map(AgentManager::moveAgents)
                .flatMap(AgentManager::getGridUnitPairings)
                .groupBy({ it.first }, { it.second })

        clearPrevious()

        currentLocations.forEach { gridUnitPoint, agents ->
            val gridUnit = grid.getGridUnitForPoint(gridUnitPoint)
            agents.forEach { gridUnit?.markAgent(it) }
        }

        previousAgentLocations = currentLocations
    }
}


val simulation by lazy {
    createActor()
}