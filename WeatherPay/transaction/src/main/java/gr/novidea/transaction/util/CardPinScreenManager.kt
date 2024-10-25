package gr.novidea.transaction.util

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat

import gr.novidea.transaction.R

class CardPinScreenManager(private val activity: Activity) {

    private var pinView: View? = null
    private var dots: Array<View> = emptyArray()

    fun showPinScreen(reset: Boolean) {
        if (pinView == null) {
            val inflater = LayoutInflater.from(activity)
            pinView = inflater.inflate(R.layout.pin_screen, null)

            val rootView = activity.findViewById<ViewGroup>(android.R.id.content)

            rootView.addView(pinView)

            dots = arrayOf(
                pinView!!.findViewById(R.id.dot_1),
                pinView!!.findViewById(R.id.dot_2),
                pinView!!.findViewById(R.id.dot_3),
                pinView!!.findViewById(R.id.dot_4)
            )
        }
        pinView?.visibility = View.VISIBLE

        if (reset) {
            updateDots(0)
        }
    }

    fun updateDots(currPinLength: Int) {

        for (i in dots.indices) {
            val color = if (i < currPinLength) {
                R.color.colorPrimary
            } else {
                R.color.grey
            }

            ImageViewCompat.setImageTintList(
                dots[i] as ImageView, ContextCompat.getColorStateList(activity.baseContext, color)
            )
        }
    }

    fun hidePinScreen(reset: Boolean) {
        pinView?.visibility = View.GONE

        if (reset) {
            updateDots(0)
        }
    }

    fun setMessage(message: String) {
        pinView?.findViewById<TextView>(R.id.message)?.text = message
    }
}