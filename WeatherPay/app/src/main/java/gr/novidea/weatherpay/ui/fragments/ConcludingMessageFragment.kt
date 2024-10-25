package gr.novidea.weatherpay.ui.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import java.util.Collections

import gr.novidea.weatherpay.R
import gr.novidea.weatherpay.data.Message
import gr.novidea.weatherpay.data.MessagesViewModel
import gr.novidea.weatherpay.databinding.FragmentConcludingMessageBinding
import gr.novidea.weatherpay.ui.adapters.ConcludingMessageAdapter
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.M)
@AndroidEntryPoint
class ConcludingMessageFragment : Fragment() {
    private var _binding: FragmentConcludingMessageBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ConcludingMessageAdapter
    private val viewModel: MessagesViewModel by activityViewModels()
    var allItems: MutableList<Message> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConcludingMessageBinding.inflate(inflater, container, false)
        viewModel.messages.observe(viewLifecycleOwner) {
            allItems = it.toMutableList()
            adapter = ConcludingMessageAdapter(
                allItems, layoutInflater, requireContext(), resources, viewModel
            )
            binding.list.layoutManager = LinearLayoutManager(requireContext())
            binding.list.adapter = adapter

            val itemTouchHelper = ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    source: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val sourcePosition = source.adapterPosition
                    val targetPosition = target.adapterPosition

                    Collections.swap(allItems, sourcePosition, targetPosition)
                    adapter.notifyItemMoved(sourcePosition, targetPosition)
                    return true

                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    TODO("Not yet implemented")
                }

                override fun clearView(
                    recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)


                    viewModel.updateIndices(allItems)
                }

            })
            itemTouchHelper.attachToRecyclerView(binding.list)

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addMessage.setOnClickListener {
            showInputDialog()
        }

        binding.deleteAll.setOnClickListener {
            AlertDialog.Builder(requireContext()).setTitle("Warning")
                .setMessage("Are you certain you wish to delete all the concluding messages?")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.deleteAll()
                    adapter.notifyDataSetChanged()
                }.setNegativeButton("No", null).create().apply {
                    show()
                    getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                        resources.getColor(
                            R.color.colorPrimary, null
                        )
                    )
                    getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                        resources.getColor(
                            R.color.colorPrimary, null
                        )
                    )
                }
        }
    }


    private fun showInputDialog() {
        // Inflate the dialog layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_input, null)

        // Get the EditText from the dialog layout
        val editTextInput: EditText = dialogView.findViewById(R.id.editTextInput)

        // Create the AlertDialog
        val dialog =
            AlertDialog.Builder(requireContext()).setView(dialogView).setTitle("Add Message")
                .setPositiveButton("Ok") { _, _ ->
                    // Save the input from the EditText to the variable
                    val userInput = editTextInput.text.toString()
                    // Add the input to the adapter's list and notify the adapter
                    if (userInput.isNotEmpty()) {
                        val item = Message(userInput, false, allItems.size)
                        allItems.add(item)
                        adapter.notifyItemInserted(allItems.size - 1)
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewModel.addMessage(item)
                        }
                    }
                }.setNegativeButton("Cancel", null).create().apply {
                    show()
                    getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                        resources.getColor(
                            R.color.colorPrimary, null
                        )
                    )
                    getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                        resources.getColor(
                            R.color.colorPrimary, null
                        )
                    )
                }

        // Show the dialog
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // To avoid memory leaks
    }
}
