package pl.cdbr.aoc.aoc2023

import pl.cdbr.aoc.common.flip
import java.io.File
import kotlin.math.min
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day13(filename: String) {
    private val map = parse(File(filename))
    
    private fun parse(input: File): List<List<String>> {
        return input.useLines { lines ->
            val result = mutableListOf<List<String>>()
            val current = mutableListOf<String>()
            lines.forEach { line ->
                if (line.isBlank() && current.isNotEmpty()) {
                    result.add(current.toList())
                    current.clear()
                } else {
                    current.add(line)
                }
            }
            if (current.isNotEmpty()) {
                result.add(current.toList())
            }
            result.toList()
        }
    }

    private fun List<String>.findMirrorBetweenLines(matchFunction: (List<String>, List<String>) -> Boolean): Int {
        return (1 until this.size).find { l ->
            val normal = this.drop(l)
            val flipped = this.take(l).reversed()
            matchFunction(normal, flipped)
        } ?: 0
    }

    private fun matchesPartially(one: List<String>, other: List<String>): Boolean {
        val sizeToCompare = min(one.size, other.size)
        return one.take(sizeToCompare) == other.take(sizeToCompare)
    }

    private fun matchesWIthSmudge(one: List<String>, other: List<String>): Boolean {
        val sizeToCompare = min(one.size, other.size)
        val diffs = one.take(sizeToCompare).zip(other.take(sizeToCompare)).sumOf { (s1, s2) -> s1.countDifferences(s2) }
        return (diffs == 1)
    }

    private fun String.countDifferences(other: String): Int {
        return this.zip(other).count { (c1, c2) -> c1 != c2 }
    }

    private fun sumOfMirrors(matchFunction: (List<String>, List<String>) -> Boolean): Int {
        return map.sumOf { m ->
            m.findMirrorBetweenLines(matchFunction) * 100 + m.flip().findMirrorBetweenLines(matchFunction)
        }
    }

    fun part1() {
        println(sumOfMirrors(::matchesPartially))
    }

    fun part2() {
        println(sumOfMirrors(::matchesWIthSmudge))
    }

}

fun main() {
    measureTimeMillis {
        val day = Day13("resources/aoc2023/day13.txt")

        print("Part 1 solution is ")
        day.part1()
        print("Part 2 solution is ")
        day.part2()
    }.also {
        println("Elapsed time: ${it.toDuration(DurationUnit.MILLISECONDS)}")
    }
}

