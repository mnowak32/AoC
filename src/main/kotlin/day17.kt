import java.io.File

class Day17(val jets: String) {
    var jetIdx = 0
    private var rockIdx = 1
    val well = Well()
    val reports = mutableMapOf<Int, Int>()

    fun dropNextRock(reportsAt: List<Int>): Int {
        val rock = Rock.getRock(rockIdx)
        // add 3 rows to the well
        well.expandBy(7)
        well.throwRockAt(2, 3, rock)

        var moving = true
        while (moving) {
            val jet = jets[jetIdx++ % jets.length]
            well.jet(jet)
            moving = well.dropRock()
        }
        well.solidifyRock()
        well.gc(rockIdx)

        if (rockIdx in reportsAt) {
            val last = reports.values.lastOrNull() ?: 0
            println("Size at rock $rockIdx: ${well.size}, delta: ${well.size - last}")
            reports[rockIdx] = well.size
        }
        rockIdx++
        return well.size
    }

    class Well {
        private var rows = mutableListOf<String>()
        private var deletedRowsCounter = 0
        private var rockY = 0
        val size
            get() = rows.size + deletedRowsCounter

        fun gc(rockIdx: Int) {
            val deleted = deleteBlockedWell()
//            if (deleted > 0) println("$rockIdx\t$deleted")

            val emptyRowsAtBeginning = rows.takeWhile { it.isBlank() }.size
            repeat(emptyRowsAtBeginning) { rows.removeFirst() }
        }
        private fun deleteBlockedWell(): Int {
            val blockAt = rows.indexOf("#######")
            return if (blockAt > 0) {
                val rowsToRemove = rows.size - blockAt - 1
                deletedRowsCounter += rowsToRemove
                repeat(rowsToRemove) { rows.removeLast() }
                rowsToRemove
            } else
                0
        }

        fun expandBy(r: Int) {
            repeat(r) { rows.add(0, "       ") }
        }

        fun throwRockAt(x: Int, y: Int, r: Rock) {
            rockY = y
            r.shape.forEachIndexed { i, s -> rows[y - i] = rows[y - 1].replaceRange(x, x + s.length, s) }
        }

        fun jet(dir: Char) {
            val rowsWithRock = rows.take(rockY + 1).takeLast(5)
            val rockRows = rowsWithRock.count { it.contains('*') }
            val patternToSearch = if (dir == '<') " *" else "* "
            val rowsCanMove = rowsWithRock.count { it.contains(patternToSearch)}
            if (rowsCanMove == rockRows) { //all rock rows have spaces on the correct side
                val (search, replace) = if (dir == '<') {
                    LEFT_SEARCH to "$1 "
                } else {
                    RIGHT_SEARCH to " $1"
                }
                for (r in rockY - 3 .. rockY) {
                    rows[r] = rows[r].replace(search, replace)
                }
            }
        }

        fun print() {
            rows.forEach { println("|$it|") }
            println("+-------+\n")
        }

        fun dropRock(): Boolean {
            if (rockY == rows.size - 1) {
                return false
            }
            val rowsWithRock = rows.take(rockY + 2).takeLast(5)
            val rowsWithRockRotated = rowsWithRock.rotateRight()
            val rockRows = rowsWithRockRotated.count { it.contains('*') }
            val rowsCanMove = rowsWithRockRotated.count { it.contains(" *")}
            if (rowsCanMove != rockRows) {
                return false
            }
            for (r in rockY downTo rockY - 3) {
                val row = rows[r].toCharArray()
                val downRow = rows[r + 1].toCharArray()
                for (c in row.indices) {
                    if (row[c] == '*') {
                        downRow[c] = row[c]
                        row[c] = ' '
                    }
                }
                rows[r] = String(row)
                rows[r + 1] = String(downRow)
            }
            rockY++
            return true
        }

        private fun List<String>.rotateRight(): List<String> {
            val longest = this.maxBy(String::length)
            val reversed = this.reversed()
            return (longest.indices).map { col ->
                String(reversed.map { it[col] }.toCharArray())
            }
        }

        fun solidifyRock() {
            for (r in rockY downTo rockY - 3) {
                rows[r] = rows[r].replace('*', '#')
            }
        }

        companion object {
            private const val TRIM_SIZE = 1000
            private val LEFT_SEARCH = " (\\*+)".toRegex()
            private val RIGHT_SEARCH = "(\\*+) ".toRegex()
        }
    }   }
    enum class Rock(val shape: List<String>) {
        MINUS(listOf(
            "****"
        )),
        PLUS(listOf(
            " * ",
            "***",
            " * "
        )),
        EL(listOf(
            "  *",
            "  *",
            "***"
        ).reversed()),
        PIPE(listOf(
            "*",
            "*",
            "*",
            "*"
        )),
        SQUARE(listOf(
            "**",
            "**"
        ));

        companion object {
            private val rockTypesCount = values().size
            fun getRock(n: Int): Rock {
                return values()[n % rockTypesCount]
            }
        }
    }



fun main() {
    val jets = File("resources/day17.txt").readLines().first()
    val d17 = Day17(jets)
    var linecount = 0

    val offset = 190
    val cycle = 1755
    val tail = 1216

    val reportsAt = listOf(offset, offset+cycle, offset+2*cycle, offset+2*cycle+tail)
    val repeats = reportsAt.last()
    println("will repeat $repeats times")
    repeat(repeats) { linecount = d17.dropNextRock(reportsAt) }
    println(linecount)
}
