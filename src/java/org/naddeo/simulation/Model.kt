package org.naddeo.simulation

data class GridUnitPoint(val x: Int, val y: Int)

data class Point(val x: Int, val y: Int) {

    fun asGridUnitPoint(): GridUnitPoint = GridUnitPoint(x - (x % simulationConfig.gridUnitSize), y - (y % simulationConfig.gridUnitSize))
}
