// src/main/java/com/both/testing_pilot_backend/repository/ExecutionBatchRepository.java
package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.model.ExecutionBatch;
import com.both.testing_pilot_backend.model.enums.ExecutionBatchStatus;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface ExecutionBatchRepository {

    @Results(id = "executionBatchMapper", value = {
            @Result(property = "batchId", column = "batch_id"),
            @Result(property = "projectId", column = "projectId"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "triggerType", column = "trigger_type"),
            @Result(property = "triggerSourceId", column = "trigger_source_id"),
            @Result(property = "startTimestamp", column = "start_timestamp"),
            @Result(property = "endTimestamp", column = "end_timestamp"),
            @Result(property = "overallStatus", column = "overall_status"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("""
            INSERT INTO execution_batches (projectId, user_id, trigger_type, trigger_source_id, start_timestamp, overall_status, created_at, updated_at)
            VALUES (#{batch.projectId}, #{batch.userId}, #{batch.triggerType}::execution_trigger_type_enum, #{batch.triggerSourceId}, #{batch.startTimestamp}, #{batch.overallStatus}::execution_status_enum, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            RETURNING *;
            """)
    ExecutionBatch save(@Param("batch") ExecutionBatch batch);

    @ResultMap("executionBatchMapper")
    @Select("""
            SELECT * FROM execution_batches
            WHERE batch_id = #{batchId};
            """)
    ExecutionBatch findById(@Param("batchId") UUID batchId); // Changed to UUID

    @ResultMap("executionBatchMapper")
    @Select("""
            SELECT * FROM execution_batches
            WHERE projectId = #{projectId}
            ORDER BY created_at DESC;
            """)
    List<ExecutionBatch> findByProjectId(@Param("projectId") UUID projectId); // New method to get batches for a project

    @ResultMap("executionBatchMapper")
    @Select("""
            SELECT * FROM execution_batches
            ORDER BY created_at DESC;
            """)
    List<ExecutionBatch> findAll();

    @Update("""
            UPDATE execution_batches SET
                end_timestamp = #{endTimestamp},
                overall_status = #{overallStatus}::execution_status_enum,
                updated_at = CURRENT_TIMESTAMP
            WHERE batch_id = #{batchId};
            """)
    void updateStatusAndEndTime(@Param("batchId") UUID batchId,
                                @Param("endTimestamp") LocalDateTime endTimestamp,
                                @Param("overallStatus") ExecutionBatchStatus overallStatus);
}
