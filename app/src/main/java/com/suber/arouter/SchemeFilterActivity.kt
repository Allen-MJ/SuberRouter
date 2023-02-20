package com.suber.arouter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.suber.router.Router

class SchemeFilterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.data?.let {
            Router.build {
                build("","/")
            }.apply {

            }
        }
    }
}