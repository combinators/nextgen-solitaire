package pysolfc.narcotic

import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.solitaire.shared.SolitaireDomain
import org.combinators.solitaire.shared.python.PythonSemanticTypes
import pysolfc.shared.GameTemplate

import org.combinators.solitaire.domain._

trait controllers extends GameTemplate  with PythonSemanticTypes  {

    // dynamic combinators added as needed
    override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :  ReflectedRepository[G] = {
      var updated = super.init(gamma, s)
      println(">>> Narcotic Controller dynamic combinators.")

      updated = updated
        .addCombinator (new ProcessView(s))

      updated
    }


}
