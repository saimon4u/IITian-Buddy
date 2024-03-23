package com.example.iitianbuddy.models

data class Event(
    var title: String,
    var imgLink: String,
    var link: String = "",
    var date: String = ""
)
{
    constructor() : this("", "")
}
