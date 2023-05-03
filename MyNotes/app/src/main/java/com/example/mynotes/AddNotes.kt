package com.example.mynotes

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mynotes.Models.NoteViewModel
import com.example.mynotes.Models.Notes
import com.example.mynotes.databinding.ActivityAddNotesBinding
import java.text.SimpleDateFormat
import java.util.*

class AddNotes : AppCompatActivity() {

    private  lateinit var binding: ActivityAddNotesBinding
    private lateinit var notes: Notes
    private lateinit var old_note: Notes
    var isUpdate = false
    lateinit var viewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            old_note = intent.getSerializableExtra("current_note") as Notes
            binding.title.setText(old_note.title)
            binding.notes.setText(old_note.note)
            isUpdate = true
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }

        binding.imgCheck.setOnClickListener {
            val title = binding.title.text.toString()
            val notes_desc = binding.notes.text.toString()

            if (title.isNotEmpty() || notes_desc.isNotEmpty()){
                val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm a")
                if (isUpdate){
                    notes = Notes(
                        old_note.id, title, notes_desc, formatter.format(Date())
                    )
                }else{
                    notes = Notes(
                        null, title, notes_desc, formatter.format(Date())
                    )
                }
                val intent = Intent()
                intent.putExtra("note", notes)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }else{
                Toast.makeText(this@AddNotes, "Please enter some data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }
        binding.imgBack.setOnClickListener{
            onBackPressed()
        }
        val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == Activity.RESULT_OK){
                val notes = result.data?.getSerializableExtra("note") as? Notes
                if (notes != null){
                    viewModel.insertNote(notes)
                }
            }
        }
    }
}