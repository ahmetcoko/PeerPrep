package com.example.peerprep.data.util

object CsvParser {
    fun parseLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val char = line[i]
            when {
                char == '"' -> {
                    inQuotes = !inQuotes
                    i++
                }
                char == ',' && !inQuotes -> {
                    result.add(current.toString())
                    current = StringBuilder()
                    i++
                }
                else -> {
                    current.append(char)
                    i++
                }
            }
        }
        result.add(current.toString())
        return result
    }
}

