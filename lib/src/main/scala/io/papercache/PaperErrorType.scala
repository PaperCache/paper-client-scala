/*
 * Copyright (c) Kia Shakiba
 *
 * This source code is licensed under the GNU AGPLv3 license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.papercache

sealed trait PaperErrorType

object PaperErrorType {
	case object Internal extends PaperErrorType

	case object InvalidAddress extends PaperErrorType

	case object ConnectionRefused extends PaperErrorType
	case object MaxConnectionsExceeded extends PaperErrorType
	case object Unauthorized extends PaperErrorType
	case object Disconnected extends PaperErrorType

	case object KeyNotFound extends PaperErrorType

	case object ZeroValueSize extends PaperErrorType
	case object ExceedingValueSize extends PaperErrorType

	case object ZeroCacheSize extends PaperErrorType

	case object UnconfiguredPolicy extends PaperErrorType
	case object InvalidPolicy extends PaperErrorType
}
