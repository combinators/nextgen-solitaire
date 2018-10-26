package org.combinators.solitaire.demon

import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import domain._
import org.combinators.cls.interpreter.{ReflectedRepository, combinator}
import org.combinators.cls.types.{Constructor, Type}
import org.combinators.cls.types.syntax._
import org.combinators.generic
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._
import org.combinators.templating.twirl.Java
//import scala.collection.JavaConverters._

/** Defines Archway's controllers and their behaviors.
  *
  * Every controller requires definitions for three actions:
  *   - Click (no dragging, like clicking on a deck to deal more cards)
  *   - Press (click, drag, release)
  *   - Release (release after press)
  *
  * Either a rule must be associated with an action, or the action must be
  * explicity ignored. See ArchwayRules in game.scala.
  */
trait controllers extends shared.Controller with shared.Moves with GameTemplate with generic.JavaCodeIdioms  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Archway Controller dynamic combinators.")

    updated = createMoveClasses(updated, s)
    updated = createDragLogic(updated, s)
    updated = generateMoveLogic(updated, s)
    updated = generateExtendedClasses(updated, s)

    updated = updated
      .addCombinator (new IgnorePressedHandler(pile))
      .addCombinator (new IgnoreClickedHandler(pile))
      //.addCombinator (new IgnoreReleasedHandler(pile))

    updated = updated
     // .addCombinator (new IgnorePressedHandler(buildablePile))
      .addCombinator (new IgnoreClickedHandler(buildablePile))
      .addCombinator (new IgnoreReleasedHandler(buildablePile))

    updated = updated
      .addCombinator (new IgnoreClickedHandler(deck))
      .addCombinator (new IgnoreReleasedHandler(deck))

      /*.addCombinator(new IgnoreClickedHandler('WastePile))
      .addCombinator(new IgnoreReleasedHandler('WastePile))
      .addCombinator(new IgnorePressedHandler('WastePile))*/

    updated = updated
      //.addCombinator (new IgnorePressedHandler(fanPile))
      .addCombinator (new IgnoreClickedHandler(fanPile))
      .addCombinator (new IgnoreReleasedHandler(fanPile))

    updated = updated
      //.addCombinator (new IgnorePressedHandler(column))
      .addCombinator (new IgnoreClickedHandler(column))
      //.addCombinator (new IgnoreReleasedHandler(column))

    updated = updated
      .addCombinator (new IgnorePressedHandler('DemonColumn))
      .addCombinator (new IgnoreClickedHandler('DemonColumn))
      .addCombinator (new IgnoreReleasedHandler('DemonColumn))

    updated = updated
      .addCombinator(new DealToTableauHandlerLocal())
      .addCombinator (new SingleCardMoveHandler(fanPile))
      //.addCombinator (new SingleCardMoveHandler(column))
      .addCombinator (new buildablePilePress.CP2())

    updated = createWinLogic(updated, s)

    // move these to shared area
    updated = updated
      .addCombinator (new DefineRootPackage(s))
      .addCombinator (new DefineNameOfTheGame(s))
      .addCombinator (new ProcessModel(s))
      .addCombinator (new ProcessView(s))
      .addCombinator (new ProcessControl(s))
      .addCombinator (new ProcessFields(s))

    updated
  }

  /**
    * Recognize that Klondike has two kinds of press moves (ones that act, and ones that lead to drags).
    * While the automatic one is handled properly, it produces terminals that will be 'dragStart'. We need
    * to chain together to form complete set.
    */

  object buildablePilePress {
    val buildablePile1:Constructor = 'BuildablePile1

    class CP2() {
      def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
        (widget, ignore) =>

          Java(s"""|BuildablePile srcPile = (BuildablePile) src.getModelElement();
                   |
                 |// Only apply if not empty AND if top card is face down
                   |if (srcPile.count() != 0) {
                   |  if (!srcPile.peek().isFaceUp()) {
                   |    Move fm = new FlipCard(srcPile, srcPile);
                   |    if (fm.doMove(theGame)) {
                   |      theGame.pushMove(fm);
                   |      c.repaint();
                   |      return;
                   |    }
                   |  }
                   |}""".stripMargin).statements()
      }

      val semanticType: Type =
        drag(drag.variable, drag.ignore) =>: controller (buildablePile1, controller.pressed)
    }

    class ChainBuildablePileTogether extends ParameterizedStatementCombiner[SimpleName, SimpleName](
      drag(drag.variable, drag.ignore) =>: controller(buildablePile1, controller.pressed),
      drag(drag.variable, drag.ignore) =>: controller(buildablePile, controller.dragStart),
      drag(drag.variable, drag.ignore) =>: controller(buildablePile, controller.pressed))
  }

  @combinator object ChainBuildablePileTogether extends buildablePilePress.ChainBuildablePileTogether


  /**
    * When dealing card(s) from the stock to all elements in Tableau
    * If deck is empty, then reset.
    * NOTE: How to make this more compositional?
    */
  class DealToTableauHandlerLocal() {
    def apply(): (SimpleName, SimpleName) => Seq[Statement] = (widget, ignore) => {
      Java(s"""|{Move m = new DealDeck(theGame.deck, theGame.waste);
               |if (m.doMove(theGame)) {
               |   theGame.pushMove(m);
               |   // have solitaire game refresh widgets that were affected
               |   theGame.refreshWidgets();
               |   return;
               |}}""".stripMargin
      ).statements()
    }

    val semanticType: Type = drag(drag.variable, drag.ignore) =>: controller(deck, controller.pressed)
  }

  /** When deck is empty and must be reset from waste pile. */
  // This should be generated from one of the rules.
  class ResetDeckLocal() {
    def apply():(SimpleName, SimpleName) => Seq[Statement] = (widget,ignore) =>{
      Java(s"""|{Move m = new ResetDeck(theGame.deck, theGame.waste);
               |if (m.doMove(theGame)) {
               |   theGame.pushMove(m);
               |   // have solitaire game refresh widgets that were affected
               |   theGame.refreshWidgets();
               |   return;
               |}}""".stripMargin).statements()
    }

    val semanticType: Type = drag(drag.variable, drag.ignore) =>: controller(deck, controller.pressed)
  }

}


