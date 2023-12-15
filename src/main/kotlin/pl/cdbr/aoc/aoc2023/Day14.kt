package pl.cdbr.aoc.aoc2023

import pl.cdbr.aoc.common.Dir
import pl.cdbr.aoc.common.Point
import java.io.File
import java.util.*
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day14(filename: String) {
    private val map = parse(File(filename))

    data class Platform(val size: Int, val stones: MutableMap<Point, StoneType>)
    enum class StoneType { ROUND, CUBE }
    private fun parse(input: File): Platform {
        val stones = mutableMapOf<Point, StoneType>()
        var count = 0
        return input.useLines { lines ->
            lines.forEachIndexed { y, s ->
                count++
                s.forEachIndexed { x, c ->
                    when (c) {
                        'O' -> stones[Point(x, y)] = StoneType.ROUND
                        '#' -> stones[Point(x, y)] = StoneType.CUBE
                    }
                }
            }
            Platform(count, stones)
        }

    }

    private fun moveStones(d: Dir) {
        (0 until map.size).forEach { coord ->
            val stonesInOrder = map.stones.filter { (p, _) -> d.coordSelector(p) == coord }.toSortedMap()
            val stones = if (d.isPositive()) { stonesInOrder.toSortedMap(Point::compareToReversed) } else { stonesInOrder.toSortedMap() }
            stones.filter { it.value == StoneType.ROUND }
                .forEach { (p, t) ->
                    val distanceToMove = if (!d.isPositive()) {
                        val prevStones = stones.keys.filter { d.moveSelector(it) < d.moveSelector(p) }
                        val prevStone = prevStones.maxByOrNull { d.moveSelector(it) }
                        if (prevStone == null) {
                            d.moveSelector(p)
                        } else {
                            d.moveSelector(p) - d.moveSelector(prevStone) - 1
                        }
                    } else {
                        val nextStones = stones.keys.filter { d.moveSelector(it) > d.moveSelector(p) }
                        val nextStone = nextStones.minByOrNull { d.moveSelector(it) }
                        if (nextStone == null) {
                            map.size - d.moveSelector(p) - 1
                        } else {
                            d.moveSelector(nextStone) - d.moveSelector(p) - 1
                        }
                    }
                    val newP = d.move(p, distanceToMove)
                    if (newP != p) {
                        stones.remove(p)
                        stones[newP] = t
                        map.stones.remove(p)
                        map.stones[newP] = t
                    }
                }
        }
    }

    private fun printPlatform() {
        (0 until map.size).forEach { y ->
            (0 until map.size).map { x ->
                val stone = map.stones[Point(x, y)]
                when (stone) {
                    null -> '.'
                    StoneType.ROUND -> 'O'
                    else -> '#'
                }
            }.toCharArray().let(::String).also(::println)
        }
    }

    fun part1() {
        val dir = Dir.N
        printPlatform()
        moveStones(dir)
        println()
        printPlatform()

        println(calculateNorthSupportLoad())
    }

    private fun calculateNorthSupportLoad(): Int {
        val dir = Dir.N
        return map.stones.filter { it.value == StoneType.ROUND }
            .map { (p, _) ->
                if (dir.isPositive()) {
                    dir.moveSelector(p) + 1
                } else {
                    map.size - dir.moveSelector(p)
                }
            }.sum()
    }

    private fun cycle() {
        Dir.values().forEach {
            moveStones(it)
        }
    }



    private fun hash(values: List<Int>): Int {
        return values.sumOf { v -> v - 100000 }
    }

    private fun modifyHash(hash: Int, add: Int, sub: Int): Int {
        return hash + add - sub
    }

    fun part2() {
        var count = 0
        var cycleFound = false
        var cycle = Pair(0, 0)
        val loadMemory = mutableListOf<Int>()
        var prevHashes = listOf<Int>()
        var prevMatching: Int? = 0
        val hashLength = 23
        val foundMatches = mutableListOf<Pair<Int, Int>>()
        while (!cycleFound) {
            loadMemory.add(0, calculateNorthSupportLoad())

            val newHashes = (1 .. hashLength).map { hash(loadMemory.take(it)) }
            val matching = newHashes.zip(prevHashes).find { (h1, h2) -> h1 == h2 }
            if (matching != null && matching.first == prevMatching) {
                val length = newHashes.indexOf(matching.first) + 1
                println("FOUND! : $matching CL $length")
                foundMatches.add(matching.first to length)
            }
            prevMatching = matching?.first
            prevHashes = newHashes
            cycle()
            count++

            foundMatches.groupingBy { it }.eachCount().maxByOrNull { (_, count) -> count }?.toPair()
                ?.let { (p, len) ->
                    if (len > MIN_CYCLE_REP_COUNT) {
                        cycleFound = true
                        cycle = p
                    }
                }
        }
    }

    companion object {
        const val MIN_CYCLE_REP_COUNT = 3
    }
}

fun main() {
    measureTimeMillis {
        val day = Day14("resources/aoc2023/day14.txt")

//        print("Part 1 solution is ")
//        day.part1()
//        print("Part 2 solution is ")
        day.part2()
    }.also {
        println("Elapsed time: ${it.toDuration(DurationUnit.MILLISECONDS)}")
    }
}
