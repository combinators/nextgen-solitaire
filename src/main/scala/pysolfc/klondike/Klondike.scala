package pysolfc.klondike

import org.combinators.templating.persistable.PythonWithPath
import org.combinators.templating.persistable.PythonWithPathPersistable._
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git.{EmptyResults, Results}
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}

trait PythonKlondikeT extends SolitaireSolution {
  lazy val repository = new KlondikeDomain(solitaire) with controllers {}
  import repository._
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val targets: Seq[Constructor] = Seq(game(complete))
  lazy val jobs =
    Gamma.InhabitationBatchJob[PythonWithPath](targets.head)    // Why just singular target here?

  lazy val results:Results = EmptyResults().addAll(jobs.run())
}

// Match the Trait with multi card moves with the model that defines multi card moves
object PythonKlondikeMain extends DefaultMain with PythonKlondikeT {
  override lazy val solitaire = org.combinators.solitaire.klondike.klondike
}
