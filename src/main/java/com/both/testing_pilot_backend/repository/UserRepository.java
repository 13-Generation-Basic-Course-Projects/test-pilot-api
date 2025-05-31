package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.model.User;
import org.apache.ibatis.annotations.*;

import java.util.UUID;

@Mapper
public interface UserRepository {
    @Results(id = "appUserMapper", value = {
        @Result(property = "userId", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "isVerified", column = "is_verify"),
        @Result(property = "profileImage", column = "profile_image")})
    @Select("""
        SELECT * FROM users WHERE email = #{email}
        """)
    User getUserByEmail(@Param("email") String email);

    @ResultMap("appUserMapper")
    @Select("""
        SELECT * FROM  users where id = #{userId};
        """)
    User findById(@Param("userId") UUID currentUserId);

    @ResultMap("appUserMapper")
    @Select("""
        INSERT INTO users (name, email, password, profile_image, is_verify)
        VALUES (#{request.name}, #{request.email}, #{request.password}, #{request.profileImage}, #{request.isVerified})
        RETURNING *;
        """)
    User saveUser(@Param("request") User request);

    @ResultMap("appUserMapper")
    @Select("""
         UPDATE users SET
         profile_image = #{fileName}
         WHERE id = #{userId}
         RETURNING *;
         """)
    User uploadUserProfileImage(@Param("userId") UUID currentUserId,@Param("fileName") String fileName);

    @Update("""
        UPDATE users SET
        is_verify = #{isVerified}
        WHERE id = #{userId};
        """)
    void updateIsVerified(@Param("userId") UUID userId,@Param("isVerified") boolean isVerified);

    @Update("""
        UPDATE users SET
        password = #{newPassword}
        WHERE id = #{userId}
        """)
    void updatePassword(UUID userId, String newPassword);


    @ResultMap("appUserMapper")
    @Select("""
        UPDATE users SET
        name = #{name},
        email = #{email}
        WHERE id = #{userId}
        RETURNING *;
        """)
    User updateUserInfo(@Param("userId") UUID userId, @Param("name") String name, @Param("email") String email);
}
