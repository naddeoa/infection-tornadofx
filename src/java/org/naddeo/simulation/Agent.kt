package org.naddeo.simulation

/**
 * An agent is the thing that roams around the board. If it
 * gets infected then it can infect other agents that it comes
 * close to.
 *
 * Most of the mutability happens to this class.
 */
data class Agent(var x: Int, var y: Int, var infected: Boolean = false) {

    /**
     * Agents are near other agents if they are within two paces in either direction
     */
    fun isNear(agent: Agent) = Math.abs(x - agent.x) <= 2 && Math.abs(y - agent.y) <= 2
}

/**
 * Manages the movement of groups of agents.
 * Really just an optimization that allows for some calculations to be
 * parallelized.
 */
class AgentManager(agentsControlled: Int) {

    private val agents: List<Agent> = (1..agentsControlled).map {
        Agent((Math.random() * simulationConfig.boardSize).toInt(), (Math.random() * simulationConfig.boardSize).toInt(), Math.random() > 0.80)
    }

    fun moveAgents(): AgentManager {
        agents.forEach {
            // Make the steps bigger for infected agents
            val stepMultiplyer = if (it.infected) 2 else 1
            it.x += randomStep() * stepMultiplyer
            it.y += randomStep() * stepMultiplyer
        }

        return this
    }

    fun getGridUnitPairings(): List<Pair<GridUnitPoint, Agent>> = agents.map {
        Pair(Point(it.x, it.y).asGridUnitPoint(), it)
    }

    private fun randomStep(): Int = if (Math.random() > 0.5) 1 else -1

    fun size() = agents.size

    fun infected() = agents.filter(Agent::infected).size
}


