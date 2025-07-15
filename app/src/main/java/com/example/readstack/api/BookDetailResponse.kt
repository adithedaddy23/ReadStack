package com.example.readstack.api

data class BookDetailResponse(
    val description: Any?, // Changed to Any to handle both cases
    val title: String?,
    val covers: List<Int>?,
    val subject_places: List<String>?,
    val subjects: List<String>?,
    val subject_people: List<String>?,
    val subject_times: List<String>?,
    val key: String?,
    val first_publish_date: String?,
    val excerpts: List<Excerpt>?,
) {
    fun getDescriptionText(): String? {
        return when (description) {
            is String -> description
            is Map<*, *> -> (description["value"] as? String)
            else -> null
        }
    }
}

data class Excerpt(
    val comment: String?,
    val text: String?
)

