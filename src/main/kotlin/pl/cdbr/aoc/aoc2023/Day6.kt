package pl.cdbr.aoc.aoc2023

import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day6(filename: String) {
    val records = parse(File(filename))
    val singleRace = parse2(File(filename))

    private fun countWaysToBeatSmart(time: Number, dist: Number): Int {
        val t = time.toLong()
        val d = dist.toLong()
        val delta = t * t - 4 * d
        return if (delta < 0) {
            0
        } else if (delta == 0L) {
            1
        } else {
            val sqrtD = sqrt(delta.toDouble())
            val k1 = (-t - sqrtD) / 2
            val k2 = (-t + sqrtD) / 2
            val from = min(k1, k2).toInt()
            val to = max(k1, k2).toInt()
            to - from
        }
    }

    private fun parse(input: File): List<Pair<Int, Int>> {
        val lines = input.readLines()
        val times = lines[0].drop(10).split("\\s+".toRegex()).filter(String::isNotBlank).map(String::toInt)
        val lengths = lines[1].drop(10).split("\\s+".toRegex()).filter(String::isNotBlank).map(String::toInt)
        return times.zip(lengths)
    }

    private fun parse2(input: File): Pair<Long, Long> {
        val lines = input.readLines()
        val time = lines[0].drop(10).split("\\s+".toRegex()).filter(String::isNotBlank).joinToString("").toLong()
        val length = lines[1].drop(10).split("\\s+".toRegex()).filter(String::isNotBlank).joinToString("").toLong()
        return time to length
    }

    fun part1() {
        val totalScore2 = records.map { (t, d) -> countWaysToBeatSmart(t, d) }.also(::println).reduce(Int::times)
        println(totalScore2)
    }

    fun part2() {
        val (t, d) = singleRace
        val ways2 = countWaysToBeatSmart(t, d)
        println(ways2)
    }
}

fun main() {
    val runningTime = measureTimeMillis {
        val day = Day6("resources/aoc2023/day6.txt")

        println(day.records)
        day.part1()
        println(day.singleRace)
        day.part2()
    }
    println("Elapsed time: ${runningTime.toDuration(DurationUnit.MILLISECONDS)}")
}
