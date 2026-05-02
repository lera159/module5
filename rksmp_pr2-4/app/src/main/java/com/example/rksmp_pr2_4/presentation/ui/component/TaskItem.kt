package com.example.rksmp_pr2_4.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.rksmp_pr2_4.domain.model.Todo

@Composable
fun TaskItem(
	task: Todo,
	isCompletedColorEnabled: Boolean,
	completedBackgroundColor: Long,
	onToggleComplete: () -> Unit,
	onDelete: () -> Unit,
	onEdit: () -> Unit
) {
	val backgroundColor = if (task.isCompleted && isCompletedColorEnabled) {
		Color(completedBackgroundColor).copy(alpha = 0.2f)
	} else {
		MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
	}

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable { onEdit() },
		shape = RoundedCornerShape(12.dp),
		colors = CardDefaults.cardColors(containerColor = backgroundColor)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			IconButton(onClick = onToggleComplete) {
				Icon(
					imageVector = if (task.isCompleted) Icons.Default.CheckCircle
					else Icons.Default.RadioButtonUnchecked,
					contentDescription = if (task.isCompleted) "Выполнено" else "Не выполнено",
					tint = if (task.isCompleted) Color(completedBackgroundColor)
					else MaterialTheme.colorScheme.onSurfaceVariant
				)
			}

			Spacer(modifier = Modifier.width(8.dp))

			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = task.title,
					style = MaterialTheme.typography.titleMedium,
					textDecoration = if (task.isCompleted) TextDecoration.LineThrough
					else TextDecoration.None,
					color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
					else MaterialTheme.colorScheme.onSurface
				)
				if (task.description.isNotBlank()) {
					Text(
						text = task.description,
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
						maxLines = 2
					)
				}
			}

			IconButton(onClick = onDelete) {
				Icon(
					Icons.Default.Delete,
					contentDescription = "Удалить",
					tint = MaterialTheme.colorScheme.error
				)
			}
		}
	}
}