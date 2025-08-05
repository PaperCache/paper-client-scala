package org.example

import scala.util.Success
import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import io.papercache.{PaperError, PaperErrorType}

@RunWith(classOf[JUnitRunner])
class WipeTest extends PaperClientTest {
	test("wipes the cache") {
		this.client.get.set("key", "value")
		this.client.get.wipe()

		val err = intercept[PaperError] {
			this.client.get.get("key")
		}

		assertResult(PaperErrorType.KeyNotFound)(err.error_type)
	}
}
