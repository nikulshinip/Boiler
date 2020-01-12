import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import obj.GPIO.OutputController
import obj.boiler.Boiler
import obj.http.Server
import obj.setting.SettingController
import obj.temperature.GetTemp
import obj.temperature.TempController

fun main() = runBlocking {

    println("//---------------------------------------------------------------------")
    val tempController = TempController("tempSensors.cfg")
    val temp = GetTemp(tempController)

    val boilerSettingController = SettingController("boilerSetting.cfg")

    val gpios = OutputController("GPIO.cfg")

    val boiler = Boiler(boilerSettingController, gpios, tempController)

    val cellarSettingController = SettingController("cellarSetting.cfg")

    launch { temp.start() }
    launch { Server(tempController, gpios, boilerSettingController, cellarSettingController).start() }
    launch { boiler.start() }

    println("//---------------------------------------------------------------------")
}