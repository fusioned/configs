/*
 * Copyright 2013 Tsukasa Kitachi
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

package com.github.kxbmap

import com.typesafe.config.{ConfigException, Config}
import shapeless.TypeOperators._


package object configs {

  type AtPath[T] = Configs[String => T]

  object AtPath {
    @inline def of[T: AtPath]: AtPath[T] = implicitly[AtPath[T]]

    @inline def apply[T](f: (Config, String) => T): AtPath[T] = Configs { c => f(c, _) }
  }


  sealed trait Bytes
  val Bytes = tag[Bytes]


  final implicit class EnrichTypesafeConfig(val c: Config) extends AnyVal {
    def extract[T: Configs]: T = Configs.of[T].get(c)

    def get[T: AtPath](path: String): T = AtPath.of[T].get(c)(path)

    def opt[T: AtPath](path: String): Option[T] = get[Option[T]](path)

    def orMissing[T: AtPath](path: String): Option[T] =
      try Some(get[T](path)) catch {
        case _: ConfigException.Missing => None
      }
  }

}
