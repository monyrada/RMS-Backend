package rmsbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import rmsbackend.dto.customer.CustomerRequest;
import rmsbackend.dto.customer.CustomerResponse;
import rmsbackend.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public CustomerResponse createCustomer(CustomerRequest customerRequest) {
        return customerService.create(customerRequest);
    }

    @GetMapping
    public List<CustomerResponse> findAllCustomers() {
        return customerService.findAll();
    }

    @GetMapping("/{id}")
    public CustomerResponse findCustomerById(@PathVariable String id) {
        return customerService.findById(id);
    }

}
