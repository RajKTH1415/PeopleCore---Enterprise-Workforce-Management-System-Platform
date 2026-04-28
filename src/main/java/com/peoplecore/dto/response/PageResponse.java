package com.peoplecore.dto.response;



import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private int numberOfElements;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;
    private String sortBy;
    private String direction;
}
