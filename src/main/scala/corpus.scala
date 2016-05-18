
package word_rep.corpus

import java.io.{File,FileInputStream}
import java.nio.file.{Path}

import chalk.tools.sentdetect._
import chalk.tools.tokenize._

import word_rep.corpus.word_bag.WordBag

object Corpus {

    def walkFiles(dir: String): Seq[File] = {
        val d = new File(dir)
        if (d.exists && d.isDirectory) {
            d.listFiles.filter(_.isFile).toSeq
        } else {
            Seq[File]()
        }
    }

    def buildSentenceDetector(modelFile: String): SentenceDetectorME =
        new SentenceDetectorME(
            new SentenceModel(
                new FileInputStream(modelFile)))

    def buildTokenizer(modelFile: String): Tokenizer =
        new TokenizerME(
            new TokenizerModel(
                new FileInputStream(modelFile)))

    def stripExt(path: Path) = {
        val basename = path.getFileName.toString
        if (basename.indexOf(".") > 0) {
            basename.substring(0, basename.lastIndexOf("."))
        } else {
            basename
        }
    }

}

class Corpus(rootDir: Path) {
    var index: Map[String,Int] = Map[String,Int]()

    def walkFiles: Seq[File] = Corpus.walkFiles(rootDir.toString)

    def indexBags(bags: Seq[WordBag]): Map[String,Int] = {
        index = bags.foldLeft(index)({ (index: Map[String,Int], bag: WordBag) =>
            bag.bag.keys.foldLeft(index)({ (m: Map[String,Int], token: String) =>
                if (m contains token) {
                    m
                } else {
                    m + (token -> m.size)
                }
            })
        })
        index
    }

    def vectorize(bags: Seq[WordBag]): Seq[Vector[Double]] =
        bags.map(_.toVector(index))

}

