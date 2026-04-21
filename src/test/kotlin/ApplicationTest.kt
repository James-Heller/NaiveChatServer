package pers.jamestang

import io.ktor.http.ContentType
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.*
import pers.jamestang.components.initializeSerialization
import pers.jamestang.expand.data
import pers.jamestang.expand.error
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun `status only response should not trigger serialization`() = testApplication {
        application {
            initializeSerialization()
            routing {
                get("/status") {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }

        val response = client.get("/status")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `wrapped json response should serialize correctly`() = testApplication {
        application {
            initializeSerialization()
            routing {
                get("/data") {
                    call.data(123)
                }
                get("/error") {
                    call.error(401, "用户名或密码错误")
                }
            }
        }

        val dataResponse = client.get("/data")
        val errorResponse = client.get("/error")

        assertEquals(HttpStatusCode.OK, dataResponse.status)
        assertEquals(ContentType.Application.Json, dataResponse.contentType())
        assertEquals("{\"code\":200,\"data\":123,\"msg\":\"OK\"}", dataResponse.bodyAsText())

        assertEquals(HttpStatusCode.Unauthorized, errorResponse.status)
        assertEquals("{\"code\":401,\"msg\":\"用户名或密码错误\"}", errorResponse.bodyAsText())
    }

}
