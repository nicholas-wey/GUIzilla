package guizilla.sol.client.parser
import java.util.ArrayList
import javafx.scene.layout.VBox
import guizilla.sol.client.Controller
import javafx.scene.text.Text

/**
 * this is the elemnt for a beginning html page
 */
abstract class HTMLPage extends Renderable

/**
 * this is the element for the beginning html page.
 *
 * @param html - this is the html stuff element within the page
 */
case class BeginHTML(html: HTMLStuff) extends HTMLPage {
  override def render(ctrl: Controller) {
    val vbx = ctrl.getVBox
    val nl = new Text("\n")
    vbx.getChildren.add(nl)
    ctrl.setVBox(vbx)
    html.render(ctrl)
  }
}