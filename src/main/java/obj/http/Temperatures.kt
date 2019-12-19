package obj.http

import com.google.gson.Gson
import obj.temperature.TempController

class Temperatures(private val tempController: TempController) {

    private data class SmallTemp(val temp : Float, val title : String)

    fun getTemp():String{
        val map = mutableMapOf<Int, SmallTemp>()
        tempController.temperatures.forEach{
            map[it.key] = SmallTemp(it.value.temp, it.value.title)
        }
        return Gson().toJson(map)
    }

}