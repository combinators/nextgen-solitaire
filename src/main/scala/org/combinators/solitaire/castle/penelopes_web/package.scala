package org.combinators.solitaire.castle

import org.combinators.solitaire.domain._
package object penelopes_web {
  def buildOnEmptyTableau(card: MovingCard.type): Constraint = {
    IsKing(card)
  }
  val tt_move:Constraint = IfConstraint(IsEmpty(Destination), buildOnEmptyTableau(MovingCard), buildOnTableau(MovingCard))

  val tf_move:Constraint = IfConstraint(IsEmpty(Destination), buildOnEmptyFoundation(MovingCard), buildOnFoundation(MovingCard))

  val tableauToTableau:Move = SingleCardMove("MoveCard", Drag,
    source=(Tableau,Truth), target=Some((Tableau, tt_move)))

  val tableauToFoundation:Move = SingleCardMove("MoveCardFoundation", Drag,
    source=(Tableau,Truth), target=Some((Foundation, tf_move)))


  val definition:Solitaire = {

    Solitaire(name="Penelope's Web",
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
