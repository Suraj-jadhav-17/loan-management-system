package com.loanapp.loan_application.repository.kycdocument;

import com.loanapp.loan_application.entity.kycdocument.KycDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KycDocumentRepository
        extends JpaRepository<KycDocument, Integer> {

    List<KycDocument> findByCustomerIdOrderByDocumentIdDesc(
            Integer customerId);

    Optional<KycDocument> findByDocumentIdAndCustomerId(
            Integer documentId,
            Integer customerId);

    @Query("select distinct k.customerId from KycDocument k")
    List<Integer> findDistinctCustomerIds();
}