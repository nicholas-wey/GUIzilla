package guizilla.sol.client.parser

/**
 * an active element is one of the active elements in the webpage. There are 3
 * types that we need to worry about. Each one is explained in further detail
 * below
 */
abstract class ActiveElement

/**
 * this is a form element
 *
 * @param name - is the name of the field
 * @param userInput - this is the user's input to this field
 */
case class AEInput(name: String, userInput: String) extends ActiveElement

/**
 * this is a submit button element
 *
 * @param url - is the url that the form data is sent to
 * @param inputList - this is the list of indices of the elements in the active
 * element array that are associated with this submit button
 */
case class AESubmit(url: String, inputList: List[Int]) extends ActiveElement

/**
 * this is the element for a link
 *
 * @param url - this is the url that this link goes to
 */
case class AELink(url: String) extends ActiveElement
