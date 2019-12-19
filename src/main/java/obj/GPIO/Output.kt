package obj.GPIO

import com.pi4j.io.gpio.GpioPinDigitalOutput
import com.pi4j.io.gpio.PinState
import kotlinx.coroutines.delay
import obj.Logging

class Output(private val firstPin : GpioPinDigitalOutput,
             private val secondPin : GpioPinDigitalOutput,
             val title: String,
             private val name : String) : Logging {

    var state : Boolean
    private var logOn = true        //писать в лог о включении
    private var logOff = false      //писать в лог о отключении

    init {
        secondPin.low()
        firstPin.low()
        firstPin.setShutdownOptions(true, PinState.LOW)
        secondPin.setShutdownOptions(true, PinState.LOW)
        state = false
        logger.info("Инициированы пара GPIO выходов с названием: $name")
    }

    suspend fun on(){
        firstPin.high()
        delay(100)
        secondPin.high()
        state = true
        if (logOn) {
            logger.info("Произошло включение GPIO контактов с именем $name")
            logOff = true
            logOn = false
        }
    }

    suspend fun off(){
        secondPin.low()
        delay(100)
        firstPin.low()
        state = false
        if (logOff) {
            logger.info("Произошло отключение GPIO контанктов с именем $name")
            logOn = true
            logOff = false
        }
    }

}