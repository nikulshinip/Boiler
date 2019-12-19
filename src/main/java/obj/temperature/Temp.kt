package obj.temperature

import obj.Logging

abstract class Temp(val address:String, val title:String) : Logging {

    var temp : Float = 9999.0F
    private var writeLog = true

    abstract fun read()

    fun readTemp(){
        try {
            read()
            writeLog = true
        }catch (e:Exception){
            temp = 9999.0F
            if (writeLog)
                logger.warn("Ошибка вычитывания температуры с датчика с описанием $title : $e")
            writeLog = false
        }
    }

    override fun toString(): String = "{${javaClass.name} : address='$address', title='$title', temp='$temp'}"

}