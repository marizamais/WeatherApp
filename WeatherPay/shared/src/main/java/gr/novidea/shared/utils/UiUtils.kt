package gr.novidea.shared.utils

import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object UiUtils {

    /**
     * Get the height of the screen in pixels
     * Example: 0.1 for 10% margin
     *
     * @param resources The resources object
     * @param percentage The percentage of the screen height
     *
     */
    fun getScreenHeightPercentage(resources: Resources, percentage: Double = 0.0): Int {

        val metrics: DisplayMetrics = resources.displayMetrics
        val screenHeight = metrics.heightPixels

        return (percentage * screenHeight).toInt()
    }

    /**
     * Add padding to a view to account for system bars,
     * avoiding content being hidden by them.
     *
     * @param view The view to apply the padding
     */
    fun applySystemBarInsets(view: View, ignoredInsets: Array<String> = emptyArray()) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            val insetsMap = mapOf(
                "left" to systemBars.left,
                "top" to systemBars.top,
                "right" to systemBars.right,
                "bottom" to systemBars.bottom
            ).mapValues { (key, value) ->
                if (ignoredInsets.contains(key)) 0 else value
            }

            v.setPadding(
                insetsMap["left"]!!, insetsMap["top"]!!, insetsMap["right"]!!, insetsMap["bottom"]!!
            )
            insets
        }
    }
}