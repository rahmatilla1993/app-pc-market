package com.example.apppcmarket.service;

import com.example.apppcmarket.entity.Address;
import com.example.apppcmarket.entity.Customer;
import com.example.apppcmarket.enums.Elements;
import com.example.apppcmarket.model.CustomerDTO;
import com.example.apppcmarket.model.Result;
import com.example.apppcmarket.repository.AddressRepository;
import com.example.apppcmarket.repository.CustomerRepository;
import com.example.apppcmarket.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    Elements messageCustomer = Elements.CUSTOMER;
    Elements messageAddress = Elements.ADDRESS;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    SupplierRepository supplierRepository;

    public Page<Customer> getAllCustomers(int page) {
        Pageable pageable = PageRequest.of(page, 5);
        return customerRepository.findAll(pageable);
    }

    public Result getCustomerById(Integer id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        return optionalCustomer.map(customer -> new Result(true, customer)).orElseGet(() -> new Result(messageCustomer.getElementNotFound(), false));
    }

    private Result addingCustomer(CustomerDTO customerDTO, boolean create, boolean edit, Integer id) {
        Customer customer = new Customer();

        if (create && customerRepository.existsByFirstNameAndLastNameAndPhoneNumber(customerDTO.getFirstName(), customerDTO.getLastName(),
                customerDTO.getPhoneNumber()) || edit && customerRepository.existsByIdIsNotAndFirstNameAndLastNameAndPhoneNumber(id,
                customerDTO.getFirstName(), customerDTO.getLastName(), customerDTO.getPhoneNumber())) {
            return new Result(messageCustomer.getElementExists(), false);
        }

        if (create && customerRepository.existsByPhoneNumber(customerDTO.getPhoneNumber()) ||
                edit && customerRepository.existsByIdIsNotAndPhoneNumber(id, customerDTO.getPhoneNumber())) {
            return new Result("Bunday telefon raqam boshqa mijozda bor", false);
        }

        Optional<Address> optionalAddress = addressRepository.findById(customerDTO.getAddress_id());
        if (!optionalAddress.isPresent()) {
            return new Result(messageAddress.getElementNotFound(), false);
        }
        Address address = optionalAddress.get();
        if (create && customerRepository.existsByAddress_Id(customerDTO.getAddress_id()) ||
                edit && customerRepository.existsByIdIsNotAndAddress_Id(id, customerDTO.getAddress_id())) {
            return new Result("Bu manzilda boshqa mijoz yashaydi", false);
        }

        if (create && supplierRepository.existsByAddress_Id(customerDTO.getAddress_id()) ||
                edit && supplierRepository.existsByIdIsNotAndAddress_Id(id, customerDTO.getAddress_id())) {
            return new Result("Bu manzilda ta'minotchi yashaydi", false);
        }

        customer.setAddress(address);
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());
        return new Result(true, customer);
    }

    public Result addCustomer(CustomerDTO customerDTO) {
        Result result = addingCustomer(customerDTO, true, false, null);
        if (result.isSuccess()) {
            Customer customer = (Customer) result.getObject();
            customerRepository.save(customer);
            return new Result(messageCustomer.getElementAdded(), true);
        }
        return result;
    }

    public Result editCustomerById(Integer id, CustomerDTO customerDTO) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isPresent()) {
            Result result = addingCustomer(customerDTO, false, true, id);
            if (result.isSuccess()) {
                Customer editCustomer = optionalCustomer.get();
                Customer customer = (Customer) result.getObject();
                editCustomer.setPhoneNumber(customer.getPhoneNumber());
                editCustomer.setAddress(customer.getAddress());
                editCustomer.setLastName(customer.getLastName());
                editCustomer.setFirstName(customer.getFirstName());
                customerRepository.save(editCustomer);
                return new Result(messageCustomer.getElementEdited(), true);
            }
            return result;
        }
        return new Result(messageCustomer.getElementNotFound(), false);
    }

    public Result deleteCustomerById(Integer id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isPresent()) {
            customerRepository.delete(optionalCustomer.get());
            return new Result(messageCustomer.getElementDeleted(), true);
        }
        return new Result(messageCustomer.getElementNotFound(), false);
    }
}
