package com.siliconstack.dealertrax.dao

import android.arch.persistence.room.*


import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.siliconstack.dealertrax.model.FloorModel

@Dao
interface FloorDAO {

    @Query("select * from FloorModel")
    fun getAll(): List<FloorModel>


    @Query("delete from FloorModel")
    fun deleteAll()

    @Insert(onConflict = REPLACE)
    fun addRow(floorModel: FloorModel)

    @Delete
    fun deleteRow(floorModel: FloorModel)

    @Update
    fun updateRow(floorModel: FloorModel)


    @Query("delete from FloorModel where id =:id")
    fun deleteById(id: Int)

}