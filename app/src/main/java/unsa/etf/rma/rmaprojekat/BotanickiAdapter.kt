package unsa.etf.rma.rmaprojekat
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.min

class BotanickiAdapter(private val context: Context,
                       private var biljkeList: List<Biljka>,
                       private val onItemClickListener: OnItemClickListener?
) :
    RecyclerView.Adapter<BotanickiAdapter.BiljkaViewHolder>() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val trefleDAO = TrefleDAO()
    private lateinit var biljkaDAO: BiljkaDAO
    private lateinit var database: BiljkaDatabase
    class BiljkaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nazivTextView: TextView = itemView.findViewById(R.id.nazivItem)
        val slikaView: ImageView = itemView.findViewById(R.id.slikaItem)
        val porodicaTextView: TextView = itemView.findViewById(R.id.porodicaItem)
        val zemljisniTipTextView: TextView = itemView.findViewById(R.id.zemljisniTipItem)
        val klimatskiTipTextView: TextView = itemView.findViewById(R.id.klimatskiTipItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):BiljkaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.botanicki_fokus, parent, false)
        database = BiljkaDatabase.getInstance(context)

        biljkaDAO = database.biljkaDAO()
        return BiljkaViewHolder(view)
    }

    override fun onBindViewHolder(holder: BiljkaViewHolder, position: Int) {
        val biljka = biljkeList[position]
        holder.nazivTextView.text = biljka.naziv
        holder.porodicaTextView.text = biljka.porodica
        holder.klimatskiTipTextView.text = biljka.klimatskiTipovi[0].opis
        holder.zemljisniTipTextView.text = biljka.zemljisniTipovi[0].naziv
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(biljka)
        }
        coroutineScope.launch {
            val bitmap = getBitmapForBiljka(biljka)
            withContext(Dispatchers.Main) {
                holder.slikaView.setImageBitmap(bitmap)
            }
        }
    }

    override fun getItemCount(): Int {
        return biljkeList.size
    }

    fun filterList(biljke: List<Biljka>, selectedPlant: Biljka) {
        val filteredList = biljke.filter { plant ->
            plant.porodica == selectedPlant.porodica &&
                    plant.klimatskiTipovi.any { selectedPlant.klimatskiTipovi.contains(it) } &&
                    plant.zemljisniTipovi.any { selectedPlant.zemljisniTipovi.contains(it) }
        }
        this.biljkeList = filteredList
        notifyDataSetChanged()
    }

    fun resetList(newList: List<Biljka>) {
        biljkeList = newList
        notifyDataSetChanged()
    }

    fun updateList(newList: List<Biljka>) {
        biljkeList = newList
    }
    private suspend fun getBitmapForBiljka(biljka: Biljka): Bitmap {
        Log.d("getBitmapForBiljka", "Fetching bitmap for Biljka with id: ${biljka.id}")

        val cachedBitmap = biljka.id?.let { biljkaDAO.getBitmapByPlantId(it) }
        return if (cachedBitmap != null) {
            Log.d("getBitmapForBiljka", "Found cached bitmap for Biljka with id: ${biljka.id}")
            cachedBitmap.bitmap
        } else {
            Log.d("getBitmapForBiljka", "No cached bitmap found for Biljka with id: ${biljka.id}, fetching from API")

            var bitmapFromApi = trefleDAO.getImage(biljka)
            if (bitmapFromApi != null) {
                Log.d("getBitmapForBiljka", "Fetched bitmap from API for Biljka with id: ${biljka.id}")

                val compressedBitmap = compressBitmap(bitmapFromApi)
                Log.d("getBitmapForBiljka", "Compressed bitmap for Biljka with id: ${biljka.id}")


                biljka.id?.let { biljkaDAO.addImage(it, compressedBitmap) }
                Log.d("getBitmapForBiljka", "Stored compressed bitmap in database for Biljka with id: ${biljka.id}")
            } else {
                Log.d("getBitmapForBiljka", "API did not return a bitmap for Biljka with id: ${biljka.id}")
            }
            bitmapFromApi ?: compressBitmap(trefleDAO.createNoPhotoBitmap()).also {
                Log.d("getBitmapForBiljka", "Using default no-photo bitmap for Biljka with id: ${biljka.id}")
            }
        }
    }

    // Function to compress bitmap
    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        val stream = ByteArrayOutputStream()
        val cropSize= minOf(min(bitmap.width, bitmap.height), 650)
        val croppedBitmap = Bitmap.createBitmap(bitmap, bitmap.width-cropSize, bitmap.height-cropSize, cropSize, cropSize)
        // Compress the bitmap with JPEG format and quality of 50%
        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)

        return BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
    }
}