package com.loanapp.loan_application.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationMetadata {


    private int page;
    private int limit;
    private long totalElements;
    private int totalPages;
    private String sortBy;
    private String direction;
    private boolean hasNext;
    private boolean hasPrevious;


}
