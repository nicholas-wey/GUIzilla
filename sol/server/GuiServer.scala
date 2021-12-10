package guizilla.sol.server

import java.net._
import java.io._
import scala.collection.mutable.HashMap
import java.util.UUID.randomUUID
import guizilla.src.Page
import scala.collection.immutable.Map
import java.net.URLDecoder

/**
 * this is our server class that the client's requests will be sent to. It will
 * read the request and send back the appropriate response.
 */
class GuiServer {

  val servsock = new ServerSocket(8080)
  val sessionMap = new HashMap[String, Page]()

  val urlProblemResponse =
    "HTTP/1.0 500 Internal Server Error\r\n" +
      "Server: Guiserver/1.0\r\n" +
      "Connection: close\r\n" +
      "Content-Type: text/html\r\n\r\n" +
      "<html><body><p>Server Error: Bad request line" +
      "</p></body></html>"

  val badStrResponse =
    "HTTP/1.0 500 Internal Server Error\r\n" +
      "Server: Guiserver/1.0\r\n" +
      "Connection: close\r\n" +
      "Content-Type: text/html\r\n\r\n" +
      "<html><body><p>Server Error: Bad version string" +
      "</p></body></html>"

  val intServErrResponse =
    "HTTP/1.0 500 Internal Server Error\r\n" +
      "Server: Guiserver/1.0\r\n" +
      "Connection: close\r\n" +
      "Content-Type: text/html\r\n\r\n" +
      "<html><body><p>Internal server error: Exception thrown when processing" +
      "</p></body></html>"

  val pageNotFoundResponse =
    "HTTP/1.0 404 Not Found\r\n" +
      "Server: Guiserver/1.0\r\n" +
      "Connection: close\r\n" +
      "Content-Type: text/html\r\n\r\n" +
      "<html><body><p>Server error: Invalid class name in URL - " +
      "NoSuchMethodException</p></body></html>"

  val badRequestResponse =
    "HTTP/1.0 400 Bad Request\r\n" +
      "Server: Guiserver/1.0\r\n" +
      "Connection: close\r\n" +
      "Content-Type: text/html\r\n\r\n" +
      "<html><body><p>Server error: Method not POST or GET" +
      "</p></body></html>"

  val normalResponse =
    "HTTP/1.0 200 OK\r\n" +
      "Server: Guiserver/1.0\r\n" +
      "Connection: close\r\n" +
      "Content-Type: text/html\r\n\r\n"

  /**
   * this method checks the type of request that was given to the server, it is
   * either a get, post or bad request
   *
   * @param req - a String representing the request
   * @return - the String that is the response to the users request
   */
  private def checkTypeRequest(req: String): String = {
    if (req.size > 5) {
      if (req.substring(0, 5) == "POST ") {
        parseRequest(req, false)
      } else if (req.substring(0, 4) == "GET ") {
        parseRequest(req, true)
      } else {
        badRequestResponse
      }
    } else {
      badRequestResponse
    }
  }

  /**
   * this method builds the input map that will be used in invoking methods from
   * pages
   *
   * @parm fInputs - this is a string containing the encoded form inputs that
   * were submitted by the user
   * @return - this is a map of String to String that goes from form fields to
   * the users submitted answer
   * @throws ArrayIndexOutOfBoundsException - when the request is bad
   */
  private def buildInputMap(fInputs: String): Map[String, String] = {
    var inputMap = Map[String, String]()
    val inputArr = fInputs.split("&")
    for (kvPair <- inputArr) {
      val kvpArr = kvPair.split("=")
      if (kvpArr.length <= 2) {
        try {
          val name = kvpArr(0)
          val decodedName = URLDecoder.decode(name, "UTF-8")
          val input = kvpArr(1)
          val decodedinput = URLDecoder.decode(input, "UTF-8")
          inputMap += (decodedName -> decodedinput)
        } catch {
          case aioobe: ArrayIndexOutOfBoundsException =>
            val name = kvpArr(0)
            val input = ""
            inputMap += (name -> input)
        }
      } else {
        throw new ArrayIndexOutOfBoundsException
      }
    }
    inputMap
  }

