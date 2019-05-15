package net.sterdsterd.fireorder

import android.Manifest
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

import kotlinx.android.synthetic.main.activity_main.*
import net.sterdsterd.fireorder.EditMenu.EditMenuFragment
import net.sterdsterd.fireorder.OrderQueue.OrderQueueFragment
import net.sterdsterd.fireorder.Statistic.StatisticFragment


class MainActivity : AppCompatActivity() {

    var nowSelected = R.id.navigation_dashboard
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.navigation_dashboard -> {
                if(nowSelected != R.id.navigation_dashboard) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, OrderQueueFragment())
                        .commit()
                    nowSelected = R.id.navigation_dashboard
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_menu -> {
                if(nowSelected != R.id.navigation_menu) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, EditMenuFragment())
                        .commit()

                    nowSelected = R.id.navigation_menu
                }
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_statistic -> {
                if(nowSelected != R.id.navigation_statistic) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, StatisticFragment())
                        .commit()

                    nowSelected = R.id.navigation_statistic
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, OrderQueueFragment())
            .commit()

        TedPermission.with(this).setPermissionListener(object: PermissionListener {
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@MainActivity, "[설정] > [권한]에서 권한을 허용해주세요.", Toast.LENGTH_LONG).show()
            }

            override fun onPermissionGranted() { }

        }).setRationaleMessage("사진을 업로드 하려면 권한이 필요해요")
            .setDeniedMessage("설정 > 권한 에서 권한을 허용할 수 있어요.")
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()

        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().subscribeToTopic("all")

    }

    fun show() {
        nav_card.clearAnimation()
        nav_card.animate().translationY(0f).duration = 100
    }

    fun hide() {
        nav_card.clearAnimation()
        nav_card.animate().translationY(dp2px(80f)).duration = 100
    }

    fun dp2px(dp: Float) = (dp * Resources.getSystem().getDisplayMetrics().density)


}
