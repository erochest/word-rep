
package word_rep

import java.io.File
import java.nio.file.Paths

import scalaz.{ \/, -\/, \/- }

import word_rep.corpus.break.BreakCorpus
import word_rep.corpus.train.Trainer


object Main extends App {
  val version = "0.1"

  case class Config (
    mode            : String  = "",
    inputDir        : String  = BreakCorpus.inputDir,
    outputDir       : String  = BreakCorpus.outputDir,
    trainingRatio   : Double  = BreakCorpus.trainingRatio,
    validationRatio : Double  = BreakCorpus.validationRatio,
    testRatio       : Double  = BreakCorpus.testRatio,
    batchSize       : Int     = 1000,
    iterations      : Int     = 3,
    layerSize       : Int     = 150
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
      cmd("train").action { (_, c) => c.copy(mode="train") }
        .text("Train a corpus using a skip-gram model.")
        .children(
          opt[String]("input").abbr("i")
            .text("the file containing the input corpus, one sentence per line.")
            .action { (i, c) => c.copy(inputDir=i) },
          opt[String]("output").abbr("o")
            .text("the file to put the output files into.")
            .action { (o, c) => c.copy(outputDir=o) },
          opt[Int]("batch-size").abbr("b")
            .text("the size of each batch.")
            .action { (b, c) => c.copy(batchSize=b) },
          opt[Int]("iterations").abbr("I")
            .text("the number of training iterations to run.")
            .action { (i, c) => c.copy(iterations=i) },
          opt[Int]("layer-size").abbr("l")
            .text("the number of nodes in the features in the word vector.")
            .action { (l, c) => c.copy(layerSize=l) }
        )
  }

  val retVal = parser.parse(args, Config()) match {
    case Some(config) =>
      if (config.mode == "break-corpus")
        BreakCorpus.break(
          config.inputDir, 
          config.outputDir,
          config.trainingRatio,
          config.validationRatio,
          config.testRatio
        )
      else if (config.mode == "train") {
        (new Trainer(
            Paths.get(config.inputDir),
            config.batchSize,
            config.iterations,
            config.layerSize
        ))
          .train(Paths.get(config.outputDir))
      } else {
        -\/("Invalid mode")
      }

    case None =>
      -\/("Oops")
  }

  retVal match {
    case -\/(msg) =>
      println(msg)
      sys.exit(1)
    case \/-( () ) => ()
  }

}
