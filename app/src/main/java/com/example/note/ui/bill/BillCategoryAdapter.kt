package com.example.note.ui.bill

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.note.R
import com.example.note.model.BillCategory

/**
 * 记账分类列表 Adapter。
 */
class BillCategoryAdapter(
    private val onItemClick: (BillCategory) -> Unit,
    private val onItemLongClick: (BillCategory) -> Unit
) : BaseQuickAdapter<BillCategory, BaseViewHolder>(R.layout.item_bill_category) {

    override fun convert(holder: BaseViewHolder, item: BillCategory) {
        holder.setText(R.id.tvCategoryName, item.categoryName ?: "")
        holder.itemView.setOnClickListener { onItemClick(item) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(item)
            true
        }
    }
}
