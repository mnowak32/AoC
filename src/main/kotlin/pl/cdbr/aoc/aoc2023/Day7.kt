package pl.cdbr.aoc.aoc2023

import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day7(filename: String) {
    val hands = parse(File(filename))

    data class Hand(val cards: String, val bid: Long): Comparable<Hand> {
        val type: HandType by lazy { HandType.match(this) }

        override fun compareTo(other: Hand): Int {
            val typeDiff = this.type.compareTo(other.type)
            return if (typeDiff != 0) {
                typeDiff
            } else {
                cards.compareCards(other.cards)
            }
        }

        private fun String.compareCards(other: String): Int {
            val thisWeights = this.toCharArray().map { cardsOrder.indexOf(it) }
            val otherWeights = other.toCharArray().map { cardsOrder.indexOf(it) }
            val toCompare = thisWeights.zip(otherWeights).firstOrNull { (c1, c2) -> c1 != c2 }
            return toCompare?.let { (c1, c2) -> c1.compareTo(c2) } ?: 0
        }

        companion object {
            const val cardsOrder = "AKQJT98765432"
        }
    }

    enum class HandType {
        FIVE(5),
        FOUR(4),
        FULL(3, 2),
        THREE(3),
        TWO_PAIR(2, 2),
        PAIR(2),
        NOTHING;

        val cardCounts: IntArray

        constructor(vararg counts: Int) {
            cardCounts = counts
        }

        companion object {
            fun match(hand: Hand): HandType {
                val countedHand = hand.cards
                    .groupBy { it }
                    .map { (char, list) -> char to list.size }
                    .sortedByDescending { (_, count) -> count }
                    .toMap().values.toList()

                return HandType.values().find { type ->
                    type.cardCounts
                        .mapIndexed { index, count -> countedHand[index] == count }
                        .all { it }
                } ?: NOTHING
            }
        }
    }

    private fun parse(input: File): List<Hand> {
        return input.useLines { lines ->
            lines.map {
                val (cards, bid) = it.split("\\s+".toRegex())
                Hand(cards, bid.toLong())
            }.toList()
        }
    }

    fun part1() {
        val sorted = hands.sorted()
        println(sorted)
        val winnings = sorted.mapIndexed { index, hand -> hand.bid * (hands.size - index) }
        println(winnings)
        println(winnings.sum())
    }

    fun part2() {
    }
}

fun main() {
    val runningTime = measureTimeMillis {
        val day = Day7("resources/aoc2023/day7.txt")

        println(day.hands)
        day.part1()
        day.part2()
    }
    println("Elapsed time: ${runningTime.toDuration(DurationUnit.MILLISECONDS)}")
}
