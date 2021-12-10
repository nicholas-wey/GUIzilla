package guizilla.sol.client.parser
import java.util.ArrayList

/**
 * this is the PageRep object that we give out of our parser. The aeArray is the
 * array of active elements for the page and page is the full HTMLPage
 * @param aeArray - our array of ActiveElements
 * @param pg - our HTMLPage
 */
class PageRep(aeArr: ArrayList[ActiveElement], pg: HTMLPage) {
  val aeArray = aeArr
  val page = pg
}
