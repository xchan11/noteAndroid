package com.example.note.ui.bill

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.note.MainActivity
import com.example.note.R
import com.example.note.base.BaseFragment
import com.example.note.databinding.FragmentBillCategoryBinding
import com.example.note.model.BillCategory
import com.example.note.utils.toastCover

/**
 * 记账分类管理：列表、新增、编辑、删除（有流水的分类后端会拒绝删除）。
 */
class BillCategoryFragment : BaseFragment<BillCategoryViewModel, FragmentBillCategoryBinding>() {

    private lateinit var adapter: BillCategoryAdapter

    override fun getLayoutId(): Int = R.layout.fragment_bill_category

    override fun initViewModel(): BillCategoryViewModel =
        ViewModelProvider(this)[BillCategoryViewModel::class.java]

    override fun initView() {
        setupToolbar("记账分类")

        dataBinding.rvCategory.layoutManager = LinearLayoutManager(requireContext())
        adapter = BillCategoryAdapter { category ->
            showLongClickDialog(category)
        }
        dataBinding.rvCategory.adapter = adapter

        dataBinding.tvAddCategory.setOnClickListener { showAddDialog() }
    }

    override fun initData() {
        viewModel.categoryList.observe(viewLifecycleOwner) { list ->
            if (list == null) return@observe
            adapter.setList(list)
            dataBinding.tvEmptyCategory.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.loadCategories()
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomBarVisible(false)
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_category, null, false)
        val etName = dialogView.findViewById<EditText>(R.id.etCategoryName)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
        val tvConfirm = dialogView.findViewById<TextView>(R.id.tvConfirm)
        tvTitle.text = "添加分类"
        tvConfirm.text = "添加"

        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()
        tvConfirm.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) {
                "分类名称不能为空".toastCover()
                return@setOnClickListener
            }
            viewModel.addCategory(name) {
                dialog.dismiss()
                viewModel.loadCategories()
            }
        }
        dialog.show()
    }

    private fun showLongClickDialog(category: BillCategory) {
        val items = arrayOf("编辑", "删除")
        AlertDialog.Builder(requireContext())
            .setItems(items) { _, which ->
                when (which) {
                    0 -> showEditDialog(category)
                    1 -> showDeleteConfirm(category)
                }
            }
            .show()
    }

    private fun showEditDialog(category: BillCategory) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_category, null, false)
        val etName = dialogView.findViewById<EditText>(R.id.etCategoryName)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
        val tvConfirm = dialogView.findViewById<TextView>(R.id.tvConfirm)
        tvTitle.text = "编辑分类"
        tvConfirm.text = "保存"
        etName.setText(category.categoryName ?: "")
        etName.setSelection(etName.text.length)

        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()
        tvConfirm.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) {
                "分类名称不能为空".toastCover()
                return@setOnClickListener
            }
            viewModel.updateCategory(category.categoryId, name) {
                dialog.dismiss()
                viewModel.loadCategories()
            }
        }
        dialog.show()
    }

    private fun showDeleteConfirm(category: BillCategory) {
        AlertDialog.Builder(requireContext())
            .setMessage("确定删除该分类吗？")
            .setPositiveButton("删除") { _, _ ->
                viewModel.deleteCategory(category.categoryId) {
                    viewModel.loadCategories()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    companion object {
        fun newInstance() = BillCategoryFragment()
    }
}
