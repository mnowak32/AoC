package pl.cdbr.aoc.aoc2023

import pl.cdbr.aoc.common.Point
import java.io.File

class Day3(filename: String) {
    private val input = parse(File(filename))

    data class Schematics(val symbols: Map<Point, Char>, val numbers: Map<Point, Int>) {
        private fun Point.parts(): List<Int> {
            return this.surroundings().mapNotNull(numbers::get).distinct()
        }

        fun partNumbers(): List<Int> {
            return symbols.flatMap { (p, _) ->
                p.parts()
            }
        }

        fun allGears(): List<Pair<Int, Int>> {
            return symbols
                .filter { (_, s) -> s == '*' }
                .map { (p, _) -> p.parts() }
                .filter { it.size == 2 }
                .map { it.zipWithNext().single() }
        }
    }

    private fun parse(file: File): Schematics {
        return file.useLines { lines ->
            val symbols = mutableMapOf<Point, Char>()
            val numbers = mutableMapOf<Point, Int>()

            lines.forEachIndexed { y, line ->
                line.forEachIndexed { x, c ->
                    if (c.isDigit()) {
                        val number = line.grabNumberAround(x)
                        numbers[Point(x, y)] = number
                    } else if (c != '.') {
                        symbols[Point(x, y)] = c
                    }
                }
            }

            Schematics(symbols.toMap(), numbers.toMap())
        }
    }

    fun part1() {
        println(input.partNumbers())
        println(input.partNumbers().sum())
    }

    fun part2() {
        println(input.allGears())
        println(input.allGears().sumOf { (g1, g2) -> g1 * g2 })
    }


}
fun String.grabNumberAround(x: Int): Int {
    val from = this.take(x).indexOfLast { !it.isDigit() } + 1
    val to = x + (this.drop(x) + '.').indexOfFirst { !it.isDigit() }

    return try {
        this.substring(from, to).toInt()
    } catch (e: Exception) {
        println(this.take(x))
        println(this.drop(x))
        println("Exception for x: $x, $from .. $to\n$this\n$e")
        throw e
    }
}
fun main() {
    val day = Day3("resources/aoc2023/day3.txt")
//    day.part1()
    day.part2()
}