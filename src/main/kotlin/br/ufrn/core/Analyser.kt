package br.ufrn.core

import java.io.BufferedReader
import java.io.File

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("/Users/alice/Development/kastree/ast/ast-common/src/main/kotlin/kastree/ast/Node.kt").bufferedReader()

    val inputString = bufferedReader.use { it.readText() }
    println(inputString)
}