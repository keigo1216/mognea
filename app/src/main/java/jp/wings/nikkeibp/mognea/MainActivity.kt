package jp.wings.nikkeibp.mognea

//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.app.Activity
//import android.view.Menu
//import android.view.MenuItem
//import android.widget.Button

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

//app.AppCompatActivityはActivityの上位互換（古い端末でも使えるようにしたもの）
class MainActivity : AppCompatActivity() {

    var mButton = Array(3, {arrayOfNulls<Button>(4)}) //ボタンのインスタンスを作成
    var mApper = Array(3, {arrayOfNulls<Int>(4)}) //モグラが現在出現しているかどうか
    var mTimeText: TextView? = null
    val mGameTime: Long = 11000 //10秒（最初の1秒の動作がおかしいから）
    val mGameIntarval: Long = 1000 //一秒毎にカウント
    var mScor:Int = 0
    val row = 3 //モグラたたきの行数
    val col = 4 //モグラたたきの列数

    //上部に出るタイマーの作成
    //無名クラスを作るときの構文
    val timer = object :CountDownTimer(mGameTime, mGameIntarval){ //開始時間・インターバル
        //途中経過・残り時間
        //p0：残り時間
        override fun onTick(p0: Long) {
            //初期化
            moleInit()
            //モグラを表示
            printMole()
            //残り時間を表示
            mTimeText?.text = "Time: ${p0 / 1000}"
        }

        //タイマーが終了したとき
        override fun onFinish() {
            //TODO("Not yet implemented")
            moleInit() //初期化
            mTimeText?.text = "Score:" + mScor //スコアを表示
            mScor = 0 //スコアの初期化
        }

    }

    //出現しているかどうかを判定する配列を初期化
    //mButtonを初期化する関数
    fun moleInit(){
        for(i in 0..(row - 1)){
            for(j in 0..(col - 1)){
                mApper[i][j] = 0
                mButton[i][j]?.setBackgroundResource(R.drawable.bg_button)
            }
        }
    }

    //画面が起動されたときに最初に呼び出されるメソッド
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) //親クラスのコンストラクタを呼びだす
        setContentView(R.layout.activity_main) //指定したxmlファイルを呼びだす

        //得点を初期化する
        mScor = 0

        //mTimeTextにゲームの残り時間を初期化する
        mTimeText = findViewById(R.id.timeText) //時間を表示するテキストのインスタンスを作る
        mTimeText?.text = "Time: 10"

        //Rクラス
        //画面のレイアウト、文字列、画像ファイルなどを参照するためのもの
        //int型のIDで管理している

        //判定する配列を初期化
        moleInit()

        //ボタンと配列を対応させる
        mButton[0][0] = findViewById(R.id.Button1A)
        mButton[0][1] = findViewById(R.id.Button1B)
        mButton[0][2] = findViewById(R.id.Button1C)
        mButton[0][3] = findViewById(R.id.Button1D)

        mButton[1][0] = findViewById(R.id.Button2A)
        mButton[1][1] = findViewById(R.id.Button2B)
        mButton[1][2] = findViewById(R.id.Button2C)
        mButton[1][3] = findViewById(R.id.Button2D)

        mButton[2][0] = findViewById(R.id.Button3A)
        mButton[2][1] = findViewById(R.id.Button3B)
        mButton[2][2] = findViewById(R.id.Button3C)
        mButton[2][3] = findViewById(R.id.Button3D)

        for(i in 0..(row - 1)){
            for(j in 0..(col - 1)){
                mButton[i][j]?.setOnClickListener{
                    //モグラが出現しているとき
                    if(mApper[i][j]  == 1){
                        mButton[i][j]?.setBackgroundResource(R.drawable.hit10) //backgroundを打たれた顔に変更
                        mScor++ //スコアを1増やす
                        mApper[i][j] = 0 //出現リストの当該箇所を0にする
                    }
                }
            }
        }

        //スタートボタンが押されたらゲームスタート
        gameStartBt.setOnClickListener{
            gameStart()
        }
    }

    //ゲームスタート
    fun gameStart(){
        timer.start()
    }

    fun printMole(){
        val r = (0..2)
        val c = (0..3)
        //Nullだったら処理をせずにNullを返す
        var r_tmp = r.random()
        var c_tmp = c.random()
        mButton[r_tmp][c_tmp]?.setBackgroundResource(R.drawable.mogura10) //指定されたボタンにもぐらが出現
        mApper[r_tmp][c_tmp] = 1 //出現リストの当該箇所を1（出現）にする
    }
}
