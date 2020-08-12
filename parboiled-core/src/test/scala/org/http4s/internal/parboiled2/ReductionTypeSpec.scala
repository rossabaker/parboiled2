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

package org.http4s.internal.parboiled2

import scala.reflect.ClassTag
import utest._

object ReductionTypeSpec extends TestSuite {

  sealed trait Foo
  case object Foo1                    extends Foo
  case class Foo2(lhs: Foo, rhs: Foo) extends Foo

  class FooParser(val input: ParserInput) extends Parser {
    def OneOrMoreExpr  = rule(foo1 ~ oneOrMore(foo1 ~> Foo2))
    def ZeroOrMoreExpr = rule(foo1 ~ zeroOrMore(foo1 ~> Foo2))
    def OptionalExpr   = rule(foo1 ~ optional(foo1 ~> Foo2))
    def TimesExpr      = rule(foo1 ~ 2.times(foo1 ~> Foo2))

    def foo1 = rule(push(Foo1))
  }

  val tests = Tests {

    "Repeating combinators should properly compute their reduction result types" - {
      "OneOrMore" - { ruleTypeOf(_.OneOrMoreExpr) ==> classOf[Foo2] }
      "ZeroOrMore" - { ruleTypeOf(_.ZeroOrMoreExpr) ==> classOf[Foo] }
      "Optional" - { ruleTypeOf(_.OptionalExpr) ==> classOf[Foo] }
      "Times" - { ruleTypeOf(_.TimesExpr) ==> classOf[Foo2] }
    }
  }

  def ruleTypeOf[T](f: FooParser => Rule1[T])(implicit tag: ClassTag[T]) = tag.runtimeClass
}
