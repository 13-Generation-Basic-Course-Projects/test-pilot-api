// src/main/java/com/both/testing_pilot_backend/repository/CollectionRepository.java
package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.model.Collection; // Renamed model
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface CollectionRepository {

    @Results(id = "collectionMapper", value = { // Renamed ResultMap ID
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "projectId", column = "project_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "deletedAt", column = "deleted_at")
    })
    @ResultMap("collectionMapper")
    @Select("""
            SELECT * FROM collections
            WHERE project_id = #{projectId} AND deleted_at IS NULL;
            """)
    List<Collection> findByProjectId(@Param("projectId") UUID projectId);


    @ResultMap("collectionMapper")
    @Select("""
            SELECT * FROM collections
            WHERE id = #{id} AND deleted_at IS NULL;
            """)
    Collection findById(@Param("id") UUID id);

    @ResultMap("collectionMapper")
    @Select("""
            SELECT * FROM collections
            WHERE id = #{id};
            """)
    Collection findByIdIncludingDeleted(@Param("id") UUID id);
    @Insert("""
        INSERT INTO collections (name, project_id, created_at, updated_at)
        VALUES (#{collection.name}, #{collection.projectId}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        RETURNING *;
        """)
    Collection save(@Param("collection") Collection collection);

    @Update("""
        UPDATE collections
        SET name = #{collection.name},
            project_id = #{collection.projectId},
            updated_at = CURRENT_TIMESTAMP
        WHERE id = #{collection.id} AND deleted_at IS NULL
        RETURNING *;
        """)
    Collection update(@Param("collection") Collection collection);

    @Update("""
        UPDATE collections
        SET deleted_at = CURRENT_TIMESTAMP,
            updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id} AND deleted_at IS NULL;
        """)
    void softDeleteById(@Param("id") UUID id);



    @Select("""
            SELECT EXISTS(
                SELECT 1
                FROM collections c
                JOIN projects p ON c.project_id = p.id
                LEFT JOIN project_collaborators pc ON p.id = pc.project_id
                WHERE c.id = #{collectionId} AND c.deleted_at IS NULL
                AND (p.project_owner_id = #{userId} OR pc.user_id = #{userId})
            )
            """)
    boolean isCollectionOwnerOrCollaborator(@Param("collectionId") UUID collectionId, @Param("userId") UUID userId);

    // Security helper method: Checks if a collection exists and is not deleted
    @Select("""
            SELECT EXISTS(
                SELECT 1
                FROM collections
                WHERE id = #{collectionId} AND deleted_at IS NULL
            )
            """)
    boolean existsByIdAndNotDeleted(@Param("collectionId") UUID collectionId);
}
