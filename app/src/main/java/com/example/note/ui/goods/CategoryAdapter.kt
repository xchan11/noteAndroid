package com.example.note.ui.goods

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.note.R
import com.example.note.model.GoodsCategory

/**
 * 分类网格适配器。
 */
class CategoryAdapter(
    private val onItemClick: (GoodsCategory) -> Unit,
    private val onItemLongClick: (GoodsCategory) -> Unit
) : BaseQuickAdapter<GoodsCategory, BaseViewHolder>(R.layout.item_goods_category) {

    override fun convert(holder: BaseViewHolder, item: GoodsCategory) {
        holder.setText(R.id.tvCategoryName, item.categoryName)
        holder.itemView.setOnClickListener { onItemClick(item) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(item)
            true
        }
    }
}

