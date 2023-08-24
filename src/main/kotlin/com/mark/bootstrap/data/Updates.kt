package com.mark.bootstrap.data

data class Updates(
    val arch : String = "",
    val hash : String = "",
    val minimumVersion : String = "",
    val name : String = "",
    val os : String = "",
    val rollout : Int = -1,
    val size : Int = -1,
    val url : String = "",
    val version : String = ""
)
