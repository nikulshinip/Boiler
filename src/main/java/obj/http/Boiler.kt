package obj.http

import com.google.gson.Gson
import io.ktor.http.Parameters
import obj.GPIO.OutputController
import obj.setting.SettingController

class Boiler(boilerSettingController: SettingController,
             private val outputController: OutputController) : Setting(boilerSettingController) {

    override fun getSetting():String{
        val map = mutableMapOf<String, Int>()
        settingController.setting.forEach { (_, setting) ->
            map += setting.name to setting.value
        }
        return Gson().toJson(map)
    }

    override fun setSetting(setting: Parameters):Boolean{
        val on = checkParam(setting, "on/off", 0..1)
        if (on == -1) return false

        val mode = checkParam(setting, "mode", 0..1)
        if (mode == -1) return false

        val power = checkParam(setting, "power", 0..150)
        if (power == -1) return false

        val limit_t1 = checkParam(setting, "limit_t1", 0..35)
        if (limit_t1 == -1) return false

        val limit_t2 = checkParam(setting, "limit_t2", 40..60)
        if (limit_t2 == -1) return false

        settingController[1].value = on
        settingController[2].value = mode
        settingController[3].value = power
        settingController[4].value = limit_t1
        settingController[5].value = limit_t2
        settingController.write()

        return super.setSetting(setting)
    }

    fun getState():String{
        val map = mutableMapOf<String, Boolean>()
        outputController.outputs.forEach { (_, output) ->
            map += output.title to output.state
        }
        return Gson().toJson(map)
    }
}