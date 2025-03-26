package com.ssafy.boney.domain.loan.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class LoanApproveResponse {
    private Integer loanId;
    private LocalDateTime approvedAt;
    private String loanStatus;

}
