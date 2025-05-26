package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.model.DataType;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface DataTypeRepository {

    @Results(id = "dataTypeMapper", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Insert("""
            INSERT INTO data_types (name, created_at, updated_at)
            VALUES (#{dataType.name}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            RETURNING *;
            """)
    DataType save(@Param("dataType") DataType dataType);

    @ResultMap("dataTypeMapper")
    @Select("""
            SELECT * FROM data_types;
            """)
    List<DataType> findAll();

    @ResultMap("dataTypeMapper")
    @Select("""
            SELECT * FROM data_types
            WHERE id = #{id};
            """)
    DataType findById(@Param("id") UUID id);

    @ResultMap("dataTypeMapper")
    @Update("""
            UPDATE data_types SET
                name = #{dataType.name},
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{dataType.id}
            RETURNING *;
            """)
    DataType update(@Param("dataType") DataType dataType);

    @Delete("""
            DELETE FROM data_types
            WHERE id = #{id};
            """)
    void deleteById(@Param("id") UUID id);
}
