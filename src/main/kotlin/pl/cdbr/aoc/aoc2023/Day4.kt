package pl.cdbr.aoc.aoc2023

import java.io.File

class Day4(filename: String) {

    private val cards = parse(File(filename))

    data class Scratchcard(val number: Int, val winning: Set<Int>, val youHave: Set<Int>) {
        val matchedCount = (winning.size) - (winning - youHave).size

        fun value(): Int {
            return if (matchedCount > 0) (1.shl(matchedCount - 1)) else 0
        }
    }

    private fun parse(input: File): List<Scratchcard> {
        return input.useLines { lines ->
            lines.map { line ->
                val (card, win, have) = line.split(":|\\|".toRegex())
                val cardNum = card.drop(5).trim().toInt()
                Scratchcard(cardNum, win.toNumbers(), have.toNumbers())
            }.toList()
        }
    }

    fun part1() {
        println(cards)
        println(cards.map(Scratchcard::value).sum())
    }

    fun part2() {
        val cardPile = cards.toMutableList()
        var i = 0
        while (i < cardPile.size) {
            val card = cardPile[i++]
            val newCardsCount = card.matchedCount
            (1 .. newCardsCount).forEach { count ->
                val toDup = card.number + count
                cards.find { it.number == toDup }?.let {
                    cardPile.add(it)
                }
            }
        }

        println(cardPile.size)
    }
}

private fun String.toNumbers(): Set<Int> = this.split("\\s+".toRegex()).filter(String::isNotBlank).map(String::toInt).toSet()

fun main() {
    val day = Day4("resources/aoc2023/day4.txt")
    day.part2()
}