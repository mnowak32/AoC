import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

class Day15(inData: List<String>) {
    private val cfgRegex = "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)".toRegex()
    private val sensors: List<Sensor>
    init {
        sensors = inData.mapNotNull(cfgRegex::matchEntire).map { result ->
            val (sx, sy, bx, by) = result.destructured
            Sensor(sx.toInt(), sy.toInt(), bx.toInt(), by.toInt())
        }
    }

    fun countScannedSpaceInRow(y: Int): Int {
        val minX = sensors.minOfOrNull(Sensor::minX)!!
        val maxX = sensors.maxOfOrNull(Sensor::maxX)!!
        return (minX .. maxX).filter { x ->
            sensors.all { it.notBeacon(x, y) }
        }.count { x ->
            sensors.any { it.inRange(x, y) }
        }
    }

    fun findTuningFreqInRange(range: Int): Long {
        val wholeRow = (0 .. range)
        return (0..range).firstNotNullOf { y ->
            val inRanges = sensors.mapNotNull { it.rangeAt(y) }.combine()
            val inRange = inRanges.first()
            if (inRanges.size == 1 && inRange.fullyContains(wholeRow)) {
                null
            } else {
                val toSubtract = inRanges.reversed().combine()
                val remaining = wholeRow.subtractAll(toSubtract)
                if (remaining.count() == 1) {
                    val x = remaining.first
                    x * 4_000_000L + y
                } else {
                    null
                }
            }
        }
    }

    private fun List<IntRange>.combine(): List<IntRange> {
        var first = this.first()
        val rest = this.drop(1).toMutableList()
        var joined = true
        while (joined && rest.isNotEmpty()) {
            joined = false
            for (r in rest) {
                if (first.intersects(r)) {
                    first = min(first.first, r.first) .. max(first.last, r.last)
                    rest.remove(r)
                    joined = true
                    break
                }
            }
        }

        return listOf(first) + rest
    }

    private fun IntRange.subtractAll(all: List<IntRange>): IntRange {
        var first = this
        val rest = all.toMutableList()
        var joined = true
        while (joined && rest.isNotEmpty()) {
            joined = false
            rest.find { first.intersects(it) }?.let {
                rest.remove(it)
                joined = true
                first = if (first.contains(it.first)) {
                    first.first until it.first
                } else {
                    it.last + 1..first.last
                }
            }
        }

        return first
    }

    data class Sensor(val sx: Int, val sy: Int, val bx: Int, val by: Int) {
        private val beaconDistance = distanceTo(bx, by)
        val minX = sx - beaconDistance
        val maxX = sx + beaconDistance
        private val rangeMap = buildMap {
            for (y in -beaconDistance..beaconDistance) {
                put(sy + y, (minX + y.absoluteValue .. maxX - y.absoluteValue))
            }
        }
        private fun distanceTo(x: Int, y: Int) = (sx - x).absoluteValue + (sy - y).absoluteValue
        fun inRange(x: Int, y: Int) = distanceTo(x, y) <= beaconDistance
        fun notBeacon(x: Int, y: Int) = !(x == bx && y == by)
        fun rangeAt(y: Int) = rangeMap[y]
    }
}

enum class Day15Data(val file: String, val countRow: Int, val distressRange: Int) {
    SAMPLE("day15sample.txt", 10, 20),
    FULL("day15.txt", 2_000_000, 4_000_000)
}

fun main() {
//    val dataSet = Day15Data.FULL
    val dataSet = Day15Data.SAMPLE
    val day15 = Day15(File("resources/${dataSet.file}").readLines())

    println("part 1: ${day15.countScannedSpaceInRow(dataSet.countRow)}")
    println("part 2: ${day15.findTuningFreqInRange(dataSet.distressRange)}")
}