package com.teammates.data

import com.devkot.teammates.data.dto.LoginResponseDto
import com.devkot.teammates.data.dto.UserDto
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */


class LoginResponseDtoTest {

    @Test
    fun `toDomain should correctly map LoginResponseDto to LoginResponse`() {
        // Arrange
        val userDto = UserDto(
            publicId = "1",
            nickname = "TestUser"
        )
        val dto = LoginResponseDto(
            user = userDto,
            accessToken = "access123",
            refreshToken = "refresh123"
        )

        // Act
        val domain = dto.toDomain()

        // Assert
        assertEquals("access123", domain.accessToken)
        assertEquals("refresh123", domain.refreshToken)
        assertEquals(userDto.nickname, domain.user.nickname)
        assertEquals(userDto.publicId, domain.user.publicId)
    }
}
