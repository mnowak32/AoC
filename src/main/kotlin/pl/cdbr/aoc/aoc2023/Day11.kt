package pl.cdbr.aoc.aoc2023

import pl.cdbr.aoc.aoc2023.Day3.Point
import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KMutableProperty0
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day11(filename: String) {
    private lateinit var expandedX: List<Int>
    private lateinit var expandedY: List<Int>

    private val pairs = parse(File(filename))

    private fun parse(input: File): Set<Pair<Point, Point>> {
        val initialMap = input.readLines()
        expand(initialMap, ::expandedY)
        val flippedMap = initialMap.flip()
        expand(flippedMap, ::expandedX)

        val galaxies = initialMap
            .flatMapIndexed { y, l -> l.mapIndexed { x, c -> if (c == '#') Point(x, y) else null } }
            .filterNotNull().toSet()
        return galaxies.crossProduct()
    }

    private fun expand(initialMap: List<String>, listSetter: KMutableProperty0<List<Int>>) {
        listSetter.set(initialMap.mapIndexedNotNull { i, line ->
            if (!line.contains('#')) i
            else null
        })
    }

    fun part1() = printExpandedDistanceSum(1)

    fun part2() = printExpandedDistanceSum(1_000_000 - 1)

    private fun printExpandedDistanceSum(rate: Int) {
        println(pairs.sumOf { (p1, p2) -> p1.distanceTo(p2, rate) })
    }

    private fun Point.distanceTo(p: Point, expansionRate: Int): Long {
        val x1 = min(this.x, p.x)
        val x2 = max(this.x, p.x)
        val y1 = min(this.y, p.y)
        val y2 = max(this.y, p.y)
        val baseDist = x2 - x1 + y2 - y1
        val expansionX = expandedX.count { it in x1 until x2 } * expansionRate
        val expansionY = expandedY.count { it in y1 until y2 } * expansionRate
        return baseDist.toLong() + expansionX + expansionY
    }

}

private fun Set<Point>.crossProduct(): Set<Pair<Point, Point>> {
    return this.flatMapIndexed { i, first -> this.drop(i + 1).map { second -> first to second } }.toSet()
}

private fun List<String>.flip(): List<String> {
    return this.first().mapIndexed { i, _ -> this.map { it[i] }.joinToString("") }
}

fun main() {
    measureTimeMillis {
        val day = Day11("resources/aoc2023/day11.txt")

        print("Part 1 solution is ")
        day.part1()
        print("Part 2 solution is ")
        day.part2()
    }.also {
        println("Elapsed time: ${it.toDuration(DurationUnit.MILLISECONDS)}")
    }
}

