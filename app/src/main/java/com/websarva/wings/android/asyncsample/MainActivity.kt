package com.websarva.wings.android.asyncsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.core.os.HandlerCompat
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    companion object {
        private  const val DEBUG_TAG = "AsyncSample"
        private const val WEATHERINFO_URL = "https://api.openweathermap.org/data/2.5/weather?lang=jp"
        private const val APP_ID = "408a1035a7fabe1cceb261a10cab8ebe"
    }
    private  var _list:MutableList<MutableMap<String,String>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _list = createList()

        val lvCityList = findViewById<ListView>(R.id.lvCityList)
        val from =arrayOf("name")
        val to = intArrayOf(android.R.id.text1)
        val adapter = SimpleAdapter(this@MainActivity,_list,android.R.layout.simple_list_item_1,from,to)
        lvCityList.adapter=adapter
        lvCityList.onItemClickListener=ListItemClickListener()
    }
    private fun createList():MutableList<MutableMap<String,String>> {

        var list:MutableList<MutableMap<String,String>> = mutableListOf()
        var city = mutableMapOf("name" to "大阪", "q" to "Osaka")
        list.add(city)
        city = mutableMapOf("name" to "神戸", "q" to "Kobe")
        list.add(city)
        city = mutableMapOf("name" to "京都", "q" to "Kyoto")
        list.add(city)
        city = mutableMapOf("name" to "大津", "q" to "Otsu")
        list.add(city)
        city = mutableMapOf("name" to "奈良", "q" to "Nara")
        list.add(city)
        city = mutableMapOf("name" to "和歌山", "q" to "Wakayama")
        list.add(city)
        city = mutableMapOf("name" to "姫路", "q" to "Himeji")
        list.add(city)

        return list
    }
    @UiThread
    private fun receiveWeatherInfo(urlFull:String){
        val handler = HandlerCompat.createAsync(mainLooper)
        val backgroundReceiver = WeatherInfoBackgroundReceiver(handler,urlFull)
        val executeService = Executors.newSingleThreadExecutor()
        executeService.submit(backgroundReceiver)
    }

    private inner class WeatherInfoBackgroundReceiver(handler: Handler, url:String):Runnable{

        private val _handler = handler
        private val _url = url
        @WorkerThread
        override fun run(){
            val postExecutor =WeatherInfoPostExecutor()
            _handler.post(postExecutor)
        }
    }

    private inner class WeatherInfoPostExecutor():Runnable{
        @UiThread
        override fun run(){

        }
    }

    private inner class ListItemClickListener: AdapterView.OnItemClickListener{
        override fun onItemClick(parent:AdapterView<*>, view: View, position:Int, id:Long){
            val item = _list.get(position)
            val q = item.get("q")
            q?.let{
                val urlFull = "$WEATHERINFO_URL&q=$q&appid=$APP_ID"
                receiveWeatherInfo(urlFull)
            }
        }
    }

}