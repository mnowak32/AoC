package pl.cdbr.aoc.aoc2023

import java.io.File
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day8(filename: String) {
    private val map = parse(File(filename))

    data class DesertMap(val directions: List<Char>, val network: Map<String, Pair<String, String>>) {
        fun next(start: String, dir: Char): String {
            val node = network[start]
            return node?.let {
                if (dir == 'L') it.first
                else it.second
            } ?: start
        }

        fun countStepsBetween(start: String, ends: Set<String>): Int {
            var currentNode = start
            var counter = 0
            var dirCounter = 0
            while (!ends.contains(currentNode)) {
                currentNode = next(currentNode, directions[dirCounter])
                if (++dirCounter >= directions.size) {
                    dirCounter = 0
                }
                counter++
            }
            return counter
        }
    }

    private fun parse(input: File): DesertMap {
        val lines = input.readLines()
        val dirs = lines.first()
        val net = lines.drop(2).associate {
            //CDL = (JLS, CPK)
            val node = it.take(3)
            val left = it.substring(7, 10)
            val right = it.substring(12, 15)
            node to (left to right)
        }
        return DesertMap(dirs.toCharArray().toList(), net)
    }

    fun part1() {
        val steps = map.countStepsBetween("AAA", setOf("ZZZ"))
        println(steps)
    }

    fun part2() {
        val starts = map.network.keys.filter { it.endsWith('A') }
        val ends = map.network.keys.filter { it.endsWith('Z') }.toSet()
        val steps = starts.map { map.countStepsBetween(it, ends) }
        val totalSteps = leastCommonMultiplier(steps.map(Int::toLong).toLongArray())
        println(totalSteps)
    }

    private fun leastCommonMultiplier(x: LongArray): Long {
        val xm = x.clone()
        while(xm.notAllEqual()) {
            val lowestIdx = xm.indexOf(xm.min())
            xm[lowestIdx] += x[lowestIdx]
        }
        return xm.first()
    }

    private fun LongArray.notAllEqual() = this.distinct().size > 1
}


fun main() {
    measureTimeMillis {
        val day = Day8("resources/aoc2023/day8.txt")

        print("Part 1 solution is ")
        day.part1()
        print("Part 2 solution is ")
        //takes a long time, on my PC it ran for over 11 minutes
        day.part2()
    }.also {
        println("Elapsed time: ${it.toDuration(DurationUnit.MILLISECONDS)}")
    }
}
