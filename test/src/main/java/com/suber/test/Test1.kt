package com.suber.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.suber.annotation.Route

@Route(path = "/test/test1")
class Test1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test1)
    }
}