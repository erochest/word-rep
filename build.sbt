lazy val root = (project in file(".")).
  settings(
      name := "web-rep",
      version := "0.1",
      scalaVersion := "2.11.8"
  ).
  settings(
      libraryDependencies ++= Seq(
          "com.github.scopt"   %% "scopt"       % "3.4.0",
          "org.scalaz"         %% "scalaz-core" % "7.2.2",
          "com.jasonbaldridge" %  "chalk"       % "1.1.0"
      ),
      resolvers ++= Seq(
          "opennlp sourceforge repo" at "http://opennlp.sourceforge.net/maven2",
          Resolver.sonatypeRepo("public")
      )
  )
