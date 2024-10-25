package gr.novidea.weatherpay.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import gr.novidea.weatherpay.data.ButtonItem
import gr.novidea.weatherpay.databinding.FragmentButtonBinding

/**
 * [RecyclerView.Adapter] that can display a [ButtonItem].
 */
class ButtonItemRecyclerViewAdapter(
    private val values: Array<ButtonItem>
) : RecyclerView.Adapter<ButtonItemRecyclerViewAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentButtonBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.buttonView.text = item.label

        item.icon?.let {
            (holder.buttonView as MaterialButton).icon =
                ContextCompat.getDrawable(holder.buttonView.context, it)
        }

        holder.buttonView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, item)
            }
        }
    }

    override fun getItemCount(): Int = values.size

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, item: ButtonItem)
    }

    inner class ViewHolder(binding: FragmentButtonBinding) : RecyclerView.ViewHolder(binding.root) {
        var buttonView: TextView = binding.itemButton
    }

}