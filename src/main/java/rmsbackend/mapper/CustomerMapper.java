package rmsbackend.mapper;

import org.springframework.stereotype.Component;
import rmsbackend.domain.Customer;
import rmsbackend.dto.customer.CustomerRequest;
import rmsbackend.dto.customer.CustomerResponse;


@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequest request) {
        return Customer.builder()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .active(request.getActive() != null ? request.getActive(): true)
                .build();
    }

    public CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phoneNumber(customer.getPhoneNumber())
                .email(customer.getEmail())
                .active(customer.getActive())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    public void updateEntity(Customer customer, CustomerRequest request) {
        customer.setName(request.getName());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setEmail(request.getEmail());

        if (request.getActive() != null) {
            customer.setActive(request.getActive());
        }
    }

}
