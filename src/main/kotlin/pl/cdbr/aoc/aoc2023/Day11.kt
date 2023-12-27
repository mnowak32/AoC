package pl.cdbr.aoc.aoc2023

import pl.cdbr.aoc.common.Point
import pl.cdbr.aoc.common.crossProduct
import pl.cdbr.aoc.common.flip
import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day11(filename: String) {
    private val spaceMap = parse(File(filename))
    private val expandedX by lazy {
        expand(spaceMap.flip())
    }
    private val expandedY by lazy {
        expand(spaceMap)
    }
    private val pairs by lazy {
        parsePairs(spaceMap)
    }

    private fun parse(input: File) = input.readLines()
    private fun parsePairs(initialMap: List<String>): Set<Pair<Point, Point>> {
        val galaxies = initialMap
            .flatMapIndexed { y, l -> l.mapIndexed { x, c -> if (c == '#') Point(x, y) else null } }
            .filterNotNull().toSet()
        return galaxies.crossProduct()
    }

    private fun expand(initialMap: List<String>): List<Int> {
        return initialMap.indices.filter {
            !initialMap[it].contains('#')
        }
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

