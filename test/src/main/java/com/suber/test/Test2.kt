package com.suber.test

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.suber.annotation.Route
import com.suber.test.databinding.ActivityTest2Binding

@Route(path = "/test/test2")
class Test2 : AppCompatActivity() {
    private lateinit var bind: ActivityTest2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityTest2Binding.inflate(layoutInflater)
        setContentView(bind.root)
        val name = intent.getStringExtra("name")
        val age = intent.getIntExtra("age",0)
        val height = intent.getDoubleExtra("height",0.00)
        val list = intent.getStringArrayListExtra("list")
        Log.d("debug","获取的数据：姓名$name，年龄${age}岁，身高${height}cm，参加了${list?.size}个项目!")
        bind.clickTv.setOnClickListener {
            setResult(Activity.RESULT_OK,intent.putExtra("todo","${name}点击了按钮！"))
            finish()
        }
    }
}