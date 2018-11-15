package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.spider.variationPoints


package object spiderette extends variationPoints {

  //TODO behaves strangely when overriding numTableau/Foundation, hardcoded in maps for now
  //override val numTableau:Int = 8
  //override val numFoundation:Int = 4
  //override val numStock:Int = 1

  override val map:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> horizontalPlacement(15, 200, 8, 13*card_height),
    StockContainer -> horizontalPlacement(15, 20, 1, card_height),
    Foundation -> horizontalPlacement(293, 20, 4, card_height)
  )


  override val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](8)(BuildablePile),
    Foundation -> Seq.fill[Element](4)(Pile),
    StockContainer -> Seq(Stock(1))
  )

  override def getDeal: Seq[DealStep] = {
    var colNum: Int = 1
    var dealSeq: Seq[DealStep] = Seq() // doesn't like me declaring it without initializing
    // Klondike deal - the ith pile gets i face down cards
    for (colNum <- 1 to 7) {
      //TODO change these to face down when FlipCardMove added
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = colNum))
      //dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = colNum))
    }
    //each pile gets a face up card
    colNum = 0
    for (colNum <- 0 to 7) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = 1))
    }

    dealSeq
  }

  val spiderette:Solitaire = {
    Solitaire(name = "Spiderette",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, deckDealMove),
      logic = BoardState(Map(Foundation -> 104)),
      solvable = false
    )
  }
}