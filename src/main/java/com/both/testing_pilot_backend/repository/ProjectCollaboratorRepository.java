package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.model.ProjectCollaborator;
import org.apache.ibatis.annotations.*;

import java.util.Optional;
import java.util.UUID;

@Mapper
public interface ProjectCollaboratorRepository {

    @Insert("""
        INSERT INTO project_collaborators
        (project_collaborator_id, project_id, user_id, is_verify)
        VALUES
        (#{projectCollaboratorId}, #{projectId}, #{userId}, #{isVerify})
        """)
    void addCollaborator(@Param("projectCollaboratorId") UUID projectCollaboratorId,
                         @Param("projectId") UUID projectId,
                         @Param("userId") UUID userId,
                         @Param("isVerify") boolean isVerify);

    @Select("""
        SELECT * FROM project_collaborators WHERE project_collaborator_id = #{projectCollaboratorId}
        """)
    Optional<ProjectCollaborator> findById(UUID projectCollaboratorId);

    @Update("""
        UPDATE project_collaborators SET is_verify = true WHERE project_collaborator_id = #{projectCollaboratorId}
        """)
    void updateVerificationStatus(@Param("projectCollaboratorId") UUID projectCollaboratorId);

    @Select("""
        SELECT COUNT(*) > 0
        FROM project_collaborators
        WHERE user_id = #{userId} AND project_id = #{projectId} AND is_verify = true
        """)
    boolean isProjectCollaborator(@Param("userId") UUID userId, @Param("projectId") UUID projectId);
}

