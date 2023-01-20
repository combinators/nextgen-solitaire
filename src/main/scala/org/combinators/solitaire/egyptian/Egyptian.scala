package org.combinators.solitaire.egyptian

import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.git._
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}
import org.combinators.templating.persistable.JavaPersistable._
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle

import javax.inject.Inject

// SINGLE card moving
trait EgyptianT extends SolitaireSolution {
  // THESE ALL HAVE TO BE LAZY VAL ...
  lazy val repository = new EgyptianDomain(solitaire) with controllers with singleCardMovers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(solitaire)

  lazy val results = EmptyInhabitationBatchJobResults(Gamma)
    .addJobs[CompilationUnit](targets).compute()

  override lazy val routingPrefix = Some("Egyptian")
}


object EgyptianMain extends DefaultMain with EgyptianT {
  override lazy val solitaire = egyptianS
}