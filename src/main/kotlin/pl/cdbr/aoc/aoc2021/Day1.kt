package pl.cdbr.aoc.aoc2021

import java.io.File
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day1(filename: String) {
    private val map = parse(File(filename))

    private fun parse(input: File): List<Int> {
        return input.useLines { lines -> lines.map(String::toInt).toList() }
    }

    fun part1() {
        countIncreases(map)
    }
    private fun countIncreases(list: List<Int>) {
        val firsts = list.dropLast(1)
        val seconds = list.drop(1)
        val increases = firsts.zip(seconds).count { (d1, d2) -> d1 < d2 }
        println(increases)
    }

    fun part2() {
        val firsts = map.dropLast(2)
        val seconds = map.drop(1).dropLast(1)
        val thirds = map.drop(2)
        val windows = firsts.zip(seconds).map { (d1, d2) -> d1 + d2 }.zip(thirds).map { (d12, d3) -> d12 + d3 }
        countIncreases(windows)
    }

}


fun main() {
    measureTimeMillis {
        val day = Day1("resources/aoc2021/day1.txt")

        print("Part 1 solution is ")
        day.part1()
        print("Part 2 solution is ")
        day.part2()
    }.also {
        println("Elapsed time: ${it.toDuration(DurationUnit.MILLISECONDS)}")
    }
}
