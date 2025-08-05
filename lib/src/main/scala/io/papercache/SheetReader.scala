/*
 * Copyright (c) Kia Shakiba
 *
 * This source code is licensed under the GNU AGPLv3 license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.papercache

import scala.util.{Try, Success}
import java.io.InputStream
import java.nio.{ByteBuffer, ByteOrder}
import java.nio.charset.StandardCharsets

class SheetReader(private val tcp_stream: InputStream) {
	def readU8(): Try[Byte] = {
		this.readBytes(1).flatMap { bytes => Success(bytes(0)) }
	}

	def readBool(): Try[Boolean] = {
		this.readU8().flatMap { value => Success(value == 33) }
	}

	def readU32(): Try[Long] = {
		this.readBytes(4).flatMap { bytes =>
			var buf = ByteBuffer.wrap(bytes)
			buf.order(ByteOrder.LITTLE_ENDIAN)
			Success(buf.getInt().toLong & 0xffffffffL)
		}
	}

	def readU64(): Try[Long] = {
		this.readBytes(8).flatMap { bytes =>
			var buf = ByteBuffer.wrap(bytes)
			buf.order(ByteOrder.LITTLE_ENDIAN)
			Success(buf.getLong())
		}
	}

	def readF64(): Try[Double] = {
		this.readBytes(8).flatMap { bytes =>
			var buf = ByteBuffer.wrap(bytes)
			buf.order(ByteOrder.LITTLE_ENDIAN)
			Success(buf.getDouble())
		}
	}

	def readString(): Try[String] = {
		this.readU32().flatMap { size =>
			this.readBytes(size.toInt).flatMap { bytes =>
				Success(new String(bytes, StandardCharsets.UTF_8))
			}
		}
	}

	def readBytes(size: Int): Try[Array[Byte]] = Try {
		val buf = new Array[Byte](size)
		var bytes_to_read = size

		while (bytes_to_read > 0) {
			bytes_to_read -= this.tcp_stream.read(buf, size - bytes_to_read, bytes_to_read)
		}

		buf
	}
}
