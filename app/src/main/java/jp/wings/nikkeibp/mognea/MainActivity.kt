package jp.wings.nikkeibp.mognea

//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.app.Activity
//import android.view.Menu
//import android.view.MenuItem
//import android.widget.Button

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.icu.util.DateInterval
import android.os.Bundle;
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.lifecycleScope
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.*

//app.AppCompatActivityはActivityの上位互換（古い端末でも使えるようにしたもの）
class MainActivity : AppCompatActivity() {

    //定数の宣言
    var mButton = Array(3, {arrayOfNulls<Button>(4)}) //ボタンのインスタンスを作成
    var mApper = Array(3, {arrayOfNulls<Int>(4)}) //モグラが現在出現しているかどうか
    var mTimeText: TextView? = null
    val mGameTime: Long = 10000 //10秒（最初の1秒の動作がおかしいから）
    val mGameIntarval: Long = 1500 //一秒毎にカウント
    var mScor:Int = 0
    val row = 3 //モグラたたきの行数
    val col = 4 //モグラたたきの列数
    val num_state = 4
    val num_hole = row * col

    //サーバーのURLなどの記述
    companion object{
        private const val DEBUG_TAG = "D-Wave"
        private const val QUBO_URL = "http://localhost:8080/" //公開するときは変更する
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

        //モグラが打たれた時の処理を記述
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
//            gameStartBt.isEnabled = false //ボタンを押せなくする
            receiveQuboInfo(QUBO_URL)
//            gameStart() //ゲームスタート
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

    //ゲームスタート
    fun gameStart(result: List<List<Int>>, mGameTime: Long, mGameIntarval: Long){

        val timer = object :CountDownTimer(mGameTime, mGameIntarval){ //開始時間・インターバル
            //途中経過・残り時間
            //p0：残り時間

            private var increment = 0 //resultのindexを指定する変数

            override fun onTick(p0: Long) {
                //初期化
                moleInit()
                //モグラを表示
                printMole(result[increment])
                //残り時間を表示
                mTimeText?.text = "Time: ${p0 / 1000}"
                increment++
            }

            //タイマーが終了したとき
            override fun onFinish() {
                //TODO("Not yet implemented")
                moleInit() //初期化
                mTimeText?.text = "Score:" + mScor //スコアを表示
                mScor = 0 //スコアの初期化
                gameStartBt.isEnabled = true //startボタンを押せるようにする
            }
        }

        timer.start()
    }

    //モグラの出力
    fun printMole(result: List<Int>){

        Log.d("test", "${result}")

        //モグラをセット
        for (i in 0 .. (row - 1)){
            for(j in 0 .. (col - 1)){
                val mogneaState = result[i * col + j]
                //もぐにーの処理が増えたら書き換える必要あり
                when(mogneaState % num_state){
                    0 -> {
                        mButton[i][j]?.setBackgroundResource(R.drawable.bg_button)
                    }
                    1 -> {
                        mButton[i][j]?.setBackgroundResource(R.drawable.mogura10)
                        mApper[i][j] = 1
                    }
                    2 -> {
                        mButton[i][j]?.setBackgroundResource(R.drawable.mogura20)
                        mApper[i][j] = 1
                    }
                    3 -> {
                        mButton[i][j]?.setBackgroundResource(R.drawable.mogura30)
                        mApper[i][j] = 1
                    }
                }
            }
        }
    }

    //サーバーとの非同期処理
    //kotlinコルーチンで記述
    @UiThread
    private fun receiveQuboInfo(urlFull: String){
        //ここにコルーチンに関するコードを記述
        //コルーチンを定義して起動させる
        lifecycleScope.launch{
            val result = QuboInfoBackgroundRunner(urlFull)
            QuboInfoPostRunner(result)
        }
    }

    //当該メソッドの実行中にほかの処理を中断させるときはsuspendを記述する
    @WorkerThread
    private suspend fun QuboInfoBackgroundRunner(url: String): String{

        //スレッドの分離
        //Dispatchersクラスの定数で記述する
        //.Mainのときメインスレッド
        //.IOのときワーカースレッド
        val returnVal = withContext(Dispatchers.IO){
            var result = ""
            val url = URL(url)
            val con = url.openConnection() as? HttpURLConnection
            con?.let{
                try{
                    it.connectTimeout = 50000
                    it.readTimeout = 50000
                    it.requestMethod = "GET"
                    it.connect()
                    val stream = it.inputStream
                    result = is2String(stream)
                    stream.close()
                }catch (ex: SocketTimeoutException){
                    Log.w(DEBUG_TAG, "通信タイムアウト", ex)
                }
            }
            result
        }
        return returnVal
    }

    @UiThread
    private fun QuboInfoPostRunner(result: String){
        //JSONファイルを解析する
        Log.d("sample", "JSONファイルを解析中")
        val mapper = jacksonObjectMapper()
        val responseData = mapper.readValue<ResponseData>(result)
        Log.d("sample", "${responseData}")
        gameStart(responseData.result, mGameTime, mGameIntarval)
    }

    //JSONファイルを保存するデータクラスを定義
    data class ResponseData(var status: String, var result: List<List<Int>>, var QUBO: List<List<Double>>)

    private fun is2String(stream: InputStream): String{
        val sb = StringBuilder()
        val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
        var line = reader.readLine()
        while(line != null){
            sb.append(line)
            line = reader.readLine()
        }
        reader.close()
        return sb.toString()
    }
}
