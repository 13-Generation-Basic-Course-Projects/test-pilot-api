package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.dto.request.ProjectCollaboratorRequest;
import com.both.testing_pilot_backend.model.ProjectCollaborator;
import org.apache.ibatis.annotations.*;

import java.util.UUID;

@Mapper
public interface ProjectCollaboratorRepository {
    @Results(id = "projectCollaboratorMapper", value = {
        @Result(property = "projectCollaboratorId", column = "project_collaborator_id"),
        @Result(property = "projectId", column = "project_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "isVerify", column = "is_verify"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("""
        INSERT INTO project_collaborators (project_id, user_id)
        VALUES (#{projectId}, #{userId})
        RETURNING *;
        """)
    ProjectCollaborator savedCollaborator(@Param("projectId") UUID project_id,@Param("userId") UUID user_id);

    @ResultMap("projectCollaboratorMapper")
    @Select("""
        SELECT COUNT(*) > 0
        FROM project_collaborators
        WHERE user_id = #{userId} AND project_id = #{projectId} AND is_verify = true
        """)
    boolean isProjectCollaborator(@Param("userId") UUID userId, @Param("projectId") UUID projectId);

    @ResultMap("projectCollaboratorMapper")
    @Select("""
            UPDATE project_collaborators
            SET is_verify = true
            WHERE project_collaborator_id = #{id}
            RETURNING *;
            """)
    ProjectCollaborator acceptInviteLink(@Param("id") UUID id);
}

