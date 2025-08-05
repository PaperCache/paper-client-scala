/*
 * Copyright (c) Kia Shakiba
 *
 * This source code is licensed under the GNU AGPLv3 license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.papercache

import java.util.concurrent.locks.ReentrantLock
import io.papercache.PaperClient

class LockableClient(private val client: PaperClient, private val mutex: ReentrantLock) {
	def lock(): PaperClient = {
		this.mutex.lock()
		this.client
	}

	def unlock() = {
		this.mutex.unlock()
	}
}
