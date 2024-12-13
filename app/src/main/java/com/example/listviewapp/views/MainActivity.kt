package com.example.listviewapp.views

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.listviewapp.R
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.listviewapp.model.Item

import com.example.listviewapp.viewmodels.ListViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)

        // Set up the Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up the "Add" button
        val addButton: Button = findViewById(R.id.add_button)
        addButton.setOnClickListener {
            // Show the Add Animal dialog
            showAddAnimalDialog()
        }

        // Add the ListFragment to the activity
        if (savedInstanceState == null) {
            val fragment = ListFragment()  // Create an instance of ListFragment
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)  // Replace the fragment container with ListFragment
            transaction.commit()  // Commit the transaction
        }
    }

    // Function to show the Add Animal dialog
    private fun showAddAnimalDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_item, null)
        builder.setView(dialogView)

        val editText = dialogView.findViewById<EditText>(R.id.addItem)
        val saveButton = dialogView.findViewById<Button>(R.id.addSaveItem)

        val dialog = builder.create()

        saveButton.setOnClickListener {
            val animalName = editText.text.toString().trim()
            if (animalName.isNotEmpty()) {
                val randomId = (1..1000).random() // Generate a random ID
                val newAnimal = Item(randomId, animalName)

                // Call ViewModel to add the animal
                viewModel.addItem(newAnimal)

                dialog.dismiss() // Dismiss the dialog
            } else {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}

