package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.service.CourseService
import com.kotlinspring.util.courseDTO
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.reactive.server.WebTestClient
import java.lang.RuntimeException
import kotlin.test.assertEquals

@WebMvcTest(controllers = [CourseController::class])
@AutoConfigureWebTestClient
class CourseControllerUnitTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    lateinit var courseServiceMockk: CourseService

    private val baseUrl = "/v1/courses"

    @Test
    fun addCourse() {

        val courseDTO = CourseDTO(null, "Build Restful APIs using Kotlin and SpringBoot", "Development")

        every { courseServiceMockk.addCourse(any()) } returns courseDTO(1)

        val savedCourseDTO = webTestClient
            .post()
            .uri(baseUrl)
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertTrue {
            savedCourseDTO?.id != null
        }
    }

    @Test
    fun addCourse_validation() {

        val courseDTO = CourseDTO(null, "", "")

        every { courseServiceMockk.addCourse(any()) } returns courseDTO(1)

        val response = webTestClient
            .post()
            .uri(baseUrl)
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        assertEquals("courseDTO.category must not be blank, courseDTO.name must not be blank", response)
    }

    @Test
    fun addCourse_runtimeException() {

        val courseDTO = CourseDTO(null, "Build Restful APIs using Kotlin and SpringBoot", "Development")

        val errorMessage = "Unexpected Error occurred"
        every { courseServiceMockk.addCourse(any()) } throws RuntimeException(errorMessage)

        val errorResponse = webTestClient
            .post()
            .uri(baseUrl)
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        assertEquals(errorMessage, errorResponse)
    }

    @Test
    fun retrieveAllCourses() {

        every { courseServiceMockk.retrieveAllCourses(any()) }.returnsMany(
            listOf(courseDTO(1), courseDTO(2, name = "Build Reactive Microservices using Spring WebFlux/SpringBoot"))
        )

        val courseDTOs = webTestClient
            .get()
            .uri(baseUrl)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(2, courseDTOs?.size)
    }

    @Test
    fun updateCourse() {
        // existing course
        val course = Course(
            null,
            "Build RestFul APis using SpringBoot and Kotlin", "Development"
        )

        every { courseServiceMockk.updateCourse(any(), any()) } returns courseDTO(100, "Build RestFul APis using SpringBoot and Kotlin1")

        //CourseId
        //updated Course
        val updatedCourseDTO = Course(
            null,
            "Build RestFul APis using SpringBoot and Kotlin2", "Development"
        )

        val updatedCourse = webTestClient
            .put()
            .uri("$baseUrl/{courseId}", 10)
            .bodyValue(updatedCourseDTO)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals("Build RestFul APis using SpringBoot and Kotlin1", updatedCourse?.name)
    }

    @Test
    fun deleteCourse() {

        every { courseServiceMockk.deleteCourse(any()) } just runs

        val updatedCourse = webTestClient
            .delete()
            .uri("$baseUrl/{courseId}", 10000)
            .exchange()
            .expectStatus().isNoContent

    }
}