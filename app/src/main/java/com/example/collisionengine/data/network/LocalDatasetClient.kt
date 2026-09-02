package com.example.collisionengine.data.network

import android.content.Context
import com.example.collisionengine.data.model.ProfileMatch
<<<<<<< HEAD
import com.example.collisionengine.data.models.Faculty
import com.example.collisionengine.data.models.Student
import com.example.collisionengine.data.models.Project
import com.example.collisionengine.data.models.Research
import com.example.collisionengine.data.models.Placement
=======
import com.example.collisionengine.data.models.*
>>>>>>> databricks/adithya
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
            // Load Projects
            context.assets.open("Projects.json").use { inputStream ->
                val jsonString = InputStreamReader(inputStream).readText()
                allProjects = jsonFormat.decodeFromString(jsonString)
            }
            // Load Research
            context.assets.open("Research.json").use { inputStream ->
                val jsonString = InputStreamReader(inputStream).readText()
                allResearch = jsonFormat.decodeFromString(jsonString)
            }
            // Load Placement
            context.assets.open("Placement.json").use { inputStream ->
                val jsonString = InputStreamReader(inputStream).readText()
                allPlacements = jsonFormat.decodeFromString(jsonString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun normalizeName(name: String?): String {
        if (name == null) return ""
        return name
            .lowercase()
<<<<<<< HEAD
            .replace(Regex("^(dr\\.|prof\\.|dr\\s|prof\\s)"), "")
=======
            .replace("dr.", "")
            .replace("dr ", "")
            .replace("prof.", "")
            .replace("prof ", "")
            .replace("professor", "")
            .replace("*", "")
            .replace("\"", "")
            .replace("'", "")
            .replace(Regex("""[<>]"""), "")
>>>>>>> databricks/adithya
            .trim()
    }

    private fun containsName(text: String, name: String): Boolean {
        val cleanName = normalizeName(name)
        if (cleanName.length < 3) return false
        val cleanText = normalizeName(text)
        return cleanText.contains(cleanName)
    }

    fun getStudentByName(name: String): Student? {
        val norm = normalizeName(name)
        if (norm.isBlank()) return null
        return allStudents.firstOrNull { 
            val sNorm = normalizeName(it.name)
            sNorm == norm || (sNorm.isNotBlank() && (sNorm.contains(norm) || norm.contains(sNorm)))
        }
    }

    fun getFacultyByName(name: String): Faculty? {
        val norm = normalizeName(name)
        if (norm.isBlank()) return null
        return allFaculty.firstOrNull { 
            val fNorm = normalizeName(it.name)
            fNorm == norm || (fNorm.isNotBlank() && (fNorm.contains(norm) || norm.contains(fNorm)))
        }
    }

    fun getProjectsForStudent(studentId: String?): List<Project> {
        if (studentId.isNullOrBlank()) return emptyList()
        return allProjects.filter { it.studentId == studentId }
    }

    fun getResearchForFaculty(facultyId: String?): List<Research> {
        if (facultyId.isNullOrBlank()) return emptyList()
        return allResearch.filter { it.personId == facultyId }
    }

    fun findMatches(
        query: String,
        aiResponse: String,
        extractedNames: List<String> = emptyList()
    ): List<ProfileMatch> {
        val matches = mutableListOf<ProfileMatch>()
        val matchedNamesLower = mutableSetOf<String>()

<<<<<<< HEAD
        // Search Students (Fuzzy: check if any part of the name matches)
        val matchedStudents = allStudents.filter { student ->
            val normStudent = normalizeName(student.name)
            normalizedNames.any { normStudent.contains(it) || it.contains(normStudent) }
        }
        matches.addAll(matchedStudents.map { student ->
            ProfileMatch(
                name = student.name ?: "Unknown",
                role = "${student.department ?: ""} • Year ${student.year ?: ""}",
                matchReasonTitle = "Project Contributor",
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
                matchReasonTitle = "Research Advisor",
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
                matchReasonTitle = "Project Contributor",
                matchReasonText = student.projects?.takeIf { it.isNotBlank() } ?: student.skills ?: "",
                tags = student.skills?.split(",")?.map { it.trim() }?.take(4) ?: emptyList()
            )
        })

        val matchedFaculty = allFaculty.filter { normalizeName(it.name).contains(q) }
        matches.addAll(matchedFaculty.map { faculty ->
            ProfileMatch(
                name = faculty.name ?: "Unknown",
                role = "Faculty • ${faculty.department ?: ""}",
                matchReasonTitle = "Research Advisor",
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
                    matchReasonTitle = "Project Contributor",
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
                    matchReasonTitle = "Research Advisor",
                    matchReasonText = faculty.expertise ?: "",
                    tags = faculty.researchInterests?.split(",")?.map { it.trim() }?.take(4) ?: emptyList()
                )
            )
        }

        return matches.distinctBy { it.name }.take(5)
=======
        // 1. Gather all candidate names from:
        //    a) extractedNames parameter (from LLM)
        //    b) Regex extracted names from AI response
        //    c) Direct scanning of database names in AI response
        val candidateNames = mutableListOf<String>()
        candidateNames.addAll(extractedNames)
        candidateNames.addAll(NvidiaClient.extractNamesRegex(aiResponse))

        allStudents.forEach { student ->
            val name = student.name ?: return@forEach
            if (name.isNotBlank() && containsName(aiResponse, name)) {
                candidateNames.add(name)
            }
        }

        allFaculty.forEach { faculty ->
            val name = faculty.name ?: return@forEach
            if (name.isNotBlank() && containsName(aiResponse, name)) {
                candidateNames.add(name)
            }
        }

        // 2. Resolve candidate names to Student / Faculty profiles
        for (candidate in candidateNames.distinct()) {
            val normCandidate = normalizeName(candidate)
            if (normCandidate.isBlank() || normCandidate in matchedNamesLower) continue

            // Check Students
            val student = allStudents.firstOrNull { 
                val sNorm = normalizeName(it.name)
                sNorm == normCandidate || (sNorm.isNotBlank() && (sNorm.contains(normCandidate) || normCandidate.contains(sNorm)))
            }
            if (student != null) {
                val sName = student.name ?: candidate
                if (sName.lowercase() !in matchedNamesLower) {
                    matchedNamesLower.add(sName.lowercase())
                    matches.add(createStudentMatch(student, query, aiResponse))
                }
                continue
            }

            // Check Faculty
            val faculty = allFaculty.firstOrNull { 
                val fNorm = normalizeName(it.name)
                fNorm == normCandidate || (fNorm.isNotBlank() && (fNorm.contains(normCandidate) || normCandidate.contains(fNorm)))
            }
            if (faculty != null) {
                val fName = faculty.name ?: candidate
                if (fName.lowercase() !in matchedNamesLower) {
                    matchedNamesLower.add(fName.lowercase())
                    matches.add(createFacultyMatch(faculty, query, aiResponse))
                }
            }
        }

        // 3. Fallback: If no direct names matched or fewer than 2 matches, search by keywords in projects, skills, research!
        if (matches.size < 2) {
            val keywordMatches = searchByKeywords(query, aiResponse, matchedNamesLower)
            for (km in keywordMatches) {
                if (matches.none { it.name.equals(km.name, ignoreCase = true) }) {
                    matches.add(km)
                    if (matches.size >= 3) break
                }
            }
        }

        return matches.take(4)
    }

    fun searchProfilesByNames(names: List<String>): List<ProfileMatch> {
        return findMatches("", "", names)
    }

    private fun createStudentMatch(student: Student, query: String, aiResponse: String): ProfileMatch {
        val projectsText = student.projects ?: ""
        val skillsText = student.skills ?: ""
        val researchText = student.researchInterests ?: ""

        var reasonTitle = "Related to your query"
        var reasonText = ""

        if (projectsText.isNotBlank()) {
            val individualProjects = projectsText.split(";").map { it.trim() }
            val mentionedProject = individualProjects.firstOrNull { proj ->
                proj.isNotBlank() && (aiResponse.contains(proj, ignoreCase = true) || query.contains(proj, ignoreCase = true))
            }
            reasonText = mentionedProject ?: projectsText
            reasonTitle = "Project Contributor"
        } else if (skillsText.isNotBlank()) {
            reasonText = skillsText
            reasonTitle = "Related to your query"
        } else if (researchText.isNotBlank()) {
            reasonText = researchText
            reasonTitle = "Research Interest"
        }

        val tags = mutableListOf<String>()
        student.skills?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.let { tags.addAll(it) }
        student.researchInterests?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.let { tags.addAll(it) }

        return ProfileMatch(
            name = student.name ?: "Unknown Student",
            role = "${student.department ?: "Student"} • Year ${student.year ?: "4"}",
            matchReasonTitle = reasonTitle,
            matchReasonText = reasonText.ifBlank { "Active student researcher" },
            tags = tags.distinct().take(4)
        )
    }

    private fun createFacultyMatch(faculty: Faculty, query: String, aiResponse: String): ProfileMatch {
        val expertiseText = faculty.expertise ?: ""
        val researchText = faculty.researchInterests ?: ""

        val reasonText = if (expertiseText.isNotBlank()) expertiseText else researchText

        val tags = mutableListOf<String>()
        faculty.researchInterests?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.let { tags.addAll(it) }
        faculty.expertise?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.let { tags.addAll(it) }

        return ProfileMatch(
            name = faculty.name ?: "Unknown Faculty",
            role = "Faculty • ${faculty.department ?: "Department"}",
            matchReasonTitle = "Related to your query",
            matchReasonText = reasonText.ifBlank { "Faculty domain expert" },
            tags = tags.distinct().take(4)
        )
    }

    private fun searchByKeywords(
        query: String,
        aiResponse: String,
        excludeNames: Set<String>
    ): List<ProfileMatch> {
        val stopWords = setOf(
            "the", "and", "for", "with", "this", "that", "from", "working", "poor",
            "good", "who", "what", "where", "project", "titled", "student", "faculty",
            "related", "only", "data", "provided", "have", "been", "was", "were",
            "are", "is", "about", "tell", "show", "give", "list", "name", "any", "all"
        )
        val combinedText = "$query $aiResponse".lowercase()
        val tokens = combinedText.split(Regex("[^a-zA-Z0-9]+"))
            .filter { it.length >= 3 && it !in stopWords }
            .distinct()

        if (tokens.isEmpty()) return emptyList()

        val scoredMatches = mutableListOf<Pair<ProfileMatch, Int>>()

        // Score students
        for (student in allStudents) {
            val name = student.name ?: continue
            if (name.lowercase() in excludeNames) continue

            var score = 0
            val sProjects = student.projects?.lowercase() ?: ""
            val sSkills = student.skills?.lowercase() ?: ""
            val sResearch = student.researchInterests?.lowercase() ?: ""
            val sDept = student.department?.lowercase() ?: ""

            for (token in tokens) {
                if (sProjects.contains(token)) score += 40
                if (sSkills.contains(token)) score += 25
                if (sResearch.contains(token)) score += 20
                if (sDept.contains(token)) score += 10
            }

            // Linked projects
            val linkedProjects = allProjects.filter { it.studentId == student.studentId }
            for (proj in linkedProjects) {
                val pText = "${proj.title} ${proj.description} ${proj.technologies}".lowercase()
                for (token in tokens) {
                    if (pText.contains(token)) score += 30
                }
            }

            if (score > 0) {
                scoredMatches.add(Pair(createStudentMatch(student, query, aiResponse), score))
            }
        }

        // Score faculty
        for (faculty in allFaculty) {
            val name = faculty.name ?: continue
            if (name.lowercase() in excludeNames) continue

            var score = 0
            val fExpertise = faculty.expertise?.lowercase() ?: ""
            val fResearch = faculty.researchInterests?.lowercase() ?: ""
            val fDept = faculty.department?.lowercase() ?: ""

            for (token in tokens) {
                if (fExpertise.contains(token)) score += 35
                if (fResearch.contains(token)) score += 25
                if (fDept.contains(token)) score += 10
            }

            // Linked research
            val linkedResearch = allResearch.filter { it.personId == faculty.facultyId }
            for (res in linkedResearch) {
                val rText = "${res.title} ${res.abstract} ${res.researchArea} ${res.currentlyWorkingOn}".lowercase()
                for (token in tokens) {
                    if (rText.contains(token)) score += 30
                }
            }

            if (score > 0) {
                scoredMatches.add(Pair(createFacultyMatch(faculty, query, aiResponse), score))
            }
        }

        return scoredMatches.sortedByDescending { it.second }.map { it.first }
>>>>>>> databricks/adithya
    }
}

