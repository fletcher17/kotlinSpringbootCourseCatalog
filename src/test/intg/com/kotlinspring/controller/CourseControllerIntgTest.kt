package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.repository.CourseRepository
import com.kotlinspring.repository.InstructorRepository
import com.kotlinspring.util.courseEntityList
import com.kotlinspring.util.instructorEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class CourseControllerIntgTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var courseRepository: CourseRepository

    @Autowired
    lateinit var instructorRepository: InstructorRepository

    val baseUrl = "/v1/courses"

    @BeforeEach
    fun setUp() {

        courseRepository.deleteAll()
        instructorRepository.deleteAll()

        val instructorEntity = instructorEntity()
        instructorRepository.save(instructorEntity)


        val courses = courseEntityList(instructorEntity)
        courseRepository.saveAll(courses)
    }

    @Test
    fun addCourse() {

        val instructor = instructorRepository.findAll().first()

        val courseDTO = CourseDTO(null, "Build Restful APIs using Kotlin and SpringBoot", "Development", instructor.id)

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
    fun retrieveAllCourses_Byname() {

        val uri = UriComponentsBuilder.fromUriString(baseUrl)
            .queryParam("course_name", "SpringBoot")
            .toUriString()

        val courseDTOs = webTestClient
            .get()
            .uri(uri)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertEquals(2, courseDTOs?.size)
    }

    @Test
    fun updateCourse() {

        val instructor = instructorRepository.findAll().first()

        // existing course
        val course = Course(
            null,
            "Build RestFul APis using SpringBoot and Kotlin", "Development", instructor
        )
        courseRepository.save(course)

        //CourseId
        //updated Course
        val updatedCourseDTO = CourseDTO(
            null,
            "Build RestFul APis using SpringBoot and Kotlin2", "Development", course.instructor?.id
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

        val instructor = instructorRepository.findAll().first()

        val course = Course(
            null,
            "Build RestFul APis using SpringBoot and Kotlin", "Development", instructor
        )
        courseRepository.save(course)

        val updatedCourse = webTestClient
            .delete()
            .uri("$baseUrl/{courseId}", course.id)
            .exchange()
            .expectStatus().isNoContent

    }

}