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
        UUID userId = (UUID) params.get("userId");

        List<Filter> combinedFilters = new ArrayList<>();

        if(filters != null && !filters.isEmpty()) {
            combinedFilters.addAll(filters);
        }

        if(search != null && !search.isEmpty()) {
            combinedFilters.addAll(search);
        }

        if (cursor != null && !cursor.isBlank()) {
            combinedFilters.add(Filter.builder()
                    .field("p.created_at")
                    .operator(Filter.Operator.LT)
                    .value(CursorPaginationUtil.decodeCursor(cursor))
                    .build()
            );
        }

        String sqlQuery = new SQL() {{
            SELECT("DISTINCT p.*");
            FROM("projects p");
            LEFT_OUTER_JOIN("project_collaborators pc ON p.id = pc.project_id AND pc.is_verify = true");

            WHERE("(p.project_owner_id = #{userId} OR pc.user_id = #{userId})");

            if (combinedFilters != null && !combinedFilters.isEmpty()) {
                combinedFilters.forEach(filter -> {
                    if (!filter.getField().contains(".")) {
                        filter.setField("p." + filter.getField());
                    }
                });
                AND(); // Add an AND operator before appending additional filters
                WHERE(buildWhereClause(combinedFilters));
            }

            if (sorts != null && !sorts.isEmpty()) {
                StringBuilder orderBy = new StringBuilder();
                sorts.forEach(sort -> {
                    if (orderBy.length() > 0) orderBy.append(", ");
                    if (!sort.getField().contains(".")) {
                        orderBy.append("p.");
                    }
                    orderBy.append(sort.getField()).append(" ").append(sort.getDirection().name());
                });
                ORDER_BY(orderBy.toString());
            } else {
                ORDER_BY("p.created_at DESC");
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
