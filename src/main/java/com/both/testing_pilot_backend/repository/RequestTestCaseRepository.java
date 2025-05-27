// src/main/java/com/both/testing_pilot_backend/repository/RequestTestCaseRepository.java
package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.model.RequestTestCase;
import com.both.testing_pilot_backend.model.enums.ApplicationContextType;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface RequestTestCaseRepository {

    @Results(id = "requestTestCaseMapper", value = {
            @Result(property = "id", column = "rtc.id"),
            @Result(property = "requestId", column = "rtc.request_id"),
            @Result(property = "testCaseId", column = "rtc.test_case_id"),
            @Result(property = "applicationContext", column = "rtc.application_context"),
            @Result(property = "isExpectedSuccess", column = "rtc.is_expected_success"),
            @Result(property = "createdAt", column = "rtc.created_at"),
            @Result(property = "updatedAt", column = "rtc.updated_at"),
            @Result(property = "request", column = "rtc.request_id", one = @One(select = "com.both.testing_pilot_backend.repository.RequestRepository.findById")),
            @Result(property = "testCase", column = "rtc.test_case_id", one = @One(select = "com.both.testing_pilot_backend.repository.TestCaseRepository.findById"))
    })
    @Select("""
            INSERT INTO request_test_cases (request_id, test_case_id, application_context, is_expected_success, created_at, updated_at)
            VALUES (#{rtc.requestId}, #{rtc.testCaseId}, #{rtc.applicationContext}::application_context_type_enum, #{rtc.isExpectedSuccess}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            RETURNING *;
            """)
    RequestTestCase save(@Param("rtc") RequestTestCase requestTestCase);

    @ResultMap("requestTestCaseMapper")
    @Select("""
            SELECT rtc.*
            FROM request_test_cases rtc
            WHERE rtc.id = #{id};
            """)
    RequestTestCase findById(@Param("id") UUID id);

    @ResultMap("requestTestCaseMapper")
    @Select("""
            SELECT rtc.*
            FROM request_test_cases rtc
            WHERE rtc.request_id = #{requestId};
            """)
    List<RequestTestCase> findByRequestId(@Param("requestId") UUID requestId);

    @ResultMap("requestTestCaseMapper")
    @Select("""
            SELECT rtc.*
            FROM request_test_cases rtc
            WHERE rtc.test_case_id = #{testCaseId};
            """)
    List<RequestTestCase> findByTestCaseId(@Param("testCaseId") UUID testCaseId);

    @ResultMap("requestTestCaseMapper")
    @Select("""
            SELECT rtc.*
            FROM request_test_cases rtc
            WHERE rtc.request_id = #{requestId}
            AND rtc.test_case_id = #{testCaseId}
            LIMIT 1;
            """)
    RequestTestCase findByRequestIdAndTestCaseId(@Param("requestId") UUID requestId, @Param("testCaseId") UUID testCaseId);

    @Delete("""
            DELETE FROM request_test_cases
            WHERE id = #{id};
            """)
    void deleteById(@Param("id") UUID id);
}
