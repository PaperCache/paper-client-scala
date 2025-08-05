/*
 * Copyright (c) Kia Shakiba
 *
 * This source code is licensed under the GNU AGPLv3 license found in the
 * LICENSE file in the root directory of this source tree.
 */

package org.example

import scala.util.{Success, Failure}
import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class VersionTest extends PaperClientTest {
	test("version returns correctly") {
		this.client.get.version() match {
			case Success(version) => assert(version.length > 0)
			case Failure(e) => fail(e.getMessage)
		}
	}
}
