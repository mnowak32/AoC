import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.sign

enum class Direction(val dx: Int, val dy: Int) {
    R(1, 0),
    D(0, -1),
    L(-1, 0),
    U(0, 1)
}
data class Coord(val x: Int, val y: Int) {
    fun move(dir: Direction) = Coord(x + dir.dx, y + dir.dy)
    fun chase(head: Coord): Coord {
        val dx = head.x - x
        val dy = head.y - y
        val dxAbs = dx.absoluteValue
        val dyAbs = dy.absoluteValue
        val unitX = dx.sign
        val unitY = dy.sign
        return if (dxAbs < 2 && dyAbs < 2) {
            this
        } else {
            this.copy(x = x + unitX, y = y + unitY)
        }
    }
}

object Rope {
    private val startCoord = Coord(0, 0)

    val tailHistory = mutableSetOf(startCoord)
    var head = startCoord
        set(value) {
            field = value
            tail = tail.chase(value)
        }
    var tail = startCoord
        set(value) {
            field = value
            tailHistory.add(value)
        }
}

object LongRope {
    private val startCoord = Coord(0, 0)

    val tailHistory = mutableSetOf(startCoord)
    val rope = mutableListOf<Coord>()
    init {
        repeat(10) { rope.add(startCoord) }
    }

    var head
        get() = rope[0]
        set(value) {
            rope[0] = value
            (1..9).forEach {
                rope[it] = rope[it].chase(rope[it - 1])
            }
            tailHistory.add(rope[9])
        }
}
fun main() {

    File("resources/day9.txt").forEachLine { cmd ->
        val (dirCmd, length) = cmd.split(" ")
        val dir = Direction.valueOf(dirCmd)
        repeat(length.toInt()) {
            Rope.head = Rope.head.move(dir)
            LongRope.head = LongRope.head.move(dir)
        }
    }

    println(Rope.tailHistory.size)
    println(LongRope.tailHistory.size)
}