package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.model.Variables;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface VariablesRepository {

    //get all variables
    @Results(id = "variableMapper", value = {
            @Result(property = "variableId", column = "id"),
            @Result(property = "keyName", column = "key_name"),
            @Result(property = "keyValue", column = "key_value"),
            @Result(property = "projectId", column = "project_id"),
            @Result(property = "enabled", column = "enabled")
    })
    @Select("""
                SELECT * FROM variables
            """)
    List<Variables> getAllVariables();

    //get variables by variable id
    @ResultMap("variableMapper")
    @Select("""
                SELECT * FROM variables WHERE id = #{variableId}
            """)
    Variables getVariablesByVariableId(UUID variableId);

    //get variables by project id
    @ResultMap("variableMapper")
    @Select("""
                SELECT * FROM variables WHERE project_id = #{projectId}
            """)
    List<Variables> getVariablesByProjectId(UUID projectId);

    //insert variable
    @ResultMap("variableMapper")
    @Select("""
                INSERT INTO variables (key_name, key_value, project_id)
                values (#{request.keyName}, #{request.keyValue}, #{request.projectId})
                RETURNING *;
            """)
    Variables saveVariable(@Param("request") Variables request);

    //update enabled status
    @Update("""
                UPDATE variables SET
                enabled = #{isEnabled}
                WHERE project_id = #{projectId};
            """)
    void changeEnabled(UUID projectId, boolean isEnabled);

    //update
    @ResultMap("variableMapper")
    @Select("""
                UPDATE variables
                SET key_name = #{variable.keyName},
                    key_value = #{variable.keyValue},
                    project_id = #{variable.projectId},
                    enabled = #{variable.enabled}
                WHERE id = #{variable.variableId}
                RETURNING *
            """)
    Variables updateVariable(@Param("variable") Variables variable);

    //yeah
    @ResultMap("variableMapper")
    @Select("""
                DELETE FROM variables
                WHERE id = #{variableId}
                RETURNING *
            """)
    Variables deleteVariable(UUID variableId);
}
