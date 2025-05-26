package com.both.testing_pilot_backend.config;// src/main/java/com/both/testing_pilot_backend/utils/JsonbTypeHandler.java

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Component; // <--- Add this import

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component // <--- Mark it as a Spring component
public class JsonbTypeHandler extends BaseTypeHandler<JsonNode> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JsonNode parameter, JdbcType jdbcType) throws SQLException {
        PGobject jsonObject = new PGobject();
        jsonObject.setType("jsonb");
        jsonObject.setValue(parameter.toString());
        ps.setObject(i, jsonObject);
    }

    @Override
    public JsonNode getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String jsonSource = rs.getString(columnName);
        if (jsonSource != null) {
            try {
                return objectMapper.readTree(jsonSource);
            } catch (IOException e) {
                throw new SQLException("Failed to parse JSONB data from column " + columnName, e);
            }
        }
        return null;
    }

    @Override
    public JsonNode getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String jsonSource = rs.getString(columnIndex);
        if (jsonSource != null) {
            try {
                return objectMapper.readTree(jsonSource);
            } catch (IOException e) {
                throw new SQLException("Failed to parse JSONB data from column at index " + columnIndex, e);
            }
        }
        return null;
    }

    @Override
    public JsonNode getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonSource = cs.getString(columnIndex);
        if (jsonSource != null) {
            try {
                return objectMapper.readTree(jsonSource);
            } catch (IOException e) {
                throw new SQLException("Failed to parse JSONB data from column at index " + columnIndex, e);
            }
        }
        return null;
    }
}