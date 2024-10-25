package gr.novidea.weatherpay.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.adapter.FragmentStateAdapter

import gr.novidea.weatherpay.R

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3 // Number of fragments

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NavHostFragment.create(R.navigation.other_nav_graph)
            1 -> NavHostFragment.create(R.navigation.home_nav_graph)
            2 -> NavHostFragment.create(R.navigation.settings_nav_graph)
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}