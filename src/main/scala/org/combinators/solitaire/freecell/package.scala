package org.combinators.solitaire

import org.combinators.solitaire.domain.{Tableau, horizontalPlacement, _}

/**
 * Any time you change a twirl template, you HAVE to force a regeneration. Unfortunately, this
 * is accomplished ONLY by launching a web server and connecting via localhost:9000
 */
package object freecell {
  // sufficientFree (Column column, Stack src, Stack destination, Stack[] reserve, Stack[] tableau) {

  case class IsSufficientFree(movingCards: MoveInformation, from: MoveInformation, to: MoveInformation) extends Constraint
  case object FreeCellPile extends Element(true)

  val structureMap:Map[ContainerType,Seq[Element]] = Map(
    StockContainer -> Seq.fill[Element](1)(Stock(1)),   // a deck must be present even though not visible
    Tableau -> Seq.fill[Element](8)(Column),
    Foundation -> Seq.fill[Element](4)(Pile),
    Reserve -> Seq.fill[Element](4)(FreeCellPile),
  )

  val layoutMap:Map[ContainerType, Seq[Widget]] = Map (
    Reserve -> horizontalPlacement(10, 10, 4, card_height),
    Foundation -> horizontalPlacement(400, 10, 4, card_height),
    Tableau -> horizontalPlacement(10, 200, num = 8, height = card_height*8),  // estimate
  )

  def getDeal: Seq[DealStep] = {
    var deal:Seq[DealStep] = Seq()
    var colNum = 0
    //only first 4 cols get a 7th
    while (colNum < 8)
    {
      deal = deal :+ DealStep(ElementTarget(Tableau, colNum), Payload(numCards =  6))
      if (colNum < 4) {
        deal = deal :+ DealStep(ElementTarget(Tableau, colNum))
      }
      colNum += 1
    }
    deal
  }

  def buildOnTableau(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    val bottomCards = BottomCardOf(cards)
    AndConstraint(NextRank(topDestination, bottomCards), OppositeColor(bottomCards, topDestination))   // was all card and MovingCard.type
  }

  def buildOnTableauOneCard(card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    AndConstraint(NextRank(topDestination, card), OppositeColor(card, topDestination))
  }

  def buildOnFoundation(card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    AndConstraint(NextRank(card, topDestination),  SameSuit(card, topDestination))
  }
  def buildOnFoundationMultiple(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    AndConstraint(NextRank(BottomCardOf(cards), topDestination),  SameSuit(BottomCardOf(cards), topDestination))
  }
  def buildOnEmptyFoundation(card: MovingCard.type): Constraint = {
    IsAce(card)
  }
  def buildOnEmptyFoundationMultiple(cards: MovingCards.type): Constraint = {
    IsAce(BottomCardOf(cards))
  }
  val tt_move:Constraint = IfConstraint(IsEmpty(Destination), AndConstraint(Descending(MovingCards), AlternatingColors(MovingCards)), buildOnTableau(MovingCards))
  val tt_move_one_card:Constraint = IfConstraint(IsEmpty(Destination), Truth, buildOnTableauOneCard(MovingCard))

  val tf_move:Constraint = IfConstraint(IsEmpty(Destination), buildOnEmptyFoundation(MovingCard), buildOnFoundation(MovingCard))
  val tf_move_multiple:Constraint = AndConstraint(IsSingle(MovingCards),
    IfConstraint(IsEmpty(Destination), buildOnEmptyFoundationMultiple(MovingCards), buildOnFoundationMultiple(MovingCards)))

  val tableauToTableauMove:Move = MultipleCardsMove("MoveColumn", Drag,    // SingleCardMove  was "MoveCard"
    source=(Tableau,Truth), target=Some((Tableau, AndConstraint(tt_move, IsSufficientFree (MovingCards, Source, Destination)))))

  val tableauToFoundationMove:Move = MultipleCardsMove("MoveCardFoundation", Drag,
    source=(Tableau,Truth), target=Some((Foundation, tf_move_multiple)))
  val fromTableauToReserve:Move = MultipleCardsMove("TableauToReserve", Drag,
    source=(Tableau,Truth), target=Some((Reserve, AndConstraint(IsSingle(MovingCards), IsEmpty(Destination)))))

  // should be "NotEmpty" not Truth
  val fromReserveToReserve:Move = SingleCardMove("ReserveToReserve", Drag,
    source=(Reserve,Truth), target=Some((Reserve, IsEmpty(Destination))))

  val fromReserveToTableau:Move = SingleCardMove("ReserveToTableau", Drag,
    source=(Reserve,Truth), target=Some((Tableau, tt_move_one_card)))

  val fromReserveToFoundation:Move = SingleCardMove("ReserveToFoundation", Drag,
    source=(Reserve,Truth), target=Some((Foundation, tf_move)))

  val freecell:Solitaire = {

    Solitaire(name="FreeCell",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal,
      /** from element can infer ks.ViewWidget as well as Base Element. */
      specializedElements = Seq(FreeCellPile),

      /** All rules here. */
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, fromTableauToReserve, fromReserveToReserve, fromReserveToTableau, fromReserveToFoundation ),
      // fix winning logic
      logic = BoardState(Map(Foundation -> 52)),
      customizedSetup = Seq.empty,

      solvable = true,
      autoMoves = true   // handle auto moves!
    )
  }
}
