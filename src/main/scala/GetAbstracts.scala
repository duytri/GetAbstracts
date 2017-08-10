package main.scala

import java.io.BufferedReader
import java.io.FileReader
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer
import java.io.BufferedWriter
import java.io.FileWriter

object GetAbstracts {
  def main(args: Array[String]): Unit = {
    val inlineSeparator = '#'
    try {
      if (args.length < 6 || args.length == 0 || args(0).equals("-h") || args(0).equals("--help"))
        printHelp
      else {
        // input arguments
        println("Getting user parameters...")
        val params = new ParamsHelper
        val indexFilePath = params.checkAndGetInput(args, "-i", "--index", ParamsHelperType.STRING).asInstanceOf[String]
        val outputFilePath = params.checkAndGetInput(args, "-o", "--output", ParamsHelperType.STRING).asInstanceOf[String]
        val abstractsFilePath = params.checkAndGetInput(args, "-a", "--abstract", ParamsHelperType.STRING).asInstanceOf[String]
        if (indexFilePath == null || outputFilePath == null || abstractsFilePath == null) throw new Exception("ERROR: You must declare all arguments")
        // Processing
        println("Processing...")
        println("Building abstract dictionary...")
        var absDict = new HashMap[Int, String]
        val abstractReader = new BufferedReader(new FileReader(abstractsFilePath))
        var absContent = abstractReader.readLine()
        while (absContent != null) {
          val keyValue = absContent.split(inlineSeparator)
          if (keyValue.length > 1) {
            absDict += keyValue(0).toInt -> keyValue(1)
          }
          absContent = abstractReader.readLine()
        }
        abstractReader.close

        println("Get abstracts...")
        var listAbstracts = new ArrayBuffer[String]
        val indexReader = new BufferedReader(new FileReader(indexFilePath))
        var idPaper = indexReader.readLine().toInt
        while (idPaper > -1) {
          if (!absDict.get(idPaper).isEmpty)
            listAbstracts += absDict.get(idPaper).get
          val lineIdPaper = indexReader.readLine()
          if (lineIdPaper != null) idPaper = lineIdPaper.toInt
          else idPaper = -1
        }
        indexReader.close

        println("Write abstracts to file...")
        val abstractWriter = new BufferedWriter(new FileWriter(outputFilePath))
        listAbstracts.foreach(line => {
          abstractWriter.write(line + "\n")
        })
        abstractWriter.close
      }
    } catch {
      case e: Exception => {
        e.printStackTrace()
        printHelp()
      }
    }
  }

  def printHelp() = {
    println("Usage: GetAbstracts [Arguments]")
    println("       Arguments:")
    println("              -i --index [path]     : Index (citation) file path")
    println("              -a --abstract [path]  : Abstracts file path")
    println("              -o --output [path]    : Output file path")
    println("              -h --help             : Print this help")
  }
}