package com.example.listviewapp.adapters

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.example.listviewapp.R
import com.example.listviewapp.model.Item




class ListAdapter(
    context: Context,
    private val onDeleteClicked: (Item) -> Unit,
    private val onEditClicked: (Item) -> Unit
) : ArrayAdapter<Item>(context, R.layout.item_list, ArrayList()) {

    private var items: MutableList<Item> = ArrayList()

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Item? = items[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_list, parent, false)

        val textView = view.findViewById<TextView>(R.id.itemText)
        val deleteButton = view.findViewById<Button>(R.id.deleteButton)
        val editButton = view.findViewById<Button>(R.id.editButton)

        val item = items[position]
        textView.text = item.name

        // Set up the delete button
        deleteButton.setOnClickListener {
            onDeleteClicked(item)
        }

        // Set up the edit button
        editButton.setOnClickListener {
            onEditClicked(item)
        }

        return view
    }

    // Method to update the adapter's data
    fun updateData(newAnimals: List<Item>) {
        this.items.clear()  // Clear existing data
        this.items.addAll(newAnimals)  // Add new data
    }
}


