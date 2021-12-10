package guizilla.sol.client

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.GridPane
import javafx.scene.text.Text
import javafx.scene.control.TextArea
import javafx.stage.Stage
import javafx.scene.layout.VBox
import javafx.scene.control._

/**
 * this is our Guizilla class that opens our gui.
 */
class Guizilla extends Application {

  /**
   * this method starts our gui.
   *
   * @parma stage - this is the stage that is given by the application that is
   * running our guizilla
   */
  override def start(stage: Stage) {
    val loader: FXMLLoader =
      new FXMLLoader(getClass().getResource("tester.fxml"))
    val root: Parent = loader.load().asInstanceOf[GridPane]
    val controller: Controller = loader.getController()
    controller.setStage(stage)
    controller.renderPage
    val scene1 = new Scene(root, 700, 700)
    stage.setTitle("Guizilla")
    stage.setScene(scene1)
    stage.show()
  }
}

/**
 * this is our companion object that runs our Guizilla object when it is opened
 */
object Guizilla {
  def main(args: Array[String]) {
    Application.launch(classOf[Guizilla], args: _*);
  }
}
