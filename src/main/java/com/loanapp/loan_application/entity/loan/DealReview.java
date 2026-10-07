package com.loanapp.loan_application.entity.loan;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "DealReviews")
public class DealReview {




    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DealId", nullable = false)
    private Long loanDeal;  //LoanDeal

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OfficerId", nullable = false)
    private Long officer;  //User

    @Column(name = "Status", length = 50)
    private String status;




}