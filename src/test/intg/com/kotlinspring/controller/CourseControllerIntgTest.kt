package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.repository.CourseRepository
import com.kotlinspring.util.courseEntityList
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class CourseControllerIntgTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var courseRepository: CourseRepository

    val baseUrl = "/v1/courses"

    @BeforeEach
    fun setUp() {

        courseRepository.deleteAll()
        val courses = courseEntityList()
        courseRepository.saveAll(courses)
    }

    @Test
    fun addCourse() {

        val courseDTO = CourseDTO(null, "Build Restful APIs using Kotlin and SpringBoot", "Development")

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
    fun retrieveAllCourses() {

        val courseDTOs = webTestClient
            .get()
            .uri(baseUrl)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertEquals(3, courseDTOs?.size)
    }

    @Test
    fun updateCourse() {
        // existing course
        val course = Course(
            null,
            "Build RestFul APis using SpringBoot and Kotlin", "Development"
        )
        courseRepository.save(course)

        //CourseId
        //updated Course
        val updatedCourseDTO = Course(
            null,
            "Build RestFul APis using SpringBoot and Kotlin2", "Development"
        )

        val updatedCourse = webTestClient
            .put()
            .uri("$baseUrl/{courseId}", course.id)
            .bodyValue(updatedCourseDTO)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertEquals("Build RestFul APis using SpringBoot and Kotlin2", updatedCourse?.name)
    }

    @Test
    fun deleteCourse() {
        // existing course
        val course = Course(
            null,
            "Build RestFul APis using SpringBoot and Kotlin", "Development"
        )
        courseRepository.save(course)

        val updatedCourse = webTestClient
            .delete()
            .uri("$baseUrl/{courseId}", course.id)
            .exchange()
            .expectStatus().isNoContent

    }

}