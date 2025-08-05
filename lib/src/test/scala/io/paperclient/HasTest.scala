package org.example

import scala.util.Success
import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import io.papercache.{PaperError, PaperErrorType}

@RunWith(classOf[JUnitRunner])
class HasTest extends PaperClientTest {
	test("non existent") {
		val has = this.client.get.has("key")
		assertResult(Success(false))(has)
	}

	test("existent") {
		this.client.get.set("key", "value")

		val has = this.client.get.has("key")
		assertResult(Success(true))(has)
	}
}
