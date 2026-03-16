package com.example.note.ui.bill

import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.note.MainActivity
import com.example.note.R
import com.example.note.base.BaseFragment
import com.example.note.databinding.FragmentBillBinding
import com.example.note.model.BillRecord
import com.example.note.utils.TimeUtils
import com.example.note.utils.toastCover
import android.app.AlertDialog

/**
 * 记账首页：预算栏、快速记账、本月概览、最近流水（左滑删除）。
 */
class BillFragment : BaseFragment<BillViewModel, FragmentBillBinding>() {

    private lateinit var recordAdapter: BillRecordAdapter

    override fun getLayoutId(): Int = R.layout.fragment_bill

    override fun initViewModel(): BillViewModel =
        ViewModelProvider(this)[BillViewModel::class.java]

    override fun initView() {
        dataBinding.layoutBudget.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_fragment_container, BudgetSetFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        dataBinding.tvQuickAdd.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_fragment_container, BillAddFragment.newAdd())
                .addToBackStack(null)
                .commit()
        }

        dataBinding.tvToChart.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_fragment_container, BillChartFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        dataBinding.rvRecent.layoutManager = LinearLayoutManager(requireContext())
        recordAdapter = BillRecordAdapter { record ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_fragment_container, BillAddFragment.newEdit(record))
                .addToBackStack(null)
                .commit()
        }
        dataBinding.rvRecent.adapter = recordAdapter
        attachSwipeToDelete(dataBinding.rvRecent)
    }

    override fun initData() {
        val yearMonth = TimeUtils.getCurrentYearMonth()

        viewModel.budgetInfo.observe(viewLifecycleOwner) { info ->
            if (info == null) {
                dataBinding.tvBudgetAmount.text = "0"
                dataBinding.tvTotalSpend.text = "已支出：0"
                dataBinding.tvRemainAmount.text = "剩余：0"
                dataBinding.tvRemainAmount.setTextColor(0xFF52C41A.toInt())
                return@observe
            }
            dataBinding.tvBudgetAmount.text = "%.2f".format(info.budgetAmount)
            dataBinding.tvTotalSpend.text = "已支出：%.2f".format(info.totalSpend)
            if (info.isOverspend) {
                // 使用后端返回的 overspendAmount 展示超支金额，无需前端再计算
                dataBinding.tvRemainAmount.text = "本月已超支 %.2f 元".format(info.overspendAmount)
                dataBinding.tvRemainAmount.setTextColor(0xFFF5222D.toInt())
            } else {
                dataBinding.tvRemainAmount.text = "剩余：%.2f".format(info.remainAmount)
                dataBinding.tvRemainAmount.setTextColor(0xFF52C41A.toInt())
            }
        }

        viewModel.recentList.observe(viewLifecycleOwner) { list ->
            recordAdapter.setList(list)
            dataBinding.tvEmptyBill.visibility = if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.monthIncome.observe(viewLifecycleOwner) { v ->
            dataBinding.tvMonthIncome.text = "收入：%.2f".format(v)
        }
        viewModel.monthExpense.observe(viewLifecycleOwner) { v ->
            dataBinding.tvMonthExpense.text = "支出：%.2f".format(v)
        }

        viewModel.loadBudget(yearMonth)
        viewModel.loadRecent(10)
        viewModel.loadMonthSummary(yearMonth)
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomBarVisible(true)
        val yearMonth = TimeUtils.getCurrentYearMonth()
        viewModel.loadBudget(yearMonth)
        viewModel.loadRecent(10)
        viewModel.loadMonthSummary(yearMonth)
    }

    private fun attachSwipeToDelete(rv: RecyclerView) {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(r: RecyclerView, h: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val item = recordAdapter.data.getOrNull(pos) ?: run {
                    recordAdapter.notifyItemChanged(pos)
                    return
                }
                AlertDialog.Builder(requireContext())
                    .setMessage("确定删除该记录吗？")
                    .setPositiveButton("删除") { _, _ ->
                        viewModel.deleteRecord(
                            viewLifecycleOwner,
                            item.recordId,
                            onSuccess = {
                                viewModel.loadRecent(10)
                                viewModel.loadMonthSummary(TimeUtils.getCurrentYearMonth())
                                viewModel.loadBudget(TimeUtils.getCurrentYearMonth())
                            },
                            onFail = { msg -> msg.toastCover(); recordAdapter.notifyItemChanged(pos) }
                        )
                    }
                    .setNegativeButton("取消") { _, _ -> recordAdapter.notifyItemChanged(pos) }
                    .setOnCancelListener { recordAdapter.notifyItemChanged(pos) }
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
                    c.drawRect(itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat(), paint)
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

    companion object {
        fun newInstance() = BillFragment()
    }
}
