package pl.cdbr.aoc.common

fun List<String>.flip(): List<String> {
    return this.first().mapIndexed { i, _ -> this.map { it[i] }.joinToString("") }
}
