package testdata

import com.github.ghik.silencer.silent

object methodSuppression {
  @silent
  def method(): Unit = {
    123
  }
}
