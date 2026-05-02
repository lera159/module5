package com.example.rksmp_pr2_4.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Long = 0,
	val title: String,
	val description: String = "",
	val isCompleted: Boolean = false,
	val createdAt: Long = System.currentTimeMillis()
)