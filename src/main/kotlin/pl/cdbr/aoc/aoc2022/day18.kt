import java.io.File

class Day18(scanData: List<String>) {
    val droplets: List<Droplet>
    val map: Map3D
    init {
        droplets = scanData.map {
            val (x, y, z) = it.split(",")
            Droplet(x.toInt(), y.toInt(), z.toInt())
        }

        val maxX = droplets.maxOfOrNull(Droplet::x)!!
        val maxY = droplets.maxOfOrNull(Droplet::y)!!
        val maxZ = droplets.maxOfOrNull(Droplet::z)!!

        map = Map3D(maxX + 1, maxY + 1, maxZ + 1)
        droplets.forEach { map.drop(it) }
        map.printSpace()
    }

    fun surfaceArea(outside: Char = ' '): Int {
        val allCoords = (0 until map.sx).flatMap { x ->
            (0 until map.sy).flatMap { y ->
                (0 until map.sz).map { Triple(x, y, it) }
            }
        }

        return allCoords
            .sumOf { (x, y, z) -> map.areaAt(x, y, z, outside) }
    }

    fun submergeInWater() {
        map.fill(0, 0, 0, ' ', '~')
        map.printSpace()
    }

    data class Droplet(val x: Int, val y: Int, val z: Int)

    data class Map3D(val sx: Int, val sy: Int, val sz: Int) {
        val space: Array<Array<Array<Char>>> = Array(sx) { Array(sy) { Array(sz) { ' ' } } }
        val linearSize = sx * sy * sz
        val xySize = sx * sy

        fun printSpace() {
            space.forEach { yzPlane ->
                yzPlane.forEach { println(String(it.toCharArray())) }
                println()
            }
        }
        fun drop(d: Droplet) { space[d.x][d.y][d.z] = '@' }
        fun areaAt(x: Int, y: Int, z: Int, outside: Char = ' '): Int {
            val voxel = spaceAt(x, y, z, outside)
            return if (voxel != '@') {
                0
            } else {
                Dir3d.values().count { spaceAt(x + it.dx, y + it.dy, z + it.dz, outside) == outside }
            }
        }

        fun spaceAt(x: Int, y: Int, z: Int, default: Char = ' '): Char {
            return if (!isCoordInMap(x, y, z)) {
                default
            } else {
                space[x][y][z]
            }
        }

        fun isCoordInMap(x: Int, y: Int, z: Int) = !(x < 0 || x >= sx || y < 0 || y >= sy || z < 0 || z >= sz)

        fun linearTo3d(pos: Int): Triple<Int, Int, Int> {
            return if (pos < 0 || pos >= linearSize) {
                Triple(-1, -1, -1)
            } else {
                val z = pos / xySize
                val y = (pos % xySize) % sx
                val x = pos % sx
                Triple(x, y, z)
            }
        }

        fun fill(x: Int, y: Int, z: Int, air: Char, water: Char) {
            if (!isCoordInMap(x, y, z) || spaceAt(x, y, z, air) != air) {
                return
            }
            space[x][y][z] = water
            Dir3d.values().forEach { fill(x + it.dx, y + it.dy, z + it.dz, air, water) }
        }
    }

    enum class Dir3d(val dx: Int, val dy: Int, val dz: Int) {
        X_NEG(-1, 0, 0),
        X_POS(1, 0, 0),
        Y_NEG(0, -1, 0),
        Y_POS(0, 1, 0),
        Z_NEG(0, 0, -1),
        Z_POS(0, 0, 1)
    }
}

fun main() {
    val data = File("resources/day18.txt").readLines()
    val d18 = Day18(data)
    println(d18.surfaceArea())
    d18.submergeInWater()
    println(d18.surfaceArea('~'))

}