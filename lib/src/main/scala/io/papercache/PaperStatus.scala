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
