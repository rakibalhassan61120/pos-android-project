package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.PosRepository
import com.example.ui.PosApp
import com.example.ui.PosViewModel
import com.example.ui.PosViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Edge-To-Edge enabled for modern full screen layouts
        enableEdgeToEdge()

        // Initialize SQLite Room database persistence
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = PosRepository(database)
        
        // Build PosViewModel via custom factory
        val viewModel = ViewModelProvider(
            this, 
            PosViewModelFactory(repository)
        ).get(PosViewModel::class.java)

        setContent {
            MyApplicationTheme {
                // Main Point of Sale App container Entry Point
                PosApp(viewModel)
            }
        }
    }
}
