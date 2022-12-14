import java.io.File

class Day13(rawData: List<String>) {
    val packets: List<Pair<Packet, Packet>>

    init {
        packets = rawData.chunked(3).map { (p1, p2, _) -> Packet.of(p1) to Packet.of(p2) }
    }

    fun validPackets() = packets.mapIndexedNotNull { index, (f, s) ->
            val valid = f.root.validateOrder(s.root)
            println("$f vs $s -> $valid")
            if (valid == Element.ValidationResult.VALID) {
                index + 1
            } else {
                null
            }
        }

    fun packetsInOrder(): List<Packet> {
        val dividerPackets = listOf(divider1, divider2)
        val allPackets = (packets.flatMap { it.toList() } + dividerPackets).toMutableList()

        var switched = true
        while (switched) {
            switched = false
            for (i in 0 until allPackets.size - 1) {
                val p1 = allPackets[i]
                val p2 = allPackets[i + 1]
                if (p1.root.validateOrder(p2.root) == Element.ValidationResult.INVALID) {
                    allPackets[i] = p2
                    allPackets[i + 1] = p1
                    switched = true
                }
            }
        }
        return allPackets
    }

    sealed interface Element {

        fun validateOrder(second: Element): ValidationResult

        companion object {
            fun from(chars: MutableList<Char>): Element {
                val elems = mutableListOf<Element>()
                while (chars.isNotEmpty()) {
                    when (val c = chars.removeFirst()) {
                        '[' -> elems += from(chars)
                        ']' -> break
                        in '0'..'9' -> {
                            val digits: List<Int> = listOf(c - '0') + chars.takeWhile { it.isDigit() }.map { it - '0'}
                            repeat(digits.size - 1) { chars.removeFirst() }
                            elems += PInt(digits.reduce { acc, n -> acc * 10 + n })
                        }
                    }
                }
                return PList(elems)
            }
        }

        enum class ValidationResult {
            VALID, INVALID, PASS
        }

        data class PList(val elements: List<Element>) : Element {
            constructor(singleElem: Element) : this(listOf(singleElem))
            override fun validateOrder(secondElem: Element): ValidationResult {
                val fixedOther = if (secondElem is PInt) {
                    PList(secondElem)
                } else {
                    secondElem as PList
                }

                val firstElems = elements.toMutableList()
                val secondElems = fixedOther.elements.toMutableList()
                while(firstElems.isNotEmpty() && secondElems.isNotEmpty()) {
                    val first = firstElems.removeFirst()
                    val second = secondElems.removeFirst()
                    val result = first.validateOrder(second)
                    if (result != ValidationResult.PASS) {
                        return result
                    }
                }
                return if (firstElems.isEmpty()) {
                    if (secondElems.isEmpty()) {
                        ValidationResult.PASS
                    } else {
                        ValidationResult.VALID
                    }
                } else {
                    ValidationResult.INVALID
                }
            }

            override fun toString() = "[${elements.joinToString(",")}]"
        }

        data class PInt(val element: Int) : Element {
            override fun validateOrder(second: Element): ValidationResult {
                return if (second is PList) {
                    PList(this).validateOrder(second)
                } else {
                    val secondValue = (second as PInt).element
                    if (element < secondValue) {
                        ValidationResult.VALID
                    } else if (element > secondValue) {
                        ValidationResult.INVALID
                    } else {
                        ValidationResult.PASS
                    }
                }
            }

            override fun toString() = element.toString()
        }
    }
    data class Packet(val root: Element) {

        override fun toString() = root.toString()
        companion object {
            fun of(raw: String) = Packet(Element.from(raw.drop(1).dropLast(1).toMutableList()))
        }
    }

    companion object {
        val divider1 = Packet(Element.PList(Element.PList(Element.PInt(2))))
        val divider2 = Packet(Element.PList(Element.PList(Element.PInt(6))))
    }

}
fun main() {
    val packets = File("resources/day13.txt").readLines()
    val day13 = Day13(packets)

//    println(day13.packets)
    val valid = day13.validPackets()
    println("$valid, sum: ${valid.sum()}")

    val ordered = day13.packetsInOrder()
    println("---")
    ordered.forEach(::println)

    val d1 = ordered.indexOf(Day13.divider1) + 1
    val d2 = ordered.indexOf(Day13.divider2) + 1
    println("$d1 * $d2 = ${d1 * d2}")
}