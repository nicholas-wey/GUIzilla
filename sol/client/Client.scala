package guizilla.sol.client
import java.io._
import java.util.ArrayList
import guizilla.sol.client.parser._
import scala.util.control.Breaks._
import java.lang.NumberFormatException
import java.net.Socket
import java.net.URLEncoder
import java.net.ConnectException
import java.net.UnknownHostException
import sparkzilla.src.HTMLTokenizer
import sparkzilla.src.LexicalException

/**
 * This is our client class that the user interacts with. It connects with the
 * server and loads the pages that the user requests.
 */
class Client {
  var eofReached = false
  var prevPages = List[(PageRep, String, String)]()
  val OpeningPage =
    new BeginHTML(new BeginPara(new TextP("Welcome to Guizilla!", EndPara), EndHTML))
  var currentPage = new PageRep(new ArrayList[ActiveElement](), OpeningPage)
  var prevHost = ""
  var currentHost = ""
  var maybeHost = ""
  var prevURL = ""
  var curURL = ""

  /**
   * this method gets input from the user
   *
   * @return - the string that the user entered
   */
  protected def getInput: String = {
    val br = new BufferedReader(new InputStreamReader(System.in))
    try {
      val query = br.readLine
      query
    } catch {
      case e: IOException =>
        println("Error reading user input, exiting.")
        System.exit(1)
        null
    }
  }

  /**
   * this method parses the url that the user either inputs is is given from a
   * link or from a form submission. It accounts for bad urls and relative ones
   *
   * @param url - the url to be parsed
   * @return - a string the parsed url
   */
  protected def parseURL(url: String): String = {
    if (url == null) {
      println("Invalid URL")
      null
    } else if (url.length == 0) {
      println("Invalid URL")
      null
    } else if (url.length() < 7) {
      if (url.substring(0, 1) == "/") {
        val newURL = url
        newURL
      } else {
        println("Invalid URL")
        null
      }
    } else if (url.substring(0, 7) == "http://") {
      if (url.length() == 7 || url.substring(7, 8) == "/") {
        ""
      } else {
        val arr = url.substring(7).split("/")
        maybeHost = arr(0)
        val lengHost = arr(0).length
        url.substring(7 + lengHost)
      }
    } else if (url.substring(0, 1) == "/") {
      val newURL = url
      newURL
    } else {
      println("Invalid URL")
      null
    }
  }

  /**
   * This method builds GET requests to be sent to the server.
   *
   * @param parsedURL - the parsed url of the page that is trying to be   *
   * requested
   *
   * @return - a string that is the full GET request to the server
   */
  protected def buildGetRequest(parsedURL: String): String = {
    var req = "GET " + parsedURL + " HTTP/1.0\r\nConnection: close\r\n" +
      "User-Agent: Sparkzilla/1.0\r\n\r\n"
    req
  }

  /**
   * This method builds POST requests to be sent to the server.
   *
   * @param parsedURL - the parsed url of the page that is trying to be
   * requested
   * @param iList - this is the list of the indices of the items in the active
   * elements array that are associated with this particular submit button
   * @return - a string that is the full POST request to the server
   */
  protected def buildPostRequest(parsedURL: String, iList: List[Int]): String = {
    var fullEncode = ""
    var anyEncode = false
    for (idx <- iList) {
      anyEncode = true
      currentPage.aeArray.get(idx) match {
        case AEInput(name, uInput) => {
          if (uInput == "______") {
            val toEncode = URLEncoder.encode(name, "UTF-8") + "="
            fullEncode += toEncode + "&"
          } else {
            val toEncode = URLEncoder.encode(name, "UTF-8") + "=" +
              URLEncoder.encode(uInput, "UTF-8")
            fullEncode += toEncode + "&"
          }
        }
        case _ => null
      }
    }
    if (anyEncode == true) {
      fullEncode = fullEncode.subSequence(0, fullEncode.length - 1).toString
    }
    var req = "POST " + parsedURL + " HTTP/1.0\r\nConnection: close\r\n" +
      "User-Agent: Sparkzilla/1.0\r\nContent-Type: " +
      "application/x-www-form-urlencoded\r\nContent-Length: " +
      fullEncode.length() + "\r\n\r\n" + fullEncode
    req
  }

  /**
   * this method sends the request to the server through the socket and sends
   * the users request
   *
   * @param req - this is the users full request as a string
   * @param url - this is the relative url of the page the user is going to
   * @return - a string that is the server's response to the request
   */
  protected def sendRequest(req: String, url: String): String = {
    println("Connecting to: " + maybeHost + ":8080")
    try {
      val socket = new Socket(maybeHost, 8080)
      println("Connected")
      prevHost = currentHost
      currentHost = maybeHost
      curURL = "http://" + currentHost + url
      val bWriter =
        new BufferedWriter(new OutputStreamWriter(socket.getOutputStream))
      val bReader =
        new BufferedReader(new InputStreamReader(socket.getInputStream))
      println("Requesting page and sending data (if need be): " + url)
      bWriter.write(req)
      bWriter.flush
      socket.shutdownOutput
      println("Request sent")
      var response = ""
      var serverOutput = bReader.read
      while (serverOutput != -1) {
        response += serverOutput.toChar
        serverOutput = bReader.read
      }
      socket.shutdownInput
      bWriter.close
      bReader.close
      socket.close
      println("Server responded with status OK")
      println("Parsing page...")
      response
    } catch {
      case ce: ConnectException => {
        currentHost = ""
        val badResponse = "\r\n\r\n<html><body><p>Error communicating with " +
          "server</p></body></html>"
        badResponse
      }
      case he: UnknownHostException =>
        currentHost = ""
        val badResponse = "\r\n\r\n<html><body><p>Unknown host" +
          "</p></body></html>"
        badResponse
    }
  }

  /**
   * this method parses the response from the server
   *
   * @param resp - this is a string that is the response from the server
   */
  protected def parseResponse(resp: String) = {
    var justHTML = resp.split("\r\n\r\n")
    var fullTree = new Parser(new HTMLTokenizer(new StringReader(justHTML(1))))
    try {
      val newPage = fullTree.parse()
      val host = prevHost
      val page = currentPage
      prevPages = (page, host, prevURL) :: prevPages
      currentPage = newPage
      val currentURL = curURL
      prevURL = currentURL
    } catch {
      case pe: ParseException   => println("Error in parsing HTML")
      case le: LexicalException => println("Error in tokenizing HTML")
      case ioe: IOException     => println("Error in reading HTML")
    }
  }
}
