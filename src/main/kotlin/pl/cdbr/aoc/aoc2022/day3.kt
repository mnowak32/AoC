import java.io.File

fun main() {

    val bags = File("resources/day3.txt").readLines()
    println(bags.map(::sharedItem).map(::toPriority).sum())
    println(bags.chunked(3).map(::findBadge).map(::toPriority).sum())
}

fun sharedItem(pack: String): Char {
    val len = pack.length / 2
    val firstCompartment = pack.take(len)
    val secondCompartment = pack.drop(len).toSet()
    return firstCompartment.first(secondCompartment::contains)
}

fun toPriority(char: Char): Int {
    return if (char.isLowerCase()) {
        char - 'a' + 1
    } else {
        char - 'A' + 27
    }
}

fun findBadge(bags: List<String>): Char {
    val (firstPack, secondPack, thirdPack) = bags.map(String::toSet)
    return firstPack.filter(secondPack::contains).first(thirdPack::contains)
}
