package org.naddeo.simulation


import javafx.scene.Parent
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import kotlinx.coroutines.experimental.async
import tornadofx.App
import tornadofx.InternalWindow
import tornadofx.View
import tornadofx.button
import tornadofx.hbox
import tornadofx.launch
import tornadofx.rectangle


const val GRID_UNIT_SIZE = 5
const val BOARD_SIZE = 400
const val GRID_UNIT_PIXELS = 4.0


class AgentManager(val agentsControlled: Int = 50) {

    val agents: List<Agent> = (1..agentsControlled).map {
        Agent((Math.random() * BOARD_SIZE).toInt(), (Math.random() * BOARD_SIZE).toInt(), Heading.random())
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

class Simulation(val grid: Grid, val numberOfManagers: Int = 10) {

    private var previousAgentLocations: Map<GridUnitPoint, List<Agent>> = mapOf()

    val agentManagers: List<AgentManager> = (1..numberOfManagers).map {
        AgentManager()
    }

    fun turn() {

        val currentLocations = agentManagers.map(AgentManager::moveAgents)
                .flatMap(AgentManager::getGridUnitPairings)
                .groupBy({ it.first }, { it.second })

        // Clear out the old locations of the agents
        previousAgentLocations.forEach { gridUnitPoint, _ ->
            grid.getGridUnitForPoint(gridUnitPoint)?.clear()
        }

        currentLocations.forEach { gridUnitPoint, agents ->
            val gridUnit = grid.getGridUnitForPoint(gridUnitPoint)
            agents.forEach { gridUnit?.markAgent(it) }
        }

        previousAgentLocations = currentLocations
    }
}

sealed class Heading {
    class North : Heading()
    class South : Heading()
    class East : Heading()
    class West : Heading()

    companion object {
        fun random(): Heading = when ((Math.random() % 3).toInt()) {
            0 -> North()
            1 -> South()
            2 -> East()
            else -> West()
        }
    }
}

data class Agent(var x: Int, var y: Int, var heading: Heading)

data class Point(val x: Int, val y: Int) {

    fun asGridUnitPoint(): GridUnitPoint = GridUnitPoint(x - (x % GRID_UNIT_SIZE), y - (y % GRID_UNIT_SIZE))
}

data class GridUnitPoint(val x: Int, val y: Int)


class GridUnit(private val x: Int, private val y: Int) : View() {

    private val endX = x + GRID_UNIT_SIZE
    private val endY = y + GRID_UNIT_SIZE
    private val units: Map<Point, Rectangle>

    override val root: Parent = VBox()

    init {
        val unitsBuilder = mutableMapOf<Point, Rectangle>()

        with(root) {
            for (i in x..x + GRID_UNIT_SIZE) {
                hbox {
                    for (j in y..y + GRID_UNIT_SIZE) {
                        rectangle {
                            fill = Color.WHITE
                            width = GRID_UNIT_PIXELS
                            height = GRID_UNIT_PIXELS
                            unitsBuilder[Point(i, j)] = this

                        }
                    }
                }
            }
        }


        units = unitsBuilder
    }

    fun clear() = units.forEach { _, rectangle ->
        rectangle.fill = Color.WHITE
    }

    fun markAgent(agent: Agent) {
        val unit = units[Point(agent.x, agent.y)]
        unit?.fill = Color.BLACK
    }

    fun contains(point: Point): Boolean = (x >= point.x && y >= point.y && endX < point.x && endY < point.y)
}


class Grid : View() {

    override val root: Parent = VBox()
    val gridUnits: Map<GridUnitPoint, GridUnit>

    init {
        val gridUnitsBuilder = mutableMapOf<GridUnitPoint, GridUnit>()
        with(root) {
            for (x in 0..BOARD_SIZE step GRID_UNIT_SIZE) {
                hbox {
                    for (y in 0..BOARD_SIZE step GRID_UNIT_SIZE) {
                        GridUnit(x, y).also {
                            add(it)
                            gridUnitsBuilder[Point(x, y).asGridUnitPoint()] = it
                        }
                    }
                }
            }
        }

        gridUnits = gridUnitsBuilder
    }

    fun getGridUnitForPoint(gridUnitPoint: GridUnitPoint): GridUnit? {
//        return gridUnits[gridUnitPoint] ?: throw NullPointerException("Grid unit doesn't exist for $gridUnitPoint")
        return gridUnits[gridUnitPoint]
    }

}

class MainScreen : View("Simulation") {


    override val root: Parent = VBox()
    private val grid: Grid by inject()
    private val simulation = Simulation(grid)

    init {
        button("Do a turn") {

            setOnMouseClicked {
                async {
                    simulation.turn()
                }
            }
        }
        root.add(grid)
    }
}

class MainApp : App(MainScreen::class, InternalWindow.Styles::class) {

    override fun start(stage: Stage) {
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    launch<MainApp>(args)
}

