package io.arrowkt.example

import io.happyvalley.sqlparserplugin.sql

//metadebug

fun helloWorld(): Unit = TODO()

fun main() {
  println("""SELECT * FROM tblHello""".sql())
}
