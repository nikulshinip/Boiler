package obj.http

import com.google.gson.Gson
import io.ktor.http.Parameters
import obj.setting.SettingController

class Cellar(private val cellarSettingController: SettingController) : Setting(cellarSettingController){

    override fun getSetting():String {
        val map = mapOf("min" to cellarSettingController[1].value,
                "max" to cellarSettingController[2].value)
        return Gson().toJson(map)
    }

    //на входе ожидаем POST c параметрами min и max
    override fun setSetting(setting: Parameters):Boolean{

        val min = checkParam(setting, "min", 0..9)
        if (min == -1) return false

        val max = checkParam(setting, "max", 1..10)
        if (max == -1) return false

        if (max <= min){
            logger.warn("переданны не верные параметры, '$max' <= '$min'")
            return false
        }
        cellarSettingController[1].value = min
        cellarSettingController[2].value = max
        cellarSettingController.write()
        return true
    }

}