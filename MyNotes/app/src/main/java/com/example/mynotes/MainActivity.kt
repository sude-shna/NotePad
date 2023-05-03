package com.example.mynotes

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mynotes.Adapter.NotesAdapter
import com.example.mynotes.DB.NoteDB
import com.example.mynotes.Models.NoteViewModel
import com.example.mynotes.Models.Notes
import com.example.mynotes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NotesAdapter.NotesClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: NoteDB
    lateinit var viewModel: NoteViewModel
    lateinit var adapter: NotesAdapter
    lateinit var selectedNotes: Notes

    private val updateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result -> if(result.resultCode == Activity.RESULT_OK){
            val notes = result.data?.getSerializableExtra("note") as? Notes
            if (notes != null){
                viewModel.updateNote(notes)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initializing UI
        initUI()

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NoteViewModel::class.java)

        viewModel.allnotes.observe(this){
            list -> list?.let{
                adapter.updateList(list)
            }
        }
        database = NoteDB.getDatabase(this)
    }

    private fun initUI() {
        binding.recycler.setHasFixedSize(true)
        binding.recycler.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        adapter = NotesAdapter(this, this)
        binding.recycler.adapter = adapter

        val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
        if (result.resultCode == Activity.RESULT_OK){
                val notes = result.data?.getSerializableExtra("note") as? Notes
                if (notes != null){
                    viewModel.insertNote(notes)
                }
            }
        }
        binding.fab.setOnClickListener{
            val intent = Intent(this, AddNotes::class.java)
            getContent.launch(intent)
        }

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null){
                    adapter.filterList(newText)
                }
                return true
            }
        })
    }

    override fun onItemClicked(notes: Notes) {
        val intent = Intent(this@MainActivity, AddNotes::class.java)
        intent.putExtra("current_note", notes)
        updateNote.launch(intent)
    }

    override fun onLongItemClicked(notes: Notes, cardView: CardView) {
        selectedNotes = notes
        popUpDisplay(cardView)
    }

    private fun popUpDisplay(cardView: CardView) {
        val popup = PopupMenu(this, cardView)
        popup.setOnMenuItemClickListener(this@MainActivity)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.delete_note){
            viewModel.deleteNote(selectedNotes)
            return true
        }
        return false
    }
    
}