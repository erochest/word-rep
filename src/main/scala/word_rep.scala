
package word_rep

import java.io.File

import scalaz.{ \/, -\/, \/- }

import word_rep.corpus.BreakCorpus

object Main extends App {
  val version = "0.1"

  case class Config (
    mode            : String  = "",
    inputDir        : String  = BreakCorpus.inputDir,
    outputDir       : String  = BreakCorpus.outputDir,
    breakSentences  : Boolean = BreakCorpus.breakSentences,
    trainingRatio   : Double  = BreakCorpus.trainingRatio,
    validationRatio : Double  = BreakCorpus.validationRatio,
    testRatio       : Double  = BreakCorpus.testRatio
  ) 

  val parser = new scopt.OptionParser[Config]("word_rep") {
    head("word representations", Main.version)

    cmd("break-corpus").action { (_, c) => c.copy(mode="break-corpus") }
      .text("Break a corpus into bits.")
      .children(
        opt[String]("input-dir").abbr("i")
          .text("the directory containing the input corpus.")
          .action { (i, c) => c.copy(inputDir=i) },
        opt[String]("output-dir").abbr("o")
          .text("the directory to put the output files into.")
          .action { (o, c) => c.copy(outputDir=o) },
        opt[Unit]("break-sentences").abbr("s")
          .text("break the input files into sentences?")
          .action { (_, c) => c.copy(breakSentences=true) },
        opt[Double]("training-ratio").abbr("tr")
          .text("The proportion of files to include in the training set.")
          .action { (tr, c) => c.copy(trainingRatio=tr) },
        opt[Double]("validation-ratio").abbr("vr")
          .text("The proportion of files to include in the validation set.")
          .action { (vr, c) => c.copy(validationRatio=vr) },
        opt[Double]("test-ratio").abbr("ttr")
          .text("The proportion of files to include in the test set.")
          .action { (ttr, c) => c.copy(testRatio=ttr) }
        )
  }

  parser.parse(args, Config()) match {
    case Some(config) =>
      if (config.mode == "break-corpus")
        BreakCorpus.break(
          config.inputDir, 
          config.outputDir,
          config.breakSentences,
          config.trainingRatio,
          config.validationRatio,
          config.testRatio
        ) match {
          case -\/(err)   => { println(err); sys.exit(1) }
          case \/-( () ) => ()
        }
      else {
        println("Invalid mode")
        sys.exit(1)
      }

    case None =>
      println("Oops")
  }

}
