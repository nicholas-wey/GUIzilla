package guizilla.sol.server.pages
import guizilla.src.Page
import java.util.ArrayList
/**
 * this is a two day calendar page. you can add todays and tomorrows events
 *  and also remove events. when you are done, you can see what you days look
 *  like.
 */
class Calendar extends Page {
  var today = new ArrayList[String]()
  var tomorrow = new ArrayList[String]()

  override def clone: Calendar = {
    val cal = super.clone.asInstanceOf[Calendar]
    cal.today = this.today.clone.asInstanceOf[ArrayList[String]]
    cal.tomorrow = this.tomorrow.clone.asInstanceOf[ArrayList[String]]
    cal
  }

  /**
   * this method is the default handler for the page. that means if there is a
   * bad relative url it will be sent here. it just calls the emptyCalendar
   * method
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def defaultHandler(inputs: Map[String, String], sessionId: String): String =
    emptyCalendar(inputs, sessionId)

  /**
   * this method is the first page that asks the user for a single event that
   * they may have for both today and tomorrow. the returned string is the html
   * representation of the page.
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def emptyCalendar(inputs: Map[String, String], sessionId: String): String = {
    "<html><body><p>Hello there, please input any events you have to do today and tomorrow</p>" +
      "<form method=\"post\" action=\"/id:" + sessionId + "/modifyCalendar\">" +
      "<p> Today: </p>" +
      "<input type=\"text\" name=\"today\" />" +
      "<p> Tomorrow: </p>" +
      "<input type=\"text\" name=\"tomorrow\" />" +
      "<input type=\"submit\" value=\"submit\" />" +
      "</form></body></html>"
  }

  /**
   * this method is the second page that is loaded once the user has submitted
   * their information from the first page.Based upon those results this page
   * will display them and ask if they have any changes they would like to make
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def modifyCalendar(inputs: Map[String, String], sessionId: String): String = {
    inputs.get("today") match {
      case Some(event1) => {
        if (event1 != "") {
          today.add(event1)
        }
        inputs.get("tomorrow") match {
          case Some(event2) => {
            if (event2 != "") {
              tomorrow.add(event2)
            }
            "<html><body><p>Hello there, here are you events today:\n\n" +
              {
                if (today.isEmpty) {
                  "no events!"
                } else {
                  today.toString()
                }
              } + "\n\nand here are your events tomorrow:\n\n" +
              {
                if (tomorrow.isEmpty) {
                  "no events!"
                } else {
                  tomorrow.toString()
                }
              } + "\n\n" +
              "Are there any more events that you would like to add?</p>" +
              "<form method=\"post\" action=\"/id:" + sessionId + "/modifyCalendar2\">" +
              "<p> Today: </p>" +
              "<input type=\"text\" name=\"today\" />" +
              "<p> Tomorrow: </p>" +
              "<input type=\"text\" name=\"tomorrow\" />" +
              "<p> Are there any events that you would like to remove? (write them in): </p>" +
              "<p> Today: </p>" +
              "<input type=\"text\" name=\"removeToday\" />" +
              "<p> Tomorrow: </p>" +
              "<input type=\"text\" name=\"removeTomorrow\" />" +
              "<input type=\"submit\" value=\"submit\" /></form><p>" +
              "<a href=\"/id:" + sessionId + "/displayResult\">Nope! I'm done with my Calendar.</a></p></body></html>"
          }
          case None => {
            "<html><body><p>I’m sorry, there was an error retrieving your input." +
              "<a href=\"/Index\">Return to the Index</a></p></body></html>"
          }
        }
      }
      case None => {
        "<html><body><p>I’m sorry, there was an error retrieving your input." +
          "<a href=\"/Index\">Return to the Index</a></p></body></html>"
      }
    }
  }

  /**
   * this method is for the page that asks the user if they have any changes
   * they would like to make to the calender. this means both adding and
   * removing events from the calendar. if not they can process by using the
   * done button.
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def modifyCalendar2(inputs: Map[String, String], sessionId: String): String = {
    inputs.get("today") match {
      case Some(event1) => {
        if (event1 != "") {
          today.add(event1)
        }
        inputs.get("tomorrow") match {
          case Some(event2) => {
            if (event2 != "") {
              tomorrow.add(event2)
            }
            inputs.get("removeToday") match {
              case Some(remToday) => {
                if (today.contains(remToday)) {
                  today.remove(remToday)
                }
                inputs.get("removeTomorrow") match {
                  case Some(remTomorrow) => {
                    if (tomorrow.contains(remTomorrow)) {
                      tomorrow.remove(remTomorrow)
                    }
                    "<html><body><p>Hello there, here are you events today:\n\n" +
                      {
                        if (today.isEmpty) {
                          "no events!"
                        } else {
                          today.toString()
                        }
                      } + "\n\nand here are your events tomorrow:\n\n" +
                      {
                        if (tomorrow.isEmpty) {
                          "no events!"
                        } else {
                          tomorrow.toString()
                        }
                      } + "\n\n" +
                      "Are there any more events that you would like to add?</p>" +
                      "<form method=\"post\" action=\"/id:" + sessionId + "/modifyCalendar2\">" +
                      "<p> Today: </p>" +
                      "<input type=\"text\" name=\"today\" />" +
                      "<p> Tomorrow: </p>" +
                      "<input type=\"text\" name=\"tomorrow\" />" +
                      "<p> Are there any events that you would like to remove? (write them in): </p>" +
                      "<p> Today: </p>" +
                      "<input type=\"text\" name=\"removeToday\" />" +
                      "<p> Tomorrow: </p>" +
                      "<input type=\"text\" name=\"removeTomorrow\" />" +
                      "<input type=\"submit\" value=\"submit\" /></form><p>" +
                      "\n\n<a href=\"/id:" + sessionId + "/displayResult\">Nope! I'm done with my Calendar.</a></p></body></html>"
                  }
                  case None => {
                    "<html><body><p>I’m sorry, there was an error retrieving your input." +
                      "<a href=\"/Index\">Return to the Index</a></p></body></html>"
                  }
                }
              }
              case None => {
                "<html><body><p>I’m sorry, there was an error retrieving your input." +
                  "<a href=\"/Index\">Return to the Index</a></p></body></html>"
              }
            }
          }
          case None => {
            "<html><body><p>I’m sorry, there was an error retrieving your input." +
              "<a href=\"/Index\">Return to the Index</a></p></body></html>"
          }
        }
      }
      case None => {
        "<html><body><p>I’m sorry, there was an error retrieving your input." +
          "<a href=\"/Index\">Return to the Index</a></p></body></html>"
      }
    }
  }

  /**
   * this is the method that will be called when the nope im done button is
   * pressed. it will load a final page with the users information for the next
   * two days. there is also a button to return to the index.
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def displayResult(inputs: Map[String, String], sessionId: String): String = {
    "<html><body><p>Hello there, here is your final schedule:\n\nToday's events:\n\n" +
      {
        if (today.isEmpty) {
          "no events!"
        } else {
          today.toString()
        }
      } + "\n\nAnd here are your events tomorrow:\n\n" +
      {
        if (tomorrow.isEmpty) {
          "no events!"
        } else {
          tomorrow.toString()
        }
      } + "\n<a href=\"/Index\">Return to the Index</a></p></body></html>"
  }
}
