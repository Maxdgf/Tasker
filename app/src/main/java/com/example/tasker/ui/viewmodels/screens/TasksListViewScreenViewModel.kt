package com.example.tasker.ui.viewmodels.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TasksListViewScreenViewModel : ViewModel() {
    var text by mutableStateOf("")
    var desc by mutableStateOf("")

    fun setContent(content: String) { text = content }
    fun setDescription(text: String) { desc = text }

    fun contentNotEmpty() = text.isNotEmpty()
    fun descriptionNotEmpty() = desc.isNotEmpty()
}