import java.io.File

class Day22(val map: List<String>, val path: String) {
    var mutableMap = map.map(String::toCharArray)

    fun cleanMap() {
        mutableMap = map.map(String::toCharArray)
    }

    fun charAt(x: Int, y: Int): Char {
      return if (y < 0 || y >= map.size) {
          ' '
      } else {
          val row = map[y]
          if (x < 0 || x >= row.length) {
              ' '
          } else {
              row[x]
          }
      }
    }

    fun followPath(wrappingFunc: WrappingFunc): Int {
        var y = 0
        var x = wrapAround(0, y, Dir.E).first
        var facing = Dir.E
        var remainingPath = path

        while (remainingPath.isNotBlank()) {
            val cmd = if (remainingPath[0].isDigit()) {
                Move(remainingPath.takeWhile { it.isDigit() })
            } else {
                Rotate(remainingPath.take(1))
            }
            remainingPath = remainingPath.drop(cmd.length)

            val (newX, newY, newDir) = cmd.execute(x, y, facing, wrappingFunc)
            x = newX
            y = newY
            facing = newDir
        }

        println("final coords: $x, $y, $facing")

        return 1000 * (y + 1) + 4 * (x + 1) + facing.ordinal
    }

    interface Cmd {
        fun execute(x: Int, y: Int, dir: Dir, wrappingFunc: WrappingFunc): Triple<Int, Int, Dir>
        val length: Int
    }

    inner class Move(private val inp: String) : Cmd {
        override fun execute(x: Int, y: Int, dir: Dir, wrappingFunc: WrappingFunc): Triple<Int, Int, Dir> {
            val steps = inp.toInt()
            var newX = x
            var newY = y
            var lastDir = dir
            repeat(steps) {
                var targetX = newX + lastDir.dx
                var targetY = newY + lastDir.dy
                val targetBlock = charAt(targetX, targetY)
                val finalTarget = if (targetBlock == ' ') {
                    val (wrappedX, wrappedY, targetDir) = wrappingFunc(targetX, targetY, lastDir)
                    targetX = wrappedX
                    targetY = wrappedY
                    val newTarget = charAt(targetX, targetY)
                    if (newTarget == '.') {
                        lastDir = targetDir
                    }
                    newTarget
                } else {
                    targetBlock
                }
                if (finalTarget == '.') { //free space, normal move
                    newX = targetX
                    newY = targetY
                }
                mutableMap[newY][newX] = lastDir.symbol
            }

            return Triple(newX, newY, lastDir)
        }

        override val length: Int
            get() = inp.length
    }

    inner class Rotate(private val inp: String) : Cmd {
        override fun execute(x: Int, y: Int, dir: Dir, wrappingFunc: WrappingFunc): Triple<Int, Int, Dir> {
            val newPosition = if (inp == "R") {
                Triple(x, y, dir.right())
            } else {
                Triple(x, y, dir.left())
            }
            mutableMap[y][x] = newPosition.third.symbol
            return newPosition
        }

        override val length: Int
            get() = 1

    }

    val wrapAround: WrappingFunc = { x: Int, y: Int, dir: Dir ->
        val slice = if (dir.horizontal) {
            map[y]
        } else {
            verticalSlice(x)
        }

        val newCoord = if (dir.fromStart) {
            slice.indexOfFirst { it != ' ' }
        } else {
            slice.indexOfLast { it != ' ' }
        }

        if (dir.horizontal) {
            Triple(newCoord, y, dir)
        } else {
            Triple(x, newCoord, dir)
        }
    }


