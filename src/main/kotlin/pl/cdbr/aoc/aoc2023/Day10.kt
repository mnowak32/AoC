
package pl.cdbr.aoc.aoc2023

import pl.cdbr.aoc.aoc2023.Day3.Point
import java.io.File
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day10(filename: String) {
    val map = parse(File(filename))


    private fun parse(input: File): List<List<Char>> {
        return input.readLines().map(String::toList)
    }

    private fun charAt(p: Point) = map.getOrNull(p.y)?.let { line -> line.getOrNull(p.x) } ?: '.'

    private fun pipeEnds(p: Point): Set<Point> {
        val shape = charAt(p)
        return when (shape) {
            '-' -> setOf(Point(p.x - 1, p.y), Point(p.x + 1, p.y))
            '|' -> setOf(Point(p.x, p.y - 1), Point(p.x, p.y + 1))
            'L' -> setOf(Point(p.x, p.y - 1), Point(p.x + 1, p.y))
            'J' -> setOf(Point(p.x, p.y - 1), Point(p.x - 1, p.y))
            '7' -> setOf(Point(p.x - 1, p.y), Point(p.x, p.y + 1))
            'F' -> setOf(Point(p.x + 1, p.y), Point(p.x, p.y + 1))
            'S' -> setOf(p.surroundings().find { s -> pipeEnds(s).contains(p) }!!)
            else -> emptySet()
        }
    }

    fun part1() {
        val startY = map.indexOfFirst { it.contains('S') }
        val startX = map[startY].indexOf('S')
        val start = Point(startX, startY)
        val pipe = mutableListOf<Point>()
        var current = pipeEnds(start).first()
        var prev = start
        do {
            pipe.add(current)
            val next = (pipeEnds(current) - prev).first()
            prev = current
            current = next
        } while (current != start)

        println((pipe.size + 1) / 2)
    }

    fun part2() {
    }

}

fun main() {
    measureTimeMillis {
        val day = Day10("resources/aoc2023/day10sample2.txt")

        print("Part 1 solution is ")
        day.part1()
        print("Part 2 solution is ")
        day.part2()
    }.also {
        println("Elapsed time: ${it.toDuration(DurationUnit.MILLISECONDS)}")
    }
}

