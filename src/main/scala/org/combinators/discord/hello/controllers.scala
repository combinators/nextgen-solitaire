package org.combinators.discord.hello

import com.sun.xml.internal.ws.addressing.v200408.WsaTubeHelperImpl
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.discord.domain._
import org.combinators.discord.shared.DiscordDomain
import org.combinators.discord.shared.SemanticTypes
import pydiscord.shared.DiscordTemplate

trait controllers extends DiscordTemplate  with SemanticTypes  {

    // dynamic combinators added as needed
    override def init[G <: DiscordDomain](gamma : ReflectedRepository[G], discord:Discord) :  ReflectedRepository[G] = {
      var updated = super.init(gamma, discord)
      println(">>> Hello Controller dynamic combinators.")

     // override as you need to..
      updated = updated.addCombinator(new Libraries(discord.libraries))
      updated = updated.addCombinator(new Description(discord.description))
      updated = updated.addCombinator(new Prefix(discord.prefix))
      updated = updated.addCombinator(new OutputFile(discord.name))
      updated = updated.addCombinator(new CommName(discord.commName))
      updated = updated.addCombinator(new CommArgs(discord.commArgs))
      updated = updated.addCombinator(new CommContent(discord.commContent))
      updated = updated.addCombinator(new EventNames(discord.eventNames))
      updated = updated.addCombinator(new EventContents(discord.eventContents))
      updated = updated.addCombinator(new Helpers(discord.helpers))
      updated
    }
}
