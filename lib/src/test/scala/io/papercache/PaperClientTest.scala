/*
 * Copyright (c) Kia Shakiba
 *
 * This source code is licensed under the GNU AGPLv3 license found in the
 * LICENSE file in the root directory of this source tree.
 */

package org.example

import scala.util.{Try, Success}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.Outcome
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import io.papercache.PaperClient

class PaperClientTest(private val authed: Boolean = true) extends AnyFunSuite {
	protected var client: Option[PaperClient] = None

	override def withFixture(test: NoArgTest): Outcome = {
		this.client = Some(new PaperClient("paper://127.0.0.1:3145"))

		if (this.authed) {
			this.client.get.auth("auth_token")
			this.client.get.wipe()
		}

		try {
			super.withFixture(test)
		} finally {
			this.client.get.disconnect()
		}
	}

	protected def getCacheSize(): Try[Long] = {
		this.client.get.status().flatMap { status => Success(status.max_size) }
	}

	protected def getCachePolicy(): Try[String] = {
		this.client.get.status().flatMap { status => Success(status.policy) }
	}
}
