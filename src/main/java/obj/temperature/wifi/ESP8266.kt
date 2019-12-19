package obj.temperature.wifi

import obj.temperature.Temp
import java.net.Socket

class ESP8266(address:String, title:String, private val port:Int = 80) : Temp(address, title) {

    private val server = address.substringBefore('/')
    private val request = '/' + address.substringAfter('/', "")

    override fun read() {
        val socket = Socket(server, port)
        if (request != "/"){
            socket.getOutputStream().write(request.toByteArray())
        }
        val value = String(socket.getInputStream().readBytes())//readAllBytes())
        temp = value.toFloat()
    }

}