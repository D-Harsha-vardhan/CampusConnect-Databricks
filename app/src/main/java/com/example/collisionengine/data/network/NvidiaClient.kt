package com.example.collisionengine.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object NvidiaClient {
<<<<<<< HEAD
    // Optional: Add your Nvidia API Key here if you want to use the LLM for name extraction
    private const val NVIDIA_API_KEY = "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"
=======
    private const val NVIDIA_API_KEY = "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"
>>>>>>> databricks/adithya
    
    suspend fun extractNames(text: String): List<String> = withContext(Dispatchers.IO) {
        val regexNames = extractNamesRegex(text)
        
        if (NVIDIA_API_KEY.isBlank()) {
            return@withContext regexNames
        }
        
        try {
            val url = URL("https://integrate.api.nvidia.com/v1/chat/completions")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.connectTimeout = 5000
            conn.readTimeout = 7000
            conn.setRequestProperty("Authorization", "Bearer $NVIDIA_API_KEY")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            val systemPrompt = "Extract ONLY the human names (students, faculty, professors, researchers) mentioned in the text. Return them as a comma-separated list. Do not include extra commentary or tags."
            
            val messages = JSONArray()
                .put(JSONObject().put("role", "system").put("content", systemPrompt))
                .put(JSONObject().put("role", "user").put("content", text))

            val payload = JSONObject()
                .put("model", "nvidia/nemotron-3.5-lightning-30b-a3b")
                .put("messages", messages)
                .put("max_tokens", 256)
                .put("temperature", 0.0)

            OutputStreamWriter(conn.outputStream).use { it.write(payload.toString()) }

            if (conn.responseCode in 200..299) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)
                val choices = json.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val message = choices.getJSONObject(0).optJSONObject("message")
                    val content = message?.optString("content") ?: ""
                    if (content.isNotBlank()) {
                        val cleaned = cleanAndParseNames(content)
                        if (cleaned.isNotEmpty()) {
                            val combined = (cleaned + regexNames).distinct()
                            return@withContext combined
                        }
<<<<<<< HEAD
                        var cleanNamesList = namesList.replace(Regex("""<[^>]*>"""), "") // strip any html tags
                        cleanNamesList = cleanNamesList.replace(Regex("""[*_`~]"""), "") // strip markdown
                        return@withContext cleanNamesList.split(",").map { it.trim() }.filter { it.isNotBlank() }
=======
>>>>>>> databricks/adithya
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return@withContext regexNames
    }

<<<<<<< HEAD
    private fun extractNamesRegex(text: String): List<String> {
        val extracted = mutableListOf<String>()
        
        // Pattern 1: Look for names bolded by Databricks Genie: **First Last**
        val regexBold = Regex("""\*\*(.*?)\*\*""")
        extracted.addAll(regexBold.findAll(text).map { it.groupValues[1].trim() })

        // Pattern 2: Look for "student [Name]", "Dr. [Name]", "Prof. [Name]"
        val regexTitle = Regex("""(?:student|faculty|prof\.|dr\.|by)\s+([A-Z][a-z]+(?:\s+[A-Z][a-z]+){1,2})""")
        extracted.addAll(regexTitle.findAll(text).map { it.groupValues[1].trim() })

        // Filter out obvious non-names like "fake news detection"
        return extracted.filter { name -> 
            val cleanName = name.replace(Regex("""[*_`~<>]"""), "")
            val words = cleanName.split(" ")
            words.size in 2..3 && words.all { it.isNotEmpty() && it.first().isUpperCase() }
        }.map { it.replace(Regex("""[*_`~<>]"""), "") }.distinct()
=======
    private fun cleanAndParseNames(content: String): List<String> {
        // Strip XML/HTML tags like <names>, </names>, <...>, etc.
        var text = content.replace(Regex("""<[^>]*>"""), " ")
        // Strip markdown bold and quotes
        text = text.replace("**", "").replace("\"", "").replace("'", "")
        // Split by comma, newlines, or semicolons
        val items = text.split(Regex("[,;\n\r]+"))
        return items.map { sanitizeName(it) }.filter { it.isNotBlank() && isValidName(it) }
    }

    private fun sanitizeName(raw: String): String {
        return raw.trim()
            .replace(Regex("""^[0-9]+[\.\-\)]\s*"""), "") // remove "1. ", "2. "
            .replace(Regex("""^[\-\*\•]\s*"""), "") // remove bullets
            .replace(Regex("""[\(\)\[\]]"""), "")
            .replace("\"", "")
            .replace("'", "")
            .trim()
    }

    private fun isValidName(name: String): Boolean {
        val clean = name.replace("Dr. ", "", ignoreCase = true)
            .replace("Dr.", "", ignoreCase = true)
            .replace("Prof. ", "", ignoreCase = true)
            .replace("Prof.", "", ignoreCase = true)
            .trim()
        val words = clean.split(Regex("""\s+""")).filter { it.isNotBlank() }
        if (words.size !in 2..4) return false
        val invalidWords = setOf(
            "electronics", "communication", "computer", "science", "engineering",
            "technology", "department", "project", "battery", "management", "system",
            "discontinued", "completed", "student", "faculty", "unknown", "artificial",
            "intelligence", "information", "electrical", "mechanical", "chemical"
        )
        if (words.any { it.lowercase() in invalidWords }) return false
        return words.all { it.firstOrNull()?.isLetter() == true }
    }

    fun extractNamesRegex(text: String): List<String> {
        val results = mutableListOf<String>()
        
        // 1. Look for bolded text in Databricks Genie: **First Last**
        val boldRegex = Regex("""\*\*(.*?)\*\*""")
        val boldMatches = boldRegex.findAll(text)
        for (m in boldMatches) {
            val candidate = sanitizeName(m.groupValues[1])
            if (isValidName(candidate) && candidate !in results) {
                results.add(candidate)
            }
        }
        
        // 2. Pattern: "student First Last", "by First Last", "Dr. First Last", "Prof. First Last"
        val patternRegex = Regex("""(?:student|faculty|by|Dr\.|Prof\.|Professor)\s+([A-Z][a-z]+(?:\s+[A-Z][a-z]+){1,2})""")
        val patternMatches = patternRegex.findAll(text)
        for (m in patternMatches) {
            val candidate = sanitizeName(m.groupValues[1])
            if (isValidName(candidate) && candidate !in results) {
                results.add(candidate)
            }
        }
        
        return results
>>>>>>> databricks/adithya
    }
}
