package pl.cdbr.aoc.aoc2023

import java.io.File

class Day1(filename: String) {

    private val file = File(filename)

    private fun extractNumber(str: String): Int {
        return str.first(Char::isDigit).digitToInt() * 10 + str.last(Char::isDigit).digitToInt()
    }

    private fun extractSpelledNumber(str: String): Int {
        val substrings = str.indices.map { str.drop(it) }
        val first = substrings.first(digitsRegex::matches)
        val last = substrings.last(digitsRegex::matches)

        return first.toDigit() * 10 + last.toDigit()
    }

    private fun String.toDigit(): Int {
        return if (this[0].isDigit()) {
            this[0].digitToInt()
        } else {
            spelledDigits.indexOfFirst { this.startsWith(it) }
        }
    }

    private fun File.toNumbers(extractor: (String) -> Int): List<Int> {
        return this.useLines {
            it.map(extractor).toList()
        }
    }

    fun part1() {
        val numbers = file.toNumbers(this::extractNumber)
        println(numbers.sum())
    }

    fun part2() {
        val numbers = file.toNumbers(this::extractSpelledNumber)
        println(numbers.sum())
    }

    companion object {
        val spelledDigits = listOf("zero", "one", "two", "three","four", "five", "six", "seven", "eight", "nine")
        val digitsRegex = "^([0-9]|${spelledDigits.joinToString("|")}).*".toRegex()
    }
}


fun main() {
    val day = Day1("resources/aoc2023/day1.txt")
    print("Part 1 solution is ")
    day.part1()
    print("Part 2 solution is ")
    day.part2()
}