  /**
   * this method parses the clients request to the server. This is where we will
   * be using our other pages in order to get information.
   *
   * @param req - a String representing is the request from the user
   * @param get - a Boolean representing if it is a get request, true if so,
   * and false otherwise
   * @return - a String the servers response
   */
  private def parseRequest(req: String, get: Boolean): String = {
    var inputs = Map[String, String]()
    try {
      if (!get) {
        val parts = req.split("\r\n\r\n")
        var contLen = -1
        for (line <- parts(0).split("\r\n")) {
          if (line.substring(0, 16).toLowerCase() == "content-length: ") {
            try {
              contLen = line.substring(16, line.size).toInt
            } catch {
              case nfe: NumberFormatException => badRequestResponse
            }
          }
        }
        if (contLen == -1) {
          badRequestResponse
        } else {
          try {
            val formInputs = parts(1).substring(0, contLen)
            inputs = buildInputMap(formInputs)
          } catch {
            case ioobe: IndexOutOfBoundsException => badRequestResponse
          }
        }
      }
      val firstLine = req.split("\r\n")(0)
      val flArr = firstLine.split(" ")
      val url = flArr(1)
      if (url == "") {
        return urlProblemResponse
      }
      val http = flArr(2)
      if (http == "HTTP/1.0" || http == "HTTP/1.1") {
        if (url.length >= 4 && url.substring(0, 4) == "/id:") {
          val idMeth = url.substring(4).split("/")
          val id = idMeth(0)
          val stringMeth = idMeth(1)
          sessionMap.get(id) match {
            case None => badRequestResponse
            case Some(page) => {
              val newId = randomUUID.toString
              val clonedPage = page.clone()
              sessionMap.put(newId, clonedPage)
              val method = clonedPage.getClass.getMethod(stringMeth, classOf[Map[String, String]], classOf[String])
              val partialResponse = method.invoke(clonedPage, inputs, newId).asInstanceOf[String]
              normalResponse + partialResponse
            }
          }
        } else {
          val arr = url.substring(1).split("/")
          val stringClass = arr(0)
          val realClass = Class.forName("guizilla.sol.server.pages." + stringClass)
          val newPage = realClass.newInstance.asInstanceOf[Page]
          val id = randomUUID.toString
          sessionMap.put(id, newPage)
          try {
            val stringMeth = arr(1)
            val method = newPage.getClass.getMethod(stringMeth, classOf[Map[String, String]], classOf[String])
            val partialResponse = method.invoke(newPage, inputs, id).asInstanceOf[String]
            normalResponse + partialResponse
          } catch {
            case aoe: ArrayIndexOutOfBoundsException =>
              if (url.substring(url.size - 1, url.size) == "/") {
                pageNotFoundResponse
              } else {
                val stringMeth = "defaultHandler"
                val method = newPage.getClass.getMethod(stringMeth, classOf[Map[String, String]], classOf[String])
                val partialResponse = method.invoke(newPage, inputs, id).asInstanceOf[String]
                normalResponse + partialResponse
              }
          }
        }
      } else {
        badStrResponse
      }
    } catch {
      case nsme: NoSuchMethodException  => pageNotFoundResponse
      case cnfe: ClassNotFoundException => pageNotFoundResponse
      case aiobe: ArrayIndexOutOfBoundsException =>
        badRequestResponse
    }
  }

  /**
   * this method gets the request from the user through the inputstream, and
   * then once the response is ready, it sends it back to the client through the
   * outputstream
   */
  private def runServer {
    while (true) {
      val socket = servsock.accept
      val bReader2 = new BufferedReader(new InputStreamReader(socket.getInputStream))
      val bWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream))
      var request = ""
      var input = bReader2.read
      while (input != -1) {
        request += input.toChar
        input = bReader2.read
      }
      val response = checkTypeRequest(request)
      bWriter.write(response)
      bWriter.flush()
      socket.shutdownOutput()
    }
  }
}

/**
 * this is our companion object for our GuiServer. this runs the server when it
 * is opened.
 */
object GuiServer {
  def main(args: Array[String]) {
    println("Server is running...")
    val server = new GuiServer
    server.runServer
  }
}
