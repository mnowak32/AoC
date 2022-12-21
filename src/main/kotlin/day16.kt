import java.io.File

class Day16(input: List<String>) {
    private val cfgRegex = "Valve ([A-Z]+) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z, ]+)".toRegex()
    val valves: Map<String, Valve>
    val start: Valve
    var dists: Map<Pair<Valve, Valve>, Int>
    init {
        valves = input.mapNotNull(cfgRegex::matchEntire).mapIndexed { idx, result ->
            val (vName, rate, paths) = result.destructured
            vName to Valve(vName, rate.toInt(), paths.split(", "), 1L.shl(idx))
        }.toMap()
        start = valves["AA"]!!
        dists = allDistances()
    }

    fun allDistances(): Map<Pair<Valve, Valve>, Int> {
        val importantValves = setOf(start) + valves.values.filter(Valve::important).toSet()
        val pairs = importantValves.flatMap { src ->
            importantValves.filter{ it != src }.map { src to it }
        }

        return pairs.associateWith { pair ->
            val (src, dst) = pair
            pathBetween(src, dst) + 1
        }
    }

    private fun pathBetween(begin: Valve, end: Valve): Int {
        val dist = valves.values.associateWith { Int.MAX_VALUE }.toMutableMap()
        val prev = mutableMapOf<Valve, Valve>()
        val q = valves.values.toMutableSet()
        dist[begin] = 0

        while (q.isNotEmpty()) {
            val u = q.minBy { dist[it]!! }
            if (u == end) {
                break
            }
            q.remove(u)

            val neighbours = u.routes.mapNotNull(valves::get).filter(q::contains)
            neighbours.forEach { v ->
                val alt = dist[u]!! + 1
                if (alt < dist[v]!!) {
                    dist[v] = alt
                    prev[v] = u
                }
            }
        }

        return dist[end] ?: Int.MAX_VALUE
    }

    fun dfs(been: Set<Valve>, begin: Valve, timeLeft: Int): VTree? {
        return if (timeLeft < 0) {
            null
        } else if (timeLeft == 0) {
            VTree(timeLeft, begin)
        } else {
            val routes = dists.filter { it.key.first == begin && it.key.second !in been }
            val paths = routes.mapNotNull { (conn, cost) ->
                val (_, to) = conn
                dfs(been + begin, to, timeLeft - cost)
            }
            VTree(timeLeft, begin, paths)
        }
    }

    fun findAllPathsTraversableIn(totalTime: Int): List<List<Pair<Int, Valve>>> {
        val tree = dfs(emptySet(), start, totalTime)
        return tree?.unTangle() ?: emptyList()
    }

    data class VTree(val timeLeft: Int, val node: Valve, val paths: List<VTree> = emptyList()) {
        fun unTangle(breadCrumb: List<Pair<Int, Valve>> = emptyList()): List<List<Pair<Int, Valve>>> {
            val newBreadCrumb = breadCrumb + (timeLeft to node)
            return if (paths.isEmpty()) {
                listOf(newBreadCrumb)
            } else {
                paths.flatMap {
                    it.unTangle(newBreadCrumb)
                }
            }
        }
    }

    data class Valve(val name: String, val flowRate: Int, val routes: List<String>, val bitMask: Long) {
        val important = flowRate > 0
    }
}

fun main() {

    // !! !!WARNING!! !!ACHTUNG!! !!ВНИМАНИЕ!! !!ATTENZIONE!!
    // brute force approach below!

    val config = File("resources/day16.txt").readLines()
    val d16 = Day16(config)
    println(d16.valves.values.filter { it.important }.size)
    println(d16.dists.filter { it.key.first == d16.start }.map { "${it.key.first.name} -> ${it.key.second.name} (${it.value})" })

    // part 1 - best single operator path traversable in 30 minutes
    val paths = d16.findAllPathsTraversableIn(30)
    val sums = paths.associateWith { path ->
        path.sumOf { (time, v) -> time * v.flowRate }
    }
    println(sums.entries.size)
    println("part 1 answer: ${sums.entries.maxBy { it.value }.value}")

    // part 2 - best pair of disjoint paths (followed by two operators) in 26 minutes

    // the following does this:
    //  - finds all paths in the tunnels traversable in exactly 26 minutes
    //  - calculates total flow value for each given path
    //  - for each path assigns a 64-bit mask (Long value) representing set of valves visited on the path
    //  - finds disjoint pairs of paths - by bit-AND-ing two masks, the result should contain only the start node bit set
    //    (bit operations are MUCH faster than Set.intersect() & Co.)
    //  - finds pair with the highest cumulative flow rate
    //  - PROFIT!!
    //
    val shortPaths = d16.findAllPathsTraversableIn(26)
    val shortSums = shortPaths.associateWith { path ->
        path.sumOf { (time, v) -> time * v.flowRate }
    }
    val total = shortPaths.size

    val pathsWithBitmasks = shortPaths.associateWith { path -> (path.sumOf { it.second.bitMask }) }
    val startNodeMask = d16.start.bitMask

    val disjointPathsPairs = shortPaths.mapIndexedNotNull { idx, path1 ->
        print("\r${idx * 100 / total} % ")
        val visited1Valves = pathsWithBitmasks[path1]!!
        // drop() omits already processed paths (cuts processing by half)
        val path1Value = shortSums[path1]!!
        shortPaths.drop(idx + 1).filter { path2 ->
            val visited2Valves = pathsWithBitmasks[path2]!!
            visited2Valves.and(visited1Valves) == startNodeMask
        }.maxOfOrNull {
            path1Value + shortSums[it]!!
        }
    }
    println()
    println(disjointPathsPairs.size)

    val max = disjointPathsPairs.max()
    println("part 2 answer: $max")

}

