package com.example.readstack.api

data class SubjectResponse(
    val key: String,
    val name: String,
    val subject_type: String,
    val work_count: Int,
    val works: List<Work>
)

data class Work(
    val key: String,
    val title: String,
    val cover_id: Int?,
    val authors: List<Author>?
)

data class Author(
    val name: String,
    val key: String
)

