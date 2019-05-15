package net.sterdsterd.fireorder.Statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import net.sterdsterd.fireorder.R
import kotlinx.android.synthetic.main.fragment_statistic.*
import java.text.SimpleDateFormat
import java.util.*
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import android.graphics.Color
import androidx.core.widget.NestedScrollView
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import net.sterdsterd.fireorder.MainActivity


class StatisticFragment : Fragment() {

    companion object {
        fun newInstance(): StatisticFragment {
            return StatisticFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statistic, container, false)
    }

    var priceList: MutableList<Pair<Int, Int>> = mutableListOf()
    var income = 0
    var cnt = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseDatabase.getInstance().getReference("menuList").addListenerForSingleValueEvent(object :
            ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val children = p0.children
                children.forEach {
                    priceList.add(Pair(it.child("id").value.toString().toInt(), it.child("price").value.toString().toInt()))
                }

                FirebaseDatabase.getInstance().getReference("orderQueue").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        income = 0
                        cnt = 0
                        val children = dataSnapshot!!.children
                        children.forEach {
                            if(it.key.toString().toLong() >= getEpoch(SimpleDateFormat("yyyy/MM/dd 00:00:00").format(Date()))) {
                                income += it.child("total").value.toString().toInt()
                                cnt++
                            }
                        }
                        incomeTv?.text = "￦${String.format("%,d", income)}"
                        countTv?.text = cnt.toString()
                    }

                    override fun onCancelled(databaseError: DatabaseError) { }
                })
            }

            override fun onCancelled(p0: DatabaseError) { }

        })

        statScrollView.setOnScrollChangeListener(object: NestedScrollView.OnScrollChangeListener{
            override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                if(oldScrollY < scrollY) (activity as MainActivity).hide()
                else (activity as MainActivity).show()
            }

        })


        val entries: MutableList<Entry> = mutableListOf()
        entries.add(Entry(0f, 0f))
        entries.add(Entry(1f, 4f))
        entries.add(Entry(2f, 11f))
        entries.add(Entry(3f, 23f))
        entries.add(Entry(4f, 40f))
        entries.add(Entry(5f, 40f))
        entries.add(Entry(6f, 23f))
        entries.add(Entry(7f, 11f))
        entries.add(Entry(8f, 4f))
        entries.add(Entry(9f, 0f))

        val lineDataSet = LineDataSet(entries, "속성명1")
        lineDataSet.lineWidth = 2f
        lineDataSet.color = resources.getColor(R.color.colorPrimary)
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawCircleHole(false)
        lineDataSet.setDrawHorizontalHighlightIndicator(true)
        lineDataSet.setDrawHighlightIndicators(false)
        lineDataSet.setDrawValues(true)
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillDrawable = resources.getDrawable(R.drawable.gradient_chart)
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER)

        val lineData = LineData(lineDataSet)
        chart.setData(lineData)
        chart.animateY(1750, Easing.EaseOutQuart)

        val xAxis = chart.getXAxis()
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setTextColor(Color.BLACK)
        xAxis.enableGridDashedLine(8f, 24f, 0f)

        val yLAxis = chart.getAxisLeft()
        yLAxis.setTextColor(Color.BLACK)
        yLAxis.axisMinimum = 0f

        chart.getAxisRight().setEnabled(false)
        chart.getLegend().setEnabled(false)

        val description = Description()
        description.setText("")

        chart.setDoubleTapToZoomEnabled(false)
        chart.setDrawGridBackground(false)
        chart.setDescription(description)
        chart.invalidate()
    }

    fun getFormattedValue(value: Float) = "" + (value as Int)

    fun getEpoch(ts: String) = SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(ts).time / 1000

}