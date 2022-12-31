package com.pertamina.transkripnilai.data

import androidx.room.*

@Dao
interface NilaiDao {
    @Insert
    suspend fun insert(nilai: Nilai)

    @Update
    suspend fun update(nilai: Nilai)

    @Delete
    suspend fun delete(nilai: Nilai)

    @Query("Select * from nilai")
    suspend fun load():List<Nilai>

    @Query("select count(*) from nilai where kdmatkul = :kdmatkul")
    suspend fun countCourse(kdmatkul: String): Int


}