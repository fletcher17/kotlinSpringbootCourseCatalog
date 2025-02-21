package com.kotlinspring.repository

import com.kotlinspring.util.PostgresSQLContainerInitializer
import com.kotlinspring.util.courseEntityList
import com.kotlinspring.util.instructorEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.util.stream.Stream


@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class CourseRepositoryIntgTest : PostgresSQLContainerInitializer() {

    @Autowired
    lateinit var courseRepository: CourseRepository

    @Autowired
    lateinit var instructorRepository: InstructorRepository

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
    fun findByNameContaining() {

        val courses = courseRepository.findByNameContaining("SpringBoot")
        println("courses : $courses")

        Assertions.assertEquals(2, courses.size)
    }

    @Test
    fun findCoursesByName() {

        val courses = courseRepository.findCoursesByName("Microservices")
        println("courses : $courses")

        Assertions.assertEquals(1, courses.size)
    }


    @ParameterizedTest
    @MethodSource("courseAndSize")
    fun findCoursesbyName_approach2(name: String, expectedSize: Int) {

    }

    companion object {

        @JvmStatic
        fun courseAndSize(): Stream<Arguments> {

            return Stream.of(Arguments.arguments("SpringBoot", 2), Arguments.arguments("Wiremock", 1))
        }
    }
}