package obj.GPIO

import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.gpio.RaspiPin
import obj.Logging
import java.io.File

class OutputController(private var fileName:String) : Logging{

    private val gpio = GpioFactory.getInstance()

    val outputs : Map<Int, Output>

    init { outputs = generate() }

    operator fun get(key : Int) = outputs.getOrElse(key, {
                                            logger.error("Выход за пределы массива 'Выходов GPIO'")
                                            throw ArrayIndexOutOfBoundsException("Выход за пределы массива GPIO контактов")
                                        })

    private fun generate():Map<Int, Output>{
        val map = mutableMapOf<Int, Output>()
        val file = File(fileName)

        file.forEachLine {
            if (it[0] == '#')       //Если строка закоментированна
                return@forEachLine  //Пропускаем ее
            val (id, title, first, second, name) = it.split(", ")
            map[id.toInt()] = Output(gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(first.toInt())),
                                        gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(second.toInt())),
                                        title,
                                        name)
        }
        return map
    }

}