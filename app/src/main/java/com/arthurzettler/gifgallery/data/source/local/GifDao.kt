package com.arthurzettler.gifgallery.data.source.local

import androidx.room.*
import com.arthurzettler.gifgallery.data.Gif

@Dao
interface GifDao {
    @Query("SELECT * FROM gif_table")
    fun getGifs(): List<Gif>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGif(gif: Gif)

    @Query("DELETE FROM gif_table WHERE id = :gifId")
    fun removeGif(gifId: String)
}