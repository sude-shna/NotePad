package com.example.mynotes.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mynotes.Models.Notes
import com.example.mynotes.Utilities.DATABASE_NAME

@Database(entities = arrayOf(Notes::class), version = 1, exportSchema = false)
abstract class NoteDB : RoomDatabase(){
    abstract  fun getNoteDao() :NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDB? = null
        fun getDatabase(context: Context): NoteDB {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDB::class.java,
                    DATABASE_NAME).build()

                INSTANCE=instance
                instance
            }
        }
    }
}