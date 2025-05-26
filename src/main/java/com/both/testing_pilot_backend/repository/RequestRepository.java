package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.model.Request;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface RequestRepository {

    @Results(id = "requestMapper", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "collectionId", column = "collection_id"),
            @Result(property = "method", column = "method"),
            @Result(property = "details", column = "details"), // No type handler specified, assumes String mapping
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Insert("""
            INSERT INTO requests (name, collection_id, method, details, created_at, updated_at)
            VALUES (#{request.name}, #{request.collectionId}, #{request.method}::http_method, #{request.details}::jsonb, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            RETURNING *;
            """)
    Request save(@Param("request") Request request);

    @ResultMap("requestMapper")
    @Select("""
            SELECT * FROM requests;
            """)
    List<Request> findAll();

    @ResultMap("requestMapper")
    @Select("""
            SELECT * FROM requests
            WHERE id = #{requestId};
            """)
    Request findById(@Param("requestId") UUID requestId);

    @ResultMap("requestMapper")
    @Select("""
            SELECT * FROM requests
            WHERE collection_id = #{collectionId};
            """)
    List<Request> findByCollectionId(@Param("collectionId") UUID collectionId);

    @ResultMap("requestMapper")
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
