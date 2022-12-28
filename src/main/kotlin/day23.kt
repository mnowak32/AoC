import java.io.File

class Day23(input: List<String>) {
    private val elves: List<Elf>
    init {
        val grove = input.map(String::toCharArray)
        elves = currentElvesList(grove)
    }

    private var scanningDir = Dir.N

    private fun currentElvesList(grove: List<CharArray>): List<Elf> {
        val newElvesList = mutableListOf<Elf>()
        for(y in grove.indices) {
            for (x in grove[y].indices) {
                if (grove[y][x] == '#') {
                    newElvesList += Elf(x, y)
                }
            }
        }
        return newElvesList
    }

    private fun firstHalf(): Boolean {
        return elves.map { e ->
            val needToMove = Dir.values().any { anyElfThere(e.x + it.dx, e.y + it.dy) }
            if (needToMove) {
                var d = scanningDir
                var chosenDir: Dir? = null
                do {
                    val isDirFree = !d.scanningDirs().any { anyElfThere(e.x + it.dx, e.y + it.dy) }
                    if (isDirFree) {
                        chosenDir = d
                        break
                    }
                    d = d.next()
                } while (d != scanningDir)
                if (chosenDir != null) {
                    e.wantToMove = true
                    e.chosenX = e.x + chosenDir.dx
                    e.chosenY = e.y + chosenDir.dy
                } else {
                    e.wantToMove = false
                }
            } else {
                e.wantToMove = false
            }
            needToMove
        }.any { it }
    }

    private fun secondHalf() {
        val movingElves = elves.filter(Elf::wantToMove)
        val busyTargets = movingElves.groupingBy { it.chosenX to it.chosenY }.eachCount().filterValues { it > 1 }.keys
        val elvesThatCanReallyMove = movingElves.filter { e ->
            val target = e.chosenX to e.chosenY
            target !in busyTargets
        }
        elvesThatCanReallyMove.forEach { e ->
            e.x = e.chosenX
            e.y = e.chosenY
        }
    }

    private fun updateScanningDir() {
        scanningDir = scanningDir.next()
    }

    fun round(): Boolean {
        val anyoneWantToMove = firstHalf()
        if (anyoneWantToMove)
            secondHalf()
        updateScanningDir()
        return anyoneWantToMove
    }

    private fun anyElfThere(x: Int, y: Int) = elves.any { it.x == x && it.y == y}

    fun print() {
        val grove = bounding()
        for(y in grove.y1 .. grove.y2) {
            val chars = CharArray(grove.xSize) { x -> if (anyElfThere(grove.x1 + x, y)) '#' else '.' }
            println(String(chars))
        }
    }

    fun groveSummary(): Int {
        val grove = bounding()
        val totalArea = grove.xSize * grove.ySize
        val elvesCount = elves.size
        return totalArea - elvesCount
    }

    private fun bounding(): Rect {
        val minX = elves.minBy(Elf::x).x
        val maxX = elves.maxBy(Elf::x).x
        val minY = elves.minBy(Elf::y).y
        val maxY = elves.maxBy(Elf::y).y
        return Rect(minX, minY, maxX, maxY)
    }

    fun waitForIdle(): Int {
        var counter = 1
        while (round()) counter++
        return counter
    }

    data class Elf(
        var x: Int,
        var y: Int,
        var wantToMove: Boolean = false,
        var chosenX: Int = 0,
        var chosenY: Int = 0
    )

    data class Rect(val x1: Int, val y1: Int, val x2: Int, val y2: Int) {
        val xSize = x2 - x1 + 1
        val ySize = y2 - y1 + 1
    }

    enum class Dir(val dx: Int, val dy: Int) {
        N(0, -1), NE(1, -1), E(1, 0), SE(1, 1),
        S(0, 1), SW(-1, 1), W(-1, 0), NW(-1, -1);

        fun scanningDirs(): List<Dir> = listOf (get(this.ordinal - 1), this, get(this.ordinal + 1))
        fun next(): Dir = getMain(mainDirs.indexOf(this) + 1)

        companion object {
            val mainDirs = listOf(N, S, W, E)
            fun get(idx: Int) = values()[idx.mod(values().size)]
            fun getMain(idx: Int) = mainDirs[idx.mod(mainDirs.size)]
        }
    }
}

fun main() {
    val input = File("resources/day23.txt").readLines()
    val d23 = Day23(input)
    repeat(10) {
        d23.round()
    }
    println("part 1: ${d23.groveSummary()} empty spaces after 10 rounds")

    val roundsTillNoMovement = 10 + d23.waitForIdle()
    println("part 2: $roundsTillNoMovement rounds before no further movement")
}