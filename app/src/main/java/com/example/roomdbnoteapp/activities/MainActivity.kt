package com.example.roomdbnoteapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdbnoteapp.R
import com.example.roomdbnoteapp.adpater.NoteListAdapter
import com.example.roomdbnoteapp.data.Note
import com.example.roomdbnoteapp.data.NoteRoomDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(),CoroutineScope,View.OnClickListener {

    private var noteDB:NoteRoomDatabase?=null
    private var adapter:NoteListAdapter?=null

    private lateinit var mJob: Job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mJob= Job()
        noteDB= NoteRoomDatabase.getDatabase(this)
        adapter= NoteListAdapter(MainActivity@this,noteDB!!)

        recycler_notes.adapter=adapter
        recycler_notes.layoutManager= LinearLayoutManager(this)

        button_new_note.setOnClickListener(this)
        button_find.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        getAllNotes()
    }

    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
    }

    fun getAllNotes(){
        launch {
            val notes:List<Note>?= noteDB?.noteDao()?.getAllNotes()
            if(notes!=null){
                adapter?.setNotes(notes)
            }
        }
    }


    override fun onClick(v: View?) {
        when(v){
            button_new_note->{
                val newNoteIntent= Intent(this,NewNoteActivity::class.java)
                startActivity(newNoteIntent)
            }

            button_find->{
                findNote()
            }
        }
    }

    fun findNote()= launch {
        val strFind= edittext_find.text.toString()
        if(!TextUtils.isEmpty(strFind)){
            val note:Note?= noteDB?.noteDao()?.findNoteByTitle(strFind)
            if(note!=null){
                val notes:List<Note> = mutableListOf(note)
                adapter?.setNotes(notes)
            }
        }
        else{
            getAllNotes()
        }
    }

    override val coroutineContext: CoroutineContext
        get() = mJob+Dispatchers.Main

}
