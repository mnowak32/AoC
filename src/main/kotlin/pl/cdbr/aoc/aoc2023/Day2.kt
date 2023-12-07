package pl.cdbr.aoc.aoc2023

import java.io.File

class Day2(filename: String, val bagState: Reveal) {
    val games = parse(File(filename))

    data class Game(val num: Int, val reveals: List<Reveal>) {
        fun possible(bagState: Reveal) = reveals.all { it.possible(bagState) }
        fun minPossibleBag(): Reveal {
            val minR = reveals.maxOfOrNull(Reveal::r) ?: 0
            val minG = reveals.maxOfOrNull(Reveal::g) ?: 0
            val minB = reveals.maxOfOrNull(Reveal::b) ?: 0
            return Reveal(minR, minG, minB)
        }
    }

    data class Reveal(val r: Int, val g: Int, val b: Int) {
        fun possible(bagState: Reveal) = r <= bagState.r && g <= bagState.g && b <= bagState.b
        fun power() = r * g * b
    }

    private fun toGame(str: String): Game {
        val num = str.drop(5).takeWhile(Char::isDigit)
        val revealsStr = str.drop(5 + num.length + 2).split("; ")
        val reveals = revealsStr.map {
            val cubes = it.split(", ")
            val cubeNumbers = cubes.associate { cube ->
                val number = cube.takeWhile(Char::isDigit)
                val colour = cube.drop(number.length + 1).take(1)
                colour to number.toInt()
            }
            Reveal(cubeNumbers["r"] ?: 0, cubeNumbers["g"] ?: 0, cubeNumbers["b"] ?: 0)
        }
        return Game(num.toInt(), reveals)
    }

    private fun parse(input: File): List<Game> {
        return input.useLines {
            it.map(this::toGame).toList()
        }
    }

    fun part1() {
        println(games)
        val possibleGames = games.filter { it.possible(bagState) }
        println(possibleGames)
        println(possibleGames.map(Game::num).sum())
    }

    fun part2() {
        val minPossibles = games.map(Game::minPossibleBag)
        println(minPossibles)
        println(minPossibles.map(Reveal::power).sum())
    }
}

fun main() {
    val day = Day2("resources/aoc2023/day2.txt", Day2.Reveal(12, 13, 14))
//    day.part1()
    day.part2()
}