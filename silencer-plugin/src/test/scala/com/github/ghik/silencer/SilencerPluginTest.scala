package com.github.ghik.silencer

import java.io.File

import org.scalatest.FunSuite

import scala.reflect.io.VirtualDirectory
import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.{Global, Settings}

class SilencerPluginTest extends FunSuite {
  suite =>

  val settings = new Settings
  settings.usejavacp.value = true
  // avoid saving classfiles to disk
  settings.outputDirs.setSingleOutput(new VirtualDirectory("(memory)", None))
  val reporter = new ConsoleReporter(settings)

  val global = new Global(settings, reporter) {
    override protected def loadRoughPluginsList() =
      new SilencerPlugin(this) :: super.loadRoughPluginsList()
  }

  def compile(filenames: String*): Unit = {
    val run = new global.Run
    run.compile(filenames.toList.map("testdata/" + _))
  }

  def assertWarnings(count: Int): Unit = {
    assert(!reporter.hasErrors)
    assert(count === reporter.warningCount)
  }

  def testFile(filename: String, expectedWarnings: Int = 0): Unit = {
    test(filename) {
      compile(filename)
      assertWarnings(expectedWarnings)
    }
  }

  testFile("unsuppressed.scala", 1)
  testFile("statementSuppression.scala")
  testFile("localValueSuppression.scala")
  testFile("methodSuppression.scala")
  testFile("classSuppression.scala")

  test("multiple files compilation") {
    compile(new File("testdata").listFiles().map(_.getName): _*)
    assertWarnings(1)
  }

}
