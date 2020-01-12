package obj.http

import com.google.gson.Gson
import io.ktor.http.Parameters
import obj.setting.SettingController

class Cellar(cellarSettingController: SettingController) : Setting(cellarSettingController){

    override fun getSetting():String {
        val map = mapOf("min" to settingController[1].value,
                "max" to settingController[2].value)
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
        settingController[1].value = min
        settingController[2].value = max
        settingController.write()
        return super.setSetting(setting)
    }

}