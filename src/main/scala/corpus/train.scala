package word_rep.corpus.train

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files,Path}
import collection.JavaConverters._

import word_rep.corpus.Corpus


class Trainer(inputDir: Path) {

  def train(output: Path): Unit = {
  }

  def readDataFrame: Seq[(Int, String)] = {
    Corpus
      .walkFiles(inputDir.toString)
      .zipWithIndex
      .map({ (inputPair) =>
        val (file, i) = inputPair
        val filepath  = file.toPath
        val lines     = Files.readAllLines(filepath, StandardCharsets.UTF_8)
        val contents  = asScalaBufferConverter(lines).asScala.mkString("\n")
        (i, contents)
      })
  }

}
