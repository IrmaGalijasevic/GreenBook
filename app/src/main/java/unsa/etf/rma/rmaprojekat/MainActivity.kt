package unsa.etf.rma.rmaprojekat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinner: Spinner
    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var resetButton: Button
    private lateinit var newPlantButton: Button
    private lateinit var searchLayout: LinearLayout
    private lateinit var pretragaText: EditText
    private lateinit var colorSpinner: Spinner
    private lateinit var pretragaButton: Button
    private val listaBiljaka: MutableLiveData<List<Biljka>> = MutableLiveData<List<Biljka>>().apply { value = emptyList() }
    private val trefleDAO = TrefleDAO()
    private lateinit var database: BiljkaDatabase
    private lateinit var biljkaDAO: BiljkaDAO

    private val novaBiljkaActivtResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode==Activity.RESULT_OK){
            Log.d("MainActivity", "NovaBiljkaActivity returned RESULT_OK")

            lifecycleScope.launch {
                val newList = withContext(Dispatchers.IO) {
                    biljkaDAO.getAllBiljkas()

                }
                listaBiljaka.value = newList
                updateRecyclerViewData(newList)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = BiljkaDatabase.getInstance(this)
        biljkaDAO = database.biljkaDAO()


        recyclerView = findViewById(R.id.biljkeRV)
        spinner = findViewById(R.id.modSpinner)
        resetButton = findViewById(R.id.resetBtn)
        newPlantButton=findViewById(R.id.novaBiljkaBtn)
        searchLayout = findViewById(R.id.searchLayout)
        pretragaText = findViewById(R.id.pretragaET)
        colorSpinner = findViewById(R.id.bojaSPIN)
        pretragaButton = findViewById(R.id.brzaPretraga)

        newPlantButton.setOnClickListener {
            novaBiljkaActivtResultLauncher.launch(Intent(this, NovaBiljkaActivity::class.java))
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.spinner_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.colors,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            colorSpinner.adapter = adapter
        }

        spinner.setSelection(0)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchPlantsFromDatabase()

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateRecyclerView(position)
                handleBotanickiModeVisibility(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                updateRecyclerView(0)
                handleBotanickiModeVisibility(0)
            }
        }
        resetButton.setOnClickListener {
            lifecycleScope.launch {
                val fetchedList = withContext(Dispatchers.IO) {
                    biljkaDAO.getAllBiljkas()
                }
                fetchedList?.let {
                    when (adapter) {
                        is MedicinskiAdapter -> (adapter as MedicinskiAdapter).resetList(it)
                        is KuharskiAdapter -> (adapter as KuharskiAdapter).resetList(it)
                        is BotanickiAdapter -> (adapter as BotanickiAdapter).resetList(it)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }

        pretragaButton.setOnClickListener {
            val searchQuery = pretragaText.text.toString()
            val selectedColor = colorSpinner.selectedItem?.toString()
            if (searchQuery.isNotBlank() && selectedColor != null) {
                lifecycleScope.launch {
                    val filteredList = withContext(Dispatchers.IO) {
                        val result = trefleDAO.getPlantswithFlowerColor(selectedColor, searchQuery)
                        result
                    }
                    (adapter as? BotanickiAdapter)?.resetList(filteredList)
                }
            }
        }
    }


    private fun updateRecyclerViewData(newList: List<Biljka>) {
        Log.d("MainActivity", "Updating RecyclerView with ${newList.size} plants")

        adapter = MedicinskiAdapter(this, newList, object : OnItemClickListener {
            override fun onItemClick(biljka: Biljka) {
                filterList(biljka)
            }
        })
        recyclerView.adapter = adapter
    }

    private fun updateRecyclerView(position: Int) {
        val list = listaBiljaka.value ?: return
        adapter = when (position) {
            0 -> MedicinskiAdapter(this, list, object : OnItemClickListener {
                override fun onItemClick(biljka: Biljka) {
                    filterList(biljka)
                }
            })
            1 -> KuharskiAdapter(this, list, object : OnItemClickListener {
                override fun onItemClick(biljka: Biljka) {
                    filterList(biljka)
                }
            })
            2 -> BotanickiAdapter(this, list, object : OnItemClickListener {
                override fun onItemClick(biljka: Biljka) {
                    if (pretragaText.text.toString().isBlank() || colorSpinner.selectedItem?.toString() == null) {
                        filterList(biljka)
                    }
                }
            })
            else -> MedicinskiAdapter(this, list, object : OnItemClickListener {
                override fun onItemClick(biljka: Biljka) {
                    filterList(biljka)
                }
            })
        }
        recyclerView.adapter = adapter
    }

    private fun filterList(selectedPlant: Biljka) {
        val list = listaBiljaka.value ?: return
        when (adapter) {
            is MedicinskiAdapter -> (adapter as? MedicinskiAdapter)?.filterList(list, selectedPlant)
            is KuharskiAdapter -> (adapter as? KuharskiAdapter)?.filterList(list, selectedPlant)
            is BotanickiAdapter -> (adapter as? BotanickiAdapter)?.filterList(list, selectedPlant)
        }
        adapter.notifyDataSetChanged()
    }

    private fun fetchPlantsFromDatabase() {
        lifecycleScope.launch {
            Log.d("fetchPlantsFromDatabase", "Fetching plants from the database")

            val fetchedList = withContext(Dispatchers.IO) {
                biljkaDAO.getAllBiljkas()
            }

            if (fetchedList.isNullOrEmpty()) {
                Log.d("fetchPlantsFromDatabase", "Database is empty, passing empty list to RecyclerView")
                updateRecyclerViewData(emptyList())
            } else {
                Log.d("fetchPlantsFromDatabase", "Fetched ${fetchedList.size} plants from the database")
                listaBiljaka.value = fetchedList ?: emptyList() // Set the LiveData value to non-null list or empty list if fetchedList is null
                updateRecyclerViewData(fetchedList)
            }
            adapter.notifyDataSetChanged()
        }
    }


    private fun clearDatabase() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                biljkaDAO.clearData()
                // Clear other tables if necessary

            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }
    private fun handleBotanickiModeVisibility(position: Int) {
        if (position == 2) {
            searchLayout.visibility = View.VISIBLE
        } else {
            searchLayout.visibility = View.GONE
        }
    }
}
