package guizilla.sol.server.pages
import guizilla.src.Page
/**
 * this is the first last class. it is a series of gui pages that ask you about
 * your names and other information and then displays the results.
 */
class FirstLast extends Page {
  var firstName = ""
  var middleName = ""
  var lastName = ""

  /**
   * this method is the default handler for the page. that means if there is a
   * bad relative url it will be sent here. it just calls the getFirstName
   * method
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page that can be displayed.
   */
  override def defaultHandler(inputs: Map[String, String], sessionId: String): String =
    getFirstName(inputs, sessionId)

  /**
   * this method is the first pages that asks the user for their first name
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def getFirstName(inputs: Map[String, String], sessionId: String): String =
    "<html><body>" +
      "<form method=\"post\" action=\"/id:" + sessionId + "/getMiddleName\">" +
      "<p> Please enter the your first name: </p>" +
      "<input type=\"text\" name=\"firstName\" />" +
      "<input type=\"submit\" value=\"submit\" />" +
      "</form></body></html>"

  /**
   * this method is the page that that the user will reach after entering their
   * first name ideally. It asks the user for their middle name
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def getMiddleName(inputs: Map[String, String], sessionId: String): String =
    inputs.get("firstName") match {
      case Some(name) =>
        firstName = name
        "<html><body><p>Hello, " + firstName + "</p>" +
          "<form method=\"post\" action=\"/id:" + sessionId + "/getLastName\">" +
          "<p>Please enter your middle name: </p>" +
          "<input type=\"text\" name=\"middleName\" />" +
          "<input type=\"submit\" value=\"submit\" />" +
          "</form><p>" +
          "<a href=\"/FirstLast\">Change your first name</a>" +
          "</p></body></html>"
      case None =>
        if (firstName != "") {
          "<html><body><p>Hello, " + firstName + "</p>" +
            "<form method=\"post\" action=\"/id:" + sessionId + "/getLastName\">" +
            "<p>Please enter your middle name: </p>" +
            "<input type=\"text\" name=\"middleName\" />" +
            "<input type=\"submit\" value=\"submit\" />" +
            "</form><p>" +
            "<a href=\"/FirstLast\">Change your first name</a>" +
            "</p></body></html>"
        } else {
          "<html><body><p>I’m sorry, you never put in a first name." +
            "<a href=\"/FirstLast\">Return to the Beginning</a></p></body></html>"
        }
    }

  /**
   * this method is the page that that the user will reach after entering their
   * middle name ideally. It asks the user for their last name
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page that string can be displayed.
   */
  def getLastName(inputs: Map[String, String], sessionId: String): String =
    inputs.get("middleName") match {
      case Some(name) =>
        middleName = name
        "<html><body><p>Hello, " + firstName + " " + middleName + "</p>" +
          "<form method=\"post\" action=\"/id:" + sessionId + "/displayResult\">" +
          "<p>Please enter your last name: </p>" +
          "<input type=\"text\" name=\"lastName\" />" +
          "<input type=\"submit\" value=\"submit\" />" +
          "</form><p>" +
          "<a href=\"/id:" + sessionId + "/getMiddleName\">Change your middle name</a>" +
          "</p></body></html>"
      case None =>
        if (firstName != "" && middleName != "") {
          "<html><body><p>Hello, " + firstName + " " + middleName + "</p>" +
            "<form method=\"post\" action=\"/id:" + sessionId + "/displayResult\">" +
            "<p>Please enter your last name: </p>" +
            "<input type=\"text\" name=\"lastName\" />" +
            "<input type=\"submit\" value=\"submit\" />" +
            "</form><p>" +
            "<a href=\"/id:" + sessionId + "/getMiddleName\">Change your middle name</a>" +
            "</p></body></html>"
        } else {
          "<html><body><p>I’m sorry, there was an error retrieving your input." +
            "<a href=\"/FirstLast\">Return to the Beginning</a></p></body></html>"
        }
    }

  /**
   * this method is the page that displays the results after the user has entered
   * their first middle and last name. it asks the user how they are doing
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
    inputs.get("lastName") match {
      case Some(lName) =>
        lastName = lName
        "<html><body>" +
          "<p>Hello, " + firstName + " " + middleName + " " + lastName + "! How has your day been today?</p>" +
          "<form method=\"post\" action=\"/id:" + sessionId + "/goodOrBad\">" +
          "<p>Good or Bad?</p>" +
          "<input type=\"text\" name=\"result\" />" +
          "<input type=\"submit\" value=\"submit\" />" +
          "</form>" +
          "<p><a href=\"/id:" + sessionId + "/getLastName\">Change your last name</a></p>" +
          "</body></html>"
      case None =>
        if (firstName != "" && middleName != "" && lastName != "") {
          "<html><body>" +
            "<p>Hello, " + firstName + " " + middleName + " " + lastName + "! How has your day been today?</p>" +
            "<form method=\"post\" action=\"/id:" + sessionId + "/goodOrBad\">" +
            "<p>Good or Bad?</p>" +
            "<input type=\"text\" name=\"result\" />" +
            "<input type=\"submit\" value=\"submit\" />" +
            "</form>" +
            "<p><a href=\"/id:" + sessionId + "/getLastName\">Change your last name</a></p>" +
            "</body></html>"
        } else {
          "<html><body><p>I’m sorry, there was an error retrieving your input." +
            "<a href=\"/FirstLast\">Return to the Beginning</a></p></body></html>"
        }
    }

  /**
   * this method is the page that the user will reach after entering how they
   * are doing and it will respond to them in some way depending on their
   * answer
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def goodOrBad(inputs: Map[String, String], sessionId: String): String =
    inputs.get("result") match {
      case Some(feeling) =>
        if (feeling.toLowerCase == "good") {
          "<html><body><p>I'm glad to hear that!</p><p>" +
            "<a href=\"/FirstLast\">Return to the Beginning</a></p></body></html>"
        } else if (feeling.toLowerCase() == "bad") {
          "<html><body><p>Oh, sorry!</p><p>" +
            "<a href=\"/FirstLast\">Return to the Beginning</a></p></body></html>"
        } else {
          "<html><body><p>You didn't enter good or bad!</p><p>" +
            "<a href=\"/id:" + sessionId + "/displayResult\">Go back</a>" +
            "</p></body></html>"
        }
      case None =>
        "<html><body><p>I’m sorry, there was an error retrieving your input." +
          "<a href=\"/FirstLast\">Return to the Beginning</a></p></body></html>"
    }

}
