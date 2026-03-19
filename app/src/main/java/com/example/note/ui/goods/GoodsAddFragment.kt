package com.example.note.ui.goods

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.note.MainActivity
import com.example.note.R
import com.example.note.base.BaseFragment
import com.example.note.databinding.FragmentGoodsAddBinding
import com.example.note.model.GoodsCategory
import com.example.note.utils.TimeUtils
import com.example.note.utils.toastCover
import java.util.Calendar

class GoodsAddFragment : BaseFragment<GoodsAddEditViewModel, FragmentGoodsAddBinding>() {

    private lateinit var categoryVm: GoodsCategoryViewModel
    private var categories: List<GoodsCategory> = emptyList()
    private var selectedShelfLife: Long? = null
    private var selectedOpenDate: Long? = null

    override fun getLayoutId(): Int = R.layout.fragment_goods_add

    override fun initViewModel(): GoodsAddEditViewModel =
        ViewModelProvider(this)[GoodsAddEditViewModel::class.java]

    override fun initView() {
        setupToolbar("新增物品")

        categoryVm = ViewModelProvider(this)[GoodsCategoryViewModel::class.java].apply {
            // 由于不是通过 BaseFragment 持有的 viewModel，这里手动注入生命周期和上下文
            lifecycleOwner = viewLifecycleOwner
            context = requireContext()
        }

        dataBinding.tvShelfLife.setOnClickListener {
            showDatePicker { ts ->
                selectedShelfLife = ts
                dataBinding.tvShelfLife.text = TimeUtils.formatYmd(ts)
            }
        }
        dataBinding.tvOpenDate.setOnClickListener {
            showDatePicker { ts ->
                selectedOpenDate = ts
                dataBinding.tvOpenDate.text = TimeUtils.formatYmd(ts)
            }
        }

        dataBinding.btnSubmit.setOnClickListener {
            submit()
        }
    }

    override fun initData() {
        categoryVm.categoryList.observe(viewLifecycleOwner) { list ->
            if (list == null) return@observe
            categories = list
            if (categories.isEmpty()) {
                "请先添加分类".toastCover()
                dataBinding.btnSubmit.isEnabled = false
            } else {
                dataBinding.btnSubmit.isEnabled = true
                val names = categories.map { it.categoryName }
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.spinner_item_category,
                    names
                ).also {
                    it.setDropDownViewResource(R.layout.spinner_dropdown_item_category)
                }
                dataBinding.spCategory.adapter = adapter
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
    }

    private fun submit() {
        if (categories.isEmpty()) {
            "请先添加分类".toastCover()
            return
        }
        val pos = dataBinding.spCategory.selectedItemPosition
        if (pos !in categories.indices) {
            "请选择所属分类".toastCover()
            return
        }
        val categoryId = categories[pos].categoryId
        val name = dataBinding.etGoodsName.text.toString().trim()
        val traceInfo = dataBinding.etTraceInfo.text.toString().trim()

        if (name.isEmpty()) {
            "物品名称不能为空".toastCover()
            return
        }
        if (traceInfo.isEmpty()) {
            "放置位置不能为空".toastCover()
            return
        }
        viewModel.addGoods(
            viewLifecycleOwner,
            categoryId,
            name,
            traceInfo,
            selectedShelfLife,
            selectedOpenDate
        )
    }

    private fun showDatePicker(onPicked: (Long) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, y, m, d ->
                cal.set(y, m, d, 0, 0, 0)
                cal.set(Calendar.MILLISECOND, 0)
                onPicked(cal.timeInMillis)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    companion object {
        fun newInstance() = GoodsAddFragment()
    }
}

