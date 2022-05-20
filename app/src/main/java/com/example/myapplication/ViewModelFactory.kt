package com.example.myapplication

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.myapplication.data.source.SummonsRepository
import com.example.myapplication.ui.summons.SummonsViewModel

/**
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val summonsRepository: SummonsRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ) = with(modelClass) {
        when {
            isAssignableFrom(SummonsViewModel::class.java) ->
                SummonsViewModel(summonsRepository)
//            isAssignableFrom(TaskDetailViewModel::class.java)   ->
//                TaskDetailViewModel(tasksRepository)
//            isAssignableFrom(AddEditTaskViewModel::class.java) ->
//                AddEditTaskViewModel(tasksRepository)
//            isAssignableFrom(TasksViewModel::class.java) ->
//                TasksViewModel(tasksRepository, handle)
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}
