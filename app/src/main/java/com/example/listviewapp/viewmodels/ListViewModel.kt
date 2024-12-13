    package com.example.listviewapp.viewmodels

    import android.util.Log
    import androidx.lifecycle.LiveData
    import androidx.lifecycle.MutableLiveData
    import androidx.lifecycle.ViewModel
    import com.example.listviewapp.model.Item
    import com.example.listviewapp.network.ApiService

    class ListViewModel : ViewModel() {

        // LiveData to hold the list of animals
        private val _itemList = MutableLiveData<List<Item>?>()
        val itemList: LiveData<List<Item>?> get() = _itemList

        // LiveData to indicate if there was an error while fetching data
        private val _error = MutableLiveData<String>()
        val error: LiveData<String> get() = _error

        fun fetchItems() {
            ApiService.getItem { items ->
                if (!items.isNullOrEmpty()) {
                    _itemList.postValue(items)
                    Log.d("ListViewModel", "Fetched item: ${items.size}")
                } else {
                    Log.w("ListViewModel", "Fetched item is empty or null, skipping update.")
                }
            }
        }

        fun deleteItem(itemId: Int) {
            ApiService.deleteItem(itemId) { isSuccess ->
                if (isSuccess) {

                    _itemList.value?.let { currentList ->

                        val updatedList = currentList.filter { it.id != itemId }

                        _itemList.postValue(updatedList)

                        if (updatedList.isEmpty()) {
                            _error.postValue("no item list")
                        }
                    }
                } else {
                    _error.postValue("error.")
                }
            }
        }

        fun updateItem(id: Int, newName: String) {
            ApiService.updateItem(id, newName) { isSuccess ->
                if (isSuccess) {
                    fetchItems() //
                } else {
                    _error.postValue("Failed to edit item.")
                }
            }
        }




        fun addItem(item: Item) {

            ApiService.addItem(item) { isSuccess ->
                if (isSuccess) {

                    val currentList = _itemList.value ?: mutableListOf()
                    val updatedList = currentList.toMutableList().apply { add(item) }

                    _itemList.postValue(updatedList) // Update LiveData on the main thread
                    Log.d("ViewModel", "Animal added successfully. Updated list: ${updatedList.joinToString { it.name }}")
                } else {
                    Log.e("ViewModel", "error.")
                }
            }
        }


    }

