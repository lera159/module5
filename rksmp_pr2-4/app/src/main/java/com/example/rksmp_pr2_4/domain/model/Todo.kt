package com.example.rksmp_pr2_4.domain.model

data class Todo(
	val id: Long = 0,
	val title: String,
	val description: String = "",
	val isCompleted: Boolean = false,
	val createdAt: Long = System.currentTimeMillis()
)