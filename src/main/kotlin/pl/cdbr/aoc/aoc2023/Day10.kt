package pl.cdbr.aoc.aoc2023

import pl.cdbr.aoc.aoc2023.Day3.Point
import java.io.File
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day10(filename: String) {
    val map = parse(File(filename))
    var pipe: List<Point> = emptyList()

    private fun parse(input: File): List<MutableList<Char>> {
        return input.readLines().map(String::toMutableList)
    }

    private fun charAt(p: Point) = map.getOrNull(p.y)?.getOrNull(p.x) ?: '.'
    private fun putCharAt(p: Point, c: Char) {
        map[p.y][p.x] = c
    }

    private fun pipeEnds(p: Point): Set<Point> {
        val shape = charAt(p)
        return when (shape) {
            '-' -> setOf(Point(p.x - 1, p.y), Point(p.x + 1, p.y))
            '|' -> setOf(Point(p.x, p.y - 1), Point(p.x, p.y + 1))
            'L' -> setOf(Point(p.x, p.y - 1), Point(p.x + 1, p.y))
            'J' -> setOf(Point(p.x, p.y - 1), Point(p.x - 1, p.y))
            '7' -> setOf(Point(p.x - 1, p.y), Point(p.x, p.y + 1))
            'F' -> setOf(Point(p.x + 1, p.y), Point(p.x, p.y + 1))
            'S' -> setOf(p.surroundings().find { s -> pipeEnds(s).contains(p) }!!)
            else -> emptySet()
        }
    }

    fun part1() {
        val startY = map.indexOfFirst { it.contains('S') }
        val startX = map[startY].indexOf('S')
        val start = Point(startX, startY)
        val pipe = mutableListOf(start)
        var current = pipeEnds(start).first()
        var prev = start
        do {
            pipe.add(current)
            val next = (pipeEnds(current) - prev).first()
            prev = current
            current = next
        } while (current != start)
        this.pipe = pipe.toList()

        println(pipe.size / 2)
    }

    private fun isOnPipe(x: Int, y: Int) = pipe.contains(Point(x, y))

    fun part2() {
        var counter = 0
        replaceStartWithPipeChar()
        map.forEachIndexed { y, line ->
            var inside = false
            var pipeStartChar = ' '
            line.forEachIndexed { x, c ->
                if (isOnPipe(x, y)) {
                    when {
                        c == '|' -> {
                            inside = !inside
                        }
                        c == 'F' || c == 'L' -> {
                            pipeStartChar = c
                        }
                        c == 'J' && pipeStartChar == 'F' -> {
                            inside = !inside
                        }
                        c == '7' && pipeStartChar == 'L' -> {
                            inside = !inside
                        }
                    }
                } else if (inside) {
                    counter++
                    line[x] = 'I'
                }
            }
        }
        println(counter)
    }
//  2
// 1.4
//  8
    private fun replaceStartWithPipeChar() {
        val start = pipe.first()
        val beforeStart = pipe.last()
        val afterStart = pipe[1]

        val dA = start.whatDirIs(afterStart)
        val dB = start.whatDirIs(beforeStart)
        val twoDirs = dA + dB
        val replacement = when (twoDirs) {
            3 -> 'J'
            5 -> '-'
            6 -> 'L'
            9 -> '7'
            10 -> '|'
            12 -> 'F'
            else -> '.'
        }

        putCharAt(start, replacement)
    }

    private fun Point.whatDirIs(p: Point): Int {
        val dx = p.x - this.x
        val dy = p.y - this.y
        return when {
            dx == -1 -> 1
            dy == -1 -> 2
            dx == 1 -> 4
            else -> 8
        }
    }
}

fun main() {
    measureTimeMillis {
        val day = Day10("resources/aoc2023/day10.txt")

        print("Part 1 solution is ")
        day.part1()
        print("Part 2 solution is ")
        day.part2()
    }.also {
        println("Elapsed time: ${it.toDuration(DurationUnit.MILLISECONDS)}")
    }
}

