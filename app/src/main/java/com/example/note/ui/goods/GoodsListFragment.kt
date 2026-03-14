package com.example.note.ui.goods

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.note.MainActivity
import com.example.note.R
import com.example.note.base.BaseFragment
import com.example.note.databinding.FragmentGoodsListBinding
import com.example.note.model.GoodsInfo
import com.example.note.utils.toastCover

class GoodsListFragment : BaseFragment<GoodsListViewModel, FragmentGoodsListBinding>() {

    private lateinit var adapter: GoodsAdapter
    private var categoryId: Long? = null
    private var type: Int? = null
    private var title: String? = null

    override fun getLayoutId(): Int = R.layout.fragment_goods_list

    override fun initViewModel(): GoodsListViewModel =
        ViewModelProvider(this)[GoodsListViewModel::class.java]

    override fun initView() {
        categoryId = if (arguments?.containsKey(ARG_CATEGORY_ID) == true) {
            arguments?.getLong(ARG_CATEGORY_ID)
        } else {
            null
        }
        type = if (arguments?.containsKey(ARG_TYPE) == true) arguments?.getInt(ARG_TYPE) else null
        title = arguments?.getString(ARG_TITLE)

        setupToolbar(title ?: "物品列表")

        dataBinding.rvGoods.layoutManager = LinearLayoutManager(requireContext())
        adapter = GoodsAdapter(
            onItemClick = { goods ->
                openEdit(goods)
            }
        )
        dataBinding.rvGoods.adapter = adapter

        attachSwipeToDelete(dataBinding.rvGoods)
    }

    override fun initData() {
        viewModel.goodsList.observe(viewLifecycleOwner) { list ->
            adapter.setList(list)
            dataBinding.tvEmptyGoods.visibility = if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
        when {
            categoryId != null -> viewModel.loadByCategory(categoryId!!)
            type != null -> viewModel.loadByRemind(type!!)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomBarVisible(false)
    }

    private fun openEdit(goods: GoodsInfo) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fl_fragment_container,
                GoodsEditFragment.newInstance(goods)
            )
            .addToBackStack(null)
            .commit()
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
                val item = adapter.data.getOrNull(pos)
                if (item == null) {
                    adapter.notifyItemChanged(pos)
                    return
                }
                AlertDialog.Builder(requireContext())
                    .setMessage("确定删除该物品吗？")
                    .setPositiveButton("删除") { _, _ ->
                        viewModel.deleteGoods(
                            viewLifecycleOwner,
                            item.goodsId,
                            onSuccess = {
                                "删除成功".toastCover()
                                // 删除成功后重新加载列表
                                when {
                                    categoryId != null -> viewModel.loadByCategory(categoryId!!)
                                    type != null -> viewModel.loadByRemind(type!!)
                                }
                            },
                            onFail = { msg ->
                                msg.toastCover()
                                adapter.notifyItemChanged(pos)
                            }
                        )
                    }
                    .setNegativeButton("取消") { _, _ ->
                        adapter.notifyItemChanged(pos)
                    }
                    .setOnCancelListener {
                        adapter.notifyItemChanged(pos)
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

    companion object {
        private const val ARG_CATEGORY_ID = "categoryId"
        private const val ARG_TYPE = "type"
        private const val ARG_TITLE = "title"

        fun newForCategory(categoryId: Long, categoryName: String): GoodsListFragment =
            GoodsListFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CATEGORY_ID, categoryId)
                    putString(ARG_TITLE, categoryName)
                }
            }

        fun newForRemind(type: Int, title: String): GoodsListFragment =
            GoodsListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TYPE, type)
                    putString(ARG_TITLE, title)
                }
            }
    }
}

