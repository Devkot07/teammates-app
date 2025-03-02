package com.pezont.teammates


import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pezont.teammates.ui.TeammatesViewModel
import com.pezont.teammates.ui.screens.TeammatesApp
import com.pezont.teammates.ui.theme.TeammatesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            TeammatesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val teammatesViewModel: TeammatesViewModel =
                        viewModel(factory = TeammatesViewModel.Factory)
                    TeammatesApp(
                        viewModel = teammatesViewModel
                    )
                }
            }
        }
    }
}
