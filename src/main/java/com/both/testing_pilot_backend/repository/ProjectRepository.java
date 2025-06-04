package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.model.Project;
import com.both.testing_pilot_backend.dto.request.PageRequest;
import com.both.testing_pilot_backend.dto.request.apiFeature.Filter;
import com.both.testing_pilot_backend.dto.request.apiFeature.Sort;
import com.both.testing_pilot_backend.repository.provider.SqlProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface ProjectRepository {

    @Results(id = "projectMapper", value = {@Result(property = "projectId", column = "project_id"),
            @Result(property = "projectId", column = "id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "deletedAt", column = "deleted_at"),
            @Result(property = "projectName", column = "name"),
            @Result(property = "projectDescription", column = "description"),
            @Result(property = "projectOwner", column = "project_owner_id", one = @One(select = "com.both.testing_pilot_backend.repository.UserRepository.findById"))})
    @Select("""
                INSERT INTO projects (name, description, project_owner_id, created_at, updated_at)
                VALUES (#{project.projectName}, #{project.projectDescription}, #{ownerId}, NOW(), NOW())
                RETURNING *;
            """)
    Project saveProject(Project project, UUID ownerId);

    @ResultMap("projectMapper")
    @SelectProvider(type = SqlProvider.class, method = "buildFindAllProjectsForUserQuery")
    List<Project> getAllProjects(@Param("filters") List<Filter> filters,
                                 @Param("sorts") List<Sort> sorts,
                                 @Param("search") List<Filter> search,
                                 @Param("pageRequest") PageRequest pageRequest,
                                 @Param("cursor") String cursor,
                                 @Param("tableName") String table,
                                 @Param("userId") UUID userId
    );

    @Select("""
                SELECT EXISTS(
                SELECT 1
                FROM projects
                WHERE id = #{projectId}
                AND project_owner_id = #{userId}
                )
            """)
    boolean isProjectOwner(UUID projectId, UUID userId);

    @Delete("""
                DELETE FROM projects
                WHERE id = #{projectId}
            """)
    void deleteByProjectId(UUID projectId);

    @ResultMap("projectMapper")
    @Select("""
                SELECT * FROM projects
                WHERE id = #{projectId};
            """)
    Project findByProjectId(@Param("projectId") UUID projectId);

    @ResultMap("projectMapper")
    @Select("""
                UPDATE projects SET
                name = #{request.projectName},
                description = #{request.projectDescription},
                updated_at = NOW()
                WHERE id = #{request.projectId}
                RETURNING *;
            """)
    Project updateProjectById(@Param("request") Project request);
}
