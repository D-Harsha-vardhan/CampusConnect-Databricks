package com.example.collisionengine.data.network

import android.content.Context
import com.example.collisionengine.data.model.ProfileMatch
import com.example.collisionengine.data.models.Faculty
import com.example.collisionengine.data.models.Student
import com.example.collisionengine.data.models.Project
import com.example.collisionengine.data.models.Research
import com.example.collisionengine.data.models.Placement
import kotlinx.serialization.json.Json
import java.io.InputStreamReader

object LocalDatasetClient {
    private var allStudents: List<Student> = emptyList()
    private var allFaculty: List<Faculty> = emptyList()
    private var allProjects: List<Project> = emptyList()
    private var allResearch: List<Research> = emptyList()
    private var allPlacements: List<Placement> = emptyList()

    private val jsonFormat = Json { ignoreUnknownKeys = true }

    fun init(context: Context) {
        try {
            context.assets.open("Students.json").use { inputStream ->
                allStudents = jsonFormat.decodeFromString(InputStreamReader(inputStream).readText())
            }
            context.assets.open("Faculty.json").use { inputStream ->
                allFaculty = jsonFormat.decodeFromString(InputStreamReader(inputStream).readText())
            }
            context.assets.open("Projects.json").use { inputStream ->
                allProjects = jsonFormat.decodeFromString(InputStreamReader(inputStream).readText())
            }
            context.assets.open("Research.json").use { inputStream ->
                allResearch = jsonFormat.decodeFromString(InputStreamReader(inputStream).readText())
            }
            context.assets.open("Placement.json").use { inputStream ->
                allPlacements = jsonFormat.decodeFromString(InputStreamReader(inputStream).readText())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun normalizeName(name: String?): String {
        if (name == null) return ""
        return name
            .lowercase()
            .replace(Regex("^(dr\\.|prof\\.|dr\\s|prof\\s)"), "")
            .trim()
    }

    fun searchProfilesByNames(names: List<String>): List<ProfileMatch> {
        if (names.isEmpty()) return emptyList()

        val normalizedNames = names.map { normalizeName(it) }
        val matches = mutableListOf<ProfileMatch>()

        // Search Students (Fuzzy: check if any part of the name matches)
        val matchedStudents = allStudents.filter { student ->
            val normStudent = normalizeName(student.name)
            normalizedNames.any { normStudent.contains(it) || it.contains(normStudent) }
        }
        matches.addAll(matchedStudents.map { student ->
            ProfileMatch(
                name = student.name ?: "Unknown",
                role = "${student.department ?: ""} • Year ${student.year ?: ""}",
                matchReasonTitle = "Related to your query",
                matchReasonText = student.projects?.takeIf { it.isNotBlank() } ?: student.skills ?: "",
                tags = student.skills?.split(",")?.map { it.trim() }?.take(4) ?: emptyList()
            )
        })

        // Search Faculty
        val matchedFaculty = allFaculty.filter { faculty ->
            val normFaculty = normalizeName(faculty.name)
            normalizedNames.any { normFaculty.contains(it) || it.contains(normFaculty) }
        }
        matches.addAll(matchedFaculty.map { faculty ->
            ProfileMatch(
                name = faculty.name ?: "Unknown",
                role = "Faculty • ${faculty.department ?: ""}",
                matchReasonTitle = "Related to your query",
                matchReasonText = faculty.expertise ?: "",
                tags = faculty.researchInterests?.split(",")?.map { it.trim() }?.take(4) ?: emptyList()
            )
        })

        return matches.distinctBy { it.name }
    }

    fun searchByNamePartial(query: String): List<ProfileMatch> {
        val q = query.lowercase().trim()
        if (q.isBlank() || q.length < 2) return emptyList()

        val matches = mutableListOf<ProfileMatch>()

        val matchedStudents = allStudents.filter { normalizeName(it.name).contains(q) }
        matches.addAll(matchedStudents.map { student ->
            ProfileMatch(
                name = student.name ?: "Unknown",
                role = "${student.department ?: ""} • Year ${student.year ?: ""}",
                matchReasonTitle = "Name Match",
                matchReasonText = student.projects?.takeIf { it.isNotBlank() } ?: student.skills ?: "",
                tags = student.skills?.split(",")?.map { it.trim() }?.take(4) ?: emptyList()
            )
        })

        val matchedFaculty = allFaculty.filter { normalizeName(it.name).contains(q) }
        matches.addAll(matchedFaculty.map { faculty ->
            ProfileMatch(
                name = faculty.name ?: "Unknown",
                role = "Faculty • ${faculty.department ?: ""}",
                matchReasonTitle = "Name Match",
                matchReasonText = faculty.expertise ?: "",
                tags = faculty.researchInterests?.split(",")?.map { it.trim() }?.take(4) ?: emptyList()
            )
        })

        return matches.take(10)
    }

    fun getStudentByName(name: String): Student? {
        val norm = normalizeName(name)
        return allStudents.find { normalizeName(it.name) == norm }
    }

    fun getFacultyByName(name: String): Faculty? {
        val norm = normalizeName(name)
        return allFaculty.find { normalizeName(it.name) == norm }
    }

    fun searchProfilesByKeywords(query: String): List<ProfileMatch> {
        if (query.isBlank()) return emptyList()
        val q = query.lowercase().trim()
        val keywords = q.split(" ").filter { it.length > 3 }
        
        if (keywords.isEmpty()) return searchByNamePartial(query)

        val matches = mutableListOf<ProfileMatch>()
        val matchedStudentIds = mutableSetOf<String>()
        val matchedFacultyIds = mutableSetOf<String>()

        // 1. Search Projects
        allProjects.filter { project ->
            val text = "${project.title} ${project.description} ${project.technologies} ${project.domain} ${project.methodology}".lowercase()
            keywords.any { text.contains(it) }
        }.forEach { project ->
            project.studentId?.let { matchedStudentIds.add(it) }
        }

        // 2. Search Research
        allResearch.filter { research ->
            val text = "${research.title} ${research.abstract} ${research.researchArea} ${research.methodologies}".lowercase()
            keywords.any { text.contains(it) }
        }.forEach { research ->
            research.personId?.let { id ->
                if (id.startsWith("F")) matchedFacultyIds.add(id)
                else if (id.startsWith("S")) matchedStudentIds.add(id)
            }
        }

        // 3. Search Placement
        allPlacements.filter { placement ->
            val text = "${placement.company} ${placement.role} ${placement.skills} ${placement.interviewTopics}".lowercase()
            keywords.any { text.contains(it) }
        }.forEach { placement ->
            placement.studentId?.let { matchedStudentIds.add(it) }
        }

        // 4. Search Students directly
        allStudents.filter { student ->
            val text = "${student.department} ${student.skills} ${student.projects} ${student.researchInterests} ${student.careerInterests}".lowercase()
            keywords.any { text.contains(it) } || matchedStudentIds.contains(student.studentId)
        }.forEach { student ->
            matches.add(
                ProfileMatch(
                    name = student.name ?: "Unknown",
                    role = "${student.department ?: ""} • Year ${student.year ?: ""}",
                    matchReasonTitle = "Keyword Match",
                    matchReasonText = student.projects?.takeIf { it.isNotBlank() } ?: student.skills ?: "",
                    tags = student.skills?.split(",")?.map { it.trim() }?.take(4) ?: emptyList()
                )
            )
        }

        // 5. Search Faculty directly
        allFaculty.filter { faculty ->
            val text = "${faculty.department} ${faculty.expertise} ${faculty.researchInterests} ${faculty.publications}".lowercase()
            keywords.any { text.contains(it) } || matchedFacultyIds.contains(faculty.facultyId)
        }.forEach { faculty ->
            matches.add(
                ProfileMatch(
                    name = faculty.name ?: "Unknown",
                    role = "Faculty • ${faculty.department ?: ""}",
                    matchReasonTitle = "Keyword Match",
                    matchReasonText = faculty.expertise ?: "",
                    tags = faculty.researchInterests?.split(",")?.map { it.trim() }?.take(4) ?: emptyList()
                )
            )
        }

        return matches.distinctBy { it.name }.take(5)
    }
}
