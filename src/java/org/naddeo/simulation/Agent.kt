package org.naddeo.simulation


data class Agent(var x: Int, var y: Int)

class AgentManager(agentsControlled: Int) {

    val agents: List<Agent> = (1..agentsControlled).map {
        Agent((Math.random() * simulationConfig.boardSize).toInt(), (Math.random() * simulationConfig.boardSize).toInt())
    }

    fun moveAgents(): AgentManager {
        agents.forEach {
            it.x += randomStep()
            it.y += randomStep()
        }

        return this
    }

    fun getGridUnitPairings(): List<Pair<GridUnitPoint, Agent>> = agents.map {
        Pair(Point(it.x, it.y).asGridUnitPoint(), it)
    }

    private fun randomStep(): Int = if (Math.random() > 0.5) 1 else -1
}


