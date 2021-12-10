package guizilla.sol.server.pages
import guizilla.src.Page
/**
 * this is our index page which can take users to any of our other pages
 */
class Index extends Page {

  /**
   * this method is the default handler for the page. that means if there is a
   * bad relative url it will be sent here. it just calls the displaySearchPage
   * method
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html string page that can be displayed.
   */
  def defaultHandler(inputs: Map[String, String], sessionId: String): String =
    startingPage(inputs, sessionId)

  /**
   * this method loads the only page in index. It sends back the code for the
   * page which has links to each of the other pages all in html
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def startingPage(inputs: Map[String, String], sessionId: String): String =
    "<html><body><p>Welcome to the localhost server!" +
      "<a href=\"/AddTwo\">AddTwo</a>" +
      "<a href=\"/Calendar\">Calendar</a>" +
      "<a href=\"/FirstLast\">FirstLast</a>" +
      "<a href=\"/Search\">Search</a>" +
      "</p></body></html>"
}
