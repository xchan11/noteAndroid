package com.example.note.ui.goods

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.note.MainActivity
import com.example.note.R
import com.example.note.base.BaseFragment
import com.example.note.databinding.FragmentGoodsCategoryBinding
import com.example.note.model.GoodsCategory
import com.example.note.ui.goods.GoodsAddFragment
import com.example.note.ui.goods.GoodsListFragment
import com.example.note.ui.goods.GoodsRemindChoiceFragment
import com.example.note.utils.toastCover

class GoodsCategoryFragment : BaseFragment<GoodsCategoryViewModel, FragmentGoodsCategoryBinding>() {

    private lateinit var adapter: CategoryAdapter

    override fun getLayoutId(): Int = R.layout.fragment_goods_category

    override fun initViewModel(): GoodsCategoryViewModel =
        ViewModelProvider(this)[GoodsCategoryViewModel::class.java]

    override fun initView() {
        dataBinding.rvCategory.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = CategoryAdapter(
            onItemClick = { category ->
                // 点击分类：跳转到分类物品列表
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fl_fragment_container,
                        GoodsListFragment.newForCategory(category.categoryId, category.categoryName)
                    )
                    .addToBackStack(null)
                    .commit()
            },
            onItemLongClick = { category ->
                // 长按：预留编辑/删除弹窗（当前简单 Toast，后续可扩展）
                showCategoryLongClickDialog(category)
            }
        )
        dataBinding.rvCategory.adapter = adapter

        dataBinding.tvAddCategory.setOnClickListener { showAddCategoryDialog() }
        dataBinding.tvAddGoods.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_fragment_container, GoodsAddFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        dataBinding.tvRemindGoods.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_fragment_container, GoodsRemindChoiceFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun initData() {
        viewModel.categoryList.observe(viewLifecycleOwner) { list ->
            adapter.setList(list)
            dataBinding.tvEmptyCategory.visibility = if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.loadCategories()
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomBarVisible(true)
    }

    private fun showAddCategoryDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_category, null, false)
        val etName = dialogView.findViewById<EditText>(R.id.etCategoryName)
        val tvConfirm = dialogView.findViewById<TextView>(R.id.tvConfirm)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

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

    private fun showCategoryLongClickDialog(category: GoodsCategory) {
        val items = arrayOf("编辑", "删除")
        AlertDialog.Builder(requireContext())
            .setItems(items) { _, which ->
                when (which) {
                    0 -> showEditCategoryDialog(category)
                    1 -> confirmDeleteCategory(category)
                }
            }
            .show()
    }

    private fun showEditCategoryDialog(category: GoodsCategory) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_category, null, false)
        val etName = dialogView.findViewById<EditText>(R.id.etCategoryName)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
        val tvConfirm = dialogView.findViewById<TextView>(R.id.tvConfirm)

        tvTitle.text = "编辑分类"
        tvConfirm.text = "保存"
        etName.setText(category.categoryName ?: "")
        etName.setSelection(etName.text.length)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

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

    private fun confirmDeleteCategory(category: GoodsCategory) {
        AlertDialog.Builder(requireContext())
            // 文案适配：级联删除分类及旗下物品，增加风险提示
            .setMessage("删除该分类将同时删除分类下所有物品，删除后无法恢复，是否确定？")
            .setPositiveButton("删除") { _, _ ->
                viewModel.deleteCategory(category.categoryId) {
                    viewModel.loadCategories()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    companion object {
        fun newInstance() = GoodsCategoryFragment()
    }
}

