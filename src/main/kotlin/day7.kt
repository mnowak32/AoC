import java.io.File

interface Node {
    val name: String
    val size: Int
}

class XFile(override val name: String, override val size: Int) : Node
class Dir(override val name: String, private val parent: Dir?) : Node {
    private val content = mutableListOf<Node>()
    override val size: Int
        get() = content.map(Node::size).sum()

    fun cd(dirName: String): Dir? = content.find { it.name == dirName } as? Dir
    fun mkDir(dirName: String) {
        content.add(Dir(dirName, this))
    }
    fun cdUp() = parent
    fun addFile(f: XFile) {
        content.add(f)
    }

    fun printTree(depth: Int = 0) {
        println("${"  ".repeat(depth)}+ $name $size")
        content.forEach {
            if (it is Dir) {
                it.printTree(depth + 1)
            } else {
                println("${"  ".repeat(depth + 1)}- ${it.name} ${it.size}")
            }
        }
    }

    fun findAllDirsWithSizeWhere(oper: (Int) -> Boolean): List<Dir> {
        val dirs = content.filterIsInstance<Dir>()
        val here = dirs.filter { oper(it.size) }
        val more = dirs.flatMap { it.findAllDirsWithSizeWhere(oper) }
        return here + more
    }

    fun path(): String = if (parent != null) { parent.path() + "$name/" } else { name }
    fun ls() = "${path()} $size"
}

fun main() {

    val root = Dir("/", null)
    var cwd = root

    val story = File("resources/day7.txt").readLines()
    story.forEach { line ->
        if (line.startsWith("$")) {
            if (line.startsWith("$ cd")) {
                val target = line.drop(5)
                cwd = when(target) {
                    "/" -> root
                    ".." -> cwd.cdUp() ?: cwd
                    else -> cwd.cd(target) ?: cwd
                }
            }
        } else {
            if (line.startsWith("dir")) {
                cwd.mkDir(line.drop(4))
            } else {
                val (size, name) = line.split(" ")
                cwd.addFile(XFile(name, size.toInt()))
            }
        }
    }

//    root.printTree()
    val smallDirs = root.findAllDirsWithSizeWhere { it <= 100000 }
//    smallDirs.map(Dir::ls).forEach(::println)
    println(smallDirs.map(Dir::size).sum())

    val totalFsSpace = 70000000
    val neededSpace = 30000000
    val currentFreeSpace = totalFsSpace - root.size
    val needToFree = neededSpace - currentFreeSpace

    val bigDirs = root.findAllDirsWithSizeWhere { it >= needToFree }

//    bigDirs.map(Dir::ls).forEach(::println)
    println(bigDirs.minBy(Dir::size).ls())
}


