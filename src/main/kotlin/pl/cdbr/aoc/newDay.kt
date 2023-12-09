package pl.cdbr.aoc

import kotlin.io.path.*

fun mkFileContents(year: Int, day:Int) = """
package pl.cdbr.aoc.aoc$year

import java.io.File
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day$day(filename: String) {
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
        val day = Day$day("resources/aoc$year/day$day.txt")

        print("Part 1 solution is ")
        day.part1()
        print("Part 2 solution is ")
        day.part2()
    }.also {
        println("Elapsed time: ${'$'}{it.toDuration(DurationUnit.MILLISECONDS)}")
    }
}

"""

fun main() {
    val year = 2023
    val day = 9
    val targetDirName = "src/main/kotlin/pl/cdbr/aoc/aoc$year"
    val targetDir = Path(targetDirName)
    if (!targetDir.exists() || !targetDir.isDirectory()) {
        targetDir.deleteIfExists()
        targetDir.createDirectories()
    }

    val targetFileName = "Day$day.kt"
    val targetFile = targetDir.resolve(targetFileName)
    targetFile.createFile().toFile().writeText(mkFileContents(year, day))
}