package guizilla.sol.client.parser
import java.util.ArrayList
import javafx.scene.layout.VBox
import guizilla.sol.client.Controller

/**
 * this the class for out HTMLStuff elements when parsing
 */
abstract class HTMLStuff extends Renderable

/**
 * this the the HTMLStuff element to signal the beginning of a form
 *
 * @param form - this is the FormStuff element
 * @param next - this is the HTMLStuff following the FormStuff
 */
case class BeginForm(form: FormStuff, next: HTMLStuff) extends HTMLStuff {
  override def render(ctrl: Controller) {
    form.render(ctrl)
    next.render(ctrl)
  }
}

/**
 * this the the HTMLStuff element to signal the beginning of a paragraph
 *
 * @param para - this is the ParaStuff element of the paragraph
 * @param next - this is the HTMLStuff following the ParaStuff
 */
case class BeginPara(para: ParaStuff, next: HTMLStuff) extends HTMLStuff {
  override def render(ctrl: Controller) {
    para.render(ctrl)
    next.render(ctrl)
  }
}

/**
 * this the the HTMLStuff element to signal the end of the HTMLStuff
 */
case object EndHTML extends HTMLStuff {
  override def render(ctrl: Controller) {
    return
  }
}
