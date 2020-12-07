package com.arthurzettler.gifgallery.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.arthurzettler.gifgallery.data.Gif

@Database(entities = [Gif::class], version = 1, exportSchema = true)
abstract class GifDatabase : RoomDatabase() {
    abstract fun gifDao(): GifDao

    companion object {
        @Volatile
        private var INSTANCE: GifDatabase? = null

        fun getDatabase(context: Context) = INSTANCE
            ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                GifDatabase::class.java,
                "gif_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}