package org.naddeo.simulation


import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import kotlinx.coroutines.experimental.launch
import tornadofx.App
import tornadofx.InternalWindow
import tornadofx.View
import tornadofx.button
import tornadofx.hbox
import tornadofx.label
import tornadofx.launch

class MainScreen : View("Simulation") {

    override val root: Parent = VBox()
    private val grid: Grid by inject()
    private val healthyLabel = Label()
    private val infectedLabel = Label()

    init {
        hbox {
            this.alignment = Pos.CENTER
            this.spacing = 20.0

            button("Do a turn") {
                setOnMouseClicked {
                    launch {
                        simulation.send(SimulationCommands.Turn())
                    }
                }
            }

            button("Play") {
                setOnMouseClicked {
                    launch {
                        simulation.send(SimulationCommands.Start(grid, healthyLabel, infectedLabel, stepDelay = 100))
                    }
                }
            }

            button("Stop") {
                setOnMouseClicked {
                    launch {
                        simulation.send(SimulationCommands.Stop())
                    }
                }
            }

            button("Resume") {
                setOnMouseClicked {
                    launch {
                        simulation.send(SimulationCommands.Resume())
                    }
                }
            }

            hbox {
                this.alignment = Pos.CENTER
                label("Healthy: ")
                add(healthyLabel)
            }

            hbox {
                this.alignment = Pos.CENTER
                label("Infected: ")
                add(infectedLabel)
            }

        }
        root.add(grid)
    }
}

class MainApp : App(MainScreen::class, InternalWindow.Styles::class)

fun main(args: Array<String>) {
    launch<MainApp>(args)
}

