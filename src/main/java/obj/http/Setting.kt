package obj.http

import io.ktor.http.Parameters
import obj.Logging
import obj.setting.SettingController

abstract class Setting(protected val settingController: SettingController) : Logging {

    abstract fun getSetting():String

    open fun setSetting(setting: Parameters):Boolean{
        var str = ""
        settingController.setting.forEach { (id, setting) ->
            str += "$id --> $setting\r\n\t"
        }
        logger.info("Были применены настройки:\r\n\t$str")
        return true
    }

    protected fun checkParam(value: String?):Int = value?.toIntOrNull() ?: -1

    protected fun checkParam(parameters:Parameters, parameterName:String, range: IntRange):Int{
        val p = checkParam(parameters[parameterName])
        if (p !in range) {
            logger.warn("передан не верный параметр \"$parameterName\"")
            return -1
        }
        return p
    }
}