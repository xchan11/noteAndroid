package com.example.note.ui.main

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.note.MainActivity
import com.example.note.R
import com.example.note.base.BaseFragment
import com.example.note.databinding.FragmentNoteBinding
import com.example.note.model.Note
import com.example.note.ui.note.NoteAdapter
import com.example.note.ui.note.NoteAddEditFragment
import com.example.note.ui.note.NoteViewModel
import com.example.note.utils.ReminderScheduler
import com.example.note.utils.toastCover

class NoteFragment : BaseFragment<NoteViewModel, FragmentNoteBinding>() {

    private lateinit var todoAdapter: NoteAdapter
    private lateinit var doneAdapter: NoteAdapter

    override fun getLayoutId(): Int = R.layout.fragment_note

    // 用 activity 级 ViewModel，保证 NoteAddEditFragment 返回后能直接更新本地列表
    override fun initViewModel(): NoteViewModel =
        ViewModelProvider(requireActivity())[NoteViewModel::class.java]

    override fun initView() {
        dataBinding.rvTodo.layoutManager = LinearLayoutManager(requireContext())
        dataBinding.rvDone.layoutManager = LinearLayoutManager(requireContext())

        todoAdapter = NoteAdapter(
            // 数据一致性兜底逻辑：先本地更新UI，失败回滚；成功再移动列表
            onToggle = { note, position, newStatus, oldStatus ->
                // 1) 本地先更新 UI（仅更新当前列表，不移动）
                note.status = newStatus
                if (position >= 0) {
                    // 立即刷新当前 item（让用户感知）
                    if (oldStatus == 0) todoAdapter.notifyItemChanged(position) else doneAdapter.notifyItemChanged(position)
                }
                // 2) 请求更新状态
                viewModel.updateStatus(
                    viewLifecycleOwner,
                    note,
                    newStatus,
                    onSuccessMove = { "更新成功".toastCover() },
                    onFailRollback = { msg ->
                        // 3) 失败回滚
                        note.status = oldStatus
                        msg.toastCover()
                        if (position >= 0) {
                            if (oldStatus == 0) todoAdapter.notifyItemChanged(position) else doneAdapter.notifyItemChanged(position)
                        }
                    }
                )
            },
            onItemClick = { note -> openEdit(note) }
        )
        doneAdapter = NoteAdapter(
            onToggle = { note, position, newStatus, oldStatus ->
                note.status = newStatus
                if (position >= 0) {
                    if (oldStatus == 0) todoAdapter.notifyItemChanged(position) else doneAdapter.notifyItemChanged(position)
                }
                viewModel.updateStatus(
                    viewLifecycleOwner,
                    note,
                    newStatus,
                    onSuccessMove = { "更新成功".toastCover() },
                    onFailRollback = { msg ->
                        note.status = oldStatus
                        msg.toastCover()
                        if (position >= 0) {
                            if (oldStatus == 0) todoAdapter.notifyItemChanged(position) else doneAdapter.notifyItemChanged(position)
                        }
                    }
                )
            },
            onItemClick = { note -> openEdit(note) }
        )

        dataBinding.rvTodo.adapter = todoAdapter
        dataBinding.rvDone.adapter = doneAdapter

        dataBinding.tvAddNote.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_fragment_container, NoteAddEditFragment.newAdd())
                .addToBackStack(null)
                .commit()
        }

        attachSwipeToDelete(dataBinding.rvTodo, isTodo = true)
        attachSwipeToDelete(dataBinding.rvDone, isTodo = false)
    }

    override fun initData() {
        viewModel.todoList.observe(viewLifecycleOwner) { list ->
            todoAdapter.setList(list)
        }
        viewModel.doneList.observe(viewLifecycleOwner) { list ->
            doneAdapter.setList(list)
        }
        viewModel.loadAll()
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomBarVisible(true)
        // 数据一致性兜底逻辑：页面可见时静默拉全量同步（失败不提示）
        viewModel.loadAll(showToastOnFail = false)
    }

    private fun openEdit(note: Note) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_fragment_container, NoteAddEditFragment.newEdit(note))
            .addToBackStack(null)
            .commit()
    }

    private fun attachSwipeToDelete(rv: RecyclerView, isTodo: Boolean) {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // 兼容旧版本：bindingAdapterPosition 可能不可用，用 adapterPosition
                val pos = viewHolder.adapterPosition
                val note = if (isTodo) todoAdapter.data.getOrNull(pos) else doneAdapter.data.getOrNull(pos)
                if (note == null) {
                    // 兜底：恢复
                    if (isTodo) todoAdapter.notifyItemChanged(pos) else doneAdapter.notifyItemChanged(pos)
                    return
                }
                AlertDialog.Builder(requireContext())
                    .setMessage("确认删除该日程？")
                    .setPositiveButton("删除") { _, _ ->
                        // 先取消本地提醒，再删除后端数据
                        ReminderScheduler.cancel(requireContext(), note.noteId)
                        // 数据一致性兜底逻辑：仅当接口成功才移除本地
                        viewModel.deleteNote(
                            viewLifecycleOwner,
                            note.noteId,
                            onSuccess = { "删除成功".toastCover() },
                            onFail = { msg ->
                                msg.toastCover()
                                if (isTodo) todoAdapter.notifyItemChanged(pos) else doneAdapter.notifyItemChanged(pos)
                            }
                        )
                    }
                    .setNegativeButton("取消") { _, _ ->
                        // 回滚滑动
                        if (isTodo) todoAdapter.notifyItemChanged(pos) else doneAdapter.notifyItemChanged(pos)
                    }
                    .setOnCancelListener {
                        if (isTodo) todoAdapter.notifyItemChanged(pos) else doneAdapter.notifyItemChanged(pos)
                    }
                    .show()
            }

            override fun onChildDraw(
                c: android.graphics.Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                // 简单红底 + “删除”文字（左滑时可见）
                val itemView = viewHolder.itemView
                if (dX < 0) {
                    val paint = android.graphics.Paint().apply { color = 0xFFD0021B.toInt() }
                    c.drawRect(
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat(),
                        paint
                    )
                    val textPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 40f
                        isAntiAlias = true
                    }
                    val text = "删除"
                    val textWidth = textPaint.measureText(text)
                    val x = itemView.right - textWidth - 40
                    val y = itemView.top + itemView.height / 2f + 15
                    c.drawText(text, x, y, textPaint)
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(rv)
    }
}

