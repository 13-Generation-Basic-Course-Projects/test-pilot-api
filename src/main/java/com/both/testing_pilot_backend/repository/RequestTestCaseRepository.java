package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.model.RequestTestCase;
import com.both.testing_pilot_backend.model.enums.ApplicationContextType;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface RequestTestCaseRepository {

    @Results(id = "requestTestCaseMapper", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "requestId", column = "request_id"),
            @Result(property = "testCaseId", column = "test_case_id"),
            @Result(property = "applicationContext", column = "application_context"),
            @Result(property = "isExpectedSuccess", column = "is_expected_success"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "request", column = "request_id", one = @One(select = "com.both.testing_pilot_backend.repository.RequestRepository.findById")),
            @Result(property = "testCase", column = "test_case_id", one = @One(select = "com.both.testing_pilot_backend.repository.TestCaseRepository.findById"))
    })
    @Select("""
            INSERT INTO request_test_cases (request_id, test_case_id, application_context, target_field_path, is_expected_success, created_at, updated_at)
            VALUES (#{req.requestId}, #{req.testCaseId}, #{req.applicationContext}::application_context_type_enum, #{req.targetFieldPath} #{req.isExpectedSuccess}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            RETURNING *;
            """)
    RequestTestCase save(@Param("req") RequestTestCase requestTestCase);

    @ResultMap("requestTestCaseMapper")
    @Select("""
            SELECT *
            FROM request_test_cases
            WHERE id = #{id};
            """)
    RequestTestCase findById(@Param("id") UUID id);

    @ResultMap("requestTestCaseMapper")
    @Select("""
            SELECT *
            FROM request_test_cases
            WHERE request_id = #{requestId};
            """)
    List<RequestTestCase> findByRequestId(@Param("requestId") UUID requestId);

    @ResultMap("requestTestCaseMapper")
    @Select("""
            SELECT *
            FROM request_test_cases
            WHERE test_case_id = #{testCaseId};
            """)
    List<RequestTestCase> findByTestCaseId(@Param("testCaseId") UUID testCaseId);

    @ResultMap("requestTestCaseMapper")
    @Select("""
        SELECT * FROM request_test_cases
        WHERE test_case_id = #{testCaseId}
        AND request_id = #{requestId}
        AND application_context = #{applicationContext}::application_context_type_enum
        AND target_field_path = #{targetFieldPath}
         LIMIT 1;
    """)
    RequestTestCase findByRequestIdAndTestCaseIdAndApplicationContextAndTargetFieldPath (
            @Param("requestId") UUID testCaseId,
            @Param("testCaseId") UUID requestId,
            @Param("applicationContext") ApplicationContextType applicationContextType,
            @Param("targetFieldPath") String targetFieldPath
    );

    @ResultMap("requestTestCaseMapper")
    @Select("""
            SELECT *
            FROM request_test_cases
            WHERE request_id = #{requestId}
            AND test_case_id = #{testCaseId}
            LIMIT 1;
            """)
    RequestTestCase findByRequestIdAndTestCaseId(@Param("requestId") UUID requestId, @Param("testCaseId") UUID testCaseId);

    @Delete("""
            DELETE FROM request_test_cases
            WHERE id = #{id};
            """)
    void deleteById(@Param("id") UUID id);
}
