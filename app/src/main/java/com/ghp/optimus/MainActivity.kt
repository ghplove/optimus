package com.ghp.optimus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ghp.engine.Engine
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_test.setOnClickListener {
            Engine.instance.init()
        }
    }
}