package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.model.TestCase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.UUID;

@Mapper
public interface TestcaseRepository {

    @Select("""
        SELECT * FROM test_cases
        WHERE id = #{id};
    """)
    TestCase getTestCaseById(UUID id);
}
