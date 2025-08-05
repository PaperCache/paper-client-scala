package org.example

import scala.util.Success
import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import io.papercache.{PaperError, PaperErrorType}

@RunWith(classOf[JUnitRunner])
class DelTest extends PaperClientTest {
	test("non existent") {
		val err = intercept[PaperError] {
			this.client.get.del("key")
		}

		assertResult(PaperErrorType.KeyNotFound)(err.error_type)
	}

	test("existent") {
		this.client.get.set("key", "value")
		this.client.get.del("key")
	}
}
