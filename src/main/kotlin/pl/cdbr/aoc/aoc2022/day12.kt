import java.io.File
import kotlin.math.absoluteValue


class TerrainMap(private val map: List<String>) {
    val height = map.size
    val width = map.maxOfOrNull(String::length)!!
    val start = findPoint("S")
    val end = findPoint("E")

    override fun toString() = "Map[width: $width, height: $height, start: $start. end: $end]"

    fun findPoint(s: String): Coord {
        val y = map.indexOfFirst { it.contains(s) }
        val x = map[y].indexOf(s)
        return Coord(x, y)
    }

    fun heightAt(point: Coord) = when (val symbolAt = map[point.y][point.x]) {
        'S' -> 'a'
        'E' -> 'z'
        else -> symbolAt
    } - 'a'

    // manhattan distance
    fun distanceToEnd(point: Coord) = (end.x - point.x).absoluteValue + (end.y - point.y).absoluteValue
//    private fun distanceToEnd(point: Coord) = hypot(end.x.toDouble() - point.x, end.y.toDouble() - point.y).roundToInt()

    fun aStar(
        begin: Coord,
        endCondition: (Coord) -> Boolean,
        heuristics: (Coord) -> Int = ::distanceToEnd,
        heightCondition: (Int, Int) -> Boolean = defaultHeightCondition
    ): PathTo? {
        val openSet = mutableSetOf(begin)
        val visited = mutableMapOf(begin to PathTo(begin, 0, heuristics(begin), null))

        while (openSet.isNotEmpty()) {
            val current = openSet.minBy { visited[it]?.f ?: Int.MAX_VALUE }
            openSet.remove(current)
            val currentPath = visited[current] ?: break
            if (endCondition(current)) {
                return visited[current]
            }

            val neighbours = current.neighboursFrom(heightCondition)
            neighbours.forEach { neighbour ->
                val prev = visited[neighbour]
                val newG = currentPath.g + 1
                if (prev != null) {
                    if (newG < prev.g) {
                        prev.g = newG
                        prev.from = currentPath
                    }
                } else {
                    val newPath = PathTo(neighbour, newG, heuristics(neighbour), currentPath)
                    openSet.add(neighbour)
                    visited[neighbour] = newPath
                }
            }
        }
        return null
    }

    data class PathTo(val c: Coord, var g: Int, val h: Int, var from: PathTo?) {
        val f
            get() = g + h

        fun tail(): List<Coord> {
            return if (from != null) {
                from!!.tail() + c
            } else {
                listOf(c)
            }
        }
    }

    inner class Coord(val x: Int, val y: Int) {

        fun inDir(d: Dir): Coord? {
            val newX = x + d.dx
            val newY = y + d.dy
            return if (newX in 0 until width && newY in 0 until height) {
                Coord(newX, newY)
            } else {
                null
            }
        }
        fun neighboursFrom(heightCondition: (Int, Int) -> Boolean): List<Coord> {
            val thisHeight = heightAt(this)
            return Dir.values().filter { dir ->
                this.inDir(dir)?.let {
                    val otherHeight = heightAt(it)
                    heightCondition(thisHeight, otherHeight)
                } ?: false
            }.mapNotNull(::inDir)
        }

        override fun toString() = "Coord[$x, $y]"
        override fun equals(other: Any?) = if (other is Coord) {
                x == other.x && y == other.y
            } else {
                super.equals(other)
        }

        override fun hashCode() = 1013 * x + y
    }

    enum class Dir(val dx: Int, val dy: Int, val s: Char) {
        R(1, 0, '>'),
        D(0, 1, 'v'),
        L(-1, 0, '<'),
        U(0, -1, '^')
    }

    companion object {
        val defaultHeightCondition: (Int, Int) -> Boolean = { thisHeight, otherHeight -> otherHeight - 1 <= thisHeight }
    }
}
fun main() {
    val rawMap = File("resources/day12.txt").readLines()
    val map = TerrainMap(rawMap)
    println(map)
    val pathMap = buildList { repeat(map.height) { add(CharArray(map.width) { '.' }) } }

//    val foundPath = map.aStar(map.start, { it == map.end })
//    val path = foundPath?.tail() ?: emptyList()
//
//    path.forEach { p ->
//        pathMap[p.y][p.x] = '*'
//    }
//    pathMap.forEach(::println)
//    println(path.size - 1)

    val foundPath2 = map.aStar(map.end, { map.heightAt(it) == 0 }, { 0 }, { thisHeight, otherHeight -> thisHeight - 1 <= otherHeight })
    val path2 = foundPath2?.tail() ?: emptyList()
    path2.forEach { p ->
        pathMap[p.y][p.x] = '%'
    }
    pathMap.forEach(::println)
    println(path2.size - 1)
}

