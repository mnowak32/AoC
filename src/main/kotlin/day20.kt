import java.io.File

class Day20 {

    fun decrypt(message: List<Long>, turns: Int = 1): List<Long> {
        val positional = message.mapIndexed { idx, num -> MessageElem(num, idx) }.toMutableList()
        val msgSize = message.size - 1
        repeat(turns) {
            for (i in message.indices) {
                val position = positional.indexOfFirst { it.origPos == i }
                if (position == -1) {
                    break
                }
                val elem = positional.removeAt(position)
                val tempPos = (position + elem.num).mod(msgSize)
                val newPos = if (tempPos == 0) msgSize else tempPos
                positional.add(newPos, elem)
            }
        }

        return positional.map(MessageElem::num).toList()
    }

    fun getCoord(decrypted: List<Long>): Long {
        val zeroPos = decrypted.indexOfFirst { it == 0L }
        val mod = decrypted.size
        val p1 = (zeroPos + 1000) % mod
        val p2 = (zeroPos + 2000) % mod
        val p3 = (zeroPos + 3000) % mod
        return decrypted[p1] + decrypted[p2] + decrypted[p3]
    }

    fun applyKey(sample: List<Long>, key: Long): List<Long> = sample.map { it * key }

    data class MessageElem(val num: Long, val origPos: Int)
}

fun main() {
//    val sample = listOf(
//        1, 2, -3, 3, -2, 0, 4
//    )
    val sample = File("resources/day20.txt").readLines().map { it.toLong() }
    val d20 = Day20()
    val decrypted = d20.decrypt(sample, 1)
    println(decrypted.size)
    println(d20.getCoord(decrypted))

    val key = 811589153L
    val realMessage = d20.applyKey(sample, key)
    val realDecrypted = d20.decrypt(realMessage, 10)
    println(d20.getCoord(realDecrypted))
}