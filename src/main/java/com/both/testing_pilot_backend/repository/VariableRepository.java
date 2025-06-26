package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.dto.request.VariableRequest;
import com.both.testing_pilot_backend.model.Variable;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface VariableRepository {

    @Results(id = "variableMapper", value = {
            @Result(property = "variableId", column = "id"),
            @Result(property = "keyName", column = "key_name"),
            @Result(property = "keyValue", column = "key_value"),
            @Result(property = "enabled", column = "enabled"),
            @Result(property = "project", column = "project_id", one = @One(select = "com.both.testing_pilot_backend.repository.ProjectRepository.findByProjectId"))
    })
    @Select("""
            SELECT * FROM variables WHERE project_id = #{projectId}
            """)
    List<Variable> getVariablesByProjectId(UUID projectId);

    @ResultMap("variableMapper")
    @Select("""
            SELECT * FROM variables WHERE id = #{variableId}
            """)
    Variable getVariablesByVariableId(UUID variableId);

    @ResultMap("variableMapper")
    @Select("""
            INSERT INTO variables (key_name, key_value, enabled, project_id)
            VALUES (#{keyName}, #{keyValue}, #{enabled}, #{projectId})
            RETURNING *;
            """)
    Variable saveVariable(VariableRequest request);

    @ResultMap("variableMapper")
    @Update("""
            UPDATE variables SET
            enabled = #{isEnabled}
            WHERE project_id = #{projectId};
            """)
    void changeEnabled(@Param("projectId") UUID projectId, @Param("isEnabled") boolean isEnabled);

    @ResultMap("variableMapper")
    @Select("""
            UPDATE variables
            SET key_name = #{req.keyName},
                key_value = #{req.keyValue},
                enabled = #{req.enabled},
                project_id = #{req.projectId}
            WHERE id = #{id}
            RETURNING *;
            """)
    Variable updateVariable(@Param("id") UUID variableId,@Param("req") VariableRequest variable);

    @ResultMap("variableMapper")
    @Select("""
            DELETE FROM variables
            WHERE id = #{variableId}
            RETURNING *;
            """)
    Variable deleteVariable(UUID variableId);
}
