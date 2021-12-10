package guizilla.sol.client.parser
import java.util.ArrayList
import guizilla.sol.client.Controller
import javafx.scene.layout.VBox
import javafx.scene.text.Text

/**
 * this is the non-terminal for handling paragraphs
 */
abstract class ParaStuff extends Renderable

/**
 * this is the case when there is text
 * @param text - this is the text inside the paragraph
 * @param next - this is the element following this ParaStuff element
 */
case class TextP(text: String, next: ParaStuff) extends ParaStuff {
  override def render(ctrl: Controller) {
    val pText = new Text(text + "\n")
    val vbx = ctrl.getVBox
    vbx.getChildren.add(pText)
    ctrl.setVBox(vbx)
    next.render(ctrl)
  }
}

/**
 * this is the element used when there is a new link in the paragraph
 *
 * @param idx - this is the number that will be entered by the user to access
 * this active element in the page
 * @param alink - this is the lLink element in the link, see that class for a
 * more informative description
 * @param next - this is the element following this ParaStuff element
 */
case class NewLink(idx: Int, alink: Link, next: ParaStuff) extends ParaStuff {
  override def render(ctrl: Controller) {
    alink.render(ctrl)
    next.render(ctrl)
  }
}

/**
 * this is the element to signal the ending of a paragraph element
 */
case object EndPara extends ParaStuff {
  override def render(ctrl: Controller) {
    return
  }
}
