package com.both.testing_pilot_backend.repository;

import org.apache.ibatis.annotations.*;

import java.util.UUID;

@Mapper
public interface ProjectCollaboratorRepository {

    @Results({
            @Result(property = "collaboratorEmail", column = "collaborator_email"),
            @Result(property = "isVerify", column = "is_verify")
    })
    @Insert("""
        INSERT INTO project_collaborators
            (project_collaborator_id, project_id, collaborator_email, user_id, is_verify)
        VALUES
            (#{projectCollaboratorId}, #{projectId}, #{collaboratorEmail}, #{inviterUserId}, #{isVerify})
        """)
    void addCollaborator(@Param("projectCollaboratorId") UUID projectCollaboratorId,
                         @Param("projectId") UUID projectId,
                         @Param("collaboratorEmail") String collaboratorEmail,
                         @Param("inviterUserId") UUID inviterUserId,
                         @Param("isVerify") Boolean isVerify);

    @Select("""
        SELECT COUNT(*) > 0
        FROM project_collaborators
        WHERE project_id = #{projectId}
          AND user_id = #{userId}
        """)
    boolean isProjectCollaborator(@Param("projectId") UUID projectId, @Param("userId") UUID userId);
}
