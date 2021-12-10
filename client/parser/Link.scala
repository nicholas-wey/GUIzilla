package guizilla.sol.client.parser
import java.util.ArrayList
import guizilla.sol.client.Controller
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.control.Hyperlink
import javafx.event.ActionEvent
import javafx.event.EventHandler

/**
 * this is the link element found in parsing
 */
abstract class Link extends Renderable

/**
 * this is a link element
 * @param idx - this is the index that is associcated with the link
 * @param text - this is the String that is associated with the link
 */
case class ALink(idx: Int, text: String) extends Link {
  override def render(ctrl: Controller) {
    val link = new Hyperlink(text)
    link.setOnAction(new EventHandler[ActionEvent]() {
      override def handle(e: ActionEvent) {
        ctrl.followLink(idx)
      }
    })
    val vbx = ctrl.getVBox
    vbx.getChildren.add(link)
    ctrl.setVBox(vbx)
  }
}
