package com.fruits.ecommerce.models.mappers;

import com.fruits.ecommerce.models.dtos.AddressDTO;
import com.fruits.ecommerce.models.entities.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressDTO toDTO(Address address);
    Address toEntity(AddressDTO addressDTO);
}