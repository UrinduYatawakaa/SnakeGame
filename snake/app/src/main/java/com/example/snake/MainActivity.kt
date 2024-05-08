package com.example.snake

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snake.ui.theme.SnakeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val butnext = findViewById<Button>(R.id.button_start_snake)
        butnext.setOnClickListener {
            val intent = Intent(this,Snake::class.java)
            startActivity(intent)
        }

        // Load highest score into highScoreView
        val sharedPreferences = getSharedPreferences("SnakeGamePrefs", Context.MODE_PRIVATE)
        val highScore = sharedPreferences.getInt("last_score", 0)
        val highScoreView = findViewById<TextView>(R.id.highScoreView)
        highScoreView.text = "Highest Score: $highScore"


    }


}
