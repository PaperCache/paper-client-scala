/*
 * Copyright (c) Kia Shakiba
 *
 * This source code is licensed under the GNU AGPLv3 license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.papercache

import io.papercache.PaperErrorType

class PaperError(val error_type: PaperErrorType) extends Exception

object PaperError {
	def fromReader(reader: SheetReader): PaperError = {
		try {
			val code = reader.readU8().get

			if (code == 0) {
				val cache_code = reader.readU8().get
				return PaperError.fromCacheCode(cache_code)
			}

			PaperError.fromCode(code)
		} catch {
			case e: Exception => new PaperError(PaperErrorType.Internal)
		}
	}

	def fromCode(code: Byte): PaperError = code match {
		case 2 => return new PaperError(PaperErrorType.MaxConnectionsExceeded)
		case 3 => return new PaperError(PaperErrorType.Unauthorized)
		case _ => return new PaperError(PaperErrorType.Internal)
	}

	def fromCacheCode(code: Byte): PaperError = code match {
		case 1 => return new PaperError(PaperErrorType.KeyNotFound)

		case 2 => return new PaperError(PaperErrorType.ZeroValueSize)
		case 3 => return new PaperError(PaperErrorType.ExceedingValueSize)

		case 4 => return new PaperError(PaperErrorType.ZeroCacheSize)

		case 5 => return new PaperError(PaperErrorType.UnconfiguredPolicy)
		case 6 => return new PaperError(PaperErrorType.InvalidPolicy)

		case _ => return new PaperError(PaperErrorType.Internal)
	}
}
