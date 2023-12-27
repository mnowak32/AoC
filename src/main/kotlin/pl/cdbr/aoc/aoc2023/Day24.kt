package pl.cdbr.aoc.aoc2023

import pl.cdbr.aoc.common.crossProduct
import java.io.File
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day24(filename: String) {
    private val stones = parse(File(filename))

    data class Point2D(val x: Double, val y: Double) {
        fun within(p1: Point2D, p2: Point2D): Boolean {
            return x in (p1.x .. p2.x) && y in (p1.y .. p2.y)
        }
    }
    data class Line2D(val a: Double, val b: Double, val nowX: Double, val dx: Double) {
        fun crossInFuture(other: Line2D): Point2D? {
            val da = a - other.a
            return if (a == 0.0) {
                null
            } else {
                val x = (other.b - b) / da
                val y = a * x + b
                if (inFuture(x, y) && other.inFuture(x, y)) {
                    Point2D(x, y)
                } else {
                    null
                }
            }
        }

        private fun inFuture(fx: Double, fy: Double): Boolean {
            val dx = fx - nowX
            return ((dx / this.dx) >= 0.0)
        }
    }
    data class Hailstone(val px: Long, val py: Long, val pz: Long, val dx: Long, val dy: Long, val dz: Long) {
        fun toEquation(): Line2D {
            val a = dy.toDouble() / dx
            val b = py.toDouble() - a * px
            return Line2D(a, b, px.toDouble(), dx.toDouble())
        }
    }
    
    private fun parse(input: File): List<Hailstone> {
        input.useLines { lines ->
            return lines.map { l ->
                val ns = l.split("[,@ ]+".toRegex()).map(String::toLong)
                Hailstone(ns[0], ns[1], ns[2], ns[3], ns[4], ns[5])
            }.toList()
        }
    }

    fun part1() {
        val equations = stones.map(Hailstone::toEquation)
        val pairs = equations.crossProduct()
        val testAreaP1 = Point2D(200000000000000.0, 200000000000000.0)
        val testAreaP2 = Point2D(400000000000000.0, 400000000000000.0)
        val crossings = pairs
            .mapNotNull { (l1, l2) -> l1.crossInFuture(l2) }
            .also(::println)
            .filter { it.within(testAreaP1, testAreaP2) }

        println(crossings)
        println(crossings.size)
    }

    fun part2() {
    }

}

fun main() {
    measureTimeMillis {
        val day = Day24("resources/aoc2023/day24.txt")

        print("Part 1 solution is ")
        day.part1()
        print("Part 2 solution is ")
        day.part2()
    }.also {
        println("Elapsed time: ${it.toDuration(DurationUnit.MILLISECONDS)}")
    }
}

