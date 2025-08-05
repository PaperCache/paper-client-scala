package org.example

import scala.util.{Try, Success, Failure}
import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import io.papercache.{PaperPool, PaperError, PaperErrorType}

@RunWith(classOf[JUnitRunner])
class PaperPoolTest extends AnyFunSuite {
	test("ping") {
		val pool = new PaperPool("paper://127.0.0.1:3145", 2)

		for (i <- 0 to 9) {
			val lockable_client = pool.client()
			val client = lockable_client.lock()

			val response = client.ping()
			assertResult(Success("pong"))(response)

			lockable_client.unlock()
		}
	}

	test("auth") {
		val pool = new PaperPool("paper://127.0.0.1:3145", 2)

		assert(!this.canSet(pool).get)
		pool.auth("auth_token")
		assert(this.canSet(pool).get)
	}

	private def canSet(pool: PaperPool): Try[Boolean] = {
		val lockable_client = pool.client()
		val client = lockable_client.lock()

		val can_set = try {
			client.set("key", "value")
			true
		} catch {
			case e: Exception => false
		} finally {
			lockable_client.unlock()
		}

		Try(can_set)
	}
}
