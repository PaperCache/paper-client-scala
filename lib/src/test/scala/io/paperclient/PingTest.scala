package org.example

import scala.util.Success
import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PingTest extends PaperClientTest {
	test("ping returns pong") {
		assertResult(Success("pong"))(this.client.get.ping())
	}
}
