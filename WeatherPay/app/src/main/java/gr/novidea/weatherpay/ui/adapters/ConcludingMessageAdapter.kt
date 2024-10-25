package gr.novidea.weatherpay.ui.adapters


import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

import gr.novidea.weatherpay.R
import gr.novidea.weatherpay.data.Message
import gr.novidea.weatherpay.data.MessagesViewModel

@RequiresApi(Build.VERSION_CODES.M)
class ConcludingMessageAdapter(
    private val courseList: MutableList<Message>,
    private val layoutInflater: LayoutInflater,
    private val context: Context,
    private val resources: Resources,
    private val viewModel: MessagesViewModel,
) : RecyclerView.Adapter<ConcludingMessageAdapter.CourseViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CourseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.concluding_message, parent, false
        )
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.text.text = courseList[position].string
        holder.checkbox.isChecked = courseList[position].checked

        holder.edit.setOnClickListener {
            showInputDialog(holder, position)
        }

        holder.checkbox.setOnClickListener {
            viewModel.updateChecked(courseList[position].id, holder.checkbox.isChecked)
        }


        holder.delete.setOnClickListener {
            holder.checkbox.isChecked = false
            viewModel.delete(courseList[position])
            viewModel.updateIndices(courseList)
        }
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.text)
        val checkbox = itemView.findViewById<CheckBox>(R.id.checkbox)
        val edit = itemView.findViewById<ImageButton>(R.id.edit)
        val delete = itemView.findViewById<ImageButton>(R.id.delete)
    }

    private fun showInputDialog(holder: CourseViewHolder, position: Int) {
        // Inflate the dialog layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_input, null)

        // Get the EditText from the dialog layout
        val editTextInput: EditText = dialogView.findViewById(R.id.editTextInput)
        editTextInput.setText(holder.text.text)
        // Create the AlertDialog
        val dialog = AlertDialog.Builder(context).setView(dialogView).setTitle("Edit Message")
            .setPositiveButton("Save") { _, _ ->
                // Save the input from the EditText to the variable

                val userInput = editTextInput.text.toString()
                holder.text.text = userInput
                viewModel.updateId(courseList[position].id, userInput)


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
}