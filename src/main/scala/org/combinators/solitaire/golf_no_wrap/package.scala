package org.combinators.solitaire

import org.combinators.solitaire.domain._

package object golf_no_wrap {
  case class ToLeftOf(destination: MoveInformation, src:MoveInformation) extends Constraint
  case class AllSameRank(src:MoveInformation) extends Constraint
  case object WastePile extends Element (true)


  val numTableau:Int = 7
  val map:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](numTableau)(Column),
    StockContainer -> Seq(Stock(1)),
    Waste -> Seq.fill[Element](1)(WastePile)
  )

  val wasteMove = OrConstraint(IsEmpty(Destination), NextRank(MovingCard, TopCardOf(Destination), true), NextRank(TopCardOf(Destination), MovingCard, true))

  val tableauToWasteMove:Move = SingleCardMove("MoveCardToWaste", Drag,
    source=(Tableau,Truth), target=Some((Waste, wasteMove)))

  val deck_move = NotConstraint(IsEmpty(Source))
  val deckDealMove:Move = DealDeckMove("DealDeck", 1,
    source=(StockContainer, deck_move), target=Some((Waste, Truth)))

  def golf_no_wrapLayout():Layout = {
    Layout(Map(
      StockContainer -> horizontalPlacement(15, 20, 1, card_height),
      Tableau -> horizontalPlacement(120, 20, numTableau, 5*card_height),
      Waste -> horizontalPlacement(15, 40 + card_height, 1, card_height)
    ))
  }

  val golf_no_wrap:Solitaire = {

    Solitaire( name="Golf_no_wrap",
      structure = map,
      layout = golf_no_wrapLayout(),
      deal = Seq(DealStep(ContainerTarget(Tableau)),
        DealStep(ContainerTarget(Tableau)),
        DealStep(ContainerTarget(Tableau)),
        DealStep(ContainerTarget(Tableau)),
        DealStep(ContainerTarget(Tableau))),
      specializedElements = Seq(WastePile),
      moves = Seq(tableauToWasteMove,deckDealMove),
      logic = BoardState(Map(Waste -> 52))
    )
  }
}