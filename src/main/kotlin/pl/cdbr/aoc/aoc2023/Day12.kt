package pl.cdbr.aoc.aoc2023

import java.io.File
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day12(filename: String) {
    private val maps = parse(File(filename))
    private val matchesCache = mutableMapOf<Pair<String,List<Int>>, Long>()

    private fun parse(input: File): List<Pair<String, List<Int>>> {
        return input.useLines { lines ->
            lines.map { line ->
                val (map, checksum) = line.split(" ")
                val cSums = checksum.split(",").map(String::toInt)
                map to cSums
            }.toList()
        }
    }

    private fun String.matchesWith(other: String) = this.zip(other).all { (c1, c2) -> (c2 == '?' || c1 == c2) }

    private fun mapFor(dots: Int, hashes: Int) = ".".repeat(dots) + "#".repeat(hashes) + "."

    private fun countPossibleMatches(pattern: String, counts: List<Int>): Long {
        val key = Pair(pattern, counts)
        return matchesCache.getOrPut(key) {
            if (counts.isEmpty()) {
                val allDots = ".".repeat(pattern.length)
                if (allDots.matchesWith(pattern)) {
                    1L
                } else {
                    0L
                }
            } else {
                val remainingHashes = counts.sum()
                val availDotSpace = pattern.length - remainingHashes
                val nextHashes = counts.first()
                (0..availDotSpace).sumOf { dots ->
                    val mapFragment = mapFor(dots, nextHashes)
                    if (mapFragment.matchesWith("$pattern.")) {
                        val newPattern = pattern.drop(mapFragment.length)
                        val newCounts = counts.drop(1)
                        countPossibleMatches(newPattern, newCounts)
                    } else {
                        0
                    }
                }
            }
        }
    }
    fun part1() {
        maps.sumOf { (pattern, counts) ->
            countPossibleMatches(pattern, counts)
        }.let(::println)
    }

    fun part2() {
        maps.sumOf { (pattern, counts) ->
            val pattern5 = listOf(pattern, pattern, pattern, pattern, pattern).joinToString("?")
            val counts5 = listOf(counts, counts, counts, counts, counts).flatten()
            countPossibleMatches(pattern5, counts5)
        }.let(::println)
    }

    fun cacheSize() = matchesCache.size
}

fun main() {
    val day = Day12("resources/aoc2023/day12.txt")
    measureTimeMillis {
        print("Part 1 solution is ")
        day.part1()
        print("Part 2 solution is ")
        day.part2()
    }.also {
        println("Elapsed time: ${it.toDuration(DurationUnit.MILLISECONDS)}")
    }
    println("Final cache size: ${day.cacheSize()}")
}

