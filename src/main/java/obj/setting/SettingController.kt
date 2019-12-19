package obj.setting

import obj.Logging
import java.io.File

class SettingController(private val fileName : String) : Logging {

    val setting : Map<Int, Setting>

    init { setting = read() }

    operator fun get(key: Int) = setting.getOrElse(key, {
                                        logger.error("Выход за пределы массива настроек")
                                        throw ArrayIndexOutOfBoundsException("Выход за пределы массива настроек")
                                    })

    private fun read():Map<Int, Setting>{
        val map = mutableMapOf<Int, Setting>()
        val file = File(fileName)
        file.forEachLine {
            val (id, name, value, title) = it.split(", ")
            map[id.toInt()] = Setting(name, value.toInt(), title)
        }
        return map
    }

    fun write(){
        val writer = File(fileName).writer()
        setting.forEach{(id, setting) ->
            val line = "$id, ${setting.name}, ${setting.value}, ${setting.title}"
            writer.appendln(line)
        }
        writer.close()
    }

}