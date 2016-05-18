package word_rep.corpus.train

import java.io.{File,FileOutputStream,ObjectOutputStream}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files,Path}
import collection.JavaConverters._

import org.deeplearning4j.text.sentenceiterator._
import org.deeplearning4j.text.tokenization.tokenizer._
import org.deeplearning4j.text.tokenization.tokenizerfactory._
import org.deeplearning4j.models.word2vec._

import word_rep.corpus.Corpus


class Trainer(input: Path, batchSize: Int, iterations: Int, layerSize: Int) {

  def train(output: Path): Unit = {
      val iter = new LineSentenceIterator(input.toFile)
      iter.setPreProcessor(new SentencePreProcessor() {
          def preProcess(sentence: String): String =
              sentence.toLowerCase
      })

      val tokenizer = new DefaultTokenizerFactory()
      tokenizer.setTokenPreProcessor(new TokenPreProcess() {
          def preProcess(token: String): String =
              token.toLowerCase.replaceAll("\\d", "#")
      })

      val vec = new Word2Vec.Builder()
          .batchSize(batchSize)
          .minWordFrequency(5)
          .useAdaGrad(false)
          .layerSize(layerSize)
          .iterations(iterations)
          .learningRate(0.025)
          .minLearningRate(1e-3)
          .negativeSample(10)
          .iterate(iter)
          .tokenizerFactory(tokenizer)
          .build
      println("training")
      vec.fit

      println("saving to " + output.toString)
      val oos = new ObjectOutputStream(new FileOutputStream(output.toString))
      oos writeObject vec
      oos close
  }

}
