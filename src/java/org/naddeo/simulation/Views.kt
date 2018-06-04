package org.naddeo.simulation

import javafx.scene.Parent
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.View
import tornadofx.hbox
import tornadofx.rectangle

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
