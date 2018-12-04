package com.siliconstack.dealertrax.dao

import android.arch.persistence.room.*


import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.siliconstack.dealertrax.model.OperatorModel

@Dao
interface NameDAO {

    @Query("select * from OperatorModel")
    fun getAll(): List<OperatorModel>

    @Insert(onConflict = REPLACE)
    fun addRow(operatorModel: OperatorModel)

    @Delete
    fun deleteRow(operatorModel: OperatorModel)

    @Update
    fun updateRow(operatorModel: OperatorModel)

    @Query("delete from OperatorModel where id =:id")
    fun deleteById(id: Int)

}