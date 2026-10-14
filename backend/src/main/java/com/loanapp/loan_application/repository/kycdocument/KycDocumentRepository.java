
package com.loanapp.loan_application.repository.kycdocument;

import com.loanapp.loan_application.entity.kycdocument.KycDocument;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KycDocumentRepository extends JpaRepository<KycDocument, Long> {

    // Get all documents uploaded by a particular customer
    List<KycDocument> findByCustomerIdOrderByDocumentIdDesc(Long customerId);

    // Find a particular document belonging to a customer
    Optional<KycDocument> findByDocumentIdAndCustomerId(Long documentId, Long customerId);

    // Get unique customer IDs that have uploaded KYC documents
    @Query("SELECT DISTINCT k.customerId FROM KycDocument k")
    List<Long> findDistinctCustomerIds();
}
