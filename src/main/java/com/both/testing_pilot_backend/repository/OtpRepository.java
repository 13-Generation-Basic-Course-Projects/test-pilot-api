package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.model.OtpCode;
import org.apache.ibatis.annotations.*;

import java.util.UUID;

@Mapper
public interface OtpRepository {
    @Insert("""
                INSERT INTO otp_tokens (hash_otp, expire_date, user_id)
                VALUES (#{request.hashOtp}, #{request.expireDate},  #{userId})
            """)
    void saveOtp(@Param("request") OtpCode request, @Param("userId") UUID userId);

    @Delete("""
                DELETE FROM otp_tokens
                WHERE user_id = #{userId};
            """)
    void deleteHashOtp(UUID userId);

    @Results(id = "otpMapper", value = {@Result(property = "hashOtp", column = "hash_otp"), @Result(property = "expireDate", column = "expire_date", javaType = java.time.LocalDateTime.class), @Result(property = "user", column = "user_id", one = @One(select = "com.both.testing_pilot_backend.repository.UserRepository.findById"))})
    @Select("""
                SELECT * FROM otp_tokens
                WHERE hash_otp = #{hashOtp} AND user_id = #{userId};
            """)
    OtpCode findByOtpAndUserId(String hashOtp, UUID userId);

    @ResultMap("otpMapper")
    @Select("""
                SELECT * FROM otp_tokens
                WHERE user_id = #{userId};
            """)
    OtpCode findByUserId(UUID userId);

    @ResultMap("otpMapper")
    @Select("""
            UPDATE otp_tokens SET verified = #{otpCode.verified} WHERE id = #{otpCode.id}
            RETURNING *;
            """)
    OtpCode updateOtp(@Param("otpCode") OtpCode otpCode);
}
