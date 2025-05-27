package com.both.testing_pilot_backend.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JsonbTypeHandler extends BaseTypeHandler<JsonNode> {

    // ObjectMapper should be static and final for performance
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JsonNode parameter, JdbcType jdbcType) throws SQLException {
        // Correct implementation for setting JSONB parameter
        PGobject jsonObject = new PGobject();
        jsonObject.setType("jsonb");
        // Convert JsonNode to String to set its value
        jsonObject.setValue(parameter.toString());
        ps.setObject(i, jsonObject);
    }

    @Override
    public JsonNode getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // Correct implementation for retrieving JSONB from ResultSet by column name
        String jsonSource = rs.getString(columnName);
        if (jsonSource != null) {
            try {
                return objectMapper.readTree(jsonSource);
            } catch (IOException e) {
                // Wrap IOException in SQLException as per MyBatis TypeHandler contract
                throw new SQLException("Failed to parse JSONB data from column " + columnName, e);
            }
        }
        return null;
    }

    @Override
    public JsonNode getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        // Correct implementation for retrieving JSONB from ResultSet by column index
        String jsonSource = rs.getString(columnIndex);
        if (jsonSource != null) {
            try {
                return objectMapper.readTree(jsonSource);
            } catch (IOException e) {
                // Wrap IOException in SQLException
                throw new SQLException("Failed to parse JSONB data from column at index " + columnIndex, e);
            }
        }
        return null;
    }

    @Override
    public JsonNode getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        // Correct implementation for retrieving JSONB from CallableStatement
        String jsonSource = cs.getString(columnIndex);
        if (jsonSource != null) {
            try {
                return objectMapper.readTree(jsonSource);
            } catch (IOException e) {
                // Wrap IOException in SQLException
                throw new SQLException("Failed to parse JSONB data from column at index " + columnIndex, e);
            }
        }
        return null;
    }
}