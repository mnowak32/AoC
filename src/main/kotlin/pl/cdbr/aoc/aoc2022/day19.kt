
import Day19.RobotType.*
class Day19 {

    val sample = BluePrint(mutableMapOf(
        ORE to (Cost(ORE to 4) to 1),
        CLAY to (Cost(ORE to 2) to 4),
        OBS to (Cost(ORE to 3, CLAY to 14) to 2),
        GEO to (Cost(ORE to 2, OBS to 7) to 99)
    ))

    fun process(bp: BluePrint) {
        val stash = Stash()
        val army = RobotArmy(mutableMapOf(ORE to 1))
        repeat(24) {
            val newBot = bp.chooseMostWantedRobot(stash, army)
            army.addTurnResources(stash)
            if (newBot != null) {
                bp.build(newBot, stash, army)
            }
            println("min: ${it + 1}, $stash, $army")
        }

    }

    class Cost(vararg matList: Pair<RobotType, Int>) {
        val materials = matList.toMap()
        fun ofType(rt: RobotType) = materials.getOrDefault(rt, 0)
    }
    data class Stash(var content: MutableMap<RobotType, Int> = mutableMapOf()) {
        fun canAfford(c: Cost) = c.materials.all { (rt, amount) -> ofType(rt) >= amount}
        fun pay(c: Cost) {
            c.materials.forEach { (rt, amount) ->
                content[rt] = ofType(rt) - amount
            }
        }

        fun ofType(rt: RobotType) = content.getOrDefault(rt, 0)
    }

    data class RobotArmy(val bots: MutableMap<RobotType, Int>) {
        fun addTurnResources(s: Stash) {
            bots.forEach { (rt, count) -> s.content[rt] = s.ofType(rt) + count}
        }

        fun ofType(rt: RobotType) = bots.getOrDefault(rt, 0)
        fun add(rt: RobotType) {
            bots[rt] = ofType(rt) + 1
        }
    }

    data class BluePrint(val bots: Map<RobotType, Pair<Cost, Int>>) {
        fun chooseMostWantedRobot(stash: Stash, army: RobotArmy): RobotType? {
            val wantedCost = bots[GEO]!!
            // TODO

            return RobotType.values().reversed().find { rt ->
                val bp = bots[rt]
                if (bp != null) {
                    army.ofType(rt) < bp.second && stash.canAfford(bp.first)
                } else {
                     false
                }
            }
        }

        fun build(newBotType: RobotType, stash: Stash, army: RobotArmy) {
            bots[newBotType]?.let { (rt, _) ->
                stash.pay(rt)
                army.add(newBotType)
            }

        }
    }

    enum class RobotType {
        ORE, CLAY, OBS, GEO
    }
}

fun main() {
    val d19 = Day19()
    d19.process(d19.sample)
}