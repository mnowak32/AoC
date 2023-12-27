package pl.cdbr.aoc.common

fun List<String>.flip(): List<String> {
    return this.first().mapIndexed { i, _ -> this.map { it[i] }.joinToString("") }
}

fun <E> Collection<E>.crossProduct(): Set<Pair<E, E>> {
    return this.flatMapIndexed { i, first -> this.drop(i + 1).map { second -> first to second } }.toSet()
}
