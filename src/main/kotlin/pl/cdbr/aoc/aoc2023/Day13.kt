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
        val lines = input.readLines()
        return lines.splitIntoParts()
    }

    private tailrec fun List<String>.splitIntoParts(begin: List<List<String>> = emptyList()): List<List<String>> {
        val (head, tail) = this.partWhile(String::isNotBlank)
        val toReturn = begin + listOf(head)

        return if (tail.isEmpty()) {
            toReturn
        } else {
            tail.splitIntoParts(toReturn)
        }
    }

    private fun List<String>.partWhile(predicate: (String) -> Boolean): Pair<List<String>, List<String>> {
        val head = this.takeWhile(predicate)
        val tail = this.drop(head.size + 1)
        return head to tail
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

    private fun matchesWithSmudge(one: List<String>, other: List<String>): Boolean {
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
        println(sumOfMirrors(::matchesWithSmudge))
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

