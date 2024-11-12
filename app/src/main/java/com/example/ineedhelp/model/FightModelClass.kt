package com.example.ineedhelp.model

data class FightGroup(
    val groupId: Int,
    val fights: List<FightModelClass>
)

data class FightModelClass (
    val id: Int,
    val  fighter1Name: String,
    val  fighter2Name: String,
    var fighter1Score: Int,
    var fighter2Score: Int,
    val groupId: Int
)