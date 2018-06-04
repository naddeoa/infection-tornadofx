package org.naddeo.simulation

/**
 * Higher level coordinate system of grid units. Grids are made of grid
 * units and grid units are located by a single coordinate one one of their
 * corners.
 */
data class GridUnitPoint(val x: Int, val y: Int)

/**
 * A point on the grid. This will be within a grid unit as well.
 */
data class Point(val x: Int, val y: Int) {

    /**
     * Map this point to the grid unit that it is on.
     */
    fun asGridUnitPoint(): GridUnitPoint = GridUnitPoint(x - (x % simulationConfig.gridUnitSize), y - (y % simulationConfig.gridUnitSize))
}
