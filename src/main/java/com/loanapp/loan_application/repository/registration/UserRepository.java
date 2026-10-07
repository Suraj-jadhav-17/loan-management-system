package com.loanapp.loan_application.repository.registration;

import com.loanapp.loan_application.entity.registration.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByEmail(@NotBlank @Email String email);
}
