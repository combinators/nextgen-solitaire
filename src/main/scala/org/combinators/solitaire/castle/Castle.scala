package org.combinators.solitaire.castle

import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git._
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}
import org.combinators.templating.persistable.JavaPersistable._

trait CastleT extends SolitaireSolution {

  lazy val repository = new CastleDomain(solitaire) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)
  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(solitaire)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()
  override lazy val routingPrefix:Option[String] = Some("castle")
}

object CastleMain extends CastleT with DefaultMain {
  lazy val solitaire = castle
}
object StreetsAndAlleysMain extends DefaultMain with CastleT {
  lazy val solitaire = org.combinators.solitaire.castle.streets_and_alleys.definition
}
object PenelopesWebMain extends DefaultMain with CastleT {
  lazy val solitaire = org.combinators.solitaire.castle.penelopes_web.definition
}