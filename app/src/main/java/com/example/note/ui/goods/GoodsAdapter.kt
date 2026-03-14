package com.example.note.ui.goods

import android.graphics.Color
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.note.R
import com.example.note.model.GoodsInfo
import com.example.note.utils.TimeUtils

/**
 * 通用物品列表适配器（支持分类列表和提醒列表）。
 */
class GoodsAdapter(
    private val onItemClick: (GoodsInfo) -> Unit
) : BaseQuickAdapter<GoodsInfo, BaseViewHolder>(R.layout.item_goods) {

    override fun convert(holder: BaseViewHolder, item: GoodsInfo) {
        holder.setText(R.id.tvGoodsName, item.goodsName)

        val shelfText = if (item.shelfLife != null) {
            "保质期：" + TimeUtils.formatYmd(item.shelfLife)
        } else {
            "无保质期"
        }
        holder.setText(R.id.tvShelfInfo, shelfText)
        holder.setText(R.id.tvTraceInfo, "放置位置：" + (item.traceInfo.ifBlank { "未知" }))

        val tvStatus = holder.getView<android.widget.TextView>(R.id.tvStatus)
        val now = System.currentTimeMillis()
        if (item.shelfLife == null) {
            tvStatus.text = "无保质期"
            tvStatus.setBackgroundColor(Color.parseColor("#999999"))
        } else {
            if (item.isExpire == 1 || item.shelfLife <= now) {
                tvStatus.text = "已过期"
                tvStatus.setBackgroundColor(Color.parseColor("#FF4D4F"))
            } else {
                val diff = item.shelfLife - now
                val oneDay = 24L * 60 * 60 * 1000
                when {
                    diff <= oneDay -> {
                        tvStatus.text = "临期1天"
                        tvStatus.setBackgroundColor(Color.parseColor("#FA8C16"))
                    }
                    diff <= 3 * oneDay -> {
                        tvStatus.text = "临期3天"
                        tvStatus.setBackgroundColor(Color.parseColor("#FAAD14"))
                    }
                    diff <= 7 * oneDay -> {
                        tvStatus.text = "临期7天"
                        tvStatus.setBackgroundColor(Color.parseColor("#52C41A"))
                    }
                    else -> {
                        tvStatus.text = "未过期"
                        tvStatus.setBackgroundColor(Color.parseColor("#1890FF"))
                    }
                }
            }
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }
}

