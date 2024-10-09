package com.fruits.ecommerce.models.mappers;

import com.fruits.ecommerce.models.dtos.UserDTO;
import com.fruits.ecommerce.models.entities.Role;
import com.fruits.ecommerce.models.entities.User;
import com.fruits.ecommerce.models.enums.RoleType;
import org.mapstruct.*;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * UserMapper interface for mapping between User entity and UserDTO.
 * MapStruct will generate an implementation of this interface at compile time.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     // Converts a User entity to a UserDTO.
     // The roles field is mapped using a custom method specified by qualifiedByName.
     // @param user The User entity to convert
     // @return The resulting UserDTO
     */
    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStringSet")
    UserDTO toDTO(User user);



    /**
     // Converts a UserDTO to a User entity.
     // The roles field is ignored in this mapping as it typically requires more complex logic.
     // @param userDTO The UserDTO to convert
    // @return The resulting User entity
     */
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", source = "password")
    User toEntity(UserDTO userDTO);

    /**
     // Custom method to convert Set<Role> to Set<String>.
     // This method is used by MapStruct when converting roles in the toDTO method.
     // @param roles Set of Role entities
     // @return Set of role names as strings
     **/
    @Named("rolesToStringSet")
    default Set<String> rolesToStringSet(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }

    /**
     // Custom method to convert Set<String> to Set<Role>.
     // This method is not directly used in the current mapper but can be useful
     // if we need to convert role names back to Role entities in the future.
     // @param roleNames Set of role names as strings
     // @return Set of Role entities
     **/

    @Named("stringSetToRoles")
    default Set<Role> stringSetToRoles(Set<String> roleNames) {
        if (roleNames == null) {
            return null;
        }
        return roleNames.stream()
                .map(name -> {
                    Role role = new Role();
                    role.setName(RoleType.valueOf(name));
                    return role;
                })
                .collect(Collectors.toSet());
    }

}