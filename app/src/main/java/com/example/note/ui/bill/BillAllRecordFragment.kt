package com.example.note.ui.bill

import android.os.SystemClock
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.note.MainActivity
import com.example.note.R
import com.example.note.base.BaseFragment
import com.example.note.databinding.FragmentBillAllRecordBinding
import com.example.note.model.BillRecord
import com.example.note.utils.TimeUtils
import com.example.note.utils.toastCover
import android.app.AlertDialog

/**
 * 某个月全部收支记录列表页面。
 */
class BillAllRecordFragment :
    BaseFragment<BillAllRecordViewModel, FragmentBillAllRecordBinding>() {

    private lateinit var adapter: BillRecordAdapter
    private var yearMonth: String = TimeUtils.getCurrentYearMonth()
    private var lastSwitchClickTime = 0L
    private var mode: String = MODE_MONTH
    private var categoryId: Long = -1L
    private var categoryName: String = ""

    override fun getLayoutId(): Int = R.layout.fragment_bill_all_record

    override fun initViewModel(): BillAllRecordViewModel =
        ViewModelProvider(this)[BillAllRecordViewModel::class.java]

    override fun initView() {
        readArgs()
        setupToolbar(if (isCategoryMode()) categoryName.ifBlank { "分类收支" } else "收支记录")

        dataBinding.rvAll.layoutManager = LinearLayoutManager(requireContext())
        adapter = BillRecordAdapter { record ->
            // 点击进入编辑
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_fragment_container, BillAddFragment.newEdit(record))
                .addToBackStack(null)
                .commit()
        }
        dataBinding.rvAll.adapter = adapter
        attachSwipeToDelete(dataBinding.rvAll)

        if (isCategoryMode()) {
            // 分类模式：不需要月份切换，标题直接显示分类名。
            dataBinding.tvPrevMonth.visibility = View.GONE
            dataBinding.tvNextMonth.visibility = View.GONE
            dataBinding.tvCurrentMonth.text = "${categoryName.ifBlank { "当前" }}分类"
            dataBinding.tvEmptyAll.text = "该分类暂无收支记录"
        } else {
            updateMonthText()
        }

        dataBinding.tvPrevMonth.setOnClickListener {
            if (isCategoryMode()) return@setOnClickListener
            if (!canSwitchMonth()) return@setOnClickListener
            yearMonth = prevMonth(yearMonth)
            updateMonthText()
            viewModel.loadByMonth(yearMonth)
        }
        dataBinding.tvNextMonth.setOnClickListener {
            if (isCategoryMode()) return@setOnClickListener
            if (!canSwitchMonth()) return@setOnClickListener
            // 不能超过当前月
            val current = TimeUtils.getCurrentYearMonth()
            val next = nextMonth(yearMonth)
            if (next > current) {
                "不能查看未来月份".toastCover()
                return@setOnClickListener
            }
            yearMonth = next
            updateMonthText()
            viewModel.loadByMonth(yearMonth)
        }
    }

    override fun initData() {
        viewModel.recordList.observe(viewLifecycleOwner) { list ->
            adapter.setList(list)
            dataBinding.tvEmptyAll.visibility =
                if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
        loadDataByMode()
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomBarVisible(false)
        loadDataByMode()
    }

    private fun readArgs() {
        mode = arguments?.getString(ARG_MODE) ?: MODE_MONTH
        categoryId = arguments?.getLong(ARG_CATEGORY_ID, -1L) ?: -1L
        categoryName = arguments?.getString(ARG_CATEGORY_NAME) ?: ""
    }

    private fun isCategoryMode(): Boolean = mode == MODE_CATEGORY && categoryId > 0

    private fun loadDataByMode() {
        if (isCategoryMode()) {
            viewModel.loadByCategory(categoryId)
        } else {
            viewModel.loadByMonth(yearMonth)
        }
    }

    private fun updateMonthText() {
        // 显示为 yyyy年MM月
        val parts = yearMonth.split("-")
        val y = parts.getOrNull(0) ?: ""
        val m = parts.getOrNull(1) ?: ""
        dataBinding.tvCurrentMonth.text = "${y}年${m}月"
    }

    private fun canSwitchMonth(): Boolean {
        val now = SystemClock.elapsedRealtime()
        if (now - lastSwitchClickTime < 1000L) return false
        lastSwitchClickTime = now
        return true
    }

    private fun attachSwipeToDelete(rv: RecyclerView) {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val item = adapter.data.getOrNull(pos) ?: run {
                    adapter.notifyItemChanged(pos)
                    return
                }
                AlertDialog.Builder(requireContext())
                    .setMessage("确定删除该记录吗？")
                    .setPositiveButton("删除") { _, _ ->
                        viewModel.deleteRecord(
                            viewLifecycleOwner,
                            item.recordId,
                            onSuccess = { viewModel.loadByMonth(yearMonth) },
                            onFail = { msg ->
                                msg.toastCover()
                                adapter.notifyItemChanged(pos)
                            }
                        )
                    }
                    .setNegativeButton("取消") { _, _ -> adapter.notifyItemChanged(pos) }
                    .setOnCancelListener { adapter.notifyItemChanged(pos) }
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
                    val x = itemView.right - textPaint.measureText(text) - 40
                    val y = itemView.top + itemView.height / 2f + 15
                    c.drawText(text, x, y, textPaint)
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(rv)
    }

    private fun prevMonth(ym: String): String {
        val parts = ym.split("-")
        var y = parts.getOrNull(0)?.toIntOrNull() ?: 2026
        var m = parts.getOrNull(1)?.toIntOrNull() ?: 1
        m--
        if (m < 1) {
            m = 12
            y--
        }
        return "%04d-%02d".format(y, m)
    }

    private fun nextMonth(ym: String): String {
        val parts = ym.split("-")
        var y = parts.getOrNull(0)?.toIntOrNull() ?: 2026
        var m = parts.getOrNull(1)?.toIntOrNull() ?: 1
        m++
        if (m > 12) {
            m = 1
            y++
        }
        return "%04d-%02d".format(y, m)
    }

    companion object {
        private const val ARG_MODE = "arg_mode"
        private const val ARG_CATEGORY_ID = "arg_category_id"
        private const val ARG_CATEGORY_NAME = "arg_category_name"
        private const val MODE_MONTH = "month"
        private const val MODE_CATEGORY = "category"

        fun newInstance() = BillAllRecordFragment().apply {
            arguments = android.os.Bundle().apply {
                putString(ARG_MODE, MODE_MONTH)
            }
        }

        fun newForCategory(categoryId: Long, categoryName: String) = BillAllRecordFragment().apply {
            arguments = android.os.Bundle().apply {
                putString(ARG_MODE, MODE_CATEGORY)
                putLong(ARG_CATEGORY_ID, categoryId)
                putString(ARG_CATEGORY_NAME, categoryName)
            }
        }
    }
}

