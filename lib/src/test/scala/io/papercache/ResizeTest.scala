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
class ResizeTest extends PaperClientTest {
	test("resizes the cache") {
		val InitialSize: Long = 40 * 1024 * 1024
		val UpdatedSize: Long = 40 * 1024 * 1024

		this.client.get.resize(InitialSize)
		assertResult(Success(InitialSize))(this.getCacheSize())

		this.client.get.resize(UpdatedSize)
		assertResult(Success(UpdatedSize))(this.getCacheSize())
	}
}
