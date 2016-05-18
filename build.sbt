lazy val root = (project in file(".")).
  settings(
      name := "web-rep",
      version := "0.1",
      scalaVersion := "2.11.8"
  ).
  settings(
      libraryDependencies ++= Seq(
          "com.github.scopt"   %% "scopt"         % "3.4.0",
          "org.scalaz"         %% "scalaz-core"   % "7.2.2",
          "com.jasonbaldridge" %  "chalk"         % "1.1.0",
          "org.apache.commons" % "commons-io" % "1.3.2",
          "org.slf4j" % "slf4j-simple" % "1.7.21",
          "com.fasterxml.jackson.core" % "jackson-core" % "2.7.4",
          "org.nd4j"           %  "nd4j"          % "0.4-rc3.9",
          // "org.nd4j"           %  "nd4j-nlp"      % "0.4-rc3.9",
          "org.nd4j" % "nd4j-common" % "0.4-rc3.9",
          "org.nd4j" % "nd4j-native" % "0.4-rc3.9",
          // "org.nd4j" % "nd4j-cuda-7.5" % "0.4-rc3.9",
          "org.bytedeco" % "javacpp" % "1.2"
      ),
      resolvers ++= Seq(
          "opennlp sourceforge repo" at "http://opennlp.sourceforge.net/maven2",
          Resolver.sonatypeRepo("public")
      )
  )
