package com.example.listviewapp.views

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.listviewapp.R
import com.example.listviewapp.adapters.ListAdapter
import com.example.listviewapp.model.Item
import com.example.listviewapp.viewmodels.ListViewModel

class ListFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var viewModel: ListViewModel
    private lateinit var listAdapter: ListAdapter




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_list, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(ListViewModel::class.java)
        listView = rootView.findViewById(R.id.listView)

        // Initialize the adapter with an empty list
        listAdapter = ListAdapter(
            requireContext(),
            onDeleteClicked = { item -> viewModel.deleteItem(item.id) },
            onEditClicked = { item -> showEditDialog(item) }
        )
        listView.adapter = listAdapter

        // Observe the animal list LiveData
        viewModel.itemList.observe(viewLifecycleOwner) { items ->
            if (items != null) {
                // Update adapter with the new data
                listAdapter.updateData(items)
                listAdapter.notifyDataSetChanged()
            }
        }

        viewModel.fetchItems()

        return rootView
    }



    private fun showEditDialog(item: Item) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_edit_item, null)
        builder.setView(dialogView)

        val editText = dialogView.findViewById<EditText>(R.id.editItem)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)

        editText.setText(item.name)

        val dialog = builder.create()

        saveButton.setOnClickListener {
            val updatedName = editText.text.toString().trim()
            if (updatedName.isNotEmpty()) {
                viewModel.updateItem(item.id, updatedName)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}

