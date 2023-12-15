package pl.cdbr.aoc.common

import kotlin.reflect.KProperty1

data class Point(val x: Int, val y: Int): Comparable<Point> {
    fun surroundings(): List<Point> = listOf(
        Point(x - 1, y - 1),
        Point(x - 1, y),
        Point(x - 1, y + 1),
        Point(x, y - 1),
        Point(x, y + 1),
        Point(x + 1, y - 1),
        Point(x + 1, y),
        Point(x + 1, y + 1)
    )

    fun isInBounds(x1: Int, y1: Int, x2: Int, y2: Int) = x in x1..x2 && y in y1 .. y2
    fun isInBounds(corners: Pair<Point, Point>) = isInBounds(corners.first.x, corners.first.y, corners.second.x, corners.second.y)
    override fun compareTo(other: Point): Int {
        val byY = this.y.compareTo(other.y)
        return if (byY == 0) { this.x.compareTo(other.x) } else { byY }
    }
    fun compareToReversed(other: Point) = -compareTo(other)
}

enum class Dir(private val dx: Int, private val dy: Int, val moveSelector: KProperty1<Point, Int>, val coordSelector: KProperty1<Point, Int>) {
    N(0, -1, Point::y, Point::x),
    W(-1, 0, Point::x, Point::y),
    S(0, 1, Point::y, Point::x),
    E(1, 0, Point::x, Point::y);

    fun move(p: Point, dist: Int = 1): Point = Point(p.x + dx * dist, p.y + dy * dist)
    fun isPositive() = dx > 0 || dy > 0
}
