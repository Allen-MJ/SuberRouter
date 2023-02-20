package com.suber.arouter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.suber.annotation.Route
import com.suber.arouter.databinding.ActivityMainBinding
import com.suber.router.Router

@Route(path = "/app/main")
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mainTv.setOnClickListener {
            /*SRouter.instance.
            build("test","/test/test1")
                .navition(this)*/
            /*Router.Builder()
                .build("test","/test/test2")
                .putExtra("name","Allen")
                .putExtra("age",10)
                .putExtra("height",185.52)
                .navition(this,10)*/
            Router.build {
                build("test","/test/test2")
                putExtra("name","Allen")
                putExtra("age",10)
                putExtra("height",185.52)
            }.apply {
                val slist = arrayListOf<String>()
                slist.add("篮球")
                slist.add("足球")
                slist.add("排球")
                this.intent?.putStringArrayListExtra("list",slist)
                navition(this@MainActivity,10)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK) {
            when(requestCode){
                10 -> Log.d("debug","返回值：${data?.getStringExtra("todo")}")
            }
        }
    }
}