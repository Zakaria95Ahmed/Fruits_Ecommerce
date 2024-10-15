package com.fruits.ecommerce.models.mappers;

import com.fruits.ecommerce.models.dtos.CustomerDTO;
import com.fruits.ecommerce.models.entities.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, AddressMapper.class})
public interface CustomerMapper {
    CustomerDTO toDTO(Customer customer);
    Customer toEntity(CustomerDTO customerDTO);
}