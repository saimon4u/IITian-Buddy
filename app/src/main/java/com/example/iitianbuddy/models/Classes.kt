package com.example.iitianbuddy.models

data class Classes(
    var courseName: String,
    var instructorName: String,
    var roomNumber: String,
    var timeFrom: String = "",
    var timeTo: String = "",
    var day: String = "",
    var type: String = ""
)
{
    constructor(): this("", "", "")
}
