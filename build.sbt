name := "zio-crud-reloaded"

version := "0.1"

scalaVersion := "3.0.2"

idePackagePrefix := Some("net.xrrocha.ziocrud")

libraryDependencies ++= {

  val zioVersion = "2.0.0-M2"

  Seq(
    "dev.zio" %% "zio" % zioVersion,
    "dev.zio" %% "zio-test" % zioVersion % Test,
    "dev.zio" %% "zio-test-sbt" % zioVersion % Test
  )
}

testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
