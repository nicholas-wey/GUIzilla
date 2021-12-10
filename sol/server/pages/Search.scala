package guizilla.sol.server.pages
import guizilla.src.Page
import java.net.Socket
import java.io._
import java.net.URLDecoder
/**
 * this is our page for the Search integration into our server
 */
class Search extends Page {
  var titles = new Array[String](11)

  /**
   * this is the method that clones the search page so that we can keep track of
   * it
   *
   * @return - a search object. the clone to be specific.
   */
  override def clone: Search = {
    val search = super.clone.asInstanceOf[Search]
    search.titles = this.titles.clone.asInstanceOf[Array[String]]
    search
  }

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
   * the client. it should contain an html page string that can be displayed.
   */
  override def defaultHandler(inputs: Map[String, String], sessionId: String): String =
    displaySearchPage(inputs, sessionId)

  /**
   * this method is the first page. it asks the user to enter a query to be
   * search for so that it can display the results
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def displaySearchPage(inputs: Map[String, String], sessionId: String): String = {
    "<html><body><p>Welcome to Search. Please enter a query.</p>" +
      "<form method=\"post\" action=\"/id:" + sessionId + "/displayTitles\">" +
      "<p> Please enter the query you would like to search for: </p>" +
      "<input type=\"text\" name=\"query\" />" +
      "<input type=\"submit\" value=\"submit\" />" +
      "</form></body></html>"
  }

  /**
   * this method is the page that displays the titles that are the results of
   * the query of the users search. It displays the top 10 results and if there
   * are fewer than it prints as many as possible.
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def displayTitles(inputs: Map[String, String], sessionId: String): String = {
    val socket = new Socket("eckert", 8081)
    var query = inputs.get("query") match {
      case Some(x) => x
      case None    => null
    }
    val bWriter =
      new BufferedWriter(new OutputStreamWriter(socket.getOutputStream))
    val bReader =
      new BufferedReader(new InputStreamReader(socket.getInputStream))
    bWriter.write("REQUEST\t" + query + "\n")
    bWriter.flush
    socket.shutdownOutput
    var response = ""
    var serverOutput = bReader.read
    while (serverOutput != -1) {
      response += serverOutput.toChar
      serverOutput = bReader.read
    }
    val titArr = response.split("\t")
    val lastTitle = titArr(titArr.length - 1)
    if (lastTitle == "\n") {
      val badQuery = "<html><body><p>Sorry, no titles match your query</p>" +
        "<p><a href=\"/Search\">Enter a new Query</a>\n</p>" +
        "</body></html>"
      return badQuery
    }
    val newTitle = lastTitle.substring(0, lastTitle.length - 1)
    titArr(titArr.length - 1) = newTitle
    var actualResponse = ""
    for (x <- 1 to titArr.length - 1) {
      val title = titArr(x)
      if (x == 10) {
        titles(x) = title.substring(3, title.length)
      } else {
        titles(x) = title.substring(2, title.length)
      }
      val linkRep = x + ". <a href=\"/id:" + sessionId + "/result" + x + "\">" + titles(x) + "</a>"
      actualResponse += "<p>" + linkRep + "</p>"
    }
    actualResponse =
      "<html><body><p>These are the results of your query:</p>" +
        "<p><a href=\"/Search\">Enter a new Query</a>\n</p>" +
        actualResponse +
        "</body></html>"
    actualResponse
  }

  /**
   * this method is the to follow the first link. It accesses the correct element in
   * the array in order to load the correct information from the server.
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def result1(inputs: Map[String, String], sessionId: String): String = {
    gettingResultPage(1)
  }

  /**
   * this method is the to follow the second link. It accesses the correct
   * element in the array in order to load the correct information from the
   * server.
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def result2(inputs: Map[String, String], sessionId: String): String = {
    gettingResultPage(2)
  }

  /**
   * this method is the to follow the third link. It accesses the correct
   * element in the array in order to load the correct information from the
   * server.
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def result3(inputs: Map[String, String], sessionId: String): String = {
    gettingResultPage(3)
  }

  /**
   * this method is the to follow the fourth link. It accesses the correct
   * element in the array in order to load the correct information from the
   * server.
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def result4(inputs: Map[String, String], sessionId: String): String = {
    gettingResultPage(4)
  }

  /**
   * this method is the to follow the fifth link. It accesses the correct
   * element in the array in order to load the correct information from the
   * server.
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def result5(inputs: Map[String, String], sessionId: String): String = {
    gettingResultPage(5)
  }

  /**
   * this method is the to follow the sixth link. It accesses the correct
   * element in the array in order to load the correct information from the
   * server.
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def result6(inputs: Map[String, String], sessionId: String): String = {
    gettingResultPage(6)
  }

  /**
   * this method is the to follow the seventh link. It accesses the correct
   * element in the array in order to load the correct information from the
   * server.
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def result7(inputs: Map[String, String], sessionId: String): String = {
    gettingResultPage(7)
  }

  /**
   * this method is the to follow the eighth link. It accesses the correct
   * element in the array in order to load the correct information from the
   * server.
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def result8(inputs: Map[String, String], sessionId: String): String = {
    gettingResultPage(8)
  }

  /**
   * this method is the to follow the ninth link. It accesses the correct
   * element in the array in order to load the correct information from the
   * server.
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def result9(inputs: Map[String, String], sessionId: String): String = {
    gettingResultPage(9)
  }

  /**
   * this method is the to follow the tenth link. It accesses the correct
   * element in the array in order to load the correct information from the
   * server.
   *
   * @param input - this is the map of inputs that are used for sending a
   * response back to the server from this page so that it can send the response
   * to the client
   * @param sessionId - this is the string form of the session id that is used
   * to help keep track of the pages
   * @return - the string that will be sent back to the server to be sent to
   * the client. it should contain an html page string that can be displayed.
   */
  def result10(inputs: Map[String, String], sessionId: String): String = {
    gettingResultPage(10)
  }

  /**
   * this method gets the page from the second server that you guys give us
   * that has all of the actual pages. It connects to the server and then uses
   * the title to get the html from the correct page. We open the sockets
   * and do all that jazz.
   *
   * @param num - this is the number of the link that we are follwoing
   * @return - the string representation of the html page string that the
   * server sends back
   */
  private def gettingResultPage(num: Int): String = {
    val socket = new Socket("eckert", 8082)
    val tit = titles(num)
    val bWriter =
      new BufferedWriter(new OutputStreamWriter(socket.getOutputStream))
    val bReader =
      new BufferedReader(new InputStreamReader(socket.getInputStream))
    bWriter.write(tit + "\n")
    bWriter.flush
    socket.shutdownOutput
    var response = ""
    var serverOutput = bReader.read
    while (serverOutput != -1) {
      response += serverOutput.toChar
      serverOutput = bReader.read
    }
    response = "<html><body><p>" + response + "</p></body></html>"
    response
  }
}
