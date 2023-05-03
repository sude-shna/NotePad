package com.example.mynotes.DB

import androidx.lifecycle.LiveData
import com.example.mynotes.Models.Notes

class NotesRepo(private val noteDao: NoteDao) {
    val allNotes : LiveData<List<Notes>> = noteDao.getAllNotes()
    suspend fun insert(note: Notes){
        noteDao.insert(note)
    }
    suspend fun delete(note: Notes){
        noteDao.delete(note)
    }
    suspend fun update(note: Notes){
        noteDao.update(note.id, note.title, note.note)
    }
}