package com.pertamina.transkripnilai

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.update
import android.text.Editable
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.pertamina.transkripnilai.adapter.NilaiAdapter
import com.pertamina.transkripnilai.data.Nilai
import com.pertamina.transkripnilai.data.NilaiDB
import com.pertamina.transkripnilai.databinding.ActivityFormInputBinding
import com.pertamina.transkripnilai.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object {
        var db: NilaiDB? = null
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = NilaiDB.getDatabase(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadDatabase()
        binding.buttonAdd.setOnClickListener{ showInputCourse() }

        }
    private fun loadDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            db?.let{ db->
                val list = db.dao().load()
                runOnUiThread{
                    binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                    binding.recyclerView.adapter = NilaiAdapter(this@MainActivity, list)
                    kalkulasiIPK(list)
                    kalkulasiSKS(list)
                }
            }
        }
    }

        fun kalkulasiSKS(list: List<Nilai>) {
        binding.textViewSKS.text =
            if (list.isEmpty()) "0" else {
                val totalSKS = list.sumOf { it.sks }.toString()
                String.format(totalSKS)
            }
    }

    val String.edittable: Editable get() = Editable.Factory.getInstance().newEditable(this)

        fun showInputCourse(mhs: Nilai?=null) {
        with(Dialog(this)){
            setContentView(R.layout.activity_form_input)
            val kdMatkul = findViewById<TextView>(R.id.editKdMatkul)
            val Matkul = findViewById<TextView>(R.id.editMatkul)
            val sks = findViewById<TextView>(R.id.editsks)
            val nilai = findViewById<TextView>(R.id.editNilai)

            mhs?.let {
                kdMatkul.text = it.kdmatkul.edittable
                Matkul.text = it.matkul.edittable
                sks.text = it.sks.toString().edittable
                nilai.text = it.nilai.toString().edittable
            }

            val list = (binding.recyclerView.adapter as NilaiAdapter).list.filter { it != mhs }

            fun isFullFielled() = kdMatkul.text.trim().isNotEmpty() &&
                    Matkul.text.trim().isNotEmpty() &&
                    sks.text.trim().isNotEmpty() &&
                    nilai.text.trim().isNotEmpty()
            findViewById<Button>(R.id.buttonCancle).setOnClickListener{
                dismiss()
            }
            findViewById<Button>(R.id.buttonSave).setOnClickListener{
                val nilai = Nilai(
                    mhs?.id?:0,  //untuk mengechek id ketika edit maka di tambahkan parameter mhs
                    kdMatkul.text.toString().trim(),
                    Matkul.text.toString().trim(),
                    sks.text.toString().toIntOrNull() ?: 3,
                    nilai.text.toString().first(),
                )
                when {
                    !isFullFielled() ->
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle(R.string.error)
                            .setMessage(R.string.data_harus_diisi)
                            .setPositiveButton(R.string.ok){_,_->}
                            .show()
                    //mengcheck ada atau tidaknya isi
                    list.find { it.kdmatkul == nilai.kdmatkul} != null ->
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle(R.string.error)
                            .setMessage(R.string.kode_sudah_ada)
                            .setPositiveButton(R.string.ok){_,_->}
                            .show()
                    else -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            db?.let { db ->
                                if (mhs == null) //kalau dia = null maka dia insert, jika tidak maka update
                                    db.dao().insert(nilai)
                                else db.dao().update(nilai)
                                val listload = db.dao().load()
                                runOnUiThread {
                                    update(listload)
                                    dismiss()
                                }
                            }
                        }
                    }// akhir dari eksekusi pemasukan data nilai
                }
                }
                show()
            }

    }

    private fun update(list: List<Nilai>) {
        (binding.recyclerView.adapter as NilaiAdapter).update(list)
        kalkulasiIPK(list)
        kalkulasiSKS(list)

    }

    fun kalkulasiIPK(list: List<Nilai>) {
        binding.textViewIPK.text =
            if (list.isEmpty()) "_" else {
                val totalSKS = list.sumOf { it.sks }
                val totalNilaiKaliSKS = list.sumOf { it.sks * ('E' - it.nilai) }.toFloat()
                val ipk = totalNilaiKaliSKS/totalSKS
                String.format("%3.2f", ipk)
            }

    }

    private fun deleteAll(list: List<Nilai>){
        (binding.recyclerView.adapter as NilaiAdapter).update(list)
        kalkulasiIPK(list)
        kalkulasiSKS(list)
    }

}