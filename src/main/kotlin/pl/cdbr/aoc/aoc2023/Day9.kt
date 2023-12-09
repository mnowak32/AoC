
package pl.cdbr.aoc.aoc2023

import java.io.File
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day9(filename: String) {
    private val map = parse(File(filename))
    
    private fun parse(input: File) {
        
    }

    fun part1() {
    }

    fun part2() {
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

