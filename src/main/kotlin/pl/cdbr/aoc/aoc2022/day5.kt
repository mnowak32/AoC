import java.io.File

fun main() {

    val (rawStack, moves) = File("resources/day5.txt").useLines { lines ->
        lines.partition { !it.startsWith("move") }
    }

    val vertStack = rawStack.dropLast(2)
    val numberOfStacks = vertStack.maxOfOrNull(String::length)?.let { (it + 3) / 4 } ?: 0
    println(vertStack)
    println(numberOfStacks)

    manipulateStack(numberOfStacks, vertStack, moves, ::doCrateMover9000)
    manipulateStack(numberOfStacks, vertStack, moves, ::doCrateMover9001)
}

typealias StackManipulator = (List<MutableList<Char>>, Int, Int, Int) -> Unit

fun manipulateStack(numberOfStacks: Int, vertStack: List<String>, moves: List<String>, manipulator: StackManipulator) {
    val stacks = buildList(numberOfStacks) {
        repeat(numberOfStacks) { stackNo ->
            add(vertStack.map { it[stackNo * 4 + 1] }.filter(Char::isLetter).reversed().toMutableList())
        }
    }

    println(stacks)

    moves.forEach { move ->
        val splat = move.split(" ")
        val count = splat[1].toInt()
        val from = splat[3].toInt() - 1
        val to = splat[5].toInt() - 1

        manipulator(stacks, from, to, count)
    }

    println(stacks)
    println(stacks.map(List<Char>::last).joinToString(""))

}

fun doCrateMover9000(stacks: List<MutableList<Char>>, from: Int, to: Int, count: Int) {
    val cargo = stacks[from].removeLast(count)
    stacks[to].addAll(cargo)
}

fun doCrateMover9001(stacks: List<MutableList<Char>>, from: Int, to: Int, count: Int) {
    val cargo = stacks[from].removeLast(count).reversed()
    stacks[to].addAll(cargo)
}


fun <T> MutableList<T>.removeLast(count: Int): List<T> {
    val removed = mutableListOf<T>()
    repeat(count) { removed.add(this.removeLast()) }
    return removed
}
