package org.example

import scala.util.Success
import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import io.papercache.{PaperError, PaperErrorType}

@RunWith(classOf[JUnitRunner])
class PolicyTest extends PaperClientTest {
	test("updates the cache policy") {
		val InitialPolicy = "lru"
		val UpdatedPolicy = "lfu"

		this.client.get.policy(InitialPolicy)
		assertResult(Success(InitialPolicy))(this.getCachePolicy())

		this.client.get.policy(UpdatedPolicy)
		assertResult(Success(UpdatedPolicy))(this.getCachePolicy())
	}
}
