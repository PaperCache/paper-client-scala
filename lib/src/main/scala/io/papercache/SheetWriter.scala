package io.papercache

import scala.util.Try
import scala.collection.mutable.ArrayBuffer
import java.io.OutputStream
import java.nio.{ByteBuffer, ByteOrder}

class SheetWriter {
	private val buf = ArrayBuffer[Byte]()

	def writeU8(value: Byte): Unit = {
		this.buf += value
	}

	def writeU32(value: Long): Unit = {
		var buf = ByteBuffer.allocate(4)
		buf.order(ByteOrder.LITTLE_ENDIAN)
		buf.putInt(value.toInt)

		for (byte <- buf.array()) {
			this.buf += byte
		}
	}

	def writeU64(value: Long): Unit = {
		var buf = ByteBuffer.allocate(8)
		buf.order(ByteOrder.LITTLE_ENDIAN)
		buf.putLong(value.toLong)

		for (byte <- buf.array()) {
			this.buf += byte
		}
	}

	def writeString(value: String): Unit = {
		this.writeU32(value.length)

		for (byte <- value.getBytes("UTF-8")) {
			this.buf += byte
		}
	}

	def send(tcp_stream: OutputStream): Try[Unit] = Try {
		Try(tcp_stream.write(this.buf.toArray))
	}
}
