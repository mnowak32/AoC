import java.io.File

data class Monkey(
    val items: MutableList<Long>,
    val oper: (Long) -> Long,
    val divBy: Long,
    val ifTrue: Int,
    val ifFalse: Int
) {
    var inspectCount: Long = 0L
    private fun inspect(i: Long, division: Long, modulo: Long): Pair<Int, Long> {
        val newWorry = (oper(i) / division) % modulo
        val target = if (newWorry % divBy == 0L) ifTrue else ifFalse
        inspectCount++
        return target to newWorry
    }

    fun inspectAll(division: Long, modulo: Long) = items.map{ inspect(it, division, modulo) }
}
fun main() {
    println("part 1\n")
    process(20, 3)
    println("\n\npart 2\n")
    process(10000)
}

fun process(rounds: Int, division: Long = 1L) {
    val monkeys = File("resources/day11.txt").readLines().chunked(7, ::toMonkey).toMap()
    val globalModulus = monkeys.values.map(Monkey::divBy).reduce(Long::times)

    println("Modulus: $globalModulus")

    println("At beginning:")
    monkeys.forEach{ (id, monkey) ->
        println("  Monkey $id: ${monkey.items.joinToString(", ")}")
    }

    for (r in 1 .. rounds) {
        monkeys.values.forEach { monkey ->
            val newItems = monkey.inspectAll(division, globalModulus)
                .groupBy({ it.first }, { it.second })
            monkey.items.clear()
            newItems.forEach { (id, items) ->
                monkeys[id]!!.items.addAll(items)
            }
        }

        if (r == 1 || r == 20 || r % 1000 == 0) {
            println("After round $r:")
            monkeys.forEach { (id, monkey) ->
                println("Monkey $id inspected items ${monkey.inspectCount} times")
            }
        }
    }

    val monkeyBusiness = monkeys.values.map(Monkey::inspectCount)
        .sortedDescending()
        .take(2).reduce(Long::times)
    println("Monkey business: $monkeyBusiness")
}

fun toMonkey(cfg: List<String>): Pair<Int, Monkey> {
    val id = cfg[0].substringAfter(" ").dropLast(1).toInt()
    val items = cfg[1].substringAfter(": ").split(", ").map(String::toLong)
    val operArg = cfg[2].substringAfter("new = old ").split(" ")
    val oper: (Long) -> Long = if (operArg[1] == "old") {
        when (operArg[0]) {
            "+" -> { i -> i + i }
            "*" -> { i -> i * i }
            else -> { i -> i }
        }
    } else {
        val arg = operArg[1].toLong()
        when (operArg[0]) {
            "+" -> { i -> i + arg }
            "*" -> { i -> i * arg }
            else -> { i -> i }
        }
    }
    val divBy = cfg[3].substringAfter(" by ").toLong()
    val ifTrue = cfg[4].substringAfter("to monkey ").toInt()
    val ifFalse = cfg[5].substringAfter("to monkey ").toInt()

    return id to Monkey(items.toMutableList(), oper, divBy, ifTrue, ifFalse)
}