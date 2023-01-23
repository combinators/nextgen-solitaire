package org.combinators.discord

import org.combinators.discord.domain.Discord

package object hello {
  val hello:Discord = {
    Discord(name = "",
      libraries = "",
      description = "",
      prefix = "",
    commName = Array(""),
      commArgs = Array(""),
      commContent = Array(""),
      eventNames = Array(""),
      eventContents = Array("")
    )
  }
}
