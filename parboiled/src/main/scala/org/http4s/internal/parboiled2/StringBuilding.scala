/*
 * Copyright 2009-2019 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Lives in core, instead of parboiled2, to get it in a separate compiler pass
 * from the rule macro.  Both source directories ultimately get packaged into
 * http4s-core, so this location looks weird to perusers on GitHub, but users
 * should be none the wiser
 */

package org.http4s.internal.parboiled2

/**
  * For certain high-performance use-cases it is better to construct Strings
  * that the parser is to produce/extract from the input in a char-by-char fashion.
  *
  * Mixing this trait into your parser gives you a simple facility to support this.
  */
private[http4s] trait StringBuilding { this: Parser =>
  protected val sb = new java.lang.StringBuilder

  def clearSB(): Rule0 = rule(run(sb.setLength(0)))

  def appendSB(): Rule0 = rule(run(sb.append(lastChar)))

  def appendSB(offset: Int): Rule0 = rule(run(sb.append(charAt(offset))))

  def appendSB(c: Char): Rule0 = rule(run(sb.append(c)))

  def appendSB(s: String): Rule0 = rule(run(sb.append(s)))

  def prependSB(): Rule0 = rule(run(doPrepend(lastChar)))

  def prependSB(offset: Int): Rule0 = rule(run(doPrepend(charAt(offset))))

  def prependSB(c: Char): Rule0 = rule(run(doPrepend(c)))

  def prependSB(s: String): Rule0 = rule(run(doPrepend(s)))

  def setSB(s: String): Rule0 = rule(run(doSet(s)))

  private def doPrepend(c: Char): Unit = {
    val saved = sb.toString
    sb.setLength(0)
    sb.append(c)
    sb.append(saved)
    ()
  }

  private def doPrepend(s: String): Unit = {
    val saved = sb.toString
    sb.setLength(0)
    sb.append(s)
    sb.append(saved)
    ()
  }

  private def doSet(s: String): Unit = {
    sb.setLength(0)
    sb.append(s)
    ()
  }
}
