package org.example

import scala.util.{Success, Failure}
import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import io.papercache.{PaperError, PaperErrorType}

@RunWith(classOf[JUnitRunner])
class SizeTest extends PaperClientTest {
	test("non existent") {
		val err = intercept[PaperError] {
			this.client.get.size("key")
		}

		assertResult(PaperErrorType.KeyNotFound)(err.error_type)
	}

	test("existent") {
		this.client.get.set("key", "value")

		this.client.get.size("key") match {
			case Success(size) => assert(size > 0)
			case Failure(e) => fail(e.getMessage)
		}
	}
}
