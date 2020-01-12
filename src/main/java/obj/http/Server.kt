package obj.http

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.receiveParameters
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import obj.GPIO.OutputController
import obj.Logging
import obj.setting.SettingController
import obj.temperature.TempController

class Server(tempController: TempController,
             outputController: OutputController,
             boilerSettingController: SettingController,
             cellarSettingController: SettingController) : Logging {

    private val temp = Temperatures(tempController)
    private val boiler = Boiler(boilerSettingController, outputController)
    private val cellar = Cellar(cellarSettingController)

    private val server = embeddedServer(Netty, 80){
//        install(FreeMarker){
//            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
//        }
        routing {

            get("/"){
                call.respondText ("ok", ContentType.Text.Plain)
            }

            get("/temperatures"){
                val response = temp.getTemp()
                call.respondText(response, ContentType.Text.Plain)
            }
            //----------------------------------------------------------------------------------------------------------
            //boiler
            get("/boiler/get"){
                val response = boiler.getSetting()
                call.respondText(response, ContentType.Text.Plain)
            }
            post("/boiler/set"){
                if (boiler.setSetting(call.receiveParameters()))
                    call.respondText("ok", ContentType.Text.Plain)
                else
                    call.respondText("fail", ContentType.Text.Plain)
            }
            get("/boiler/state"){
                val response = boiler.getState()
                call.respondText(response, ContentType.Text.Plain)
            }

            //----------------------------------------------------------------------------------------------------------
            //cellar
            get("/pogrebok/getsetting/"){        //для сохранения обратной совместимости
                val response = cellar.getSetting()
                call.respondText(response, ContentType.Text.Plain)
            }

            get("/cellar/get"){
                call.respondRedirect("/pogrebok/getsetting/")
            }

            post("/cellar/set"){
                if(cellar.setSetting(call.receiveParameters()))
                    call.respondText("ok", ContentType.Text.Plain)
                else
                    call.respondText("fail", ContentType.Text.Plain)
            }

        }

    }

    fun start(){
        server.start(false)
        logger.info("http server is start")
    }
}