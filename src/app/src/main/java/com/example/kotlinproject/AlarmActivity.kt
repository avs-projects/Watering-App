package com.example.kotlinproject

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.ViewPager
import com.example.kotlinproject.alarm.adapter.PageAdapter
import com.example.kotlinproject.databinding.ActivityAlarmBinding
import com.example.kotlinproject.service.NotificationAlarm
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

/**

@AlarmActivity

 */

@ExperimentalCoroutinesApi
@RequiresApi(Build.VERSION_CODES.O)

class AlarmActivity : AppCompatActivity() {

    private lateinit var mainIntent: Intent

    private val binding: ActivityAlarmBinding by lazy { ActivityAlarmBinding.inflate(layoutInflater) }
    private val viewPager: ViewPager by lazy { findViewById(R.id.viewPager) }
    private val tabLayout: TabLayout by lazy { findViewById(R.id.tabLayout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = "Alarm List"

        // Fragment ViewPager
        viewPager.adapter = PageAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)

        // Call of daily notification management
        NotificationAlarm().setNotification(this@AlarmActivity)

        // If click on button, then go to main activity
        binding.fabGoToMain.setOnClickListener {
            mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }
    }

}

