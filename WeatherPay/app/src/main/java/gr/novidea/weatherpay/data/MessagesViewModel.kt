package gr.novidea.weatherpay.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(private val messageManager: IMessage) : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    init {
        viewModelScope.launch {
            fetchMessages()
        }
    }

    private suspend fun fetchMessages() {
        withContext(Dispatchers.IO) {
            val results = messageManager.findAll().sortedBy { it.index }.toMutableList()
            _messages.postValue(results)
        }
    }

    // Convert addMessage to a suspend function
    suspend fun addMessage(transaction: Message) {
        withContext(Dispatchers.IO) {
            messageManager.upsert(transaction)
        }
    }

    // Convert fetchSharedPref to a suspend function
    suspend fun fetchSharedPref(applicationContext: Context) {
        val sharedPreferences = applicationContext.getSharedPreferences("Concluding Messages", Context.MODE_PRIVATE)

        // Use a list to hold all the messages to be added
        val messagesToAdd = mutableListOf<Message>()

        for (i in 0 until 3) {
            val con = sharedPreferences.getString("con$i", "none")
            if (con != "none" && con != null && con != "") {
                messagesToAdd.add(Message(con, false, i))
            }
        }

        // Use coroutine to add messages and fetch them
        withContext(Dispatchers.IO) {
            messagesToAdd.forEach { message ->
                addMessage(message)
            }
            fetchMessages()
        }
    }

    fun updateIndices(list: MutableList<Message>) {
        viewModelScope.launch {
            messageManager.updateIndices(list)
            fetchMessages()
        }
    }

    fun updateId(id: String, userInput: String) {
        viewModelScope.launch {
            messageManager.updateId(id, userInput)
            fetchMessages()
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            messageManager.deleteAll()
            fetchMessages()
        }
    }

    fun delete(item: Message) {
        viewModelScope.launch {
            messageManager.delete(item)
            fetchMessages()
        }
    }

    fun updateChecked(id: String, isChecked: Boolean) {
        viewModelScope.launch {
            messageManager.updateChecked(id, isChecked)
            fetchMessages()
        }
    }

    suspend fun getCheckedMessages(): List<Message> {
        return messageManager.findAll().sortedBy { it.index }.toMutableList().filter { it.checked }
    }
}
