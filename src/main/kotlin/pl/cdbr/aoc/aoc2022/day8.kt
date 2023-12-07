import java.io.File

fun main() {
    val forestRows = File("resources/day8.txt").readLines()
    val cols = forestRows.maxOfOrNull(String::length)!!

    val forestCols = buildList {
        repeat(cols) { colNo ->
            val col = forestRows.map { it[colNo] }.joinToString("")
            add(col)
        }
    }

    val visible = forestRows.flatMapIndexed { y, row ->
        row.mapIndexed { x, _ ->
            val col = forestCols[x]
            treeVisible(row, x) || treeVisible(col, y)
        }
    }

    println(visible.count { it })

    val scores = forestRows.flatMapIndexed { y, row ->
        row.mapIndexed { x, _ ->
            val col = forestCols[x]
            treeScore(row, x) * treeScore(col, y)
        }
    }

    println(scores.max())

}

fun treeVisible(row: String, x: Int): Boolean {
    val before = row.take(x)
    val after = row.drop(x + 1)
    val tree = row[x]
    return tree.allBelow(before) || tree.allBelow(after)
}

fun Char.allBelow(others: String) = others.isEmpty() || others.toList().all { it < this }

fun treeScore(row: String, x: Int): Int {
    val before = row.take(x)
    val after = row.drop(x + 1)
    val tree = row[x]
    return tree.score(before.reversed()) * tree.score(after)
}

fun Char.score(others: String) : Int {
    return if (others.isEmpty()) {
        0
    } else {
        val firstIdx = others.indexOfFirst { it >= this }
        if (firstIdx == -1) {
            others.length
        } else {
            firstIdx + 1
        }
    }
}

