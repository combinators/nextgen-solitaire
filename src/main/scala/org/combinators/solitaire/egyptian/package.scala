package org.combinators.solitaire

import org.combinators.solitaire.domain._

package object egyptian {

  def numTableau :Int = 10
  def numFoundation: Int = 8

  case object WastePile extends Element (true)

  val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](numTableau)(Column),
    Foundation -> Seq.fill[Element](numFoundation)(Pile),
    StockContainer -> Seq(Stock(2)),
    Waste -> Seq.fill[Element](1)(WastePile)
  )

  val layout:Layout = Layout(Map(
    StockContainer -> horizontalPlacement(15, 20, 1, card_height),
    Foundation -> horizontalPlacement(210, 20, numFoundation, card_height),
    Tableau -> horizontalPlacement(15, 150, numTableau, 13*card_height),
    Waste -> horizontalPlacement(20+card_width,20,1,card_height)
  ))

  val deck_move = NotConstraint(IsEmpty(Source))
  val deckDeal: Move = DealDeckMove("DealDeck", 1, source = (StockContainer, deck_move), target = Some((Waste, Truth)))


  def buildOnTableau(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    val bottomMoving = BottomCardOf(cards)
    val isEmpty = IsEmpty(Destination)
    val descend = Descending(cards)
    val suit = AlternatingColors(cards)
    OrConstraint(AndConstraint(isEmpty, descend, suit), AndConstraint(descend, suit, OppositeColor(topDestination, bottomMoving)))
  }

  def buildOnFoundation(card: MovingCard.type): Constraint = {
    val isEmpty = IsEmpty(Destination)
    val isAce = IsAce(card)
    val topDestination = TopCardOf(Destination)
    OrConstraint(AndConstraint(isEmpty, isAce), AndConstraint(SameSuit(topDestination, card)), NextRank(card, topDestination, wrapAround = true))
  }

  val tableauToTableauMove: Move = MultipleCardsMove("MoveColumn", Drag,
    source = (Tableau, Truth), target = Some((Tableau, buildOnTableau(MovingCards))))

  val tableauToFoundationMove: Move = SingleCardMove("MoveCardFoundation ", Drag,
    source = (Tableau, Truth), target = Some(Foundation, IfConstraint(IsEmpty(Destination), IsKing(MovingCard), buildOnFoundation(MovingCard))))

  val deckReset:Move =  ResetDeckMove("ResetDeck", source=(StockContainer, IsEmpty(Source)), target=Some(Waste,Truth))

  val moveCard = OrConstraint(IsEmpty(Destination), NextRank(TopCardOf(Destination), MovingCard))

  val wasteToTableau: Move = SingleCardMove("MoveCard", Drag,
    source = (Waste, Truth), target = Some(Tableau, moveCard))

  def getDeal: Seq[DealStep] = {
    val colNum = 0;
    var dealSeq: Seq[DealStep] = Seq()
    var cardNum = 1;
    for(colNum <- 0 to 4){
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(numCards = cardNum))
      cardNum = cardNum + 2
    }
    cardNum = 10
    for (colNum <- 5 to 9) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(numCards = cardNum))
      cardNum = cardNum - 2
    }

    dealSeq

  }

  case object TableauToEmptyTableau extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Tableau, 2))

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      RemoveStep(targetElement.get),
      MovingCardStep(CardCreate(Clubs, Eight))
    )
  }

  val egyptianS:Solitaire = {

    Solitaire( name="Egyptian",
      structure = structureMap,
      layout = layout,
      deal = getDeal,

      /** from element can infer ks.ViewWidget as well as Base Element. */
      specializedElements = Seq(WastePile),

      /** All rules here. */
      moves = Seq( deckDeal, deckReset,tableauToTableauMove,tableauToFoundationMove),

      // fix winning logic
      logic = BoardState(Map(Tableau -> 0)),

      customizedSetup = Seq(TableauToEmptyTableau)
    )
  }
}
