package com.example.note.ui.note

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.note.MyApplication
import com.example.note.base.BaseViewModel
import com.example.note.model.Note
import com.example.note.model.RequestType
import com.example.note.utils.cookie_tool.JSONBodyBuilder
import com.example.note.utils.cookie_tool.JSONBodyBuilder.addParams
import com.example.note.utils.cookie_tool.JSONBodyBuilder.submit

class NoteEditViewModel : BaseViewModel() {

    val saveResult = MutableLiveData<RequestType<Note>?>()

    fun addNote(
        owner: LifecycleOwner,
        title: String,
        content: String?,
        planTime: Long,
        priority: Int,
        remindTime: Long?
    ) {
        val body = JSONBodyBuilder.build()
            .addParams("title", title)
            .addParams("content", content)
            .addParams("planTime", planTime)
            .addParams("priority", priority)
            .addParams("remindTime", remindTime)
            .submit()
        MyApplication.apiService.addNote(body).observe(owner) { saveResult.postValue(it) }
    }

    fun updateNote(
        owner: LifecycleOwner,
        noteId: Int,
        title: String,
        content: String?,
        planTime: Long,
        priority: Int,
        remindTime: Long?
    ) {
        val body = JSONBodyBuilder.build()
            .addParams("noteId", noteId)
            .addParams("title", title)
            .addParams("content", content)
            .addParams("planTime", planTime)
            .addParams("priority", priority)
            .addParams("remindTime", remindTime)
            .submit()
        MyApplication.apiService.updateNote(body).observe(owner) { saveResult.postValue(it) }
    }
}

