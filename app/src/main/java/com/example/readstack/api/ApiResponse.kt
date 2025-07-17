package com.example.readstack.api

data class ApiResponse(
    val docs: List<Doc>,
    val documentation_url: String,
    val numFound: Int,
    val numFoundExact: Boolean,
    val num_found: Int,
    val offset: Any?,
    val q: String,
    val start: Int
)