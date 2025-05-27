package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.dto.request.CollectionsRequest;
import com.both.testing_pilot_backend.model.Collections;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface CollectionsRepository {

    @Results(id = "collectionsMapper", value = {
            @Result(property = "collectionsId", column = "id"),
            @Result(property = "collectionsName", column = "name"),
            @Result(property = "projectId", column = "project_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "deletedAt", column = "deleted_at")
    })
    @Select("SELECT * FROM collections")
    List<Collections> getAllCollections();

    @ResultMap("collectionsMapper")
    @Select("SELECT * FROM collections WHERE id = #{collectionsId}")
    Collections getCollectionsById(UUID collectionsId);

    @ResultMap("collectionsMapper")
    @Select("""
        INSERT INTO collections (name, project_id)
        VALUES (#{collections.collectionsName}, #{collections.projectId})
        RETURNING id, name
        """)
    Collections saveCollections(@Param("collections") CollectionsRequest collections);

    @ResultMap("collectionsMapper")
    @Select("""
        UPDATE collections
        SET name = #{collections.collectionsName},
            project_id = #{collections.projectId},
            updated_at = CURRENT_TIMESTAMP
        WHERE id = #{collections.collectionsId}
        RETURNING id, name, project_id, created_at, updated_at, deleted_at
        """)
    Collections updateCollections(@Param("collections") Collections collections);

    @Select("DELETE FROM collections WHERE id = #{collectionsId} RETURNING id")
    UUID deleteCollections(UUID collectionsId);

    @Select("""
            SELECT * FROM collections
            WHERE project_id = #{projectId};
            """)
    List<Collections> findByProjectId(@Param("projectId") UUID projectId);
}
