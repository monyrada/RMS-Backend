package rmsbackend.service;

import rmsbackend.dto.customer.CustomerRequest;
import rmsbackend.dto.customer.CustomerResponse;

public interface CustomerService {

    CustomerResponse create(CustomerRequest request);

}
