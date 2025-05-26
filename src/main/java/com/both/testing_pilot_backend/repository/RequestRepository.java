package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.model.Request;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;


@Mapper
public interface RequestRepository {

    // Moved @Results definition to findById to ensure it's registered
    @Results(id = "requestMapper", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "collectionId", column = "collection_id"),
            @Result(property = "method", column = "method"),
            @Result(property = "details", column = "details", javaType = JsonNode.class, typeHandler = com.both.testing_pilot_backend.utils.JsonbTypeHandler.class),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("""
            SELECT * FROM requests
            WHERE id = #{requestId};
            """)
    Request findById(@Param("requestId") UUID requestId);


    @Insert("""
            INSERT INTO requests (name, collection_id, method, details, created_at, updated_at)
            VALUES (#{request.name}, #{request.collectionId}, #{request.method}::http_method, #{request.details}::jsonb, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            RETURNING *;
            """)
        // Note: save method does not need @ResultMap if it returns a new object,
        // as the RETURNING * clause handles the mapping directly for the inserted row.
    Request save(@Param("request") Request request);


    @ResultMap("requestMapper") // Now references the map defined on findById
    @Select("""
            SELECT * FROM requests;
            """)
    List<Request> findAll();

    @ResultMap("requestMapper") // Now references the map defined on findById
    @Select("""
            SELECT * FROM requests
            WHERE collection_id = #{collectionId};
            """)
    List<Request> findByCollectionId(@Param("collectionId") UUID collectionId);

    @ResultMap("requestMapper") // Now references the map defined on findById
    @Update("""
            UPDATE requests SET
                name = #{request.name},
                collection_id = #{request.collectionId},
                method = #{request.method}::http_method,
                details = #{request.details}::jsonb,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{request.id}
            RETURNING *;
            """)
    Request updateRequest(@Param("request") Request request);

    @Delete("""
            DELETE FROM requests
            WHERE id = #{requestId};
            """)
    void deleteById(@Param("requestId") UUID requestId);
}
