package com.example.iitianbuddy.models

data class Student(
    var fullName: String = "",
    var email: String = "",
    var password: String = "",
    var userId: String = "",
    var joinedDate:String = "",
    var bloodGroup: String = "",
    var phoneNumber: String = "",
    var batch: String = "",
    var profileImg: String = "",
    var facebookLink: String = "",
    var linkedinLink: String = "",
    var githubLink: String = "",
    var isCr: Boolean = false,
    var isPhonePublic: Boolean = false,
    var isFacebookPublic: Boolean = true,
    var isLinkedinPublic: Boolean = true,
    var isGithubPublic: Boolean = true
)