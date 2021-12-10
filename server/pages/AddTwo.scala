package guizilla.sol.server.pages
import guizilla.src.Page

class AddTwo extends Page {

  private var num1 = 0

  /**
   * this method is the default handler for the page. that means if there is a
   * bad relative url it will be sent here.
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  override def defaultHandler(inputs: Map[String, String], sessionId: String): String =
    addTwoNumbers(inputs, sessionId)

  /**
   * this method is the first page of the AddTwo page. It is really just the
   * home page where you enter the first number.
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def addTwoNumbers(inputs: Map[String, String], sessionId: String): String =
    "<html><body><p>Add Two Numbers</p>" +
      "<form method=\"post\" action=\"/id:" + sessionId + "/secondNumber\">" +
      "<p> Please enter the first number you would like to add: </p>" +
      "<input type=\"text\" name=\"num1\" />" +
      "<input type=\"submit\" value=\"submit\" />" +
      "</form></body></html>"

  /**
   * this method is the second page of the method that you will reach once you
   * have successfully entered a first number.
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def secondNumber(inputs: Map[String, String], sessionId: String): String =
    inputs.get("num1") match {
      case Some(num) =>
        try {
          num1 = num.toInt
          "<html><body><p>The first number entered was " + num1 + "</p>" +
            "<form method=\"post\" action=\"/id:" + sessionId + "/displayResult\">" +
            "<p>Please enter the second number you would like to add: </p>" +
            "<input type=\"text\" name=\"num2\" />" +
            "<input type=\"submit\" value=\"submit\" />" +
            "</form><p>" +
            "<a href=\"/AddTwo\">Re-input first number</a>" +
            "</p></body></html>"
        } catch {
          case _: NumberFormatException =>
            "<html><body><p>Sorry you did not input a valid number." +
              "<a href=\"/AddTwo\">Return to the Start</a></p></body></html>"
        }
      case None =>
        "<html><body><p>Sorry, there was an error retrieving your input." +
          "<a href=\"/AddTwo\">Return to the Start</a></p></body></html>"
    }

  /**
   * this method is the page that will be shown once you have successfully
   * entered two numbers and asked for the answer.
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def displayResult(inputs: Map[String, String], sessionId: String): String =
    inputs.get("num2") match {
      case Some(num) =>
        try {
          val num2 = num.toInt
          "<html><body>" +
            "<p>" + num1 + " + " + num2 + " = " + (num1 + num2) + "</p>" +
            "</body></html>"
        } catch {
          case _: NumberFormatException =>
            "<html><body><p>You did not input a valid number." +
              "<a href=\"/AddTwo\">Return to the Start</a></p></body></html>"
        }
      case None =>
        "<html><body><p>Sorry, there was an error retrieving your input." +
          "<a href=\"/AddTwo\">Return to the Start</a></p></body></html>"
    }
}
