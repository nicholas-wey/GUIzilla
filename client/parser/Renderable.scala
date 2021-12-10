package guizilla.sol.client.parser
import java.util.ArrayList
import javafx.scene.layout.VBox
import guizilla.sol.client.Controller

/**
 * an trait for classes that can be renderable
 */
trait Renderable {

  /**
   * this method renders the element in the tree as a string
   *
   * @param ctrl - this is the controller from our gui/client
   */
  def render(ctrl: Controller)
}
