// src/main/java/com/both/testing_pilot_backend/repository/TestCaseRepository.java
package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.model.TestCase;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface TestCaseRepository {

    @Results(id = "testCaseMapper", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "projectId", column = "project_id"),
            @Result(property = "dataTypeId", column = "data_type_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "value", column = "value"),
            @Result(property = "isPredefined", column = "is_predefined"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            // Nested mapping for DataType
            @Result(property = "dataType", column = "data_type_id", one = @One(select = "com.both.testing_pilot_backend.repository.DataTypeRepository.findById"))
    })
    @Select("""
            INSERT INTO test_cases (project_id, data_type_id, name, value, is_predefined)
            VALUES (#{testCase.projectId}, #{testCase.dataTypeId}, #{testCase.name}, #{testCase.value}, #{testCase.isPredefined})
            RETURNING *;
            """)
    TestCase save(@Param("testCase") TestCase testCase);

    @ResultMap("testCaseMapper")
    @Select("""
            SELECT * FROM test_cases;
            """)
    List<TestCase> findAll();

    @ResultMap("testCaseMapper")
    @Select("""
            SELECT * FROM test_cases
            WHERE id = #{id};
            """)
    TestCase findById(@Param("id") UUID id);

    @ResultMap("testCaseMapper")
    @Select("""
            SELECT * FROM test_cases
            WHERE project_id = #{projectId};
            """)
    List<TestCase> findByProjectId(@Param("projectId") UUID projectId);

    @ResultMap("testCaseMapper")
    @Select("""
            SELECT * FROM test_cases
            WHERE is_predefined = TRUE;
            """)
    List<TestCase> findPredefinedTestCases();

    @ResultMap("testCaseMapper")
    @Select("""
            UPDATE test_cases SET
                project_id = #{testCase.projectId},
                data_type_id = #{testCase.dataTypeId},
                name = #{testCase.name},
                value = #{testCase.value},
                is_predefined = #{testCase.isPredefined}
            WHERE id = #{testCase.id}
            RETURNING *;
            """)
    TestCase update(@Param("testCase") TestCase testCase);

    @Delete("""
            DELETE FROM test_cases
            WHERE id = #{id};
            """)
    void deleteById(@Param("id") UUID id);

    // Security helper method - simplified as logic is now primarily in TestCaseSecurity
    // This method is no longer directly used by @PreAuthorize but can be used internally by service if needed
    @Select("""
            SELECT EXISTS(
                SELECT 1
                FROM test_cases tc
                LEFT JOIN projects p ON tc.project_id = p.id
                LEFT JOIN project_collaborators pc ON tc.project_id = pc.project_id
                WHERE tc.id = #{testCaseId}
                AND (
                    (tc.is_predefined = TRUE AND #{isAdmin} = TRUE) OR
                    (tc.is_predefined = FALSE AND (p.project_owner_id = #{userId} OR pc.user_id = #{userId}))
                )
            )
            """)
    boolean isTestCaseAuthorized(@Param("testCaseId") UUID testCaseId, @Param("userId") UUID userId, @Param("isAdmin") boolean isAdmin);
}
