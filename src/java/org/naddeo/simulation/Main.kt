package org.naddeo.simulation


import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.VBox
import kotlinx.coroutines.experimental.launch
import tornadofx.App
import tornadofx.InternalWindow
import tornadofx.View
import tornadofx.button
import tornadofx.hbox
import tornadofx.launch

class MainScreen : View("Simulation") {

    override val root: Parent = VBox()
    private val grid: Grid by inject()

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
                        simulation.send(SimulationCommands.Start(grid, stepDelay = 250))
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

        }
        root.add(grid)
    }
}

class MainApp : App(MainScreen::class, InternalWindow.Styles::class)

fun main(args: Array<String>) {
    launch<MainApp>(args)
}

