package com.example.ineedhelp.helpers

import com.example.ineedhelp.model.FightGroup
import com.example.ineedhelp.model.FightModelClass

class TournamentManager (private val jsonHelper: FightJsonHelper) {

    fun generateRoundRobinFights(fighterGroups: List<List<String>>): List<FightGroup> {
        val fightGroups = mutableListOf<FightGroup>()

        for ((groupId, group) in fighterGroups.withIndex()) {
            val fights = mutableListOf<FightModelClass>()
            val fighters = if (group.size % 2 == 0) group.toMutableList() else (group + listOf("bye")).toMutableList()
            val numFighters = fighters.size
            val halfSize = numFighters / 2

            for (round in 0 until numFighters - 1) {
                val roundFights = mutableListOf<FightModelClass>()
                for (i in 0 until halfSize) {
                    val fighter1 = fighters[i]
                    val fighter2 = fighters[numFighters - i - 1]

                    if (fighter1 != "bye" && fighter2 != "bye") {
                        roundFights.add(
                            FightModelClass(
                                id = fights.size + roundFights.size,
                                fighter1Name = fighter1,
                                fighter2Name = fighter2,
                                fighter1Score = 0,
                                fighter2Score = 0,
                                groupId = groupId // Використовуємо порядковий groupId
                            )
                        )
                    }
                }

                if (fights.isNotEmpty() && roundFights.isNotEmpty()) {
                    val lastFight = fights.last()
                    val firstNewFight = roundFights.first()
                    if (lastFight.fighter1Name == firstNewFight.fighter1Name || lastFight.fighter1Name == firstNewFight.fighter2Name ||
                        lastFight.fighter2Name == firstNewFight.fighter1Name || lastFight.fighter2Name == firstNewFight.fighter2Name
                    ) {
                        roundFights.add(roundFights.removeAt(0))
                    }
                }

                fights.addAll(roundFights)

                val lastFighter = fighters.removeLast()
                fighters.add(1, lastFighter)
            }

            // Додаємо FightGroup для кожної групи
            fightGroups.add(FightGroup(groupId = groupId, fights = fights))
        }
        return fightGroups
    }

    fun saveToBuffer(fightGroups: List<FightGroup>) {
        jsonHelper.generateBufferFile(fightGroups)
    }
}