package com.worker8.androiddevnews.common.util

class DurationParser {
    companion object {
        // input "00:12:34" --> "12mins"
        // input "03:12:34" --> "3h 12mins"
        // input "12:34" --> "12mins"
        fun parse(duration: String): String {
            val durationList = duration.split(":")
            var result = ""
            when (durationList.size) {
                2 -> {
                    result += if (durationList[0].toInt() != 0) {
                        durationList[0].toInt().toString() + "m "
                    } else {
                        ""
                    }
                    result += if (durationList[1].toInt() != 0) {
                        durationList[1].toInt().toString() + "s"
                    } else {
                        if (result.isNotEmpty()) {
                            ""
                        } else {
                            "0s"
                        }
                    }
                }
                3 -> {
                    result += if (durationList[0].toInt() != 0) {
                        durationList[0].toInt().toString() + "h "
                    } else {
                        ""
                    }
                    result += if (durationList[1].toInt() != 0) {
                        durationList[1].toInt().toString() + "m "
                    } else {
                        ""
                    }
                    result += if (durationList[2].toInt() != 0) {
                        durationList[2].toInt().toString() + "s"
                    } else {
                        if (result.isNotEmpty()) {
                            ""
                        } else {
                            "0s"
                        }
                    }
                }
            }
            return result.trim()
        }
    }
}