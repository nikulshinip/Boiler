package obj.temperature

import obj.Logging
import obj.temperature.oneWare.DS18B20
import obj.temperature.wifi.ESP8266
import java.io.File

class TempController(private val fileName:String) : Logging {
    val temperatures : Map<Int, Temp>

    init { temperatures = generate() }

    operator fun get(key:Int) = temperatures.getOrElse(key, {
                                logger.error("Обращение к несуществующему индексу массива датчиков температур")
                                throw ArrayIndexOutOfBoundsException("Обращение к несуществующему индексу массива датчиков температур")
                            })

    private fun generate():Map<Int, Temp>{
        val tempMap = mutableMapOf<Int, Temp>()
        val file = File(fileName)
        file.forEachLine {
            if (it[0] == '#')       //Если строка закоментированна
                return@forEachLine  //Пропускаем ее
            val (id, type, address, title) = it.split(", ")
            when(type){
                "wi-fi" -> tempMap[id.toInt()] = ESP8266(address, title)
                "1ware" -> tempMap[id.toInt()] = DS18B20(address, title)
                else ->  logger.error("Ошибочная строка в файле датчиков температуры.")
            }
        }
        return tempMap
    }

}