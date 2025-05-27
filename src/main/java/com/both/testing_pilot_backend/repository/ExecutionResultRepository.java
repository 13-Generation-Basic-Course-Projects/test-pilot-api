package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.model.ExecutionResult;
import com.both.testing_pilot_backend.utils.JsonbTypeHandler;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface ExecutionResultRepository {

    @Results(id = "executionResultMapper", value = {
            @Result(property = "resultId", column = "result_id"),
            @Result(property = "batchId", column = "batch_id"),
            @Result(property = "requestId", column = "request_id"),
            @Result(property = "testCaseId", column = "test_case_id"),
            @Result(property = "isExpectedSuccess", column = "isExpectedSuccess"),
            @Result(property = "requestDefinitionSnapshot", column = "request_definition_snapshot", javaType = JsonNode.class, typeHandler = JsonbTypeHandler.class),
            @Result(property = "executionOrder", column = "execution_order"),
            @Result(property = "startTimestamp", column = "start_timestamp"),
            @Result(property = "endTimestamp", column = "end_timestamp"),
            @Result(property = "status", column = "status"),
            @Result(property = "requestSentDetails", column = "request_sent_details", javaType = JsonNode.class, typeHandler = JsonbTypeHandler.class),
            @Result(property = "responseStatusCode", column = "response_status_code"),
            @Result(property = "responseHeaders", column = "response_headers", javaType = JsonNode.class, typeHandler = JsonbTypeHandler.class),
            @Result(property = "responseBody", column = "response_body"),
            @Result(property = "responseSizeBytes", column = "response_size_bytes"),
            @Result(property = "durationMs", column = "duration_ms"),
            @Result(property = "assertionResults", column = "assertion_results", javaType = JsonNode.class, typeHandler = JsonbTypeHandler.class),
            @Result(property = "createdAt", column = "created_at")
    })
    @Select("""
            SELECT * FROM execution_results
            WHERE batch_id = #{batchId}
            ORDER BY execution_order ASC;
            """)
    List<ExecutionResult> findByBatchId(@Param("batchId") UUID batchId);

    @Insert("""
            INSERT INTO execution_results (result_id, batch_id, request_id, test_case_id, isExpectedSuccess, request_definition_snapshot, execution_order, start_timestamp, end_timestamp, status, request_sent_details, response_status_code, response_headers, response_body, response_size_bytes, duration_ms, assertion_results, created_at)
            VALUES (#{result.resultId}, #{result.batchId}, #{result.requestId}, #{result.testCaseId}, #{result.isExpectedSuccess}, #{result.requestDefinitionSnapshot, jdbcType=OTHER, typeHandler=com.both.testing_pilot_backend.utils.JsonbTypeHandler}::jsonb, #{result.executionOrder}, #{result.startTimestamp}, #{result.endTimestamp}, #{result.status}::execution_status_enum, #{result.requestSentDetails, jdbcType=OTHER, typeHandler=com.both.testing_pilot_backend.utils.JsonbTypeHandler}::jsonb, #{result.responseStatusCode}, #{result.responseHeaders, jdbcType=OTHER, typeHandler=com.both.testing_pilot_backend.utils.JsonbTypeHandler}::jsonb, #{result.responseBody}, #{result.responseSizeBytes}, #{result.durationMs}, #{result.assertionResults, jdbcType=OTHER, typeHandler=com.both.testing_pilot_backend.utils.JsonbTypeHandler}::jsonb, CURRENT_TIMESTAMP)
            """)
    void save(ExecutionResult result);



    @ResultMap("executionResultMapper")
    @Select("""
            SELECT * FROM execution_results
            WHERE result_id = #{resultId};
            """)
    ExecutionResult findById(@Param("resultId") UUID resultId);

    @Update("""
            UPDATE execution_results SET
                end_timestamp = #{result.endTimestamp},
                status = #{result.status}::execution_status_enum,
                request_sent_details = #{result.requestSentDetails, jdbcType=OTHER, typeHandler=com.both.testing_pilot_backend.utils.JsonbTypeHandler}::jsonb,
                response_status_code = #{result.responseStatusCode},
                response_headers = #{result.responseHeaders, jdbcType=OTHER, typeHandler=com.both.testing_pilot_backend.utils.JsonbTypeHandler}::jsonb,
                response_body = #{result.responseBody},
                response_size_bytes = #{result.responseSizeBytes},
                duration_ms = #{result.durationMs},
                assertion_results = #{result.assertionResults, jdbcType=OTHER, typeHandler=com.both.testing_pilot_backend.utils.JsonbTypeHandler}::jsonb
            WHERE result_id = #{result.resultId};
            """)
    void update(@Param("result") ExecutionResult result);
}
