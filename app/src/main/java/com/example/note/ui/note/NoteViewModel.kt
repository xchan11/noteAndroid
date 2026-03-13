package com.example.note.ui.note

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.note.MyApplication
import com.example.note.auth.AuthPrefs
import com.example.note.base.BaseViewModel
import com.example.note.model.Note
import com.example.note.utils.cookie_tool.JSONBodyBuilder
import com.example.note.utils.cookie_tool.JSONBodyBuilder.addParams
import com.example.note.utils.cookie_tool.JSONBodyBuilder.submit
import com.example.note.utils.isOk
import com.example.note.utils.toastCover

class NoteViewModel : BaseViewModel() {

    val todoList = MutableLiveData<MutableList<Note>>(mutableListOf())
    val doneList = MutableLiveData<MutableList<Note>>(mutableListOf())

    fun loadAll(showToastOnFail: Boolean = true) {
        val owner = lifecycleOwner ?: return
        val ctx = context as? Context ?: return
        MyApplication.apiService.getNoteList().observe(owner) { result ->
            if (result.isOk()) {
                val all = result.data ?: emptyList()
                val todo = all.filter { it.status == 0 }.toMutableList()
                val done = all.filter { it.status == 1 }.toMutableList()
                todoList.postValue(todo)
                doneList.postValue(done)
            } else {
                if (result?.code == 401) {
                    AuthPrefs.setLoggedIn(ctx, false)
                    MyApplication.cookieJar.clear()
                }
                // 数据一致性兜底逻辑：静默同步失败不提示
                if (showToastOnFail) {
                    val msg = result?.message?.takeIf { it.isNotBlank() } ?: "刷新失败，请重试"
                    msg.toastCover()
                }
            }
        }
    }

    fun addOrReplace(note: Note) {
        // 编辑/新增成功后：本地替换，不重新请求列表
        val todo = (todoList.value ?: mutableListOf()).toMutableList()
        val done = (doneList.value ?: mutableListOf()).toMutableList()

        fun replace(list: MutableList<Note>): Boolean {
            val idx = list.indexOfFirst { it.noteId == note.noteId }
            if (idx >= 0) {
                list[idx] = note
                return true
            }
            return false
        }

        val replaced = replace(todo) || replace(done)
        if (!replaced) {
            // 新增：按 status 放入
            if (note.status == 1) done.add(0, note) else todo.add(0, note)
        } else {
            // 编辑：若 status 发生变化，移动列表
            val nowInTodo = todo.any { it.noteId == note.noteId }
            val nowInDone = done.any { it.noteId == note.noteId }
            if (note.status == 0 && nowInDone) {
                done.removeAll { it.noteId == note.noteId }
                todo.add(0, note)
            }
            if (note.status == 1 && nowInTodo) {
                todo.removeAll { it.noteId == note.noteId }
                done.add(0, note)
            }
        }

        todoList.postValue(todo)
        doneList.postValue(done)
    }

    fun updateStatus(
        owner: LifecycleOwner,
        note: Note,
        newStatus: Int,
        onSuccessMove: () -> Unit,
        onFailRollback: (msg: String) -> Unit
    ) {
        val body = JSONBodyBuilder.build()
            .addParams("noteId", note.noteId)
            .addParams("status", newStatus)
            .submit()
        MyApplication.apiService.updateNoteStatus(body).observe(owner) { result ->
            if (result != null && result.code == 200) {
                // 成功：本地移动
                moveBetweenLists(note, newStatus)
                onSuccessMove()
            } else {
                val msg = result?.message?.takeIf { it.isNotBlank() } ?: "网络错误，状态更新失败"
                onFailRollback(msg)
            }
        }
    }

    private fun moveBetweenLists(note: Note, newStatus: Int) {
        val todo = (todoList.value ?: mutableListOf()).toMutableList()
        val done = (doneList.value ?: mutableListOf()).toMutableList()
        todo.removeAll { it.noteId == note.noteId }
        done.removeAll { it.noteId == note.noteId }
        note.status = newStatus
        if (newStatus == 1) done.add(0, note) else todo.add(0, note)
        todoList.postValue(todo)
        doneList.postValue(done)
    }

    fun deleteNote(
        owner: LifecycleOwner,
        noteId: Int,
        onSuccess: () -> Unit,
        onFail: (msg: String) -> Unit
    ) {
        // 后端改为：DELETE /note/delete?noteId=1（Retrofit 原生支持）
        MyApplication.apiService.deleteNote(noteId).observe(owner) { result ->
            if (result != null && result.code == 200) {
                val todo = (todoList.value ?: mutableListOf()).toMutableList()
                val done = (doneList.value ?: mutableListOf()).toMutableList()
                todo.removeAll { it.noteId == noteId }
                done.removeAll { it.noteId == noteId }
                todoList.postValue(todo)
                doneList.postValue(done)
                onSuccess()
            } else {
                val msg = result?.message?.takeIf { it.isNotBlank() } ?: "网络错误，请检查网络后重试"
                onFail(msg)
            }
        }
    }
}

