package org.combinators.discord

import org.combinators.discord.domain.Discord

package object hello {
  val hello:Discord = {
    Discord(name = "BotGeneration2",
      libraries = "import discord\nfrom discord.ext import commands",
      description = "The second generation of a bot, take from Aakarshan-chauhan on github",
      prefix = "!",
    commName = Array("repeat","kick","ban","unban"),
      commArgs = Array("ctx, *, sentence","ctx, member : discord.Member, *, reason = None","ctx, member : discord.Member, *, reason = None","ctx, member"),
      commContent = Array("await ctx.send(f\"\\\"{sentence}\\\"\")","await member.kick(reason=reason)","await member.ban(reason=reason)","banned_list = await ctx.guild.bans()\n\n\tname, num = member.split(\"#\")\n\t\n\tfor user in banned_list:\n\t\tm = ban_entry.user\n\n\t\tif (m.name, m.discriminator) == (name, num):\n\t\t\tawait ctx.guild.unban(m)\n\n\t\t\tawait ctx.send(f\"Unbanned {name}#{num}\")\n\t\t\treturn\n\n\tawait ctx.send(f\"Cant find the user in ban list\")"),
      eventNames = Array(""),
      eventContents = Array(""),
      helpers = Array("")
    )
  }
}
