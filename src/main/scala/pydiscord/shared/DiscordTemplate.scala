package pydiscord.shared

import org.combinators.cls.interpreter.{ReflectedRepository, combinator}
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.discord.domain._
import org.combinators.discord.shared.{DiscordDomain, SemanticTypes}
import org.combinators.templating.twirl.Python

import java.nio.file.Paths
import org.combinators.discord.shared._
import org.combinators.templating.persistable.PythonWithPath

import scala.annotation.switch

trait DiscordTemplate extends Base with SemanticTypes {

  /**
    * Opportunity to customize based on solitaire domain object.
    */
  override def init[G <: DiscordDomain](gamma : ReflectedRepository[G], discord:Discord) : ReflectedRepository[G] = {
    var updated = gamma

    updated = updated
      .addCombinator (new MakeMain(discord))
    updated
  }
  class CommArgs(arr: Array[String]){
    def  apply(): Array[String] = {
      arr
    }
    val semanticType:Type = bot(bot.commArgs)
  }
  class CommContent(arr: Array[String]){
    def  apply(): Array[String] = {
      arr
    }
    val semanticType:Type = bot(bot.commContent)
  }
  class CommName(arr: Array[String]){
    def  apply(): Array[String] = {
      arr
    }
    val semanticType:Type = bot(bot.commName)
  }
  class EventNames(arr: Array[String]){
    def  apply(): Array[String] = {
      arr
    }
    val semanticType:Type = bot(bot.eventNames)
  }
  class EventContents(arr: Array[String]){
    def  apply(): Array[String] = {
      arr
    }
    val semanticType:Type = bot(bot.eventContents)
  }
  class Helpers(arr:Array[String]){
    def  apply(): Array[String] = {
      arr
    }
    val semanticType:Type = bot(bot.helpers)
  }
  // instantiate THIS
  class Description(str:String) {
    def apply() : String = {
      str
    }

    val semanticType:Type = bot(bot.description)
  }
  class Libraries(str:String) {
    def apply() : String = {
      str
    }
    val semanticType:Type = bot(bot.libraries)
  }
  // instantiate THIS
  class Prefix(str:String) {
    def apply() : String = {
      str
    }

    val semanticType:Type = bot(bot.prefix)
  }

  class OutputFile(str:String) {
    def apply: String = str
    val semanticType:Type = bot(bot.fileName)
  }
  class MakeMain(disc:Discord) {
    def genEvent(eventNames: Array[String], eventContents: Array[String]) : String = {
      var eventCode = ""
      for ( i <- 0 to eventNames.length-1) {
        val x = eventNames(i)
        val cont = eventContents(i)
        (x : @switch) match {
          case "on_ready" => eventCode += "@bot.event\nasync def on_ready():\n    " + cont +"\n"
          case "on_message_edit" => eventCode += "@bot.event\nasync def on_message_edit(before, after):\n    " + cont+"\n"
          case "on_message_delete" => eventCode += "@bot.event\nasync def on_message_delete(message):\n    " + cont+"\n"
          case "on_message" => eventCode += "@bot.event\nasync def on_message(message):\n    " + cont+"\n"
          case "on_voice_state_update" => eventCode += "@bot.event\nasync def on_voice_state_update(member, before, after):\n    " + cont+"\n"
          case "on_guild_join" => eventCode += "@bot.event\nasync def on_guild_join(guild):\n    " + cont+ "\n"
          case "on_guild_remove" => eventCode += "@bot.event\nasync def on_guild_remove(guild):\n    " + cont+ "\n"
          case "on_member_join" => eventCode += "@bot.event\nasync def on_member_join(member):\n    " + cont+ "\n"
          case "on_member_remove" => eventCode += "@bot.event\nasync def on_member_remove(member):\n    " + cont+ "\n"
          case "on_user_update" => eventCode += "@bot.event\nasync def on_user_update(before, after):\n    " + cont+ "\n"
          case "on_member_update" => eventCode += "@bot.event\nasync def on_member_update(before, after):\n    " + cont+ "\n"
          case "on_bulk_message_delete" => eventCode += "@bot.event\nasync def on_bulk_message_delete(messages):\n    " + cont+ "\n"
          case "on_guild_channel_delete" => eventCode += "@bot.event\nasync def on_guild_channel_delete(channel):\n    " + cont+ "\n"
          case "on_guild_channel_create" => eventCode += "@bot.event\nasync def on_guild_channel_create(channel):\n    " + cont+ "\n"
          case "on_guild_channel_update" => eventCode += "@bot.event\nasync def on_guild_channel_update(before, after):\n    " + cont+"\n"
          case "on_invite_create" => eventCode += "@bot.event\nasync def on_invite_create(invite):\n    " + cont + "\n"
          case "on_invite_delete" => eventCode += "@bot.event\nasync def on_invite_delete(invite):\n    " + cont + "\n"
          case "on_member_ban" => eventCode += "@bot.event\nasync def on_member_ban(guild, user):\n    " + cont + "\n"
          case "on_member_unban" => eventCode += "@bot.event\nasync def on_member_unban(guild, user):\n    " + cont + "\n"
          case "on_guild_role_create" => eventCode += "@bot.event\nasync def on_guild_role_create(role):\n    " + cont + "\n"
          case "on_guild_role_delete" => eventCode += "@bot.event\nasync def on_guild_role_delete(role):\n    " + cont + "\n"
          case "on_guild_role_update" => eventCode += "@bot.event\nasync def on_guild_role_update(before, after):\n    " + cont+"\n"
          case _ => println("invalid event name!")
        }
      }
      eventCode
    }
    def genCommand(commName: Array[String], commArgs: Array[String], commContent: Array[String]): String = {
      var commCode = ""
      for (i <- 0 to (commName.length-1)){
        commCode += "@bot.command()\nasync def " + commName(i) + "(" + commArgs(i) + "):\n    " + commContent(i) + "\n"
      }
      commCode
    }
    def genHelpers(strings: Array[String]): String ={
      var helperCode = ""
      for (i <- 0 to (strings.length-1)){
        helperCode += strings(i)
      }
      helperCode
    }
    def apply(fileName:String, libraries:String, description:String, prefix:String, commName: Array[String], commArgs: Array[String], commContent: Array[String], eventNames: Array[String], eventContents: Array[String], helpers: Array[String]): PythonWithPath = {
      val eventCode = genEvent(eventNames, eventContents)
      val commCode = genCommand(commName,commArgs ,commContent)
      val helperCode = genHelpers(helpers)
      val code =
        Python(s"""|$libraries
                   |
                   |# depending on certain events, these are auto-generated
                   |intents = discord.Intents.default()
                   |intents.message_content = True
                   |intents.members = True
                   |
                   |description = '$description'
                   |
                   |# ************************************************************************
                   |# * $description
                   |# ************************************************************************
                   |
                   |# something would generate this
                   |bot = commands.Bot(command_prefix='$prefix', description=description, intents=intents)
                   |# Events
                   |$eventCode
                   |# Commands
                   |$commCode
                   |
                   |#helper functions
                   | $helperCode
                   |# Now run...
                   |bot.run()
                   |
                   |""".stripMargin)
      PythonWithPath(code, Paths.get(fileName + ".py"))
    }
    val semanticType:Type = bot(bot.fileName) =>: bot(bot.libraries) =>: bot(bot.description) =>: bot(bot.prefix) =>: bot(bot.commName) =>: bot(bot.commArgs) =>: bot(bot.commContent) =>: bot(bot.eventNames) =>: bot(bot.eventContents) =>: bot(bot.helpers) =>: bot(complete)
  }
}