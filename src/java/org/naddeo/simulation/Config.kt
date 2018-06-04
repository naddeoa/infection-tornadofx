package org.naddeo.simulation


/**
 * Simulation configuration
 */
class SimulationConfig internal constructor(
        val gridUnitSize: Int = 5,
        val boardSize: Int = 400,
        val gridUnitPixels: Double = 4.0)

val simulationConfig = SimulationConfig()

