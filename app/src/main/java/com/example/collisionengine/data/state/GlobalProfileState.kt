package com.example.collisionengine.data.state

import kotlinx.coroutines.flow.MutableStateFlow

object GlobalProfileState {
    val name = MutableStateFlow("Rahul Bose")
    val role = MutableStateFlow("Computer Science @ MIT")
    val bio = MutableStateFlow("Passionate about AI, scalable systems, and building tools that connect researchers globally. Always open to collaborate!")
    val githubLink = MutableStateFlow("https://github.com/")
    val googleScholarLink = MutableStateFlow("https://scholar.google.com/")
}
