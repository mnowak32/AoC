import java.io.File
import kotlin.math.absoluteValue

enum class Instr(val cycles: Int, val logic: (Cpu) -> Unit) {
    NOOP(1, {}),
    ADDX(2, { it.regX += it.instrArg })
}
object Cpu {
    var clock: Int = 0
    var regX = 1
    private var currentInstr: Instr = Instr.NOOP
    var instrArg: Int = 0
    private var instrCycle = 0
    var prog = mutableListOf<Pair<Instr, Int>>()
    var halted = false

    var crt = mutableListOf<Char>()

    fun cycle() {
        if (halted) {
            return
        }
        clock++
        instrCycle++
        if (instrCycle == currentInstr.cycles) {
            currentInstr.logic(this)
            if (prog.isEmpty()) {
                halted = true
            } else {
                val (instr, arg) = prog.removeFirst()
                currentInstr = instr
                instrArg = arg
                instrCycle = 0
            }
        }

        val crtX = (clock - 1) % 40
        val spriteHit = (regX - crtX).absoluteValue
        crt.add(if (spriteHit < 2) '#' else ' ')
    }
}
fun main() {
    val program = File("resources/day10.txt").readLines().map { line ->
        val instrArgs = line.split(" ")
        val (rawInstr, rawArg) = if (instrArgs.size == 1) {
            instrArgs + "0"
        } else {
            instrArgs
        }
        val instr = Instr.valueOf(rawInstr.uppercase())
        val arg = rawArg.toInt()
        instr to arg
    }
    println(program)

    val debugCycles = listOf(20, 60, 100, 140, 180, 220)
    var sum = 0

    Cpu.prog.addAll(program)
    while (!Cpu.halted) {
        if (Cpu.clock in debugCycles) {
            sum += (Cpu.clock * Cpu.regX)
            println("Cycle ${Cpu.clock}, X: ${Cpu.regX}, strength: ${Cpu.clock * Cpu.regX}, sum: $sum")
        }
        Cpu.cycle()
    }

    printCrt(Cpu.crt)
}

fun printCrt(crt: List<Char>) {
    crt.joinToString("").chunked(40).forEach(::println)
}
