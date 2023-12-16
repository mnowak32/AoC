package pl.cdbr.aoc.aoc2023

import pl.cdbr.aoc.common.Dir
import pl.cdbr.aoc.common.Point
import java.io.File
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day16(filename: String) {
    private val map = parse(File(filename))
    private val bounds = Pair(Point(0, 0), map.keys.maxBy { it.x + it.y })

    data class Ray(val p: Point, val dir: Dir) {
        fun next() = Ray(dir.move(p), dir)
    }

    enum class Grid(val c: Char, val translate: (Ray) -> List<Ray>) {
        EMPTY('.', { r -> listOf(r.next()) }),
        MIRROR_45('/', { r ->
            val newDir = when (r.dir) {
                Dir.N -> Dir.E
                Dir.E -> Dir.N
                Dir.S -> Dir.W
                Dir.W -> Dir.S
            }
            listOf(Ray(newDir.move(r.p), newDir))
        }),
        MIRROR_135('\\', { r ->
            val newDir = when (r.dir) {
                Dir.N -> Dir.W
                Dir.E -> Dir.S
                Dir.S -> Dir.E
                Dir.W -> Dir.N
            }
            listOf(Ray(newDir.move(r.p), newDir))
        }),
        SPLIT_H('-', { r ->
            if (r.dir == Dir.E || r.dir == Dir.W) {
                listOf(r.next())
            } else {
                listOf(
                    Ray(Dir.E.move(r.p), Dir.E),
                    Ray(Dir.W.move(r.p), Dir.W)
                )
            }
        }),
        SPLIT_V('|', { r ->
            if (r.dir == Dir.N || r.dir == Dir.S) {
                listOf(r.next())
            } else {
                listOf(
                    Ray(Dir.N.move(r.p), Dir.N),
                    Ray(Dir.S.move(r.p), Dir.S)
                )
            }
        });

        companion object {
            fun forSymbol(c: Char): Grid = values().find { it.c == c } ?: EMPTY
        }
    }
    
    private fun parse(input: File): Map<Point, Grid> {
        val mapInProgress = mutableMapOf<Point, Grid>()
        return input.useLines { lines ->
            lines.forEachIndexed { y, s ->
                s.forEachIndexed { x, c ->
                    mapInProgress[Point(x, y)] = Grid.forSymbol(c)
                }
            }
            mapInProgress.toMap()
        }
    }

    fun part1() {
        println(countEnergizedFieldsFor(Ray(Point(0, 0), Dir.E)))
    }

    private fun countEnergizedFieldsFor(start: Ray): Int {
        val pastRays = mutableListOf<Ray>()
        var currentRays = listOf(start)

        while (currentRays.isNotEmpty()) {
            val nextRays = currentRays.flatMap { r ->
                val grid = map[r.p] ?: Grid.EMPTY
                grid.translate(r)
            }.filter { it.p.isInBounds(bounds) && !pastRays.contains(it) }
            pastRays.addAll(currentRays)
            currentRays = nextRays
        }

        return pastRays.map(Ray::p).distinct().count()
    }

    fun part2() {
        val startRays =
            (0..bounds.second.x).map { Ray(Point(it, 0), Dir.S) } +
            (0..bounds.second.y).map { Ray(Point(bounds.second.x, it), Dir.W) } +
            (0..bounds.second.x).map { Ray(Point(it, bounds.second.y), Dir.N) } +
            (0..bounds.second.x).map { Ray(Point(0, it), Dir.E) }

        val counts = startRays.parallelStream().map { it to countEnergizedFieldsFor(it) }.toList().toMap()
        val max = counts.maxBy { it.value }
        println(max)

    }

}

fun main() {
    measureTimeMillis {
        val day = Day16("resources/aoc2023/day16.txt")

        print("Part 1 solution is ")
        day.part1()
        print("Part 2 solution is ")
        day.part2()
    }.also {
        println("Elapsed time: ${it.toDuration(DurationUnit.MILLISECONDS)}")
    }
}

