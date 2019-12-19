package obj.boiler

import kotlinx.coroutines.delay
import obj.GPIO.OutputController
import obj.Logging
import obj.setting.SettingController
import obj.temperature.TempController


class Boiler(
        private val boilerSettings : SettingController,
        private val gpioOutputs : OutputController,
        private val temperatures : TempController) : Logging {

    private var overheatLog = true
    private var logPowerOn = true
    private var logPowerOff = true

    //включение мощности котла по настройке
    private suspend fun setPowerOn(){
        val power = boilerSettings[3].value / 25
        require(power < 7){"Ошибка определения мощности котла: $power"}
        for (i in 1..power){
            gpioOutputs[i].on()
        }
        if (logPowerOn) {
            logger.info("Мощность котла установлена на $power единиц")
            logPowerOff = true
            logPowerOn = false
        }
    }

    //отключение мощности котла
    private suspend fun setPowerOff(){
        for (i in 6 downTo 1){
            gpioOutputs[i].off()
        }
        if (logPowerOff) {
            logger.info("Мощность котла отключена")
            logPowerOn = true
            logPowerOff = false
        }
    }

    //выключение отопления
    private suspend fun off(){
        setPowerOff()
        delay(1000)
        gpioOutputs[7].off()
    }

    //режим работы по температуре в одной из комнат
    private suspend fun room(){
        if( temperatures[4].temp < (boilerSettings[4].value - (boilerSettings[6].value / 2.0F)) ){
            gpioOutputs[7].on()
        }else if ( temperatures[4].temp > ((boilerSettings[4].value + (boilerSettings[6].value / 2.0F))) ){
            gpioOutputs[7].off()
        }
        if (gpioOutputs[7].state){
            if (temperatures[1].temp < 90){
                setPowerOn()
                overheatLog = true
            }else{
                setPowerOff()
                if (overheatLog) {
                    logger.warn("котел перегрелся до ${temperatures[1].temp} °C")
                    overheatLog = false
                }
            }
        }else{
            setPowerOff()
        }

    }

    //Режим работы по температуре обратки котла
    private suspend fun back(){
        gpioOutputs[7].on()
        if ( (temperatures[2].temp < (boilerSettings[5].value - (boilerSettings[7].value / 2.0F))) //температура обратки ниже установленной
                && (temperatures[1].temp <= 90)   //и нет перегрева котла
                && gpioOutputs[7].state ){        //а также работает циркуляционный насос
            setPowerOn()
            overheatLog = true
        }
        if( (temperatures[2].temp > (boilerSettings[5].value + (boilerSettings[7].value / 2.0F))) //температура обратки больше установленной
                || (temperatures[1].temp > 90) ){ //или есть перегрев котла
            setPowerOff()
            if ((temperatures[1].temp > 90)){
                if (overheatLog) {
                    logger.warn("котел перегрелся до ${temperatures[1].temp} °C")
                    overheatLog = false
                }
            }
        }
    }

    //Один такт работы котла
    suspend fun tact(){
        if (boilerSettings[1].value > 0){         //если в настройках отопление включенно
            if (boilerSettings[2].value == 0){     //режим работы отопления
                back()
            }else{
                room()
            }
        }else
            off()
    }

    //Функция запуска алгоритма управления котлом
    suspend fun run(){
        while (true){
            tact()
        }
    }

}