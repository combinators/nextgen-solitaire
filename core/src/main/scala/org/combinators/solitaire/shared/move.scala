package org.combinators.solitaire.shared

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.{Taxonomy, Type}
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import org.combinators.solitaire.shared

trait Moves {
  class Move(semanticMoveNameType: Type) {
    def apply(rootPackage: NameExpr,
      moveName: NameExpr,
      helper: Seq[BodyDeclaration],
      doStmts: Seq[Statement],
      undoStmts: Seq[Statement],
      checkValid: Seq[Statement]): CompilationUnit = {
      shared.moves.java.Move.render(
        RootPackage = rootPackage,
        MoveName = moveName,
        Helper = helper,
        Do = doStmts,
        Undo = undoStmts,
        CheckValid = checkValid
      ).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>:
        semanticMoveNameType =>:
        'Move(semanticMoveNameType, 'HelperStatements) =>:
        'Move(semanticMoveNameType, 'DoStatements) =>:
        'Move(semanticMoveNameType, 'UndoStatements) =>:
        'Move(semanticMoveNameType, 'CheckValidStatements) =>:
        'Move(semanticMoveNameType :&: 'GenericMove, 'CompleteMove)
  }

  class PotentialMove(semanticMoveNameType: Type) {
    def apply(rootPackage: NameExpr, moveName: NameExpr, draggingCardVariableName: NameExpr): CompilationUnit  = {
      shared.moves.java.PotentialMove.render(
        RootPackage = rootPackage,
        MoveName = moveName,
        DraggingCardVariableName = draggingCardVariableName
      ).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>:
      semanticMoveNameType =>:
      'DraggingCardVariableName =>:
      'Move(semanticMoveNameType :&: 'PotentialMove, 'CompleteMove)
  }

  @combinator object ResetDeck {
    def apply(rootPackage: NameExpr): CompilationUnit = {
      shared.moves.java.ResetDeck.render(rootPackage).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>: 'Move('ResetDeck, 'CompleteMove)
  }

  @combinator object RemovedCard {
    def apply(rootPackage: NameExpr): CompilationUnit = {
      shared.moves.java.RemovedCard.render(rootPackage).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>: 'Move('RemoveCard, 'CompleteMove)
  }

  @combinator object DealStacks {
    def apply(rootPackage: NameExpr): CompilationUnit = {
      shared.moves.java.DealStacksMove.render(rootPackage).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>: 'Move('DealStacks, 'CompleteMove)
  }

  val moveTaxonomy: Taxonomy =
    Taxonomy("GenericMove")
      .addSubtype("PotentialMove")
      .addSubtype("ResetDeck")
      .addSubtype("RemoveCard")
      .addSubtype("DealStacks")
}