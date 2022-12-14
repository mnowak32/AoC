import java.io.File

class Day14(scan: List<String>) {
    val map: Map
    val map2: Map

    init {
        val patches = scan.map { patchDef ->
            val coords = patchDef.split(" -> ").map {
                val (x, y) = it.split(",")
                Coord(x.toInt(), y.toInt())
            }
            Patch(coords)
        }
        map = mapForPatches(patches)

        val floor = listOf(Coord(map.minX - map.height * 2, map.height + 2), Coord(map.minX + map.width + map.height * 2, map.height + 2))
        val patchesWithFloor = patches + Patch(floor)

        map2 = mapForPatches(patchesWithFloor)
    }

    fun mapForPatches(patches: List<Patch>): Map {
        val minMaxes = patches.flatMap(Patch::extremeValues)
        val (minCoord, maxCoord) = Patch(minMaxes).extremeValues()
        val width = maxCoord.x - minCoord.x
        val height = maxCoord.y
        val map = Map(width + 2, height, minCoord.x - 1, 0)
        println("built map: $map")

        patches.forEach { it.drawOnto(map) }
        return map
    }

    fun dropSand(map: Map): Boolean {
        var position = Coord(500, 0)
        var moving = true
        while (moving) {
            if (map.charAt(position.x, position.y) != ' ') {
                moving = false
            } else if (position.y >= map.height) {
                moving = false
            } else if (map.charAt(position.x, position.y + 1) == ' ') {
                position = position.copy(y = position.y + 1)
            } else if (map.charAt(position.x - 1, position.y + 1) == ' ') {
                position = position.copy(x = position.x - 1, y = position.y + 1)
            } else if (map.charAt(position.x + 1, position.y + 1) == ' ') {
                position = position.copy(x = position.x + 1, y = position.y + 1)
            } else {
                map.putAt(position.x, position.y, 'o')
                moving = false
            }
        }
//        println(map.forPrint())
        return position.y > 0 && position.y < map.height
    }

    data class Map(val width: Int, val height: Int, val minX: Int, val minY: Int) {
        private val map = CharArray((height + 1) * width) { ' ' }
        fun putAt(x: Int, y: Int, c: Char) {
            map[realCoord(x, y)] = c
        }
        fun charAt(x: Int, y: Int) = map[realCoord(x, y)]
        private fun realCoord(x: Int, y: Int) = (y - minY) * width + (x - minX)
        fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int, c: Char) {
            if (x1 == x2) {
                (if (y1 < y2) (y1 .. y2) else (y2 .. y1))
                    .forEach { putAt(x1, it, c) }
            } else if (y1 == y2) {
                (if (x1 < x2) (x1..x2) else (x2 .. x1))
                    .forEach { putAt(it, y1, c) }
            } else {
                TODO()
            }
        }

        fun forPrint(): String {
            return (0 .. height).joinToString("\n") { y ->
                if (y < minY) {
                    " ".repeat(width)
                } else {
                    val idx = realCoord(minX, y)
                    String(map.sliceArray(idx until (idx + width)))
                }
            }
        }
    }

    data class Patch(val coords: List<Coord>) {

        fun extremeValues(): List<Coord> {
            val maxX = coords.maxOfOrNull(Coord::x)!!
            val maxY = coords.maxOfOrNull(Coord::y)!!
            val minX = coords.minOfOrNull(Coord::x)!!
            val minY = coords.minOfOrNull(Coord::y)!!
            return listOf(Coord(minX, minY), Coord(maxX, maxY))
        }
        fun drawOnto(map: Map) {
            coords.zipWithNext().forEach { (p1, p2) -> map.drawLine(p1.x, p1.y, p2.x, p2.y, '#') }
        }
    }
}
fun main() {
    val scan = File("resources/day14.txt").readLines()
    val d15 = Day14(scan)

    var sandCount = 0
    while (d15.dropSand(d15.map)) {
        sandCount++
    }
    println(d15.map.forPrint())
    println(sandCount)

    sandCount = 0
    while (d15.dropSand(d15.map2)) {
        sandCount++
    }
    println(d15.map2.forPrint())
    println(sandCount + 1) //last sand piece not counted above

}