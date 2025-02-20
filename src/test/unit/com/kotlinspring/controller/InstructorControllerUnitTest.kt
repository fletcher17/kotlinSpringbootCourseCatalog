package com.kotlinspring.controller

import com.kotlinspring.dto.InstructorDTO
import com.kotlinspring.service.InstructorService
import com.kotlinspring.util.instructorDTO
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertEquals


@WebMvcTest(controllers = [InstructorController::class])
@AutoConfigureWebTestClient
class InstructorControllerUnitTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    lateinit var instructorServiceMockk: InstructorService

    private val baseUrl = "/v1/instructors"

    @Test
    fun addInstructor() {

        val instructorDTO = InstructorDTO(null, "Femo lala")

        every { instructorServiceMockk.createInstructor(any()) } returns instructorDTO(89)

        val savedInstructorDTO = webTestClient
            .post()
            .uri(baseUrl)
            .bodyValue(instructorDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(InstructorDTO::class.java)
            .returnResult()
            .responseBody

        println("saved instructor: $savedInstructorDTO")

        Assertions.assertTrue {
            savedInstructorDTO?.id != null
        }
    }

    @Test
    fun addInstructor_validation() {

        val instructorDTO = InstructorDTO(null, "")

        every { instructorServiceMockk.createInstructor(any()) } returns instructorDTO(1, "")

        val response = webTestClient
            .post()
            .uri(baseUrl)
            .bodyValue(instructorDTO)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        println("response: $response")

        assertEquals("instructorDTO.name must not be blank", response)
    }
}