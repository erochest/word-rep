
package word_rep.corpus.break

import java.io.{BufferedReader,File,FileInputStream,FileReader}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files,FileSystems,Path,Paths,StandardOpenOption}
import scala.util.{Either,Left,Right}
import collection.JavaConverters._

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
    val trainingDir    = new File(outputD, "training")
    val validatingDir  = new File(outputD, "validating")
    val testDir        = new File(outputD, "test")
    val rand           = new java.util.Random()

    if (! outputD.exists       ) outputD.mkdirs
    if (! trainingDir.exists   ) trainingDir.mkdirs
    if (! validatingDir.exists ) validatingDir.mkdirs
    if (! testDir.exists       ) testDir.mkdirs

    files.foreach { filename =>
      val filepath = Paths.get(filename.toURI)
      val basename = Corpus.stripExt(filepath)
      val lines    = Files.readAllLines(filepath, StandardCharsets.UTF_8)
      val contents = asScalaBufferConverter(lines).asScala.mkString("\n")

      sdetector
        .sentDetect(contents)
        .zipWithIndex
        .foreach { (indexed_sent) =>
          val (sent, i)  = indexed_sent
          val r          = rand.nextDouble
          val outputName = f"$basename%s-$i%06d.txt"
          val bucket = if (r <= trainingRatio) "training"
                       else if (r <= (trainingRatio + validationRatio)) "validating"
                       else "test"
          val outputPath = Paths.get(cwd, outputDir, bucket, outputName)

          Files.write(
            outputPath,
            asJavaIterableConverter(Seq[String](sent)).asJava,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE
            )
          ()
        }
    }

    \/- ()
  }

}

