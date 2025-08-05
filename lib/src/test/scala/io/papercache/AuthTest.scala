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
class AuthTest extends PaperClientTest(false) {
	test("incorrect auth token") {
		val set_err = intercept[PaperError] {
			this.client.get.set("key", "value")
		}

		assertResult(PaperErrorType.Unauthorized)(set_err.error_type)

		val auth_err = intercept[PaperError] {
			this.client.get.auth("incorrect_auth_token")
		}

		assertResult(PaperErrorType.Unauthorized)(auth_err.error_type)
	}

	test("correct auth token") {
		val set_err = intercept[PaperError] {
			this.client.get.set("key", "value")
		}

		assertResult(PaperErrorType.Unauthorized)(set_err.error_type)

		this.client.get.auth("auth_token")
		this.client.get.set("key", "value")
	}
}
