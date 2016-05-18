
package word_rep.corpus.break

import java.io.{BufferedReader,File,FileInputStream,FileReader}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files,FileSystems,Path,Paths,StandardOpenOption}
import scala.util.{Either,Left,Right}
import collection.JavaConverters._
import scala.collection.mutable

import scalaz.{ \/, -\/, \/- }

import word_rep.corpus.Corpus


object BreakCorpus {
  val inputDir        : String  = "input"
  val outputDir       : String  = "corpus"
  val trainingRatio   : Double  = 0.6
  val validationRatio : Double  = 0.2
  val testRatio       : Double  = 0.2

  def break(
    inputDir        : String,
    outputDir       : String,
    trainingRatio   : Double,
    validationRatio : Double,
    testRatio       : Double
  ): String \/ Unit = {
    if (trainingRatio + validationRatio + testRatio > 1.0) {
      return -\/ ("Invalid ratios add to more than one.")
    }

    val cwd            = Paths.get("").toAbsolutePath.toString
    val files          = Corpus.walkFiles(inputDir)
    lazy val sdetector = Corpus.buildSentenceDetector("models/en-sent.bin")
    val outputD        = new File(outputDir)
    val rand           = new java.util.Random()
    val test           = mutable.ArrayBuffer.empty[String]
    val training       = mutable.ArrayBuffer.empty[String]
    val validation     = mutable.ArrayBuffer.empty[String]

    if (! outputD.exists) outputD.mkdirs

    files.foreach { filename =>
      val filepath = Paths.get(filename.toURI)
      val basename = Corpus.stripExt(filepath)
      val lines    = Files.readAllLines(filepath, StandardCharsets.UTF_8)
      val contents = asScalaBufferConverter(lines).asScala.mkString("\n")

      sdetector
        .sentDetect(contents)
        .foreach { (sent: String) =>
          val r      = rand.nextDouble
          val normed = sent
            .split(Array[Char](' ', '\t', '\n', '\r'))
            .mkString(" ")

          if (r <= trainingRatio)
            training += normed
          else if (r <= (trainingRatio + validationRatio))
            validation += normed
          else
            test += normed
        }
    }

    println(s"Writing ${training.size} training sentences.")
    writeLines(Paths.get(cwd, outputDir, "training.txt"), training)

    println(s"Writing ${validation.size} validation sentences.")
    writeLines(Paths.get(cwd, outputDir, "validation.txt"), validation)

    println(s"Writing ${test.size} test sentences.")
    writeLines(Paths.get(cwd, outputDir, "test.txt"), test)

    \/- ()
  }

  def writeLines(filepath: Path, lines: mutable.ArrayBuffer[String]): Unit =
    Files.write(
      filepath,
      asJavaIterableConverter(lines).asJava,
      StandardCharsets.UTF_8,
      StandardOpenOption.CREATE
    )

}

