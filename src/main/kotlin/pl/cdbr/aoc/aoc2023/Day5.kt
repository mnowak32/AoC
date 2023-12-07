package pl.cdbr.aoc.aoc2023

import java.io.File
import kotlin.system.measureTimeMillis
import kotlin.time.*

class Day5(filename: String) {
    private val input = parse(File(filename))

    data class Almanac(
        val seeds: List<Long>,
        val toSoil: AlmanacMap,
        val toFertilizer: AlmanacMap,
        val toWater: AlmanacMap,
        val toLight: AlmanacMap,
        val toTemperature: AlmanacMap,
        val toHumidity: AlmanacMap,
        val toLocation: AlmanacMap
    ) {
        private val seedRanges = seeds.zipWithNext(::IdRange)

        private fun seedToLocation(seed: Long): Long {
            return seed
                .let(toSoil::map)
                .let(toFertilizer::map)
                .let(toWater::map)
                .let(toLight::map)
                .let(toTemperature::map)
                .let(toHumidity::map)
                .let(toLocation::map)
        }

        private fun seedToLocation(seed: IdRange): List<IdRange> {
            return seed
                .let(toSoil::map)
                .flatMap(toFertilizer::map)
                .flatMap(toWater::map)
                .flatMap(toLight::map)
                .flatMap(toTemperature::map)
                .flatMap(toHumidity::map)
                .flatMap(toLocation::map)
        }

        fun allLocations() = seeds.associateWith(this::seedToLocation)
        fun allLocationRanges() = seedRanges.flatMap(this::seedToLocation).compress()
    }

    data class IdRange(val from: Long, val size: Long) {

        val to = from + size - 1
        fun contains(num: Long) = (num in from..to)
        fun isEmpty() = size == 0L

        override fun toString(): String {
            return "IdRange(from=$from, size=$size, to=$to)"
        }

        companion object {
            val EMPTY = IdRange(0, 0)
        }
    }

    data class AlmanacMap(val ranges: List<AlmanacRange>) {
        fun map(src: Long): Long {
            val mappedValue = ranges.firstNotNullOfOrNull { range -> range.map(src) }
            return mappedValue ?: src
        }

        fun map(src: IdRange): List<IdRange> {
            var remaining = src
            val mapped = mutableListOf<IdRange>()
            ranges.forEach { range ->
                if (!remaining.isEmpty()) {
                    val (rMapped, rRest) = range.map(remaining)
                    mapped.add(rMapped)
                    remaining = rRest
                }
            }
            return (mapped.toList() + remaining).compress()
        }
    }

    data class AlmanacRange(val destStart: Long, val srcStart: Long, val size: Long) {
        val srcRange = IdRange(srcStart, size)

        fun map(src: Long): Long? {
            val delta = src - srcStart
            return if (delta < 0 || delta >= size) {
                null
            } else {
                destStart + delta
            }
        }

        // returns a pair of: mapped values range; remaining unmapped range
        fun map(range: IdRange): Pair<IdRange, IdRange> {
            return if (!srcRange.contains(range.from) && !srcRange.contains(range.to)) {
                IdRange.EMPTY to range
            } else if (srcRange.contains(range.from)) {
                val delta = range.from - srcStart
                val sizeLeft = size - delta
                IdRange(destStart + delta, sizeLeft) to if (range.size > sizeLeft) {
                    IdRange(range.from + sizeLeft, range.size - sizeLeft)
                } else { IdRange.EMPTY }
            } else {
                val delta = srcStart - range.from
                val sizeLeft = range.size - delta
                IdRange(destStart, sizeLeft) to if (range.size > sizeLeft) {
                    IdRange(range.from, range.size - sizeLeft)
                } else { IdRange.EMPTY }
            }
        }
    }

    private fun parse(input: File): Almanac {
        return input.useLines { linesSeq ->
            val lines = linesSeq.toList()
            val seeds = lines.first().drop(7).split("\\s+".toRegex()).map(String::toLong)
            val maps = lines.drop(2).partByNewline()
            Almanac(seeds,
                maps[0].toAlmanacMap(),
                maps[1].toAlmanacMap(),
                maps[2].toAlmanacMap(),
                maps[3].toAlmanacMap(),
                maps[4].toAlmanacMap(),
                maps[5].toAlmanacMap(),
                maps[6].toAlmanacMap()
            )
        }
    }

    fun part1() {
        val locs = input.allLocations()
//        println(locs)
        println("Part 1 answer: ${locs.values.min()}")
    }

    fun part2() {
        val ranges = input.allLocationRanges()
//        println(ranges)
        println("Part 2 answer: ${ranges.first().from}")
    }
}

private fun List<Day5.IdRange>.compress(): List<Day5.IdRange> {
    return if (this.isEmpty()) {
        emptyList()
    } else {
        val sorted = this.filterNot(Day5.IdRange::isEmpty).sortedBy { it.from }
        var current = sorted.first()
        val result = mutableListOf<Day5.IdRange>()
        sorted.drop(1).forEach { next ->
            if (current.contains(next.from)) {
                current = Day5.IdRange(current.from, (next.size + (next.from - current.from)))
            } else {
                result.add(current)
                current = next
            }
        }
        result.add(current)
        result.toList()
    }

}

private fun List<String>.partByNewline(): List<List<String>> {
    val output = mutableListOf<List<String>>()
    var input = this.toList()
    while(input.isNotEmpty()) {
        val part = input.takeWhile(String::isNotBlank)
        output.add(part)
        input = input.drop(part.size + 1)
    }
    return output.toList()
}

private fun List<String>.toAlmanacMap(): Day5.AlmanacMap {
    val ranges = this.drop(1)
    return Day5.AlmanacMap(ranges.map { r ->
        val (dst, src, len) = r.split("\\s+".toRegex()).map(String::toLong)
        Day5.AlmanacRange(dst, src, len)
    })
}

fun main() {
    val runningTime = measureTimeMillis {
        val day = Day5("resources/aoc2023/day5.txt")
        day.part1()
        day.part2()
    }
    println("Elapsed time: ${runningTime.toDuration(DurationUnit.MILLISECONDS)}")
}
