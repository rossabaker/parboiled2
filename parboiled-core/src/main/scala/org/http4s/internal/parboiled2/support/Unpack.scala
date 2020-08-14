/*
 * Copyright 2009-2020 org.http4s
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

package org.http4s.internal.parboiled2.support

/**
  * "Unpacks" an HList if it has only zero or one element(s).
  *   Out =
  *     Unit  if L == HNil
  *     T     if L == T :: HNil
  *     L     otherwise
  *
  *  You can `import Unpack.dontUnpack` if you'd like to circumvent this unpacking logic.
  */
sealed private[http4s] trait Unpack[L <: HList] {
  type Out
  def apply(hlist: L): Out
}

private[http4s] object Unpack extends AlternativeUnpacks {

  implicit def fromAux[L <: HList, Out0](implicit aux: Aux[L, Out0]) =
    new Unpack[L] {
      type Out = Out0
      def apply(hlist: L) = aux(hlist)
    }

  sealed trait Aux[L <: HList, Out0] {
    def apply(hlist: L): Out0
  }

  implicit def hnil[L <: HNil]: Aux[L, Unit] = HNilUnpack.asInstanceOf[Aux[L, Unit]]

  implicit object HNilUnpack extends Aux[HNil, Unit] {
    def apply(hlist: HNil): Unit = ()
  }

  implicit def single[T]: Aux[T :: HNil, T] = SingleUnpack.asInstanceOf[Aux[T :: HNil, T]]

  private object SingleUnpack extends Aux[Any :: HList, Any] {
    def apply(hlist: Any :: HList): Any = hlist.head
  }
}

sealed abstract private[http4s] class AlternativeUnpacks {

  /**
    * Import if you'd like to *always* deliver the valueStack as an `HList`
    * at the end of the parsing run, even if it has only zero or one element(s).
    */
  implicit def dontUnpack[L <: HList]: Unpack.Aux[L, L] = DontUnpack.asInstanceOf[Unpack.Aux[L, L]]

  private object DontUnpack extends Unpack.Aux[HList, HList] {
    def apply(hlist: HList): HList = hlist
  }
}
