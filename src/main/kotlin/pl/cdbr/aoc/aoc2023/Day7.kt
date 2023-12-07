package pl.cdbr.aoc.aoc2023

import java.io.File
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Day7(filename: String) {
    val hands = parse(File(filename))

    data class Hand(val cards: String, val bid: Long) {
        private val type: HandType by lazy { HandType.match(this) }
        private val typeWithJokers: HandType by lazy { HandType.match(this, withJokers = true) }

        fun compareWithoutJokers(other: Hand) = compareWithType(other, Hand::type, CARDS_ORDER)
        fun compareWithJokers(other: Hand) = compareWithType(other, Hand::typeWithJokers, CARDS_ORDER_WITH_JOKERS)

        private fun compareWithType(other: Hand, typeAccessor: Hand.() -> HandType, cardOrder: String): Int {
            val typeDiff = this.typeAccessor().compareTo(other.typeAccessor())
            return if (typeDiff != 0) {
                typeDiff
            } else {
                cards.compareCards(other.cards, cardOrder)
            }
        }

        private fun String.compareCards(other: String, cardOrder: String): Int {
            val thisWeights = this.toCharArray().map { cardOrder.indexOf(it) }
            val otherWeights = other.toCharArray().map { cardOrder.indexOf(it) }
            val toCompare = thisWeights.zip(otherWeights).firstOrNull { (c1, c2) -> c1 != c2 }
            return toCompare?.let { (c1, c2) -> c1.compareTo(c2) } ?: 0
        }
    }

    enum class HandType(vararg counts: Int) {
        FIVE(5),
        FOUR(4),
        FULL(3, 2),
        THREE(3),
        TWO_PAIR(2, 2),
        PAIR(2),
        NOTHING;

        val cardCounts: IntArray = counts

        companion object {
            fun match(hand: Hand, withJokers: Boolean = false): HandType {
                val countedHand = hand.cards
                    .groupBy { it }
                    .map { (char, list) -> char to list.size }
                    .sortedByDescending { (c, count) -> count * 20 - CARDS_ORDER.indexOf(c) }

                val (jokers, other) = countedHand.partition { (c, _) -> c == 'J' }

                val countedValues = if (withJokers && jokers.isNotEmpty() && other.isNotEmpty()) {
                    val jokerCount = jokers.first().second
                    val first = other.first().second
                    val rest = other.drop(1).map(Pair<Char, Int>::second).toTypedArray()
                    listOf(listOf(first + jokerCount, *rest))
                } else {
                    emptyList()
                } + listOf(countedHand.toMap().values.toList())

                return countedValues.minOfOrNull(Companion::matchValues) ?: NOTHING

            }
            private fun matchValues(countedValues: List<Int>): HandType {
                return HandType.values().find { type ->
                    type.cardCounts
                        .mapIndexed { index, count -> countedValues.getOrElse(index) { 0 } == count }
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
        val sorted = hands.sortedWith(Hand::compareWithoutJokers)
        val winnings = sorted.mapIndexed { index, hand -> hand.bid * (hands.size - index) }
        println(winnings.sum())
    }

    fun part2() {
        val sorted = hands.sortedWith(Hand::compareWithJokers)
        val winnings = sorted.mapIndexed { index, hand -> hand.bid * (hands.size - index) }
        println(winnings.sum())
    }

    companion object {
        private const val CARDS_ORDER = "AKQJT98765432"
        private const val CARDS_ORDER_WITH_JOKERS = "AKQT98765432J"
    }
}

fun main() {
    val runningTime = measureTimeMillis {
        val day = Day7("resources/aoc2023/day7.txt")

        print("Part 1 solution is ")
        day.part1()
        print("Part 2 solution is ")
        day.part2()
    }
    println("Elapsed time: ${runningTime.toDuration(DurationUnit.MILLISECONDS)}")
}
