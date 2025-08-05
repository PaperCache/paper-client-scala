package org.example

import scala.util.Success
import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import io.papercache.{PaperError, PaperErrorType}

@RunWith(classOf[JUnitRunner])
class GetTest extends PaperClientTest {
	test("non existent") {
		val err = intercept[PaperError] {
			this.client.get.get("key")
		}

		assertResult(PaperErrorType.KeyNotFound)(err.error_type)
	}

	test("existent") {
		this.client.get.set("key", "value")
		val got = this.client.get.get("key")

		assertResult(Success("value"))(got)
	}
}
