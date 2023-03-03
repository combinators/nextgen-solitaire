package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.templating.twirl.Java

package object castle extends variationPoints {




  val castle:Solitaire = {

    Solitaire(name="Castle",
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
