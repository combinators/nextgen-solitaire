package org.combinators.solitaire

import org.combinators.solitaire.domain._

package object klondike {

  // note: don't forget to override methods in controllers
  case object WastePile extends Element (true)

  val map:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](7)(BuildablePile),
    Waste -> Seq.fill[Element](1)(WastePile),
    Foundation -> Seq.fill[Element](4)(Pile),
    StockContainer -> Seq(Stock())
  )

  val layoutMap:Map[ContainerType, Seq[Widget]] = Map (
    StockContainer -> horizontalPlacement(10, 10, 1, card_height),
    Foundation -> horizontalPlacement(240, 10, 4, card_height),
    Tableau -> horizontalPlacement(10, 200, num = 7, height = card_height*8),  // estimate
    Waste -> horizontalPlacement(20 + card_width, 10, 1, card_height),
  )

  def getDeal: Seq[DealStep] = {
    var deal:Seq[DealStep] = Seq()

    // each of the BuildablePiles gets a number of facedown cards, 0 to first Pile, 1 to second pile, etc...
    // don't forget zero-based indexing.
    for (pileNum <- 1 until 7) {
      deal = deal :+ DealStep(ElementTarget(Tableau, pileNum), new Payload(false, pileNum))
    }

    // finally each one gets a single faceup Card, and deal one to waste pile
    //add(new DealStep(new ContainerTarget(SolitaireContainerTypes.Tableau), new Payload()));
    deal = deal :+ DealStep(ContainerTarget(Tableau))
    deal = deal :+ DealStep(ContainerTarget(Waste))
    deal
  }

  val deck_move = NotConstraint(IsEmpty(Source))
  val deckDealMove:Move = DealDeckMove("DealDeck", 1,
    source=(StockContainer, deck_move), target=Some((Waste, Truth)))

  def buildOnTableau(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    val bottomMoving = BottomCardOf(cards)
    val isEmpty = IsEmpty(Destination)
    val descend = Descending(cards)
    val alternating = AlternatingColors(cards)
    OrConstraint(AndConstraint(isEmpty, descend, alternating),
      AndConstraint(descend, alternating, NextRank(topDestination, bottomMoving), OppositeColor(topDestination, bottomMoving)))
  }

  val tf_tgt = IfConstraint(IsEmpty(Destination),
    AndConstraint (IsSingle(MovingCards), IsAce(BottomCardOf(MovingCards))),
    AndConstraint (IsSingle(MovingCards),
      NextRank(BottomCardOf(MovingCards), TopCardOf(Destination)),
      SameSuit(BottomCardOf(MovingCards), TopCardOf(Destination))))

  val buildFoundation:Move = MultipleCardsMove("BuildFoundation", Drag,
    source=(Tableau, Truth), target=Some((Foundation, tf_tgt)))

  val wf_tgt =  IfConstraint(IsEmpty(Destination),
    AndConstraint (IsSingle(MovingCard), IsAce(MovingCard)),
    AndConstraint(NextRank(MovingCard, TopCardOf(Destination)),
      SameSuit(MovingCard, TopCardOf(Destination))))

  val buildFoundationFromWaste:Move = SingleCardMove("BuildFoundationFromWaste", Drag,
    source=(Waste, Truth), target=Some((Foundation, wf_tgt)))

  def foundationToTableauConstraint:Constraint = OrConstraint(
    IsEmpty(Destination),
    AndConstraint(
      OppositeColor(MovingCard, TopCardOf(Destination)),
      NextRank(TopCardOf(Destination), MovingCard))
  )

  val tableauToTableauMove:Move = MultipleCardsMove("MoveColumn", Drag,
    source=(Tableau, Truth), target=Some((Tableau, buildOnTableau(MovingCards))))

  val allowed = AndConstraint(NotConstraint(IsEmpty(Source)), NotConstraint(IsFaceUp(TopCardOf(Source))))
  val flipMove:Move = FlipCardMove("FlipCard", Press, source = (Tableau, allowed))

  // reset deck by pulling together all cards from the piles.
  val deckReset: Move = ResetDeckMove("ResetDeck",
    source = (StockContainer, IsEmpty(Source)), target = Some((Waste, Truth)))

  //2. waste to tableau
  val moveCard= OrConstraint(IsEmpty(Destination),
    AndConstraint(OppositeColor(TopCardOf(Destination), MovingCard), NextRank(TopCardOf(Destination), MovingCard)))
  val wasteToTableau:Move = SingleCardMove("MoveCard", Drag,
    source=(Waste,Truth), target=Some(Tableau, moveCard))

  val klondike:Solitaire = {

    Solitaire( name="Klondike",
      structure = map,
      layout = Layout(layoutMap),
      deal = getDeal,

      specializedElements = Seq(WastePile),

      moves = Seq(tableauToTableauMove, wasteToTableau, buildFoundation, buildFoundationFromWaste, deckDealMove, deckReset, flipMove),

      // fix winning logic
      logic = BoardState(Map(Foundation -> 52)),
      customizedSetup = Seq.empty
    )
  }
}
