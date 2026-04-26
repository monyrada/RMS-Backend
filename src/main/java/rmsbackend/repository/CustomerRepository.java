package rmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rmsbackend.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);
}
