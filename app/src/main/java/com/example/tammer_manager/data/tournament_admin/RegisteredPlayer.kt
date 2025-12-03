package com.example.tammer_manager.data.tournament_admin

data class RegisteredPlayer (val fullName: String, val rating: Int){
    var score = 0f
    var tpn: Int? = null
    var isActive: Boolean = true
}