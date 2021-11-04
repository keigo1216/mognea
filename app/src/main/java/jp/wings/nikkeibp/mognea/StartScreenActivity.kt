package jp.wings.nikkeibp.mognea

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_start_screen.*

class StartScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen)
    }

    //ボタンが押された時の処理
    fun onStartBtClick(view: View){
        val intentStartScreenActivity = Intent(this@StartScreenActivity, MainActivity::class.java)
        startActivity(intentStartScreenActivity)
    }
}