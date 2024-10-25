package gr.novidea.weatherpay.data

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionManager: ITransaction,
    @Named(Constants.SharedPrefKeys.TRANSACTIONS) private val sharedPreferences: SharedPreferences,
) : ViewModel() {

    private val _transactions = MutableLiveData<List<RealmTransaction>>()
    val transactions: LiveData<List<RealmTransaction>> get() = _transactions

    private val _latestClosedBatchTransactions = MutableLiveData<List<RealmTransaction>>()
    val latestClosedBatchTransactions: LiveData<List<RealmTransaction>> get() = _latestClosedBatchTransactions

    private val _batchTransactions = MutableLiveData<List<RealmTransaction>>()

    val batchTransactions: LiveData<List<RealmTransaction>> get() = _batchTransactions

    private val _batchTotal = MutableLiveData<Long>()
    val batchTotal: LiveData<Long> get() = _batchTotal

    private val _batch = MutableLiveData(0)
    val batch: LiveData<Int> get() = _batch

    private val _transacting = MutableLiveData(false)
    val transacting: LiveData<Boolean> get() = _transacting
    private fun setTransacting(value: Boolean) {
        _transacting.value = value
        _transacting.postValue(value)
    }

    init {
        val currentBatch = sharedPreferences.getInt(Constants.SharedPrefKeys.BATCH, 1)
        _batch.value = currentBatch
        _batch.postValue(currentBatch)

        setTransacting(false)
        fetchTransactions()
    }

    fun initBatch(newBatch: Int) {
        with(sharedPreferences.edit()) {
            putInt(Constants.SharedPrefKeys.BATCH, newBatch)
            apply()
        }
        _batch.value = newBatch
        _batch.postValue(newBatch)
        fetchTransactions()
    }

    private fun fetchTransactions() {
        viewModelScope.launch {
            val results = transactionManager.findAll()
            _transactions.value = results
            _transactions.postValue(results)

            val batchResults = results.filter { it.batch == _batch.value }
            _batchTransactions.value = batchResults
            _batchTransactions.postValue(batchResults)

            val sum = batchResults.fold(0L) { acc, transaction -> acc + transaction.amount }
            _batchTotal.value = sum
            _batchTotal.postValue(sum)

            val prevBatch = _batch.value!! - 1
            if (prevBatch > 0) {
                val latestClosedBatchResults = results.filter { it.batch == prevBatch }
                _latestClosedBatchTransactions.value = latestClosedBatchResults
                _latestClosedBatchTransactions.postValue(latestClosedBatchResults)
            }
        }
    }

    fun addTransaction(transaction: RealmTransaction) {
        if (_batchTransactions.value == null) return

        viewModelScope.launch {
            transactionManager.upsert(transaction)
            fetchTransactions() // Refresh the list
            setTransacting(false)
        }
    }

    private suspend fun closeBatch() {
        withContext(Dispatchers.Main) {
            val newBatch = _batch.value!! + 1
            with(sharedPreferences.edit()) {
                putInt(Constants.SharedPrefKeys.BATCH, newBatch)
                apply()
            }
            _batch.value = newBatch
            _batch.postValue(newBatch)

            transactionManager.closeBatch()
            fetchTransactions()
            setTransacting(false)
        }
    }

    fun closeBatchAsync(onComplete: () -> Unit) {
        viewModelScope.launch {
            closeBatch()
            onComplete()
        }
    }
}
