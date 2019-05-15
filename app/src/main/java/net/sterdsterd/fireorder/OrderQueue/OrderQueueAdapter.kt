package net.sterdsterd.fireorder.OrderQueue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_order.view.*
import net.sterdsterd.fireorder.OrderData
import net.sterdsterd.fireorder.R






class OrderQueueAdapter(val items : MutableList<OrderData>) : RecyclerView.Adapter<OrderQueueAdapter.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MainViewHolder(parent)

    override fun getItemCount(): Int = items.size


    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = position

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.tableNoTv.text = "${items.get(position).tableNo}번 테이블"
        holder.ordTimeTv.text = items.get(position).time
        holder.ordMenuTv.text = items.get(position).orderMenu.substring(0, items.get(position).orderMenu.length - 1)
        holder.ordQtTv.text = items.get(position).orderQt.substring(0, items.get(position).orderQt.length - 1)

    }


    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)) {
        val tableNoTv = itemView.tableNoTv
        val ordTimeTv = itemView.ordTimeTv
        val ordMenuTv = itemView.ordMenuTv
        val ordQtTv = itemView.ordQtTv
    }
}