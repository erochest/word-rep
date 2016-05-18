
package word_rep.corpus.word_bag

import chalk.tools.tokenize._

import word_rep.corpus.Corpus

object WordBag {

    def frequencies(tokens: Iterable[String]): WordBag = {
        val bag = new WordBag
        bag.bag = tokens.foldLeft(Map[String,Int]())({ (m: Map[String,Int], t: String) =>
            m + (t -> (m.getOrElse(t, 0) + 1))
        })
        bag
    }

}

class WordBag {
    var bag: Map[String,Int] = Map[String,Int]()

    def toVector(index: Map[String,Int]): Vector[Double] = {
        val buffer = new Array[Double](index.size)

        bag foreach { (token_index: (String, Int)) =>
            val (token: String, count: Int) = token_index
            index get token match {
                case Some(i) => buffer(i) = count.toDouble
                case None    => ()
            }
        }

        buffer.toVector
    }

}

