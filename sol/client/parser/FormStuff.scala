package guizilla.sol.client.parser
import java.util.ArrayList
import javafx.scene.layout.VBox
import javafx.scene.control.TextField
import javafx.scene.control.Button
import javafx.event.ActionEvent
import javafx.event.EventHandler
import guizilla.sol.client.Controller
import javafx.scene.text.Text
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

/**
 * form stuff are elements of the type forms stuff in our LL(1)
 */
abstract class FormStuff extends Renderable

/**
 * this marks the beginning of a paragraph within a form
 *
 * @param para - this is a ParaStuff element
 * @param next - this is the FormStuff that follows the above ParaStuff
 */
case class BeginParaF(para: ParaStuff, next: FormStuff) extends FormStuff {
  override def render(ctrl: Controller) {
    para.render(ctrl)
    next.render(ctrl)
  }
}

/**
 * this marks an input element in a form
 *
 * @param idx - this is the number that will be entered by the user to access
 * this active element in the page
 * @param name - this is the name of this form field
 * @param next - this is the FormStuff that follows
 */
case class InputF(idx: Int, name: String, next: FormStuff) extends FormStuff {
  override def render(ctrl: Controller) {
    val vbx = ctrl.getVBox
    val field = new TextField
    ctrl.currentPage.aeArray.get(idx) match {
      case AEInput(n, uI) => {
        if (uI == "______") {
          field.setPromptText("Enter text here...")
          vbx.getChildren.add(field)
        } else {
          field.setText(uI)
          vbx.getChildren.add(field)
        }
      }
      case _ =>
        throw new ParseException("AEInput in this array index")
    }
    field.textProperty.addListener(new ChangeListener[String] {
      override def changed(o: ObservableValue[_ <: String], old: String, updated: String) {
        ctrl.currentPage.aeArray.get(idx) match {
          case AEInput(name, uInput) => {
            ctrl.currentPage.aeArray.set(idx, new AEInput(name, updated))
          }
        }
      }
    })
    val nl = new Text("\n")
    vbx.getChildren.add(nl)
    ctrl.setVBox(vbx)
    next.render(ctrl)
  }
}

/**
 * this marks a submit element in a form
 *
 * @param idx - this is the number that will be entered by the user to access
 * this active element in the page
 * @param next - this is the FormStuff that follows
 */
case class SubmitF(idx: Int, next: FormStuff) extends FormStuff {
  override def render(ctrl: Controller) {
    val submitButton = new Button("Submit")
    submitButton.setOnAction(new EventHandler[ActionEvent]() {
      override def handle(e: ActionEvent) {
        ctrl.submitForm(idx)
      }
    })
    val vbx = ctrl.getVBox
    vbx.getChildren.addAll(submitButton)
    ctrl.setVBox(vbx)
    next.render(ctrl)
  }
}

/**
 * this is the element used to signal the end of a form
 */
case object EndForm extends FormStuff {
  override def render(ctrl: Controller) {
    return
  }
}
