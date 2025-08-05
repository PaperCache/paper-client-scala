package io.papercache

import scala.util.Try
import java.util.concurrent.locks.ReentrantLock
import io.papercache.{PaperClient, PaperError, LockableClient}

class PaperPool(paper_addr: String, size: Int) {
	private val clients = new Array[PaperClient](size)
	private val locks = new Array[ReentrantLock](size)
	private var index: Int = 0

	for (i <- 0 to size - 1) {
		clients(i) = new PaperClient(paper_addr)
		locks(i) = new ReentrantLock()
	}

	def auth(token: String): Try[Unit] = Try {
		for (i <- 0 to this.clients.length - 1) {
			this.locks(i).lock()
			this.clients(i).auth(token)
			this.locks(i).unlock()
		}
	}

	def client(): LockableClient = {
		val index = this.index
		this.index = (index + 1) % this.clients.length

		new LockableClient(this.clients(index), this.locks(index))
	}
}
