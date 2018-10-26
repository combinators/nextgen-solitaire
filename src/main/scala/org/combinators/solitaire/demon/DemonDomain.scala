package org.combinators.solitaire.demon

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{BodyDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, Name}
import com.github.javaparser.ast.stmt.Statement
import domain._
import domain.demon.StartingBase
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types._
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared.compilation.{CodeGeneratorRegistry, generateHelper}
import org.combinators.templating.twirl.Java

/**
  * Defines Java package, the game's name, initializes the domain model,
  * the UI, and the controllers (doesn't define them, just generates),
  * and includes extra fields and methods.
  */
class DemonDomain(override val solitaire: Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with Controller {

  object demonCodeGenerator {
    val generators:CodeGeneratorRegistry[Expression] = CodeGeneratorRegistry.merge[Expression](

      CodeGeneratorRegistry[Expression, StartingBase] {
        case (registry:CodeGeneratorRegistry[Expression], c:StartingBase) =>
          val movingCard = registry(c.src).get
          val src = registry(c.src).get
          Java(s"""((org.combinators.solitaire.demon.Demon)game).startingBase($movingCard)""").expression()

      },

    ).merge(constraintCodeGenerators.generators)
  }

  @combinator object DemonGenerator {
    def apply: CodeGeneratorRegistry[Expression] = demonCodeGenerator.generators
    val semanticType: Type = constraints(constraints.generator)
  }

  /** Each Solitaire variation must provide default do generation. */
  @combinator object DefaultDoGenerator {
    def apply: CodeGeneratorRegistry[Seq[Statement]] = constraintCodeGenerators.doGenerators

    val semanticType: Type = constraints(constraints.do_generator)
  }

  /** Each Solitaire variation must provide default conversion for moves. */
  @combinator object DefaultUndoGenerator {
    def apply: CodeGeneratorRegistry[Seq[Statement]] = constraintCodeGenerators.undoGenerators

    val semanticType: Type = constraints(constraints.undo_generator)
  }

  @combinator object HelperMethodsArchway {
    def apply(): Seq[BodyDeclaration[_]] = generateHelper.helpers(solitaire)

    val semanticType: Type = constraints(constraints.methods)
  }

  /**
    * Deal may require additional generators.
    */
  @combinator object DefaultDealGenerator {
    def apply: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.mapGenerators
    val semanticType: Type = constraints(constraints.map)
  }

  //@combinator object MakeWastePile extends ExtendModel("Pile", "WastePile", 'WastePileClass)
  //@combinator object MakeWastePileView extends ExtendView("View", "WastePileView", "WastePile", 'WastePileViewClass)
  //@combinator object MakeDemonColumn     extends ExtendModel("Column",    "DemonColumn", 'DemonColumnPileClass)
  //@combinator object MakeDemonColumnView extends ExtendView("View", "DemonColumnPileView", "DemonColumn", 'DemonColumnViewClass)
  /**
    * Generates import statements for the model and controller packages.
    */
  @combinator object ExtraImports {
    def apply(nameExpr: Name): Seq[ImportDeclaration] = {
      Seq(
        Java(s"import $nameExpr.controller.*;").importDeclaration(),
        Java(s"import $nameExpr.model.*;").importDeclaration()
      )
    }
    val semanticType: Type = packageName =>: game(game.imports)
  }

  /**
    * Generate extra methods.
    */
  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] =

      Java(s"""
              |public boolean startingBase(Card moving) {
              |    	// assumes first card has already been dealt
              |    	if (foundation[0].empty()) { return false; }
              |
              |    	Card c = foundation[0].peek(foundation[0].count() - 1);
              |    	return c.getRank() == moving.getRank();
              |}
              |
              |public java.util.Enumeration<Move> availableMoves() {
              |    java.util.Vector<Move> v = new java.util.Vector<Move>();
              |
              |	  if (!this.deck.empty()) {
              |	    DealDeck dd = new DealDeck(deck, tableau);
              |		  if (dd.valid(this)) {
              |			  v.add(dd);
              |		  }
              |		}
              |    return v.elements();
              |}
       """.stripMargin).methodDeclarations()

    val semanticType: Type = game(game.methods :&: game.availableMoves)
  }

}
