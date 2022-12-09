import java.io.File

fun main() {

    val dataStream = File("resources/day6.txt").useLines { it.first() }.asSequence()

    val startPacketMarker = dataStream.findFirstUnique(4)
    val startMessageMarker = dataStream.findFirstUnique(14)

    println("packet start '${startPacketMarker.first}' @ ${startPacketMarker.second}")
    println("message start '${startMessageMarker.first}' @ ${startMessageMarker.second}")

}

fun Sequence<Char>.findFirstUnique(length: Int): Pair<String, Int> {
    val buffer = mutableListOf<Char>()
    this.forEachIndexed { idx, data ->
        if (buffer.size == length) {
            buffer.removeFirst()
        }
        buffer.add(data)
        if (buffer.toSet().size == length) {
            return buffer.joinToString("") to idx + 1
        }
    }
    return "" to 0
}
