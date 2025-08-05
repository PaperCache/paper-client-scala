/*
 * Copyright (c) Kia Shakiba
 *
 * This source code is licensed under the GNU AGPLv3 license found in the
 * LICENSE file in the root directory of this source tree.
 */

package org.example

import scala.util.Success
import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import io.papercache.{PaperError, PaperErrorType}

@RunWith(classOf[JUnitRunner])
class PeekTest extends PaperClientTest {
	test("non existent") {
		val err = intercept[PaperError] {
			this.client.get.peek("key")
		}

		assertResult(PaperErrorType.KeyNotFound)(err.error_type)
	}

	test("existent") {
		this.client.get.set("key", "value")
		val got = this.client.get.peek("key")

		assertResult(Success("value"))(got)
	}
}
