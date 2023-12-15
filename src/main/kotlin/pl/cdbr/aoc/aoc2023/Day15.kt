package pl.cdbr.aoc.aoc2023

import java.io.File
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day15(filename: String) {
    private val initSeq = parse(File(filename))

    data class Lens(val label: String, val focal: Int) {
        val box by lazy { label.hash() }
    }
    
    private fun parse(input: File): List<String> {
        return input.readLines().flatMap { it.split(",") }
    }

    fun part1() {
        println(initSeq.sumOf(String::hash))
    }

    fun part2() {
        val boxes = Array<MutableList<Lens>>(256) { mutableListOf() }
        initSeq.forEach { instr ->
            val label = instr.takeWhile(Char::isLetter)
            val op = instr.drop(label.length).first()
            val box = boxes[label.hash()]
            if (op == '-') {
                box.removeIf { it.label == label }
            } else {
                val focal = instr.drop(label.length + 1).toInt()
                val already = box.indexOfFirst { it.label == label }
                val newLens = Lens(label, focal)
                if (already > -1) {
                    box[already] = newLens
                } else {
                    box.add(newLens)
                }
            }
        }

        val totalFocal = boxes.flatMapIndexed { boxNo, lenses ->
            lenses.mapIndexed { lenNo, lens ->
                (boxNo + 1) * (lenNo + 1) * lens.focal
            }
        }.sum()
        println(totalFocal)
    }

}
private fun String.hash() = this.fold(0) { h, c -> ((h + c.code) * 17) % 256 }

fun main() {
    measureTimeMillis {
        val day = Day15("resources/aoc2023/day15.txt")

        print("Part 1 solution is ")
        day.part1()
        print("Part 2 solution is ")
        day.part2()
    }.also {
        println("Elapsed time: ${it.toDuration(DurationUnit.MILLISECONDS)}")
    }
}
