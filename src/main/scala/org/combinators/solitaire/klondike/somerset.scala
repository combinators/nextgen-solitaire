package org.combinators.solitaire.klondike

import org.combinators.solitaire.domain._

package object somerset extends variationPoints {

  override val klondikeMap: Map[ContainerType, Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](10)(BuildablePile),
    Waste -> Seq.fill[Element](1)(WastePile),
    Foundation -> Seq.fill[Element](4)(Pile),
    StockContainer -> Seq(Stock())
  )

  override val klondikeLayout: Map[ContainerType, Seq[Widget]] = Map(
    StockContainer -> horizontalPlacement(10, 10, 1, card_height),
    Foundation -> horizontalPlacement(240, 10, 4, card_height),
    Tableau -> horizontalPlacement(10, 200, num = 10, height = card_height * 8), // estimate
    Waste -> horizontalPlacement(20 + card_width, 10, 1, card_height),
  )

  val somerset:Solitaire = {
    Solitaire( name="Somerset",
      structure = klondikeMap,
      layout = Layout(klondikeLayout),
      deal = somersetDeal,

      specializedElements = Seq(WastePile),

      moves = Seq(tableauToTableau, wasteToTableau, tableauToFoundation, wasteToFoundation, deckDeal, deckResetFromWaste, flipTableau),

      // fix winning logic
      logic = BoardState(Map(Foundation -> 52)),
      customizedSetup = Seq.empty
    )
  }
}