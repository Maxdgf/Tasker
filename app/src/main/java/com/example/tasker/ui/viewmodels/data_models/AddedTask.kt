package com.example.tasker.ui.viewmodels.data_models

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
data class AddedTask(
    val id: String = UUID.randomUUID().toString(),
    val taskId: String,
    val content: String,
    val description: String? = null
)
