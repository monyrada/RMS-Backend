package rmsbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rmsbackend.dto.customer.CustomerRequest;
import rmsbackend.dto.customer.CustomerResponse;
import rmsbackend.service.CustomerService;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public CustomerResponse createCustomer(CustomerRequest customerRequest) {
        return customerService.create(customerRequest);
    }

}
