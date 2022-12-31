package com.pertamina.transkripnilai.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Nilai::class], version = 1)

abstract class NilaiDB: RoomDatabase() {
    abstract fun dao(): NilaiDao

    companion object{
        @Volatile
        private var INSTANCE: NilaiDB? = null

        fun getDatabase(context: Context): NilaiDB{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NilaiDB::class.java,
                    "NilaiDB"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}