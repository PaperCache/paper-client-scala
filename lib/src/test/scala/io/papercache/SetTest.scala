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
import java.lang.Thread
import io.papercache.{PaperError, PaperErrorType}

@RunWith(classOf[JUnitRunner])
class SetTest extends PaperClientTest {
	test("no ttl") {
		this.client.get.set("key", "value")
	}

	test("ttl") {
		this.client.get.set("key", "value", 2)
	}

	test("expiry") {
		this.client.get.set("key", "value", 1)

		val got = this.client.get.get("key")
		assertResult(Success("value"))(got)

		Thread.sleep(2000)

		val err = intercept[PaperError] {
			this.client.get.get("key")
		}

		assertResult(PaperErrorType.KeyNotFound)(err.error_type)
	}
}
