package com.ghp.optimus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ghp.engine.Engine

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Engine.instance.init()
    }
}