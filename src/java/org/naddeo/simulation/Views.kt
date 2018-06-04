package org.naddeo.simulation

import javafx.scene.Parent
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.View
import tornadofx.hbox
import tornadofx.rectangle

class GridUnit(private val x: Int, private val y: Int) : View() {

    private val endX = x + simulationConfig.gridUnitSize
    private val endY = y + simulationConfig.gridUnitSize
    private val units: Map<Point, Rectangle>

    override val root: Parent = VBox()

    init {
        val unitsBuilder = mutableMapOf<Point, Rectangle>()

        with(root) {
            for (i in x..x + simulationConfig.gridUnitSize) {
                hbox {
                    for (j in y..y + simulationConfig.gridUnitSize) {
                        rectangle {
                            fill = Color.WHITE
                            width = simulationConfig.gridUnitPixels
                            height = simulationConfig.gridUnitPixels
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

    fun markColor(point: Point, color: Color) {
        val unit = units[point]
        unit?.fill = color
    }

    fun contains(point: Point): Boolean = (x >= point.x && y >= point.y && endX < point.x && endY < point.y)
}

class Grid : View() {

    override val root: Parent = VBox()
    val gridUnits: Map<GridUnitPoint, GridUnit>

    init {
        val gridUnitsBuilder = mutableMapOf<GridUnitPoint, GridUnit>()
        with(root) {
            for (x in 0..simulationConfig.boardSize step simulationConfig.gridUnitSize) {
                hbox {
                    for (y in 0..simulationConfig.boardSize step simulationConfig.gridUnitSize) {
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
        return gridUnits[gridUnitPoint]
    }

}
