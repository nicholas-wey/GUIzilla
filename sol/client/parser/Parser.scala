package guizilla.sol.client.parser
import sparkzilla.src.HTMLTokenizer
import sparkzilla.src.Token
import sparkzilla.src.OpenHTML
import sparkzilla.src.CloseHTML
import sparkzilla.src.OpenParagraph
import sparkzilla.src.CloseParagraph
import sparkzilla.src.OpenBody
import sparkzilla.src.CloseBody
import sparkzilla.src.OpenLink
import sparkzilla.src.CloseLink
import sparkzilla.src.Text
import sparkzilla.src.Input
import sparkzilla.src.Submit
import sparkzilla.src.OpenForm
import sparkzilla.src.CloseForm
import sparkzilla.src.Eof
import java.util.ArrayList
import java.io._

/**
 * a class that parses HTML files into parse trees and stores relevant
 * active element information. Note: if necessary, see our HTML_LL1.txt
 * (in this folder) for any clarifications about the design of the parser
 */
class Parser(tokenizer: HTMLTokenizer) {

  private def current: Option[Token] = tokenizer.current
  private def advance() = tokenizer.advance()
  var aeIndex = 3
  var activeElementArr = new ArrayList[ActiveElement]()
  for (i <- 0 to 3) {
    activeElementArr.add(null)
  }
  var inputIdxList = List[Int]()
  var submitIdxList = List[Int]()

  /**
   * a method that parses html files
   *
   * @return - a PageRep object which contains the parse tree for an HTML file as
   * well as an dynamic array containing the active elements in the HTML file
   * @throws ParseException - when there is an unexpected token
   * @throws LexicalException - if a problem occurs when tokenizing
   * @throws IOException - if there is a problem reading from the file
   */
  def parse(): PageRep = {
    advance()
    val exp = parseHTMLPage()
    current match {
      case Some(CloseHTML) =>
        advance()
        current match {
          case Some(Eof) => new PageRep(activeElementArr, exp)
          case _         => throw new ParseException("End of file")
        }
      case _ => throw new ParseException("</html>")
    }
  }

  /**
   * a method that parses an html page
   *
   * @return - an HTMLPage representing the html page
   * @throws ParseException - when there is an unexpected token
   * @throws LexicalException - if a problem occurs when tokenizing
   * @throws IOException - if there is a problem reading from the file
   */
  private def parseHTMLPage(): HTMLPage = current match {
    case Some(OpenHTML) => {
      advance()
      current match {
        case Some(OpenBody) => {
          advance()
          new BeginHTML(parseHTMLStuff())
        }
        case _ => throw new ParseException("<body>")
      }
    }
    case _ => throw new ParseException("<html>")
  }

  /**
   * a method that parses an html stuff
   *
   * @return - an HTMLStuff representing the html stuff
   * @throws ParseException - when there is an unexpected token
   * @throws LexicalException - if a problem occurs when tokenizing
   * @throws IOException - if there is a problem reading from the file
   */
  private def parseHTMLStuff(): HTMLStuff = current match {
    case Some(OpenForm(url)) => {
      inputIdxList = List[Int]()
      submitIdxList = List[Int]()
      advance()
      new BeginForm(parseFormStuff(url), parseHTMLStuff())
    }
    case Some(OpenParagraph) => {
      advance()
      new BeginPara(parseParaStuff(), parseHTMLStuff())
    }
    case Some(CloseBody) => {
      advance()
      EndHTML
    }
    case _ => throw new ParseException("<form>, <p>, or </body>")
  }

  /**
   * a method that parses a form stuff
   *
   * @param url - a String representing the url
   * @return - an FormStuff representing the form stuff
   * @throws ParseException - when there is an unexpected token
   * @throws LexicalException - if a problem occurs when tokenizing
   * @throws IOException - if there is a problem reading from the file
   */
  private def parseFormStuff(url: String): FormStuff = current match {
    case Some(OpenParagraph) => {
      advance()
      new BeginParaF(parseParaStuff(), parseFormStuff(url))
    }
    case Some(Input(name)) => {
      aeIndex += 1 // Figure out where to index
      var input = new AEInput(name, "______")
      activeElementArr.add(input)
      inputIdxList ::= aeIndex
      advance()
      new InputF(aeIndex, name, parseFormStuff(url))
    }
    case Some(Submit) => {
      aeIndex += 1
      var submit = new AESubmit(url, List[Int]())
      activeElementArr.add(submit)
      submitIdxList ::= aeIndex
      advance()
      new SubmitF(aeIndex, parseFormStuff(url))
    }
    case Some(CloseForm) => {
      for (i <- submitIdxList) {
        activeElementArr.get(i) match {
          case AESubmit(url, iList) => {
            activeElementArr.set(i, new AESubmit(url, inputIdxList))
          }
          case _ => throw new ParseException("AESubmit in this array index")
        }
      }
      advance()
      EndForm
    }
    case _ => throw new ParseException("<p>, an input, a submit, or </form> ")
  }

  /**
   * a method that parses a para stuff
   *
   * @return a ParaStuff representing the para stuff
   * @throws ParseException - when there is an unexpected token
   * @throws LexicalException - if a problem occurs when tokenizing
   * @throws IOException - if there is a problem reading from the file
   */
  private def parseParaStuff(): ParaStuff = current match {
    case Some(Text(text)) => {
      advance()
      new TextP(text, parseParaStuff())
    }
    case Some(OpenLink(url)) => {
      aeIndex += 1
      val link = new AELink(url)
      activeElementArr.add(link)
      advance()
      new NewLink(aeIndex, parseLink(aeIndex), parseParaStuff())
    }
    case Some(CloseParagraph) => {
      advance()
      EndPara
    }
    case _ => throw new ParseException("text, a link, or </p>")
  }

  /**
   * a method that parses a link
   *
   * @return a Link representing the link
   * @throws ParseException - when there is an unexpected token
   * @throws LexicalException - if a problem occurs when tokenizing
   * @throws IOException - if there is a problem reading from the file
   */
  private def parseLink(aeIdx: Int): Link = current match {
    case Some(Text(text)) => {
      advance()
      current match {
        case Some(CloseLink) => {
          advance()
          new ALink(aeIdx, text)
        }
        case _ => throw new ParseException("a closelink")
      }
    }
    case _ => throw new ParseException("text")
  }
}
