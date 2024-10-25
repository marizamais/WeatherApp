package gr.novidea.weatherpay.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList

import gr.novidea.weatherpay.data.RealmTransaction
import gr.novidea.weatherpay.databinding.FragmentTransactionItemBinding
import gr.novidea.weatherpay.util.formatDate
import gr.novidea.weatherpay.util.maskCardNumber

import gr.novidea.shared.utils.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionItemRecyclerViewAdapter :
    RecyclerView.Adapter<TransactionItemRecyclerViewAdapter.ViewHolder>() {

    private val allItems: MutableList<RealmTransaction> = mutableListOf()
    private val sortedList: SortedList<RealmTransaction> =
        SortedList(RealmTransaction::class.java, object : SortedList.Callback<RealmTransaction>() {
            override fun compare(o1: RealmTransaction, o2: RealmTransaction): Int {
                return o1.id.compareTo(o2.id)
            }

            override fun onChanged(position: Int, count: Int) {
                notifyItemRangeChanged(position, count)
            }

            override fun areContentsTheSame(
                oldItem: RealmTransaction, newItem: RealmTransaction
            ): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(
                item1: RealmTransaction, item2: RealmTransaction
            ): Boolean {
                return item1.id == item2.id
            }

            override fun onInserted(position: Int, count: Int) {
                notifyItemRangeInserted(position, count)
            }

            override fun onRemoved(position: Int, count: Int) {
                notifyItemRangeRemoved(position, count)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                notifyItemMoved(fromPosition, toPosition)
            }
        })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentTransactionItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = sortedList[position]
        holder.transactionId.text = item.id

        val locales = CurrencyUtils.getCurrencyLocales()
        holder.amount.text = CurrencyUtils.longToCurrency(item.amount, locales)

        holder.cardNumber.text = item.cardNumber.maskCardNumber()
        holder.type.text = item.type
        holder.date.text = item.date.formatDate()
        holder.status.text = item.status
    }

    override fun getItemCount(): Int = sortedList.size()

    fun addItems(items: List<RealmTransaction>) {
        allItems.addAll(items)
        sortedList.addAll(items)
    }

    fun filter(status: String) {
        sortedList.clear()
        if (status.isEmpty()) {
            sortedList.addAll(allItems)
        } else {
            sortedList.addAll(allItems.filter { it.status == status })
        }
    }

    fun filterDates(start: Date, end: Date): List<RealmTransaction> {
        val pattern = "EEE MMM dd HH:mm:ss zzz yyyy"
        val dateFormat = SimpleDateFormat(pattern, Locale.ENGLISH)

        val transactionsList = allItems.filter { dateFormat.parse(it.date)!! in start..end }
        sortedList.addAll(transactionsList)
        return transactionsList
    }

    inner class ViewHolder(binding: FragmentTransactionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val transactionId: TextView = binding.transactionId
        val amount: TextView = binding.amount
        val cardNumber: TextView = binding.cardNumber
        val type: TextView = binding.type
        val date: TextView = binding.date
        val status: TextView = binding.status
    }

}