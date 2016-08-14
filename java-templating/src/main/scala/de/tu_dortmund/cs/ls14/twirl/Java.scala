package de.tu_dortmund.cs.ls14.twirl

import java.io.StringReader

import com.github.javaparser.{ASTParser, InstanceJavaParser, JavaParser, StreamProvider}
import com.github.javaparser.ast.{CompilationUnit, ImportDeclaration, Node}
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.expr.{Expression, NameExpr}
import com.github.javaparser.ast.stmt.Statement
import org.apache.commons.lang3.StringEscapeUtils
import play.twirl.api.{BufferedContent, Format, Formats}

import scala.collection.immutable
import scala.collection.JavaConversions._

/**
  * A Java fragment.
  */
class Java private(elements: immutable.Seq[Java], text: String) extends BufferedContent[Java](elements, text) {
  def this(text: String) = this(Nil, Formats.safe(text))
  def this(elements: immutable.Seq[Java]) = this(elements, "")

  private lazy val fullText: String = (text +: elements).mkString

  /**
    * Content type of Java
    */
  val contentType = "text/x-java"

  /**
    * Parse this element as a java compilation unit.
    */
  def compilationUnit(): CompilationUnit = JavaParser.parse(new StringReader(fullText))

  /**
    * Parse an import declaration.
    */
  def importDeclaration(): ImportDeclaration = JavaParser.parseImport(fullText)

  /**
    * Parse this element as a single statement.
    */
  def statement(): Statement = JavaParser.parseStatement(fullText)
  /**
    * Parse this element as multiple statements.
    */
  def statements(): Seq[Statement] = JavaParser.parseStatements(fullText)

  /**
    * Parse this element as an expression.
    */
  def expression(): Expression = JavaParser.parseExpression(fullText)

  /**
    * Parse this element as a name expression.
    */
  def nameExpression(): NameExpr =
    JavaParser.parseImport(Java(s"import $fullText;").text).getName

  /**
    * Parse this element as a class body declaration (e.g. a method or a field).
    */
  def classBodyDeclaration(): BodyDeclaration = JavaParser.parseBodyDeclaration(fullText)

  /**
    * Parse this element as multiple class body declarations.
    */
  def classBodyDeclarations(): Seq[BodyDeclaration] =
    JavaParser.parse(new StringReader(s"class C { ${fullText} }")).getTypes().head.getMembers.toSeq


  /**
    * Parse this element as an interface body declaration (e.g. a method signature).
    */
  def interfaceBodyDeclaration(): BodyDeclaration = JavaParser.parseInterfaceBodyDeclaration(fullText)
}

/**
  * Helper for Java utility methods.
  */
object Java {
  /**
    * Creates a Java fragment with initial content specified.
    */
  def apply(text: String): Java = {
    new Java(text)
  }

  /**
    * Creates a Java fragment with initial content from the given `text` separated by `separator`.
    */
  def apply(text: Seq[String], separator: String = ";"): Java = {
    apply(text.mkString(separator))
  }

  /**
    * Creates a Java fragment with initial content from the ast `node`.
    */
  def apply(node: Node): Java = Java(node.toString)

  /**
    * Creates a Java fragment with initial content from the asts `nodes`.
    */
  def apply(nodes: Seq[Node]): Java = new Java(immutable.Seq(nodes map apply : _*))
}

object JavaFormat extends Format[Java] {
  /**
    * Integrate `text` without performing any escaping process.
    * @param text Text to integrate
    */
  def raw(text: String): Java = Java(text)

  /**
    * Escapes `text` using Java String rules.
    * @param text Text to integrate
    */
  def escape(text: String): Java = Java(StringEscapeUtils.escapeJava(text))

  /**
    * Generate an empty Java fragment
    */
  val empty: Java = new Java("")

  /**
    * Create an Java Fragment that holds other fragments.
    */
  def fill(elements: immutable.Seq[Java]): Java = new Java(elements)

}