    data class CubeEdge(val xr: IntRange, val yr: IntRange, val fromDir: Dir, val transform: WrappingFunc) {
        fun contains(x: Int, y: Int, d: Dir) = xr.contains(x) && yr.contains(y) && fromDir == d
    }
    private val sampleEdges = listOf(
  /*0*/ CubeEdge(7..7, 0..3, Dir.W)    { _, y, _ -> Triple(4 + y, 4, Dir.S) }, // 1 -> 3
        CubeEdge(8..11, -1..-1, Dir.N) { x, _, _ -> Triple(11 - x, 4, Dir.S) }, // 1 -> 2
        CubeEdge(12..12, 0..3, Dir.E)  { _, y, _ -> Triple(15, 11 - y, Dir.W) }, // 1 -> 6
        CubeEdge(-1..-1, 4..7, Dir.W)  { _, y, _ -> Triple(19 - y, 11, Dir.N) }, // 2 -> 6
        CubeEdge(0..3, 3..3, Dir.N)    { x, _, _ -> Triple(11 - x, 0, Dir.S) }, // 2 -> 1
  /*5*/ CubeEdge(4..7, 3..3, Dir.N)    { x, _, _ -> Triple(8, x - 4, Dir.E) }, // 3 -> 1
        CubeEdge(12..12, 4..7, Dir.E)  { _, y, _ -> Triple(19 - y, 8, Dir.S) }, // 4 -> 6
        CubeEdge(0..3, 8..8, Dir.S)    { x, _, _ -> Triple(11 - x, 11, Dir.N) }, // 2 -> 5
        CubeEdge(4..7, 8..8, Dir.S)    { x, _, _ -> Triple(8, 15 - x, Dir.E) }, // 3 -> 5
        CubeEdge(12..15, 7..7, Dir.N)  { x, _, _ -> Triple(11, 19 - x, Dir.W) }, // 6 -> 4
 /*10*/ CubeEdge(7..7, 8..11, Dir.W)   { _, y, _ -> Triple(15 - y, 8, Dir.N) }, // 5 -> 3
        CubeEdge(16..16, 8..11, Dir.E) { _, y, _ -> Triple(11, 11 - y, Dir.W) }, // 6 -> 1
        CubeEdge(8..11, 12..12, Dir.S) { x, _, _ -> Triple(11 - x, 7, Dir.N) }, // 5 -> 2
        CubeEdge(12..15, 12..12, Dir.S){ x, _, _ -> Triple(0, 19 - x, Dir.E) }  // 6 -> 2
    )
    private val identityTransform: WrappingFunc = { x, y, d -> Triple(x, y, d) }

    val wrapCubedSample: WrappingFunc = { x: Int, y: Int, dir: Dir ->
        val edgeTransform = sampleEdges.find { it.contains(x, y, dir) }?.transform ?: identityTransform
        edgeTransform.invoke(x, y, dir)
    }

    private fun verticalSlice(x: Int): String {
        return String(map.map { if (x < it.length) it[x] else ' ' }.toCharArray())
    }

    fun printMap() {
        mutableMap.forEach { println(String(it)) }
    }

    enum class Dir(val dx: Int, val dy: Int, val horizontal: Boolean, val fromStart: Boolean, val symbol: Char) {
        E(1, 0, true, true, '>'),
        S(0, 1, false, true, 'v'),
        W(-1, 0, true, false, '<'),
        N(0, -1, false, false, '^');

        fun right() = ofIndex(ordinal + 1)
        fun left() = ofIndex(ordinal - 1)
        companion object {
            private fun ofIndex(idx: Int): Dir {
                val truIdx = idx.mod(values().size)
                return values()[truIdx]
            }
        }
    }
}

typealias WrappingFunc = (Int, Int, Day22.Dir) -> Triple<Int, Int, Day22.Dir>

fun main() {
    val inFile = File("resources/day22sample.txt").readLines()
    val map = inFile.takeWhile(String::isNotBlank)
    val path = inFile.last(String::isNotBlank)
    val d22 = Day22(map, path)

    println(d22.followPath(d22.wrapAround))
    d22.printMap()

    d22.cleanMap()
    println(d22.followPath(d22.wrapCubedSample))
    d22.printMap()
}

