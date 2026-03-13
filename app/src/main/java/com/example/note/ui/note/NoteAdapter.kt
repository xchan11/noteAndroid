package com.example.note.ui.note

import android.widget.CheckBox
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.note.R
import com.example.note.model.Note
import com.example.note.utils.TimeUtils

class NoteAdapter(
    private val onToggle: (note: Note, position: Int, newStatus: Int, oldStatus: Int) -> Unit,
    private val onItemClick: (note: Note) -> Unit
) : BaseQuickAdapter<Note, BaseViewHolder>(R.layout.item_note) {

    // 数据一致性兜底逻辑：CheckBox 1s 防抖
    private val lastToggleAt = hashMapOf<Int, Long>() // noteId -> last click time

    override fun convert(holder: BaseViewHolder, item: Note) {
        holder.getView<TextView>(R.id.tvTitle).text = item.title ?: ""
        holder.getView<TextView>(R.id.tvContent).text = item.content ?: ""
        holder.getView<TextView>(R.id.tvTime).text = TimeUtils.formatYmdHm(item.planTime)

        val priorityView = holder.getView<android.view.View>(R.id.viewPriority)
        val priorityBg = when (item.priority) {
            2 -> R.drawable.shape_priority_yellow
            3 -> R.drawable.shape_priority_red
            else -> R.drawable.shape_priority_blue
        }
        priorityView.setBackgroundResource(priorityBg)

        val cb = holder.getView<CheckBox>(R.id.cbDone)
        cb.setOnCheckedChangeListener(null)
        cb.isChecked = item.status == 1
        cb.setOnCheckedChangeListener { _, isChecked ->
            val now = System.currentTimeMillis()
            val last = lastToggleAt[item.noteId] ?: 0L
            if (now - last < 1000) {
                // 防抖：回滚 UI 到当前 item.status
                cb.setOnCheckedChangeListener(null)
                cb.isChecked = item.status == 1
                cb.setOnCheckedChangeListener { _, checkedAgain ->
                    val ns = if (checkedAgain) 1 else 0
                    onToggle(item, holder.adapterPosition, ns, item.status)
                }
                return@setOnCheckedChangeListener
            }
            lastToggleAt[item.noteId] = now

            val newStatus = if (isChecked) 1 else 0
            val oldStatus = item.status
            onToggle(item, holder.adapterPosition, newStatus, oldStatus)
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }
}

