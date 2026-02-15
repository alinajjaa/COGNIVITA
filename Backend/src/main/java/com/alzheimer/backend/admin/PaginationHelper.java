package com.alzheimer.backend.admin;

import java.util.*;

public class PaginationHelper {
    public static <T> List<T> paginate(List<T> list, int page, int size) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        int start = page * size;
        int end = Math.min(start + size, list.size());

        if (start >= list.size()) {
            return Collections.emptyList();
        }

        return list.subList(start, end);
    }

    public static <T> Map<String, Object> createPaginatedResponse(List<T> content, int page, int size, long total) {
        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("currentPage", page);
        response.put("pageSize", size);
        response.put("totalElements", total);
        response.put("totalPages", (int) Math.ceil((double) total / size));
        response.put("hasNext", (page + 1) * size < total);
        response.put("hasPrevious", page > 0);
        return response;
    }
}
