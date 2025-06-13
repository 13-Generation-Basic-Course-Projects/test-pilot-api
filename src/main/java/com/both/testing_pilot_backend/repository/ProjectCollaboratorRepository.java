package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.model.ProjectCollaborator;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@Mapper
public interface ProjectCollaboratorRepository {

    @Results(id = "projectCollaboratorMapper", value = {
            @Result(property = "projectCollaboratorId", column = "project_collaborator_id"),
            @Result(property = "projectId", column = "project_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "user", column = "user_id", one = @One(select = "com.both.testing_pilot_backend.repository.UserRepository.findById")),
            @Result(property = "isVerify", column = "is_verify"),
            @Result(property = "verificationToken", column = "verification_token"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("""
            INSERT INTO project_collaborators (project_collaborator_id, project_id, user_id, is_verify, verification_token, created_at, updated_at)
            VALUES (#{collaborator.projectCollaboratorId}, #{collaborator.projectId}, #{collaborator.userId}, #{collaborator.isVerify}, #{collaborator.verificationToken}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            RETURNING *;
            """)
    ProjectCollaborator save(@Param("collaborator") ProjectCollaborator collaborator);


    @ResultMap("projectCollaboratorMapper")
    @Select("""
            SELECT * FROM project_collaborators
            WHERE project_collaborator_id = #{projectCollaboratorId};
            """)
    ProjectCollaborator findById(@Param("projectCollaboratorId") UUID projectCollaboratorId);

    @ResultMap("projectCollaboratorMapper")
    @Select("""
            SELECT * FROM project_collaborators
            WHERE verification_token = #{verificationToken} AND is_verify = FALSE;
            """)
    ProjectCollaborator findByVerificationTokenAndNotVerified(@Param("verificationToken") String verificationToken);


    @ResultMap("projectCollaboratorMapper")
    @Select("""
            SELECT * FROM project_collaborators
            WHERE project_id = #{projectId} AND user_id = #{userId};
            """)
    ProjectCollaborator findByProjectIdAndUserId(@Param("projectId") UUID projectId, @Param("userId") UUID userId);

    @Select("""
            SELECT EXISTS(
                SELECT 1
                FROM project_collaborators
                WHERE project_id = #{projectId}
                AND user_id = #{userId}
            )
            """)
    boolean isProjectCollaborator(@Param("projectId") UUID projectId, @Param("userId") UUID userId);

    @Update("""
            UPDATE project_collaborators
            SET is_verify = TRUE,
                verification_token = NULL, -- Clear token after verification
                updated_at = CURRENT_TIMESTAMP
            WHERE project_collaborator_id = #{projectCollaboratorId} AND is_verify = FALSE;
            """)
    void updateVerificationStatus(@Param("projectCollaboratorId") UUID projectCollaboratorId);

    @Select("""
            SELECT user_id
            FROM project_collaborators
            WHERE project_collaborator_id = #{projectCollaboratorId};
            """)
    UUID getUserIdByCollaboratorId(@Param("projectCollaboratorId") UUID projectCollaboratorId);

    @Delete("""
            DELETE FROM project_collaborators
            WHERE project_collaborator_id = #{projectCollaboratorId};
            """)
    void deleteById(@Param("projectCollaboratorId") UUID projectCollaboratorId);

    @Select("""
            SELECT EXISTS(
                SELECT 1
                FROM project_collaborators
                WHERE project_collaborator_id = #{projectCollaboratorId}
                AND is_verify = FALSE
            )
            """)
    boolean existsByIdAndNotVerified(@Param("projectCollaboratorId") UUID projectCollaboratorId);


    @Select("""
            SELECT EXISTS(
                SELECT 1
                FROM project_collaborators
                WHERE project_id = #{projectId}
                AND user_id = #{userId}
                AND is_verify = FALSE
            )
            """)
    boolean isUnverifiedCollaborator(@Param("projectId") UUID projectId, @Param("userId") UUID userId);

    @ResultMap("projectCollaboratorMapper")
    @Select("""
        SELECT * FROM project_collaborators
        WHERE project_id = #{projectId}
        AND is_verify = true
        ORDER BY created_at ASC;
        """)
    List<ProjectCollaborator> findByProjectId(@Param("projectId") UUID projectId);
}
