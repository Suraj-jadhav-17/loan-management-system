package com.loanapp.loan_application.repository.register;

import com.loanapp.loan_application.entity.register.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByEmail(@NotBlank @Email String email);

    Optional<User> findByEmail(String email);
}
