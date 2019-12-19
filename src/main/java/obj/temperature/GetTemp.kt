package obj.temperature

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import obj.Logging

class GetTemp(private val temps: TempController) : Logging {

    suspend fun tact(){
        val jobs = mutableSetOf<Job>()
        temps.temperatures.forEach { (n, temp) ->
            val job = GlobalScope.launch {
                temp.readTemp()
                logger.debug("Полученна температура с датчика $n -> $temp")
            }
            logger.debug("Запушен процесс получения температуры с датчика $n")
            jobs.add(job)
        }
        jobs.forEach{
            it.join()
        }
        logger.debug("Окончен такт считывания температур с датчиков")
    }

    suspend fun run(){
//        var count = 3
//        while (count > 0){
//            tact()
//            count--
//        }
        while (true)
            tact()
    }
}