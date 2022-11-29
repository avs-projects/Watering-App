package com.example.kotlinproject.alarm.adapter

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.kotlinproject.alarm.fragment.ListNutrientsAlarmFragment
import com.example.kotlinproject.alarm.fragment.ListWateringAlarmFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**

Management of fragments displayed in pager of @AlarmActivity

 */

@ExperimentalCoroutinesApi
@RequiresApi(Build.VERSION_CODES.O)

class PageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    // Number items
    override fun getCount(): Int {
        return 2
    }

    // Print fragment
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                ListWateringAlarmFragment()
            }
            1 -> {
                ListNutrientsAlarmFragment()
            }
            else -> {
                ListWateringAlarmFragment()
            }
        }
    }

    // Title of items
    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> {
                return "Watering"
            }
            1 -> {
                return "Nutrients"
            }
        }
        return super.getPageTitle(position)
    }

}