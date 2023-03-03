package org.combinators.discord.domain

case class Discord (
   /** Every discord app has its own name. */
   name:String,
   libraries: String,
   description:String,
   prefix:String,
   commName:Array[String],
   commArgs:Array[String],
   commContent: Array[String],
   eventNames: Array[String],
   eventContents: Array[String],
   helpers:Array[String]




  // events

)