package net.sterdsterd.fireorder.OrderQueue

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator
import kotlinx.android.synthetic.main.fragment_dashboard.*
import net.sterdsterd.fireorder.OrderData
import net.sterdsterd.fireorder.R
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.RecyclerView
import android.content.res.Resources
import net.sterdsterd.fireorder.MainActivity


class OrderQueueFragment : Fragment() {

    companion object {
        fun newInstance(): OrderQueueFragment {
            return OrderQueueFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }


    val orderQueue: MutableList<OrderData> = mutableListOf()
    val menuList: MutableList<String> = MutableList(100) {""}

    private var linearLayoutManager = LinearLayoutManager(activity)

    var rmv = false
    var position = 0
    fun updateList() {
        var last = 0
        emptyTv.visibility = View.GONE

        FirebaseDatabase.getInstance().getReference("menuList").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot!!.children
                children.forEach {
                    last = it.child("id").value.toString().toInt()
                    menuList.add(last - 1, it.child("name").value.toString())
                }

                orderList?.adapter = OrderQueueAdapter(orderQueue)

                orderList?.itemAnimator = FadeInLeftAnimator().apply {
                    setInterpolator(OvershootInterpolator())
                }

                orderList?.layoutManager = linearLayoutManager

                val mIth = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                    override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder) = true

                    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                        position = viewHolder.adapterPosition

                        rmv = true
                        FirebaseDatabase.getInstance().getReference("orderQueue").child(orderQueue[position].key!!).child("confirmed").setValue(true)
                        //orderQueue.removeAt(position)

                    }

                    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                        val animator = viewHolder.itemView.animate()
                        viewHolder.itemView.elevation = dp2px(4f)
                        animator.start()
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    }
                })

                if (orderList != null) mIth.attachToRecyclerView(orderList!!)

                FirebaseDatabase.getInstance().getReference("orderQueue").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        orderQueue.clear()
                        val children = dataSnapshot!!.children
                        children.forEach {
                            if(!it.child("confirmed").value.toString().toBoolean()) {
                                var tableNo = it.child("tableNo").value.toString().toInt()
                                var time = it.child("time").value.toString()
                                var orderName = ""
                                var orderQty = ""

                                for (i in 0..(last - 1)) {
                                    if (it.child("qt${i + 1}").value.toString().toIntOrNull() != null) {
                                        orderName += "${menuList[i]}\n"
                                        orderQty += "${it.child("qt${i + 1}").value.toString()}\n"
                                    }
                                }
                                orderQueue.add(0, OrderData(tableNo, time, orderName, orderQty, it.key))
                            }
                        }
                        if(orderQueue.size == 0){
                            progress_circular?.visibility = View.GONE
                            emptyTv?.visibility = View.VISIBLE
                        } else emptyTv?.visibility = View.GONE

                        if(rmv) {
                            orderList?.adapter?.notifyItemRemoved(position)
                            orderList?.adapter?.notifyItemRangeRemoved(0, orderQueue.size)
                            rmv = false
                        } else {
                            if(orderQueue.size <= 1){
                                orderList?.adapter?.notifyDataSetChanged()
                            } else {
                                orderList?.adapter?.notifyItemInserted(0)
                                Log.d("cc", orderQueue.size.toString())
                            }
                            orderList?.layoutManager?.scrollToPosition(0)
                        }

                        progress_circular?.visibility = View.GONE
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })

            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    fun dp2px(dp: Float) = (dp * Resources.getSystem().getDisplayMetrics().density)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    (activity as MainActivity).hide()
                } else {
                    (activity as MainActivity).show()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        updateList()

    }
}