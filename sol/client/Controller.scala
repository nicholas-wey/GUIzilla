package guizilla.sol.client

import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.stage.Stage
import javafx.scene.control._
import javafx.scene.layout.VBox
import lab12.sol.ResultPageController
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
import guizilla.sol.client.parser._

/**
 * Class controlling the main page of Client.
 */
class Controller extends Client {
  @FXML private var urlText: TextField = _
  @FXML private var backButton: Button = _
  @FXML private var forwardButton: Button = _
  @FXML private var quitButton: Button = _
  @FXML var theVBox: VBox = _
  private var stage: Stage = null
  private var resultPage: Scene = null
  private var resultPageController: ResultPageController = null

  /**
   * this method gets the theVBox field from our object
   *
   * @return - the theVBox, VBox from this object
   */
  def getVBox: VBox = this.theVBox

  /**
   * this method sets the theVBox field from of object
   *
   * @param bvx - this is a VBox. In all cases that we use this it is the
   * theVBox field from our object
   */
  def setVBox(vbx: VBox) {
    this.theVBox = vbx
  }

  /**
   * this method renders our page by calling render on the pageRep object and
   * passing it this object as the necessary controller
   */
  def renderPage {
    println("Rendering...")
    theVBox.getChildren.clear()
    currentPage.page.render(this)
    println("Done rendering")
  }

  /**
   * this method submits a form when you are on a webpage.
   *
   * @param idx - this is the index of the submit button in the active elements
   * array in our object. This is so that it can get the correct url
   */
  def submitForm(idx: Int) {
    currentPage.aeArray.get(idx) match {
      case AESubmit(url, iList) => {
        val parsedURL = parseURL(url)
        val req = buildPostRequest(parsedURL, iList)
        val resp = sendRequest(req, parsedURL)
        parseResponse(resp)
        urlText.setText("http://" + currentHost + parsedURL)
        renderPage
      }
      case _ => println("this should not happen")
    }
  }

  /**
   * this method follows a link on a given page in the gui
   *
   * @param idx - this is the index of the particular link in the active elements
   * array in our object. This is so that it can get the correct url
   */
  def followLink(idx: Int) {
    currentPage.aeArray.get(idx) match {
      case AELink(url) => {
        val parsedURL = parseURL(url)
        val req = buildGetRequest(parsedURL)
        val resp = sendRequest(req, parsedURL)
        parseResponse(resp)
        urlText.setText("http://" + currentHost + parsedURL)
        renderPage
      }
      case _ => println("this should not happen")
    }
  }

  /**
   * Handles the pressing of the submit button on the main GUI page.
   *
   * @param event - this is the event of the go forward button being pressed.
   */
  @FXML protected def goToURL(event: ActionEvent) {
    val url = urlText.getText
    val parsedURL = parseURL(url)
    if (parsedURL == null) {
      return
    } else {
      val req = buildGetRequest(parsedURL)
      val resp = sendRequest(req, parsedURL)
      parseResponse(resp)
      urlText.setText("http://" + currentHost + parsedURL)
      renderPage
    }
  }

  /**
   * Handles the pressing of the clear button on the main GUI page.
   *
   * @param event - this is the event of the back button being pressed.
   */
  @FXML protected def goBack(event: ActionEvent) {
    if (prevPages.isEmpty) {
      return
    } else {
      val newPage = prevPages.head
      currentPage = newPage._1
      currentHost = newPage._2
      maybeHost = newPage._2
      val url = newPage._3
      prevPages = prevPages.drop(1)
      if (prevPages.isEmpty) {
        urlText.setText("")
        currentHost = ""
        maybeHost = ""
        renderPage
      } else {
        urlText.setText(url)
        prevURL = url
        renderPage
      }
    }
  }

  /**
   * Handles the pressing of the quit button that is present on every page.
   *
   * @param event - this is the event that the button is actually pressed.
   */
  @FXML protected def quit(event: ActionEvent) {
    stage.close()
  }

  /**
   * Sets the stage field of the controller to the given stage.
   *
   * @param stage - this is the The stage
   */
  def setStage(stage: Stage) {
    this.stage = stage
  }
}
