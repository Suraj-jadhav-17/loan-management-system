package com.loanapp.loan_application.repository.cibil;

import com.loanapp.loan_application.entity.cibil.CIBILReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CIBILReportRepo extends JpaRepository<CIBILReport,Long> {
}
