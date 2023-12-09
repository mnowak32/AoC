
package pl.cdbr.aoc.aoc2021

import java.io.File
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day2(filename: String) {
    private val dirs = parse(File(filename))
    
    private fun parse(input: File) = input.readLines()

    private fun parsePart1(): Int {
        val horiz = mutableListOf<Int>()
        val vert = mutableListOf<Int>()
        dirs.forEach { line ->
            val (cmd, amount) = line.split(" ")
            when (cmd.first()) {
                'f' -> horiz.add(amount.toInt())
                'd' -> vert.add(amount.toInt())
                'u' -> vert.add(-amount.toInt())
            }
        }
        return horiz.sum() * vert.sum()
    }

    private fun parsePart2(): Int {
        var horizPos = 0
        var vertPos = 0
        var aim = 0
        dirs.forEach { line ->
            val (cmd, amount) = line.split(" ")
            when (cmd.first()) {
                'f' -> {
                    val len = amount.toInt()
                    horizPos += len
                    vertPos += len * aim
                }
                'd' -> aim += amount.toInt()
                'u' -> aim -= amount.toInt()
            }
        }
        return horizPos * vertPos
    }

    fun part1() {
        println(parsePart1())
    }

    fun part2() {
        println(parsePart2())
    }

}

fun main() {
    measureTimeMillis {
        val day = Day2("resources/aoc2021/day2.txt")

        print("Part 1 solution is ")
        day.part1()
        print("Part 2 solution is ")
        day.part2()
    }.also {
        println("Elapsed time: ${it.toDuration(DurationUnit.MILLISECONDS)}")
    }
}

