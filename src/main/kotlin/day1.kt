import java.io.File

// https://adventofcode.com/2022/day/1
// input: https://adventofcode.com/2022/day/1/input

fun main() {
    val elves = mutableListOf<Int>()
    var elfCalories = 0
    File("resources/day1.txt").forEachLine {
        if (it.isBlank()) {
            elves.add(elfCalories)
            elfCalories = 0
        } else {
            elfCalories += it.toInt()
        }
    }
    println(elves.max())
    println(elves.sorted().takeLast(3).sum())
}

