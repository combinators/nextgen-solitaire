package org.combinators.solitaire.castle

import org.combinators.solitaire.domain._
import org.combinators.templating.twirl.Java

package object streets_and_alleys {
  def getDeal: Seq[Step] = {
    var deal:Seq[Step] = Seq (FilterStep(IsAce(DealComponents)))
    var colNum = 0
        while (colNum < 4)
        {
          deal = deal :+ DealStep(ElementTarget(Tableau, colNum), Payload(numCards =  7))
          colNum += 1
        }
    while (colNum < 8)
    {
      deal = deal :+ DealStep(ElementTarget(Tableau, colNum), Payload(numCards =  6))
      colNum += 1
    }


    deal
  }

  val definition:Solitaire = {

    Solitaire(name="Streets_And_Alleys",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal,

      /** from element can infer ks.ViewWidget as well as Base Element. */
      specializedElements = Seq.empty,

      /** All rules here. */
      moves = Seq(tableauToTableau, tableauToFoundation),
      logic = BoardState(Map(Tableau -> 0, Foundation -> 52)),
      solvable = true,
      customizedSetup = Seq(PrepareTableauToFoundation)
    )
  }
}
