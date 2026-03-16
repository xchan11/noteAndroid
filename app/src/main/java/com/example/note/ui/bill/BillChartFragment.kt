package com.example.note.ui.bill

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import com.example.note.MainActivity
import com.example.note.R
import com.example.note.base.BaseFragment
import com.example.note.databinding.FragmentBillChartBinding
import com.example.note.model.ChartCategoryRatioItem
import com.example.note.model.ChartTrendItem
import com.example.note.utils.TimeUtils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import java.util.Calendar

/**
 * 数据可视化：收支趋势（折线）+ 支出分类占比（饼图）。月份切换。
 */
class BillChartFragment : BaseFragment<BillChartViewModel, FragmentBillChartBinding>() {

    private var yearMonth: String = TimeUtils.getCurrentYearMonth()

    override fun getLayoutId(): Int = R.layout.fragment_bill_chart

    override fun initViewModel(): BillChartViewModel =
        ViewModelProvider(this)[BillChartViewModel::class.java]

    override fun initView() {
        setupToolbar("收支图表")
        dataBinding.tvCurrentMonth.text = yearMonth

        // 统一关闭 MPAndroidChart 默认“no data”文案，空数据时用我们自己的「暂无数据」TextView
        dataBinding.chartTrend.setNoDataText("")
        dataBinding.chartPie.setNoDataText("")

        dataBinding.tvPrevMonth.setOnClickListener {
            yearMonth = prevMonth(yearMonth)
            dataBinding.tvCurrentMonth.text = yearMonth
            viewModel.loadTrend(yearMonth)
            viewModel.loadCategoryRatio(yearMonth)
        }
        dataBinding.tvNextMonth.setOnClickListener {
            yearMonth = nextMonth(yearMonth)
            dataBinding.tvCurrentMonth.text = yearMonth
            viewModel.loadTrend(yearMonth)
            viewModel.loadCategoryRatio(yearMonth)
        }
    }

    override fun initData() {
        viewModel.trendList.observe(viewLifecycleOwner) { list ->
            setupTrendChart(list)
            dataBinding.tvNoData.visibility = if (list.isNullOrEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }
        viewModel.ratioList.observe(viewLifecycleOwner) { list ->
            setupPieChart(list)
            if (dataBinding.tvNoData.visibility != android.view.View.VISIBLE)
                dataBinding.tvNoData.visibility = if (list.isNullOrEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }
        viewModel.loadTrend(yearMonth)
        viewModel.loadCategoryRatio(yearMonth)
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomBarVisible(false)
    }

    private fun setupTrendChart(list: List<ChartTrendItem>) {
        val chart = dataBinding.chartTrend
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.setDrawGridBackground(false)
        if (list.isEmpty()) {
            chart.clear()
            chart.invalidate()
            return
        }

        // X 轴只显示“日”，避免 yyyy-MM-dd 过于拥挤
        val labels = list.map { item ->
            val d = item.date ?: ""
            when {
                d.length >= 2 && d.contains("-") -> d.substringAfterLast("-")
                else -> d
            }
        }.toMutableList()
        val entriesIncome = mutableListOf<Entry>()
        val entriesSpend = mutableListOf<Entry>()
        list.forEachIndexed { i, item ->
            entriesIncome.add(Entry(i.toFloat(), item.income.toFloat()))
            entriesSpend.add(Entry(i.toFloat(), item.spend.toFloat()))
        }

        val dsIncome = LineDataSet(entriesIncome, "收入").apply {
            color = Color.parseColor("#52C41A")
            setCircleColor(Color.parseColor("#52C41A"))
            lineWidth = 2f
            setDrawValues(true)
        }
        val dsSpend = LineDataSet(entriesSpend, "支出").apply {
            color = Color.parseColor("#F5222D")
            setCircleColor(Color.parseColor("#F5222D"))
            lineWidth = 2f
            setDrawValues(true)
        }
        chart.data = LineData(dsIncome, dsSpend)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        chart.xAxis.granularity = 1f
        chart.xAxis.setAvoidFirstLastClipping(true)
        chart.axisLeft.setDrawGridLines(true)
        chart.legend.isEnabled = true
        chart.invalidate()
    }

    private fun setupPieChart(list: List<ChartCategoryRatioItem>) {
        val chart = dataBinding.chartPie
        chart.description.isEnabled = false
        chart.setUsePercentValues(true)
        chart.setDrawEntryLabels(true)

        if (list.isEmpty()) {
            chart.clear()
            chart.invalidate()
            return
        }
        val entries = list.map { PieEntry(it.totalAmount.toFloat(), it.categoryName) }
        val colors = listOf(
            Color.parseColor("#1890FF"),
            Color.parseColor("#52C41A"),
            Color.parseColor("#FA8C16"),
            Color.parseColor("#722ED1"),
            Color.parseColor("#EB2F96"),
            Color.parseColor("#13C2C2")
        )
        val ds = PieDataSet(entries, "").apply {
            this.colors = colors
            valueTextSize = 12f
        }
        chart.data = PieData(ds).apply {
            setValueFormatter(PercentFormatter())
        }
        chart.invalidate()
    }

    private fun prevMonth(ym: String): String {
        val parts = ym.split("-")
        var y = parts.getOrNull(0)?.toIntOrNull() ?: 2026
        var m = parts.getOrNull(1)?.toIntOrNull() ?: 1
        m--
        if (m < 1) { m = 12; y-- }
        return "%04d-%02d".format(y, m)
    }

    private fun nextMonth(ym: String): String {
        val parts = ym.split("-")
        var y = parts.getOrNull(0)?.toIntOrNull() ?: 2026
        var m = parts.getOrNull(1)?.toIntOrNull() ?: 1
        m++
        if (m > 12) { m = 1; y++ }
        return "%04d-%02d".format(y, m)
    }

    companion object {
        fun newInstance() = BillChartFragment()
    }
}
