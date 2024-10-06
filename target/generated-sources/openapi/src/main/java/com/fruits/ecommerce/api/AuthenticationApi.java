/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (7.3.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package com.fruits.ecommerce.api;

import com.fruits.ecommerce.model.AuthResponseDTO;
import com.fruits.ecommerce.model.LoginRequestDTO;
import com.fruits.ecommerce.model.UserDTO;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-09-30T16:39:02.804191900+02:00[Africa/Cairo]")
@Validated
@Tag(name = "Authentication", description = "the Authentication API")
public interface AuthenticationApi {

    /**
     * POST /auth/lock-user : Lock a user account
     *
     * @param identifier Username or email of the user to lock (required)
     * @return User account locked successfully (status code 200)
     *         or Bad request (User not found or already locked) (status code 400)
     *         or Forbidden (Insufficient permissions) (status code 403)
     *         or Internal server error (status code 500)
     */
    @Operation(
        operationId = "authLockUserPost",
        summary = "Lock a user account",
        tags = { "Authentication" },
        responses = {
            @ApiResponse(responseCode = "200", description = "User account locked successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request (User not found or already locked)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (Insufficient permissions)"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        },
        security = {
            @SecurityRequirement(name = "bearerAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/auth/lock-user"
    )
    
    ResponseEntity<Void> _authLockUserPost(
        @NotNull @Parameter(name = "identifier", description = "Username or email of the user to lock", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "identifier", required = true) String identifier
    );


    /**
     * POST /auth/login : Login user
     *
     * @param loginRequestDTO  (required)
     * @return Login successful (status code 200)
     *         or Unauthorized (User not found, Account locked, or Bad credentials) (status code 401)
     */
    @Operation(
        operationId = "authLoginPost",
        summary = "Login user",
        tags = { "Authentication" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDTO.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized (User not found, Account locked, or Bad credentials)")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/auth/login",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    
    ResponseEntity<AuthResponseDTO> _authLoginPost(
        @Parameter(name = "LoginRequestDTO", description = "", required = true) @Valid @RequestBody LoginRequestDTO loginRequestDTO
    );


    /**
     * POST /auth/register : Register a new user
     *
     * @param userDTO  (required)
     * @return User successfully registered (status code 201)
     *         or Bad request (Username exists, Email exists, Invalid role, or Invalid user data) (status code 400)
     *         or Internal server error (Failed to send email) (status code 500)
     */
    @Operation(
        operationId = "authRegisterPost",
        summary = "Register a new user",
        tags = { "Authentication" },
        responses = {
            @ApiResponse(responseCode = "201", description = "User successfully registered", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request (Username exists, Email exists, Invalid role, or Invalid user data)"),
            @ApiResponse(responseCode = "500", description = "Internal server error (Failed to send email)")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/auth/register",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    
    ResponseEntity<UserDTO> _authRegisterPost(
        @Parameter(name = "UserDTO", description = "", required = true) @Valid @RequestBody UserDTO userDTO
    );


    /**
     * POST /auth/unlock-user : Unlock a user account
     *
     * @param identifier Username or email of the user to unlock (required)
     * @return User account unlocked successfully (status code 200)
     *         or Bad request (User not found or not locked) (status code 400)
     *         or Forbidden (Insufficient permissions) (status code 403)
     *         or Internal server error (status code 500)
     */
    @Operation(
        operationId = "authUnlockUserPost",
        summary = "Unlock a user account",
        tags = { "Authentication" },
        responses = {
            @ApiResponse(responseCode = "200", description = "User account unlocked successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request (User not found or not locked)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (Insufficient permissions)"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        },
        security = {
            @SecurityRequirement(name = "bearerAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/auth/unlock-user"
    )
    
    ResponseEntity<Void> _authUnlockUserPost(
        @NotNull @Parameter(name = "identifier", description = "Username or email of the user to unlock", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "identifier", required = true) String identifier
    );

}
