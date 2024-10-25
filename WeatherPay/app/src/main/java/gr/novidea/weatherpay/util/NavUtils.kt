package gr.novidea.weatherpay.util


import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.appbar.MaterialToolbar

import gr.novidea.weatherpay.R

object NavUtils {

    /**
     * Check if the current destination is the root destination
     *
     * @param navController The NavController
     */
    fun isNavRoot(navController: NavController): Boolean {

        val currId = navController.currentDestination?.id
        val rootId = navController.graph.startDestinationId
        return currId == rootId
    }

    fun setupActionBar(activity: AppCompatActivity, navController: NavController) {
        val appBarConfiguration: AppBarConfiguration =
            AppBarConfiguration.Builder(navController.graph).build()
        NavigationUI.setupActionBarWithNavController(
            activity, navController, appBarConfiguration
        )

        val actionBar: ActionBar? = activity.supportActionBar
        actionBar.let {
            it?.setDisplayHomeAsUpEnabled(true)
            it?.setHomeAsUpIndicator(R.drawable.back)

            it?.setIcon(null)
        }

        val topNav = activity.findViewById<View>(R.id.topNav)
        val toolbar = activity.findViewById<MaterialToolbar>(R.id.toolbar)

        toolbar?.setNavigationOnClickListener {
            navController.navigateUp()

            val isRoot = isNavRoot(navController)
            if (isRoot) {
                topNav.visibility = View.VISIBLE
                actionBar?.hide()
            }
        }

    }
}