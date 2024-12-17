package unsa.etf.rma.rmaprojekat

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NovaBiljkaActivity: AppCompatActivity()  {
    private lateinit var nazivET: EditText
    private lateinit var porodicaET: EditText
    private lateinit var medicinskoUpozorenjeET: EditText
    private lateinit var jeloET: EditText
    private lateinit var medicinskaKoristLV: ListView
    private lateinit var klimatskiTipLV: ListView
    private lateinit var zemljisniTipLV: ListView
    private lateinit var profilOkusaLV: ListView
    private lateinit var jelaLV: ListView
    private lateinit var dodajJeloBtn: Button
    private lateinit var dodajBiljkuBtn: Button
    private lateinit var uslikajBiljkuBtn: Button
    private lateinit var slikaBiljke: ImageView
    private  var jelaList= mutableListOf<String>()
    private val REQUEST_IMAGE_CAPTURE = 1
    private val CAMERA_PERMISSION_REQUEST_CODE = 101

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val trefleDAO = TrefleDAO()
    private lateinit var biljkaDAO : BiljkaDAO
    private lateinit var database: BiljkaDatabase

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nova_biljka_activity)

        nazivET = findViewById(R.id.nazivET)
        porodicaET = findViewById(R.id.porodicaET)
        medicinskoUpozorenjeET = findViewById(R.id.medicinskoUpozorenjeET)
        jeloET = findViewById(R.id.jeloET)
        medicinskaKoristLV = findViewById(R.id.medicinskaKoristLV)
        klimatskiTipLV = findViewById(R.id.klimatskiTipLV)
        zemljisniTipLV = findViewById(R.id.zemljisniTipLV)
        profilOkusaLV = findViewById(R.id.profilOkusaLV)
        jelaLV = findViewById(R.id.jelaLV)
        dodajJeloBtn = findViewById(R.id.dodajJeloBtn)
        dodajBiljkuBtn = findViewById(R.id.dodajBiljkuBtn)
        uslikajBiljkuBtn = findViewById(R.id.uslikajBiljkuBtn)
        slikaBiljke=findViewById(R.id.slikaIV)

        Log.d("DEBUG", "NazivET hint: ${nazivET.hint}")
        Log.d("DEBUG", "PorodicaET hint: ${porodicaET.hint}")

        uslikajBiljkuBtn.setOnClickListener {
            checkCameraPermission()
        }

        database = BiljkaDatabase.getInstance(this)
        biljkaDAO = database.biljkaDAO()
        medicinskaKoristLV.choiceMode=ListView.CHOICE_MODE_MULTIPLE
        klimatskiTipLV.choiceMode=ListView.CHOICE_MODE_MULTIPLE
        zemljisniTipLV.choiceMode=ListView.CHOICE_MODE_MULTIPLE
        profilOkusaLV.choiceMode=ListView.CHOICE_MODE_SINGLE
        jelaLV.choiceMode=ListView.CHOICE_MODE_SINGLE

        val medicinskeKoristiValues = MedicinskaKorist.values().map { it.opis }.toTypedArray()
        val adapterMedicinskaKoristLV = ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, medicinskeKoristiValues)
        medicinskaKoristLV.adapter = adapterMedicinskaKoristLV

        val zemljisteValues = Zemljiste.values().map { it.naziv }.toTypedArray()
        val adapterZemljisniTipLV = ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, zemljisteValues)
        zemljisniTipLV.adapter = adapterZemljisniTipLV

        val klimatskiTipValues = KlimatskiTip.values().map { it.opis }.toTypedArray()
        val adapterKlimatskiTipLV = ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, klimatskiTipValues)
        klimatskiTipLV.adapter = adapterKlimatskiTipLV

        val profilOkusaValues = ProfilOkusaBiljke.values().map { it.opis }.toTypedArray()
        val adapterProfilOkusaLV = ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, profilOkusaValues)
        profilOkusaLV.adapter = adapterProfilOkusaLV

        // Set an item click listener for the ListView to populate the jeloET with the selected dish name
        jelaLV.setOnItemClickListener { parent, view, position, id ->
            val izabranoJelo = jelaList[position]
            jeloET.setText(izabranoJelo)
            dodajJeloBtn.setText("Izmijeni jelo")
        }

        dodajJeloBtn.setOnClickListener {
            if(jeloET.text.toString().length<2 || jeloET.text.toString().length>20){
                jeloET.setError("Dužina imena jela smije biti između 2 i 20 znakova");
            }
            else {
                val novoJelo = jeloET.text.toString().trim().lowercase()
                val selectedPosition = jelaLV.checkedItemPosition
                if (selectedPosition == ListView.INVALID_POSITION) {
                    if (novoJelo.isNotEmpty()) {
                        if (novoJeloValidation(novoJelo))
                            jelaList.add(novoJelo)
                    }
                } else {
                    if (novoJelo.isNotEmpty()) {
                        if (novoJeloValidation(novoJelo))
                            jelaList[selectedPosition] = novoJelo
                    } else {
                        jelaList.removeAt(selectedPosition)
                    }
                }
                jeloET.setText("")
                dodajJeloBtn.text = "Dodaj jelo"
                jelaLV.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, jelaList)
            }
        }


        dodajBiljkuBtn.setOnClickListener {
            val validationError = biljkaObjectValidation()

            if (!validationError){



                val naziv : String = nazivET.text.toString().trim()
                val porodica : String= porodicaET.text.toString().trim()
                val medicinskoUpozorenje : String= medicinskoUpozorenjeET.text.toString().trim()



                val selectedMedicinskaKorist = mutableListOf<MedicinskaKorist>().apply {
                    addCheckedItemsToList(medicinskaKoristLV) { item ->
                        MedicinskaKorist.entries.find { it.opis == item }
                    }
                }

                val selectedKlimatskiTip = mutableListOf<KlimatskiTip>().apply {
                    addCheckedItemsToList(klimatskiTipLV) { item ->
                        KlimatskiTip.entries.find { it.opis == item }
                    }
                }

                val selectedZemljisniTip = mutableListOf<Zemljiste>().apply {
                    addCheckedItemsToList(zemljisniTipLV) { item ->
                        Zemljiste.entries.find { it.naziv == item }
                    }
                }


                val selectedProfilOkusa = profilOkusaLV.checkedItemPosition.takeIf { it != ListView.INVALID_POSITION }?.let {
                    val tasteProfileString = profilOkusaLV.getItemAtPosition(it) as String
                    ProfilOkusaBiljke.entries.find { it.opis == tasteProfileString }
                } ?: ProfilOkusaBiljke.SLATKI


                coroutineScope.launch {
                    try {
                        val maxId = biljkaDAO.getMaxIdForBiljka() ?:0L
                        val uniqueId = maxId+1
                        // Create Plant object
                        var novaBiljka = Biljka(uniqueId, naziv, porodica, medicinskoUpozorenje, selectedMedicinskaKorist,
                            selectedProfilOkusa, jelaList, selectedKlimatskiTip, selectedZemljisniTip, false)

                        novaBiljka = trefleDAO.fixData(novaBiljka)
                        val insertedId = biljkaDAO.saveBiljka(novaBiljka)
                        if (insertedId.equals(-1L)) {
                            Log.d("INSERTBILJKE" , "Insert nije uspio")
                        } else {
                            Log.d("INSERTBILJKE" , "Biljka uspjesno ubacena u bazu")

                        }// Clear EditText fields
                        clearEditTextFields()

                        // Clear ListView selections
                        clearListViewSelections()
                        setResult(Activity.RESULT_OK)
                        finish()
                        // Do something with the fixed plant
                    } catch (e: Exception) {
                        // Handle exceptions
                        e.printStackTrace()
                    }
                }


            }

        }

    }

    inline fun <reified T> MutableList<T>.addCheckedItemsToList(listView: ListView, findFunction: (String) -> T?) {
        for (i in 0 until listView.count) {
            if (listView.isItemChecked(i)) {
                val item = listView.getItemAtPosition(i) as String
                findFunction(item)?.let { add(it) }
            }
        }
    }


    private fun clearEditTextFields() {
        nazivET.text.clear()
        porodicaET.text.clear()
        medicinskoUpozorenjeET.text.clear()
        jeloET.text.clear()
    }


    private fun clearListViewSelections() {
        medicinskaKoristLV.clearChoices()
        klimatskiTipLV.clearChoices()
        zemljisniTipLV.clearChoices()
        profilOkusaLV.clearChoices()
        jelaLV.clearChoices()
    }

    private fun biljkaObjectValidation():Boolean{
        var errorOccurred= false;
        if(nazivET.text.toString().length<2 || nazivET.text.toString().length>40){
            errorOccurred=true
            nazivET.setError("Dužina imena biljke smije biti između 2 i 40 znakova")

        }
        if(porodicaET.text.toString().length<2 || porodicaET.text.toString().length>40){
            errorOccurred=true
            porodicaET.setError("Dužina imena porodice smije biti između 2 i 40 znakova")
        }
        if(medicinskoUpozorenjeET.text.toString().length<2 || medicinskoUpozorenjeET.text.toString().length>40){
            errorOccurred=true
            medicinskoUpozorenjeET.setError("Dužina teksta medicinskog upozorenja smije biti između 2 i 40 znakova")
        }
/*
        if(medicinskaKoristLV.checkedItemCount<1){
            errorOccurred=true
            Toast.makeText(applicationContext, "Odaberite makar jednu medicinsku korist", Toast.LENGTH_LONG).show()
        }
        if(klimatskiTipLV.checkedItemCount<1){
            errorOccurred=true
            Toast.makeText(applicationContext, "Odaberite makar jedan klimatski tip", Toast.LENGTH_LONG).show()
        }
        if(zemljisniTipLV.checkedItemCount<1){
            errorOccurred=true
            Toast.makeText(applicationContext, "Odaberite makar jedan zemljisni tip", Toast.LENGTH_LONG).show()
        }
        if(profilOkusaLV.checkedItemCount<1){
            errorOccurred=true
            Toast.makeText(applicationContext, "Odaberite jedan profil okusa", Toast.LENGTH_LONG).show()
        }
        if(jelaLV.size<1){
            Toast.makeText(applicationContext, "Dodajte makar jedno jelo u listu jela", Toast.LENGTH_LONG).show()
            errorOccurred=true

        }
        */

        if(medicinskaKoristLV.checkedItemCount<1 || klimatskiTipLV.checkedItemCount<1 || zemljisniTipLV.checkedItemCount<1
            || profilOkusaLV.checkedItemCount<1 || jelaLV.size<1){
            errorOccurred=true
            dodajBiljkuBtn.setError("Niste odabrali polje u svim listama")
        }


        return errorOccurred
    }

    fun novoJeloValidation(novoJelo : String):Boolean{
        var validEntry=true
        for(i in 0 until jelaList.size){
            if(jelaLV.getItemAtPosition(i).toString().lowercase()==novoJelo){
                jeloET.setError("Jelo već postoji")
                validEntry=false
            }
        }

        return validEntry
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission has already been granted
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try{
            startActivityForResult(takePictureIntent, this.REQUEST_IMAGE_CAPTURE)
        }
        catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No camera app found to handle the image capture intent.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start camera intent
                dispatchTakePictureIntent()
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(
                    this,
                    "Camera permission denied. Cannot open camera.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            slikaBiljke.setImageBitmap(imageBitmap)
        }
    }

}