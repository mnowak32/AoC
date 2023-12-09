
package pl.cdbr.aoc.aoc2023

import java.io.File
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day9(filename: String) {
    private val values = parse(File(filename))
    
    private fun parse(input: File): List<List<Int>> {
        return input.useLines { lines ->
            lines.map { line -> line.split("\\s+".toRegex()).map(String::toInt) }.toList()
        }
    }

    private fun List<Int>.deltas(): List<Int> {
        val firsts = this.dropLast(1)
        val seconds = this.drop(1)
        return firsts.zip(seconds).map { (f, s) -> s - f }
    }

    private fun List<Int>.allZeros() = this.all { it == 0 }

    private fun List<Int>.extendForward(): Int {
        val stack = extensions()
        return stack.map(List<Int>::last).sum()
    }

    private fun List<Int>.extendBack(): Int {
        val stack = extensions()
        return stack.map(List<Int>::first).mapIndexed { index, i -> if (index % 2 == 1) -i else i }.sum()
    }

    private fun List<Int>.extensions(): List<List<Int>> {
        val stack = mutableListOf(this)
        var current = stack.last()
        do {
            current = current.deltas().also { stack.add(it) }
        } while (!current.allZeros())
        return stack.toList()
    }

    fun part1() {
        values.map { it.extendForward() }.sum().also(::println)
    }

    fun part2() {
        values.map { it.extendBack() }.sum().also(::println)
    }

}

fun main() {
    measureTimeMillis {
        val day = Day9("resources/aoc2023/day9.txt")

        print("Part 1 solution is ")
        day.part1()
        print("Part 2 solution is ")
        day.part2()
    }.also {
        println("Elapsed time: ${it.toDuration(DurationUnit.MILLISECONDS)}")
    }
}

