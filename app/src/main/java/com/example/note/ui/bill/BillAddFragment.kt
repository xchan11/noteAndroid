package com.example.note.ui.bill

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.note.MainActivity
import com.example.note.R
import com.example.note.base.BaseFragment
import com.example.note.databinding.FragmentBillAddBinding
import com.example.note.model.BillCategory
import com.example.note.model.BillRecord
import com.example.note.utils.TimeUtils
import com.example.note.utils.toastCover
import java.util.Calendar

/**
 * 新增/编辑收支记录。type: 1=收入，2=支出。金额>0 必填。
 */
class BillAddFragment : BaseFragment<BillAddViewModel, FragmentBillAddBinding>() {

    private lateinit var categoryVm: BillCategoryViewModel
    private var categories: List<BillCategory> = emptyList()
    private var recordTime: Long = System.currentTimeMillis()
    private var editingRecord: BillRecord? = null

    override fun getLayoutId(): Int = R.layout.fragment_bill_add

    override fun initViewModel(): BillAddViewModel =
        ViewModelProvider(this)[BillAddViewModel::class.java]

    override fun initView() {
        setupToolbar(if (arguments?.getSerializable(ARG_RECORD) != null) "编辑记录" else "记一笔")

        categoryVm = ViewModelProvider(this)[BillCategoryViewModel::class.java].apply {
            lifecycleOwner = viewLifecycleOwner
            context = requireContext()
        }

        editingRecord = arguments?.getSerializable(ARG_RECORD) as? BillRecord
        if (editingRecord != null) {
            val r = editingRecord!!
            dataBinding.rbExpense.isChecked = r.type == 2
            dataBinding.rbIncome.isChecked = r.type == 1
            dataBinding.etAmount.setText(r.amount.toString())
            dataBinding.etRemark.setText(r.remark ?: "")
            parseAndSetRecordTime(r.createTime)
        } else {
            dataBinding.tvRecordTime.text = TimeUtils.formatYmdHms(recordTime)
        }

        dataBinding.tvRecordTime.setOnClickListener { showDateTimePicker() }
        dataBinding.tvManageCategory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_fragment_container, BillCategoryFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        dataBinding.btnSave.setOnClickListener { submit() }
    }

    private fun parseAndSetRecordTime(createTime: String?) {
        if (createTime.isNullOrBlank()) return
        try {
            val s = createTime.replace("T", " ")
            // 兼容旧返回（到秒）与新返回（到分）
            val fmtSec = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            val fmtMin = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            recordTime = (fmtSec.parse(s) ?: fmtMin.parse(s))?.time ?: recordTime
            dataBinding.tvRecordTime.text = TimeUtils.formatYmdHms(recordTime)
        } catch (_: Exception) { }
    }

    override fun initData() {
        categoryVm.categoryList.observe(viewLifecycleOwner) { list ->
            if (list == null) return@observe
            categories = list
            if (categories.isEmpty()) {
                dataBinding.btnSave.isEnabled = false
                return@observe
            }
            dataBinding.btnSave.isEnabled = true
            val names = list.map { it.categoryName ?: "" }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            dataBinding.spCategory.adapter = adapter
            editingRecord?.let { r ->
                val idx = list.indexOfFirst { it.categoryId == r.categoryId }
                if (idx >= 0) dataBinding.spCategory.setSelection(idx)
            }
        }
        categoryVm.loadCategories()

        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            if (result == null) return@observe
            result.message.toastCover()
            if (result.code == 200) {
                parentFragmentManager.popBackStack()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomBarVisible(false)
        categoryVm.loadCategories()
    }

    private fun submit() {
        if (categories.isEmpty()) {
            "请先添加分类".toastCover()
            return
        }
        val pos = dataBinding.spCategory.selectedItemPosition
        if (pos !in categories.indices) {
            "请选择分类".toastCover()
            return
        }
        val categoryId = categories[pos].categoryId
        val type = if (dataBinding.rbIncome.isChecked) 1 else 2
        val amountStr = dataBinding.etAmount.text.toString().trim()
        if (amountStr.isEmpty()) {
            "请输入金额".toastCover()
            return
        }
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        if (amount <= 0) {
            "金额必须大于0".toastCover()
            return
        }
        val remark = dataBinding.etRemark.text.toString().trim().takeIf { it.isNotBlank() }
        val createTime = TimeUtils.formatYmdHms(recordTime)

        val r = editingRecord
        if (r == null) {
            viewModel.addRecord(viewLifecycleOwner, categoryId, type, amount, remark, createTime)
        } else {
            viewModel.updateRecord(viewLifecycleOwner, r.recordId, categoryId, type, amount, remark, createTime)
        }
    }

    private fun showDateTimePicker() {
        val cal = Calendar.getInstance()
        cal.timeInMillis = recordTime
        DatePickerDialog(
            requireContext(),
            { _, y, m, d ->
                TimePickerDialog(
                    requireContext(),
                    { _, hh, mm ->
                        cal.set(y, m, d, hh, mm, 0)
                        cal.set(Calendar.MILLISECOND, 0)
                        recordTime = cal.timeInMillis
                        dataBinding.tvRecordTime.text = TimeUtils.formatYmdHms(recordTime)
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
        private const val ARG_RECORD = "record"
        fun newAdd() = BillAddFragment()
        fun newEdit(record: BillRecord) = BillAddFragment().apply {
            arguments = Bundle().apply { putSerializable(ARG_RECORD, record) }
        }
    }
}
