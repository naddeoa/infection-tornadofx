package org.naddeo.simulation


import javafx.scene.Parent
import javafx.stage.Stage
import tornadofx.App
import tornadofx.Controller
import tornadofx.InternalWindow
import tornadofx.View
import tornadofx.launch
import tornadofx.textfield

class LoginController : Controller() {
    val loginScreen: LoginScreen by inject()

    fun showLoginScreen(message: String, shake: Boolean = false) {
        loginScreen.replaceWith(loginScreen, sizeToScene = true, centerOnScreen = true)
    }
}

class LoginScreen : View("Please log in") {

    val loginController: LoginController by inject()


    override val root: Parent = textfield("hi")

}

class LoginApp : App(LoginScreen::class, InternalWindow.Styles::class) {
    val loginController: LoginController by inject()

    override fun start(stage: Stage) {
        super.start(stage)
//        loginController.init()
    }
}

fun main(args: Array<String>) {
    launch<LoginApp>(args)
}

