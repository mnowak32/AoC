import java.io.File

fun main() {
    val pairs = File("resources/day4.txt").readLines()
    println(pairs.map(::toRanges).count(::fullyContains))
    println(pairs.map(::toRanges).count(::overlaps))
}


fun toRanges(pair: String) = pair.split(",").map {
    val (from, to) = it.split("-").map(String::toInt)
    from..to
}

fun fullyContains(pair: List<IntRange>): Boolean {
    val (range1, range2) = pair
    return range1.fullyContains(range2) || range2.fullyContains(range1)
}

fun overlaps(pair: List<IntRange>): Boolean {
    val (range1, range2) = pair
    return range1.intersects(range2) || range2.intersects(range1)
}

fun IntRange.fullyContains(other: IntRange) = this.first <= other.first && this.last >= other.last
fun IntRange.intersects(other: IntRange) = this.contains(other.first) || this.contains(other.last)
