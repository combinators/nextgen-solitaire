package pysolfc.robert

import org.combinators.cls.git.{EmptyResults, Results}
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}
import org.combinators.templating.persistable.PythonWithPath
import org.combinators.templating.persistable.PythonWithPathPersistable._

trait PythonRobertT extends SolitaireSolution {
  lazy val repository = new RobertDomain(solitaire) with controllers {}
  import repository._
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val targets: Seq[Constructor] = Seq(game(complete))
  lazy val jobs =
    Gamma.InhabitationBatchJob[PythonWithPath](targets.head)    // Why just singular target here?

  lazy val results:Results = EmptyResults().addAll(jobs.run())
}

// Match the Trait with multi card moves with the model that defines multi card moves
object PythonRobertMain extends DefaultMain with PythonRobertT {
  override lazy val solitaire = org.combinators.solitaire.golf.robert.definition
}
