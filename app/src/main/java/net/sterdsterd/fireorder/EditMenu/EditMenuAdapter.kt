package net.sterdsterd.fireorder.EditMenu

import android.app.ProgressDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.item_edit.view.*
import net.sterdsterd.fireorder.MenuData
import net.sterdsterd.fireorder.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_edit.*


class EditMenuAdapter(val items : MutableList<MenuData>, act: FragmentActivity, fra: EditMenuFragment) : RecyclerView.Adapter<EditMenuAdapter.MainViewHolder>() {

    var activity = act
    var fragment = fra

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MainViewHolder(parent)

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = position

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {

        holder.nameET.setText(items[position].name)
        holder.nameET.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun afterTextChanged(p0: Editable?) {
                FirebaseDatabase.getInstance().getReference("menuList").child(items[position].id.toString()).child("name").setValue(holder.nameET.text.toString())
                fragment.menuList.add(position, MenuData(fragment.menuList[position].id, items[position].name, fragment.menuList[position].price, fragment.menuList[position].src))
                fragment.menuList.removeAt(position + 1)
            }
        })
        holder.priceET.setText(items[position].price.toString())
        holder.priceET.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun afterTextChanged(p0: Editable?) {
                var prc = 0
                if(holder.priceET.text.isNotEmpty()) prc = holder.priceET.text.toString().toInt()
                else holder.priceET.setText("0")
                FirebaseDatabase.getInstance().getReference("menuList").child(items[position].id.toString()).child("price").setValue(prc)
                fragment.menuList.add(position, MenuData(fragment.menuList[position].id, fragment.menuList[position].name, prc, fragment.menuList[position].src))
                fragment.menuList.removeAt(position + 1)
            }
        })

        holder.getImg.setOnClickListener { v ->
            TedBottomPicker.with(activity).show {
                Log.e("URI", it.toString())

                var progressDialog = ProgressDialog(v.context)
                progressDialog.setTitle("Uploading...")
                progressDialog.setMessage("Uploading Image")
                progressDialog.show()

                var fbStorage = FirebaseStorage.getInstance()
                var filename = "${items[position].id}.png"
                var storageRef = fbStorage.getReferenceFromUrl("gs://order-5de62.appspot.com").child("img/$filename")

                holder.imgView.setImageResource(android.R.color.transparent)

                storageRef.putFile(it).addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(activity.applicationContext, "Success", Toast.LENGTH_LONG).show()
                    FirebaseDatabase.getInstance().getReference("menuList").child(items[position].id.toString()).child("img").setValue((0..it.bytesTransferred).random())
                    FirebaseStorage.getInstance().getReferenceFromUrl("gs://order-5de62.appspot.com")
                        .child("img/${items[position].id}.png").downloadUrl.addOnSuccessListener {
                        Glide.with(activity.applicationContext).load(it).into(holder.imgView)
                    }
                }
            }
        }

        holder.deleteBtn.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("menuList").child(items[position].id.toString()).removeValue()
            var fbStorage = FirebaseStorage.getInstance().reference
            fbStorage.child("img/${items[position].id}.png").delete().addOnSuccessListener { }
            fragment.removeItem(position)
            fragment.count--
            fragment.last = fragment.menuList.last().id
        }

        FirebaseStorage.getInstance().getReferenceFromUrl("gs://order-5de62.appspot.com")
            .child("img/${items[position].id}.png").downloadUrl.addOnSuccessListener {
            Glide.with(activity.applicationContext).load(it).apply(RequestOptions().override(100, 100)).into(holder.imgView)
        }.addOnCompleteListener {
            holder.progress.visibility = View.GONE
        }

    }

    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_edit, parent, false)) {
        val nameET = itemView.nameET
        val priceET = itemView.priceET
        val getImg = itemView.getImg
        val imgView = itemView.imgView
        val progress = itemView.loadProgress
        val deleteBtn = itemView.deleteBtn

    }
}