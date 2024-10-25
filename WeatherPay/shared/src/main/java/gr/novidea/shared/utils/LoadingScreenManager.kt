package gr.novidea.shared.utils

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gr.novidea.shared.databinding.LoadingScreenBinding

class LoadingScreenManager(private val activity: Activity) {

    private var loadingView: View? = null
    private lateinit var binding: LoadingScreenBinding

    fun showLoading(text: String) {
        if (loadingView == null) {
            binding = LoadingScreenBinding.inflate(LayoutInflater.from(activity))
            loadingView = binding.root

            val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
            rootView.addView(loadingView)
        }

        binding.loadingText2.text = text
        loadingView?.visibility = View.VISIBLE
    }

    fun hideLoading() {
        loadingView?.visibility = View.GONE
    }
}
