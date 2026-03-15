package com.example.note.ui.bill

import androidx.lifecycle.ViewModelProvider
import com.example.note.MainActivity
import com.example.note.R
import com.example.note.base.BaseFragment
import com.example.note.databinding.FragmentBudgetSetBinding
import com.example.note.utils.TimeUtils
import com.example.note.utils.toastCover

/**
 * 月度预算设置。GET/POST 预算接口。
 */
class BudgetSetFragment : BaseFragment<BudgetViewModel, FragmentBudgetSetBinding>() {

    private var yearMonth: String = TimeUtils.getCurrentYearMonth()

    override fun getLayoutId(): Int = R.layout.fragment_budget_set

    override fun initViewModel(): BudgetViewModel =
        ViewModelProvider(this)[BudgetViewModel::class.java]

    override fun initView() {
        setupToolbar("月度预算")
        dataBinding.tvYearMonth.text = yearMonth

        viewModel.budgetInfo.observe(viewLifecycleOwner) { info ->
            if (info != null) {
                dataBinding.etBudget.setText(if (info.budgetAmount > 0) "%.2f".format(info.budgetAmount) else "")
            }
        }

        dataBinding.btnSave.setOnClickListener {
            val amountStr = dataBinding.etBudget.text.toString().trim()
            if (amountStr.isEmpty()) {
                "请输入预算金额".toastCover()
                return@setOnClickListener
            }
            val amount = amountStr.toDoubleOrNull() ?: 0.0
            if (amount <= 0) {
                "预算金额必须大于0".toastCover()
                return@setOnClickListener
            }
            viewModel.setBudget(yearMonth, amount) {
                parentFragmentManager.popBackStack()
            }
        }
    }

    override fun initData() {
        viewModel.loadBudget(yearMonth)
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomBarVisible(false)
    }

    companion object {
        fun newInstance() = BudgetSetFragment()
    }
}
