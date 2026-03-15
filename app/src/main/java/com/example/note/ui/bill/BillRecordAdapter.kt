package com.example.note.ui.bill

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.note.R
import com.example.note.model.BillRecord

/**
 * 最近收支流水列表 Adapter。收入绿色，支出红色。
 */
class BillRecordAdapter(
    private val onItemClick: (BillRecord) -> Unit
) : BaseQuickAdapter<BillRecord, BaseViewHolder>(R.layout.item_bill_record) {

    override fun convert(holder: BaseViewHolder, item: BillRecord) {
        holder.setText(R.id.tvCategoryName, item.categoryName ?: "")
        val amount = item.amount
        val tvAmount = holder.getView<android.widget.TextView>(R.id.tvAmount)
        if (item.type == 1) {
            tvAmount.text = "+%.2f".format(amount)
            tvAmount.setTextColor(0xFF52C41A.toInt())
        } else {
            tvAmount.text = "-%.2f".format(amount)
            tvAmount.setTextColor(0xFFF5222D.toInt())
        }
        val timeStr = (item.createTime ?: "").replace("T", " ").take(16)
        holder.setText(R.id.tvTime, timeStr)
        holder.setText(R.id.tvRemark, item.remark?.takeIf { it.isNotBlank() } ?: "")
        holder.itemView.setOnClickListener { onItemClick(item) }
    }
}
