package com.example.note.ui.note

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.note.MainActivity
import com.example.note.R
import com.example.note.base.BaseFragment
import com.example.note.databinding.FragmentNoteAddEditBinding
import com.example.note.model.Note
import com.example.note.utils.TimeUtils
import com.example.note.utils.ReminderScheduler
import com.example.note.utils.toastCover
import java.util.Calendar

class NoteAddEditFragment : BaseFragment<NoteEditViewModel, FragmentNoteAddEditBinding>() {

    private lateinit var sharedVm: NoteViewModel
    private var editingNote: Note? = null

    // 新建日程默认计划时间为当前时间 + 30 分钟（可手动修改）
    private var planTime: Long = System.currentTimeMillis() + 30 * 60 * 1000
    // 新建日程默认提醒时间为当前时间 + 1 小时（可手动修改；勾选提醒时作为初始值）
    private var remindTime: Long? = System.currentTimeMillis() + 60 * 60 * 1000

    override fun getLayoutId(): Int = R.layout.fragment_note_add_edit

    override fun initViewModel(): NoteEditViewModel =
        ViewModelProvider(this)[NoteEditViewModel::class.java]

    override fun initView() {
        // 统一标题栏（默认“添加日程”，编辑时会在下方改成“编辑日程”）
        setupToolbar("添加日程")

        sharedVm = ViewModelProvider(requireActivity())[NoteViewModel::class.java]

        editingNote = arguments?.getSerializable(ARG_NOTE) as? Note
        if (editingNote != null) {
            setToolbarTitle("编辑日程")
            dataBinding.etTitle.setText(editingNote?.title ?: "")
            dataBinding.etContent.setText(editingNote?.content ?: "")
            planTime = editingNote?.planTime ?: planTime
            remindTime = editingNote?.remindTime
            when (editingNote?.priority ?: 1) {
                2 -> dataBinding.rbP2.isChecked = true
                3 -> dataBinding.rbP3.isChecked = true
                else -> dataBinding.rbP1.isChecked = true
            }
            if (remindTime != null) {
                dataBinding.swRemind.isChecked = true
                enableRemind(true)
                dataBinding.tvRemindTime.text = TimeUtils.formatYmdHm(remindTime!!)
            }
        }

        dataBinding.tvPlanTime.text = TimeUtils.formatYmdHm(planTime)
        dataBinding.tvPlanTime.setOnClickListener { pickDateTime(planTime) { planTime = it; dataBinding.tvPlanTime.text = TimeUtils.formatYmdHm(it) } }

        dataBinding.swRemind.setOnCheckedChangeListener { _, checked ->
            enableRemind(checked)
            if (!checked) {
                remindTime = null
                dataBinding.tvRemindTime.text = "选择提醒时间"
            } else {
                val init = remindTime ?: planTime
                pickDateTime(init) {
                    remindTime = it
                    dataBinding.tvRemindTime.text = TimeUtils.formatYmdHm(it)
                }
            }
        }
        dataBinding.tvRemindTime.setOnClickListener {
            val init = remindTime ?: planTime
            pickDateTime(init) {
                remindTime = it
                dataBinding.tvRemindTime.text = TimeUtils.formatYmdHm(it)
            }
        }

        dataBinding.btnSubmit.setOnClickListener {
            val title = dataBinding.etTitle.text.toString().trim()
            val content = dataBinding.etContent.text.toString().trim().ifEmpty { null }
            if (title.isEmpty() || title.length > 50) {
                "标题需为1-50字".toastCover()
                return@setOnClickListener
            }
            val priority = when (dataBinding.rgPriority.checkedRadioButtonId) {
                R.id.rbP2 -> 2
                R.id.rbP3 -> 3
                else -> 1
            }

            val note = editingNote
            if (note == null) {
                viewModel.addNote(viewLifecycleOwner, title, content, planTime, priority, remindTime)
            } else {
                viewModel.updateNote(viewLifecycleOwner, note.noteId, title, content, planTime, priority, remindTime)
            }
        }
    }

    override fun initData() {
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            if (result == null) return@observe
            result.message.toastCover()
            if (result.code == 200 && result.data != null) {
                val saved = result.data
                // 更新列表
                sharedVm.addOrReplace(saved)
                // 先取消旧提醒，再根据新数据决定是否重新设置
                editingNote?.let { old ->
                    ReminderScheduler.cancel(requireContext(), old.noteId)
                }
                if (saved.remindTime != null) {
                    ReminderScheduler.schedule(requireContext(), saved)
                }
                parentFragmentManager.popBackStack()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomBarVisible(false)
    }

    private fun enableRemind(enable: Boolean) {
        dataBinding.tvRemindTime.isEnabled = enable
        dataBinding.tvRemindTime.alpha = if (enable) 1f else 0.5f
    }

    private fun pickDateTime(initial: Long, onPicked: (Long) -> Unit) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = initial
        DatePickerDialog(
            requireContext(),
            { _, y, m, d ->
                TimePickerDialog(
                    requireContext(),
                    { _, hh, mm ->
                        val c2 = Calendar.getInstance()
                        c2.set(y, m, d, hh, mm, 0)
                        c2.set(Calendar.MILLISECOND, 0)
                        onPicked(c2.timeInMillis)
                    },
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true
                ).show()
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    companion object {
        private const val ARG_NOTE = "arg_note"

        fun newAdd(): NoteAddEditFragment = NoteAddEditFragment()

        fun newEdit(note: Note): NoteAddEditFragment = NoteAddEditFragment().apply {
            arguments = Bundle().apply { putSerializable(ARG_NOTE, note) }
        }
    }
}

