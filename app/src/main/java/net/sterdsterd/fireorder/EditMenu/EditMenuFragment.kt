package net.sterdsterd.fireorder.EditMenu

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_edit.*
import net.sterdsterd.fireorder.MenuData
import net.sterdsterd.fireorder.R
import androidx.recyclerview.widget.RecyclerView
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator
import net.sterdsterd.fireorder.MainActivity


class EditMenuFragment : Fragment() {

    companion object {
        fun newInstance(): EditMenuFragment {
            return EditMenuFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    var count = 0
    var last = 0

    val menuList: MutableList<MenuData> = mutableListOf()


    fun removeItem(position: Int) {
        menuList.removeAt(position)
        editList.adapter?.notifyItemRemoved(position)
    }

    fun update() {
        FirebaseDatabase.getInstance().getReference("menuList").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                addFab?.show()
                count = dataSnapshot.childrenCount.toInt()
                if(count == 0) {
                    progress_circular?.visibility = View.GONE
                    emptyTv?.visibility = View.VISIBLE
                } else emptyTv?.visibility = View.GONE
                val children = dataSnapshot!!.children
                children.forEach {
                    menuList.add(MenuData(it.child("id").value.toString().toInt(), it.child("name").value.toString(),
                        it.child("price").value.toString().toInt(), it.child("img").value.toString()))
                }

                editList?.layoutManager = linearLayoutManager
                progress_circular?.visibility = View.GONE

                last = menuList.last().id
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var linearLayoutManager = LinearLayoutManager(activity)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editList?.adapter = EditMenuAdapter(menuList, activity as FragmentActivity, this)
        editList?.itemAnimator = FadeInLeftAnimator().apply {
            setInterpolator(OvershootInterpolator())
        }

        editList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    (activity as MainActivity).hide()
                    slideDown()
                } else {
                    (activity as MainActivity).show()
                    slideUp()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        addFab?.setOnClickListener{
            count++
            last++
            FirebaseDatabase.getInstance().getReference("menuList").child(last.toString()).child("id").setValue(last)
            FirebaseDatabase.getInstance().getReference("menuList").child(last.toString()).child("name").setValue("")
            FirebaseDatabase.getInstance().getReference("menuList").child(last.toString()).child("price").setValue(0)
            FirebaseDatabase.getInstance().getReference("menuList").child(last.toString()).child("img").setValue("")
            menuList.add(MenuData(last, "", 0, ""))
            editList.adapter?.notifyItemInserted(menuList.size)
            editList.smoothScrollToPosition(menuList.size - 1)
        }

        addFab?.hide()

        update()

    }

    fun slideUp() {
        addFab.clearAnimation()
        addFab.animate().translationY(0f).duration = 100
    }

    fun slideDown() {
        addFab.clearAnimation()
        addFab.animate().translationY(dp2px(70f)).duration = 100
    }

    fun dp2px(dp: Float) = (dp * Resources.getSystem().getDisplayMetrics().density)
}