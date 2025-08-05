/*
 * Copyright (c) Kia Shakiba
 *
 * This source code is licensed under the GNU AGPLv3 license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.papercache

case class PaperStatus(
	id: Long,

	max_size: Long,
	used_size: Long,
	num_objects: Long,

	rss: Long,
	hwm: Long,

	total_gets: Long,
	total_sets: Long,
	total_dels: Long,

	miss_ratio: Double,

	policies: Array[String],
	policy: String,
	is_auto_policy: Boolean,

	uptime: Long,
)
