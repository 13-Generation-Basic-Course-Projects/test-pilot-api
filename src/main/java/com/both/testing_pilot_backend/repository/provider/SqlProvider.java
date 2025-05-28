package com.both.testing_pilot_backend.repository.provider;

import com.both.testing_pilot_backend.dto.request.PageRequest;
import com.both.testing_pilot_backend.dto.request.apiFeature.Filter;
import com.both.testing_pilot_backend.dto.request.apiFeature.Sort;
import com.both.testing_pilot_backend.utils.CursorPaginationUtil;
import com.both.testing_pilot_backend.utils.SqlFieldValidator;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SqlProvider {

    public String buildFindAllQuery(Map<String, Object> params) {
        List<Filter> filters = (List<Filter>) params.get("filters");
        List<Sort> sorts = (List<Sort>) params.get("sorts");
        List<Filter> search = (List<Filter>) params.get("search");
        PageRequest pageRequest = (PageRequest) params.get("pageRequest");
        String cursor = (String) params.get("cursor");
        String tableName = (String) params.get("tableName");

        List<Filter> combinedFilters = filters;

        if(filters != null && !filters.isEmpty()) {
            combinedFilters.addAll(filters);
        }

        if(search != null && !search.isEmpty()) {
            filters.addAll(search);
        }


        // 2) Inject cursor filter if present
        if (cursor != null && !cursor.isBlank()) {
            combinedFilters.add(Filter.builder()
                    .field("created_at")
                    .operator(Filter.Operator.LT)
                    .value(CursorPaginationUtil.decodeCursor(cursor))
                    .build()
            );
        }

        SqlFieldValidator.validate(tableName, filters, sorts);

        String sqlQuery = new SQL() {{
            SELECT("*"); // SELECT *
            FROM(tableName); // Dynamic table name ex: FROM users

            // build filter query
            if (combinedFilters != null && !combinedFilters.isEmpty()) {
                WHERE(buildWhereClause(combinedFilters)); // where
            }

            // build sort query
            if (sorts != null && !sorts.isEmpty()) {
                ORDER_BY(buildOrderByClause(sorts));
            }

            // Note: LIMIT and OFFSET aren't native SQL methods, handle manually
        }}.toString() + buildLimitOffsetClause(pageRequest);

        System.out.println("queryyyyyyy " + sqlQuery);
        return  sqlQuery; // Manual append of LIMIT/OFFSET
    }

    public String buildCountQuery(Map<String, Object> params) {
        List<Filter> filters = (List<Filter>) params.get("filters");
        String tableName = (String) params.get("tableName");

        return new SQL() {{
            SELECT("COUNT(*)");
            FROM(tableName); // Dynamic table name

            if (filters != null && !filters.isEmpty()) {
                WHERE(buildWhereClause(filters));
            }
        }}.toString();
    }

    public String buildFindAllProjectsForUserQuery(Map<String, Object> params) {
        List<Filter> filters = (List<Filter>) params.get("filters");
        List<Sort> sorts = (List<Sort>) params.get("sorts");
        List<Filter> search = (List<Filter>) params.get("search");
        PageRequest pageRequest = (PageRequest) params.get("pageRequest");
        String cursor = (String) params.get("cursor");
        UUID userId = (UUID) params.get("userId"); // Extract userId

        List<Filter> combinedFilters = new ArrayList<>();

        if(filters != null && !filters.isEmpty()) {
            combinedFilters.addAll(filters);
        }

        if(search != null && !search.isEmpty()) {
            combinedFilters.addAll(search); // Add search filters to combinedFilters
        }

        // Inject cursor filter if present
        if (cursor != null && !cursor.isBlank()) {
            combinedFilters.add(Filter.builder()
                    .field("p.created_at") // Use alias for created_at
                    .operator(Filter.Operator.LT)
                    .value(CursorPaginationUtil.decodeCursor(cursor))
                    .build()
            );
        }

        // Validate fields against 'projects' table (or a combined view)
        // SqlFieldValidator.validate("projects", combinedFilters, sorts); // Adjust validation if needed

        String sqlQuery = new SQL() {{
            SELECT("DISTINCT p.*"); // Use DISTINCT because of the JOIN with project_collaborators
            FROM("projects p");
            LEFT_OUTER_JOIN("project_collaborators pc ON p.id = pc.project_id");

            // Main WHERE clause for user ownership/collaboration
            // This is the primary filter, and others will be ANDed to it.
            WHERE("(p.project_owner_id = #{userId} OR pc.user_id = #{userId})");

            // Apply additional filters from combinedFilters
            if (combinedFilters != null && !combinedFilters.isEmpty()) {
                // IMPORTANT: Ensure filter fields use 'p.' alias if they refer to project columns
                // You might need to adjust buildWhereClause to handle aliases or pass aliases.
                // For simplicity, assuming filter fields are already aliased or are generic.
                combinedFilters.forEach(filter -> {
                    // Prepend 'p.' to filter fields if they are not already aliased
                    if (!filter.getField().contains(".")) { // Simple check, might need more robust logic
                        filter.setField("p." + filter.getField());
                    }
                });
                AND(); // Add an AND operator before appending additional filters
                WHERE(buildWhereClause(combinedFilters));
            }

            // Apply sorting
            if (sorts != null && !sorts.isEmpty()) {
                StringBuilder orderBy = new StringBuilder();
                sorts.forEach(sort -> {
                    if (orderBy.length() > 0) orderBy.append(", ");
                    // Prepend 'p.' to sort fields if they are not already aliased
                    if (!sort.getField().contains(".")) {
                        orderBy.append("p.");
                    }
                    orderBy.append(sort.getField()).append(" ").append(sort.getDirection().name());
                });
                ORDER_BY(orderBy.toString());
            } else {
                ORDER_BY("p.created_at DESC"); // Default sort for this specific query
            }

        }}.toString() + buildLimitOffsetClause(pageRequest);

        System.out.println("Projects for User Query: " + sqlQuery);
        return sqlQuery;
    }
    private String buildWhereClause(List<Filter> filters) {
        StringBuilder where = new StringBuilder();
        for (Filter filter : filters) {
            if (where.length() > 0) where.append(" AND ");
            where.append(buildCondition(filter));
        }
        return where.toString();
    }

    private String buildCondition(Filter filter) {
        String field = filter.getField();
        Filter.Operator op = filter.getOperator();
        String value = filter.getValue() == null ? "null" : "'" + filter.getValue() + "'";

        return switch (op) {
            case EQ -> field + " = " + value;
            case NE -> field + " <> " + value;
            case GT -> field + " > " + value;
            case GTE -> field + " >= " + value;
            case LT -> field + " < " + value;
            case LTE -> field + " <= " + value;
            case LIKE -> field + " LIKE CONCAT('%', " + value + ", '%')";
            case IN -> field + " IN (" + value + ")";
            case NIN -> field + " NOT IN (" + value + ")";
        };
    }

    private String buildOrderByClause(List<Sort> sorts) {
        return sorts.stream()
                .map(s -> s.getField() + " " + s.getDirection())
                .collect(Collectors.joining(", "));
    }

    private String buildLimitOffsetClause(PageRequest pageRequest) {
        if (pageRequest == null) return "";
        int size = pageRequest.getSize();
        int offset = (pageRequest.getPage() - 1) * size;

        return " LIMIT " + (size + 1);

        // return " LIMIT " + size + " OFFSET " + offset;
    }
}
