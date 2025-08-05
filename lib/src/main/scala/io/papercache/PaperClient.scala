/*
 * Copyright (c) Kia Shakiba
 *
 * This source code is licensed under the GNU AGPLv3 license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.papercache

import scala.util.{Try, Success, Failure}
import java.io.IOException
import java.net.Socket
import io.papercache.{PaperError, PaperErrorType, PaperStatus, SheetReader, SheetWriter}

class PaperClient(paper_addr: String) {
	private val MaxReconnectAttempts = 3;

	private val CommandBytePing: Byte = 0;
	private val CommandByteVersion: Byte = 1;

	private val CommandByteAuth: Byte = 2;

	private val CommandByteGet: Byte = 3;
	private val CommandByteSet: Byte = 4;
	private val CommandByteDel: Byte = 5;

	private val CommandByteHas: Byte = 6;
	private val CommandBytePeek: Byte = 7;
	private val CommandByteTtl: Byte = 8;
	private val CommandByteSize: Byte = 9;

	private val CommandByteWipe: Byte = 10;

	private val CommandByteResize: Byte = 11;
	private val CommandBytePolicy: Byte = 12;

	private val CommandByteStatus: Byte = 13;

	if (!paper_addr.startsWith("paper://")) {
		throw new PaperError(PaperErrorType.InvalidAddress)
	}

	private val replaced_addr = paper_addr.stripPrefix("paper://")
	private val parsed_addr = replaced_addr.split(":")

	if (parsed_addr.length != 2) {
		throw new PaperError(PaperErrorType.InvalidAddress)
	}

	private val host = parsed_addr(0)

	private val port = Try(parsed_addr(1).toInt) match {
		case Success(value) => value
		case Failure(_) => throw new PaperError(PaperErrorType.InvalidAddress)
	}

	private var auth_token: Option[String] = None
	private var reconnect_attempts = 0

	private var tcp_client = try {
		new Socket(host, port)
	} catch {
		case e: Exception => throw new PaperError(PaperErrorType.ConnectionRefused)
	}

	this.handshake()

	def disconnect(): Try[Unit] = {
		try {
			Try(this.tcp_client.close())
		} catch {
			case e: Exception => throw new PaperError(PaperErrorType.Internal)
		}
	}

	def ping(): Try[String] = {
		var writer = new SheetWriter
		writer.writeU8(CommandBytePing)

		this.processData(writer)
	}

	def version(): Try[String] = {
		var writer = new SheetWriter
		writer.writeU8(CommandByteVersion)

		this.processData(writer)
	}

	def auth(token: String): Try[Unit] = {
		var writer = new SheetWriter
		writer.writeU8(CommandByteAuth)
		writer.writeString(token)

		val result = this.process(writer)
		this.auth_token = Some(token)

		result
	}

	def get(key: String): Try[String] = {
		var writer = new SheetWriter
		writer.writeU8(CommandByteGet)
		writer.writeString(key)

		this.processData(writer)
	}

	def set(key: String, value: String, ttl: Long = 0): Try[Unit] = {
		var writer = new SheetWriter
		writer.writeU8(CommandByteSet)
		writer.writeString(key)
		writer.writeString(value)
		writer.writeU32(ttl)

		this.process(writer)
	}

	def del(key: String): Try[Unit] = {
		var writer = new SheetWriter
		writer.writeU8(CommandByteDel)
		writer.writeString(key)

		this.process(writer)
	}

	def has(key: String): Try[Boolean] = {
		var writer = new SheetWriter
		writer.writeU8(CommandByteHas)
		writer.writeString(key)

		this.processHas(writer)
	}

	def peek(key: String): Try[String] = {
		var writer = new SheetWriter
		writer.writeU8(CommandBytePeek)
		writer.writeString(key)

		this.processData(writer)
	}

	def ttl(key: String, ttl: Long = 0): Try[Unit] = {
		var writer = new SheetWriter
		writer.writeU8(CommandByteTtl)
		writer.writeString(key)
		writer.writeU32(ttl)

		this.process(writer)
	}

	def size(key: String): Try[Long] = {
		var writer = new SheetWriter
		writer.writeU8(CommandByteSize)
		writer.writeString(key)

		this.processSize(writer)
	}

	def wipe(): Try[Unit] = {
		var writer = new SheetWriter
		writer.writeU8(CommandByteWipe)

		this.process(writer)
	}

	def resize(size: Long): Try[Unit] = {
		var writer = new SheetWriter
		writer.writeU8(CommandByteResize)
		writer.writeU64(size)

		this.process(writer)
	}

	def policy(policy: String): Try[Unit] = {
		var writer = new SheetWriter
		writer.writeU8(CommandBytePolicy)
		writer.writeString(policy)

		this.process(writer)
	}

	def status(): Try[PaperStatus] = {
		var writer = new SheetWriter
		writer.writeU8(CommandByteStatus)

		this.processStatus(writer)
	}

	private def process(writer: SheetWriter): Try[Unit] = {
		try {
			val output_stream = this.tcp_client.getOutputStream()
			writer.send(output_stream)

			val reader = new SheetReader(this.tcp_client.getInputStream())

			val is_ok = reader.readBool().get
			if (!is_ok) throw PaperError.fromReader(reader)

			this.reconnect_attempts = 0

			Try {}
		} catch {
			case e: IOException => {
				if (!this.reconnect()) throw new PaperError(PaperErrorType.Disconnected)
				return this.process(writer)
			}
		}
	}

	private def processData(writer: SheetWriter): Try[String] = {
		try {
			val output_stream = this.tcp_client.getOutputStream()
			writer.send(output_stream)

			val reader = new SheetReader(this.tcp_client.getInputStream())

			val is_ok = reader.readBool().get
			if (!is_ok) throw PaperError.fromReader(reader)

			this.reconnect_attempts = 0
			reader.readString()
		} catch {
			case e: IOException => {
				if (!this.reconnect()) throw new PaperError(PaperErrorType.Disconnected)
				return this.processData(writer)
			}
		}
	}

	private def processHas(writer: SheetWriter): Try[Boolean] = {
		try {
			val output_stream = this.tcp_client.getOutputStream()
			writer.send(output_stream)

			val reader = new SheetReader(this.tcp_client.getInputStream())

			val is_ok = reader.readBool().get
			if (!is_ok) throw PaperError.fromReader(reader)

			this.reconnect_attempts = 0
			reader.readBool()
		} catch {
			case e: IOException => {
				if (!this.reconnect()) throw new PaperError(PaperErrorType.Disconnected)
				return this.processHas(writer)
			}
		}
	}

	private def processSize(writer: SheetWriter): Try[Long] = {
		try {
			val output_stream = this.tcp_client.getOutputStream()
			writer.send(output_stream)

			val reader = new SheetReader(this.tcp_client.getInputStream())

			val is_ok = reader.readBool().get
			if (!is_ok) throw PaperError.fromReader(reader)

			this.reconnect_attempts = 0
			reader.readU32()
		} catch {
			case e: IOException => {
				if (!this.reconnect()) throw new PaperError(PaperErrorType.Disconnected)
				return this.processSize(writer)
			}
		}
	}

	private def processStatus(writer: SheetWriter): Try[PaperStatus] = {
		try {
			val output_stream = this.tcp_client.getOutputStream()
			writer.send(output_stream)

			val reader = new SheetReader(this.tcp_client.getInputStream())

			val is_ok = reader.readBool().get
			if (!is_ok) throw PaperError.fromReader(reader)

			this.reconnect_attempts = 0

			val pid = reader.readU32().get

			val max_size = reader.readU64().get
			val used_size = reader.readU64().get
			val num_objects = reader.readU64().get

			val rss = reader.readU64().get
			val hwm = reader.readU64().get

			val total_gets = reader.readU64().get
			val total_sets = reader.readU64().get
			val total_dels = reader.readU64().get

			val miss_ratio = reader.readF64().get

			val num_policies = reader.readU32().get.toInt
			var policies = new Array[String](num_policies)

			for (i <- 0 to num_policies - 1) {
				policies(i) = reader.readString().get
			}

			val policy = reader.readString().get
			val is_auto_policy = reader.readBool().get

			val uptime = reader.readU64().get

			val paper_status = PaperStatus(
				pid,

				max_size,
				used_size,
				num_objects,

				rss,
				hwm,

				total_gets,
				total_sets,
				total_dels,

				miss_ratio,

				policies,
				policy,
				is_auto_policy,

				uptime,
			)

			return Success(paper_status)
		} catch {
			case e: IOException => {
				if (!this.reconnect()) throw new PaperError(PaperErrorType.Disconnected)
				return this.processStatus(writer)
			}
		}
	}

	private def reconnect(): Boolean = {
		this.reconnect_attempts += 1

		if (this.reconnect_attempts > MaxReconnectAttempts) {
			return false
		}

		try {
			this.tcp_client = new Socket(this.host, this.port)
			this.handshake()

			this.auth_token match {
				case Some(auth_token) => this.auth(auth_token)
				case None => {}
			}
		} catch {
			case e: IOException => return false
		}

		true
	}

	private def handshake(): Try[Unit] = Try {
		try {
			var reader = new SheetReader(this.tcp_client.getInputStream)

			val is_ok = reader.readBool().get
			if (!is_ok) throw PaperError.fromReader(reader)
		} catch {
			case e: IOException => throw new PaperError(PaperErrorType.ConnectionRefused)
		}
	}
}
