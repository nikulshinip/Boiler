package obj.temperature.oneWare

import obj.temperature.Temp
import java.io.File

class DS18B20(address:String, title:String) : Temp("/mnt/1wire/$address/temperature", title) {

    override fun read() {
        File(address).readText().also {
            temp = it.toFloat()
        }
    }

}