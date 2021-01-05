package com.nirmal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nirmallib.NirmalLib.lOG

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lOG(this)
    }
}