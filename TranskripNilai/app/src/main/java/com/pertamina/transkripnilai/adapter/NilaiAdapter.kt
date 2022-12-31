package com.pertamina.transkripnilai.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pertamina.transkripnilai.MainActivity
import com.pertamina.transkripnilai.R
import com.pertamina.transkripnilai.data.Nilai
import com.pertamina.transkripnilai.data.NilaiDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NilaiAdapter (val context: Context, var list: List<Nilai>): RecyclerView.Adapter<NilaiAdapter.ItemViewHolder>(){
    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val kdMatkul = view.findViewById<TextView>(R.id.textViewKdMatkul)
        val matkul = view.findViewById<TextView>(R.id.textViewMatkul)
        val sks = view.findViewById<TextView>(R.id.textViewSKS)
        val nilai = view.findViewById<TextView>(R.id.textViewNilai)
        val buttonDelete: ImageView = view.findViewById(R.id.imageViewDelete)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_data,
                    parent,
                    false
                )
        )
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        with(holder){
            val mhs = list[position]
            kdMatkul.text = mhs.kdmatkul
            matkul.text = mhs.matkul
            sks.text = mhs.sks.toString()
            nilai.text = mhs.nilai.toString()

            buttonDelete.setOnClickListener{
                AlertDialog.Builder(context)
                    .setTitle(R.string.confirmation)
                    .setMessage(R.string.konfirmasi_hapus)
                    .setPositiveButton(R.string.ya){_,_->
                        //untuk menghapus di UI
                        update(list.filter { it.id != mhs.id })
                        (context as MainActivity).kalkulasiIPK(list)
                        (context as MainActivity).kalkulasiSKS(list)
                        //untuk menghapus dri database
                        CoroutineScope(Dispatchers.IO).launch {
                            with(NilaiDB.getDatabase(context).dao()){
                                delete(mhs)
                            }
                        }
                    }
                    .setNegativeButton(R.string.tidak){_,_->}
                    .show()

            }
            kdMatkul.setOnClickListener {
                (context as MainActivity).showInputCourse(mhs)
            }


        }
    }

    override fun getItemCount() = list.size

    @SuppressLint("NotifyDataChanged")
    fun update(listUpdate: List<Nilai>){
        list = listUpdate
        notifyDataSetChanged()
    }


    fun isCodeExist(kdmatkul: String) = list.find { it.kdmatkul == kdmatkul } !=null

}
