package com.pertamina.transkripnilai.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nilai")
data class Nilai (
    @PrimaryKey (autoGenerate = true) @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "kdmatkul") val kdmatkul: String,
    @ColumnInfo(name = "matkul") val matkul: String,
    @ColumnInfo(name = "sks") val sks: Int,
    @ColumnInfo(name = "nilai") val nilai: Char,
        )