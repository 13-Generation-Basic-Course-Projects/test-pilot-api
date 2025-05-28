// src/main/java/com/both/testing_pilot_backend/repository/ProjectCollaboratorRepository.java
package com.both.testing_pilot_backend.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.UUID;

@Mapper
public interface ProjectCollaboratorRepository {

    /**
     * Checks if a given user is a collaborator on a specific project.
     * @param projectId The ID of the project.
     * @param userId The ID of the user.
     * @return true if the user is a collaborator, false otherwise.
     */
    @Insert("""
        INSERT INTO project_collaborators (project_collaborator_id, project_id, user_id)
        VALUES (#{projectCollaboratorId}, #{projectId}, #{userId})
        """)
    void addCollaborator(@Param("projectCollaboratorId") UUID projectCollaboratorId,
                         @Param("projectId") UUID projectId,
                         @Param("userId") UUID userId);

    @Select("""
            SELECT EXISTS(
                SELECT 1
                FROM project_collaborators
                WHERE project_id = #{projectId}
                AND user_id = #{userId}
            )
            """)
    boolean isProjectCollaborator(@Param("projectId") UUID projectId, @Param("userId") UUID userId);




}
