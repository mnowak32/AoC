import java.io.File

class Day21 {

    fun buildTree(apes: List<String>): Monkey {
        val apeMap = apes.associate {
            val (name, value) = it.split(": ")
            name to value
        }


        val builtApes = apeMap.mapNotNull { ape ->
            val numericValue = ape.value.toLongOrNull()
            numericValue?.let {
                ape.key to Monkey(ape.key, number = it)
            }
        }.toMap().toMutableMap()

        val calculatingApeRecognizer = "([a-z]+) ([-+*/]) ([a-z]+)".toRegex()
        val calculatingApes = apeMap.mapNotNull { ape ->
            val match = calculatingApeRecognizer.matchEntire(ape.value)
            if (match != null) {
                val (ape1, oper, ape2) = match.destructured
                ape.key to Triple(ape1, ape2, oper.first())
            } else {
                null
            }
        }.toMutableList()

        while (calculatingApes.isNotEmpty()) {
            val ape = calculatingApes.first { (_, params) ->
                builtApes.containsKey(params.first) && builtApes.containsKey(params.second)
            }
            calculatingApes.remove(ape)
            val (name, params) = ape
            builtApes[name] = Monkey(name, dependOn = builtApes[params.first]!! to builtApes[params.second]!!, oper = params.third)
        }

        println(builtApes["humn"])

        return builtApes["root"]!!
    }

    fun equalize(root: Monkey, variable: String): Long {
        val branchWithVariable = root.whereCanIFind(variable)
        if (root.dependOn == null) {
            return 0L
        }
        println(branchWithVariable?.name)
        val equalizeValue = if (branchWithVariable == root.dependOn.first) {
            val desiredValue = root.dependOn.second.value
            root.dependOn.first.equalize(desiredValue, variable)
        } else {
            val desiredValue = root.dependOn.first.value
            root.dependOn.second.equalize(desiredValue, variable)
        }

        return equalizeValue
    }

    data class Monkey(val name: String, val number: Long = 0, val dependOn: Pair<Monkey, Monkey>? = null, val oper: Char? = null) {
        val value: Long
            get() = if (dependOn == null) {
                number
            } else {
                val op1 = dependOn.first.value
                val op2 = dependOn.second.value
                when (oper) {
                    '+' -> op1 + op2
                    '-' -> op1 - op2
                    '*' -> op1 * op2
                    '/' -> op1 / op2
                    else -> number
                }
            }

        fun whereCanIFind(ape: String): Monkey? {
            return if (name == ape) {
                this
            } else if (dependOn == null) {
                null
            } else {
                if (dependOn.first.whereCanIFind(ape) != null) {
                    dependOn.first
                } else if (dependOn.second.whereCanIFind(ape) != null) {
                    dependOn.second
                } else {
                    null
                }
            }
        }

        fun equalize(desiredValue: Long, variableName: String): Long {
            return if (dependOn == null) {
                0L
            } else {
                val unknownSide = whereCanIFind(variableName)
                if (unknownSide == dependOn.first) {
                    val secondValue = dependOn.second.value
                    val newDesiredValue = when (oper) {
                        '+' -> desiredValue - secondValue
                        '-' -> desiredValue + secondValue
                        '*' -> desiredValue / secondValue
                        '/' -> desiredValue * secondValue
                        else -> number
                    }
                    if (dependOn.first.name == variableName) {
                        newDesiredValue
                    } else {
                        dependOn.first.equalize(newDesiredValue, variableName)
                    }
                } else if (unknownSide == dependOn.second) {
                    val firstValue = dependOn.first.value
                    val newDesiredValue = when (oper) {
                        '+' -> desiredValue - firstValue
                        '-' -> firstValue - desiredValue
                        '*' -> desiredValue / firstValue
                        '/' -> firstValue / desiredValue
                        else -> number
                    }
                    if (dependOn.second.name == variableName) {
                        newDesiredValue
                    } else {
                        dependOn.second.equalize(newDesiredValue, variableName)
                    }
                } else {
                    0L
                }
            }
        }
    }


}

fun main() {
    val d21 = Day21()
    val input = File("resources/day21.txt").readLines()
    val root = d21.buildTree(input)
//    println(root)
//    println(root.value)

    println(d21.equalize(root, "humn"))
}

