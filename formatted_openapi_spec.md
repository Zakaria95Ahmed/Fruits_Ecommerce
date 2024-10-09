# Fruits E-commerce API

## Overview

- **Version:** 1.0.0
- **Description:** API for managing a fruits e-commerce platform Online

## Server

- **URL:** http://localhost:9090/api
- **Description:** Local development server

## Tags

- **Authentication:** User authentication and account management
- **Products:** Product management

## Paths

### Authentication

#### Register a new user

- **Path:** `/auth/register`
- **Method:** POST
- **Summary:** Register a new user
- **Operation ID:** registerUser

##### Request Body

- **Content Type:** application/json
- **Schema:** UserDTO

##### Responses

- **201:** User successfully registered
  - **Content Type:** application/json
  - **Schema:** UserDTO
- **400:** Bad request (Username exists, Email exists, Invalid role, or Invalid user data)
- **500:** Internal server error (Failed to send email)
- **Default:** Unexpected Error

#### Login user

- **Path:** `/auth/login`
- **Method:** POST
- **Summary:** Login user
- **Operation ID:** loginUser

##### Request Body

- **Content Type:** application/json
- **Schema:** LoginRequestDTO

##### Responses

- **200:** Login successful
  - **Content Type:** application/json
  - **Schema:** AuthResponseDTO
- **401:** Unauthorized (User not found, Account locked, or Bad credentials)
- **Default:** Unexpected Error

#### Lock a user account

- **Path:** `/auth/lock-user`
- **Method:** POST
- **Summary:** Lock a user account
- **Operation ID:** lockUserAccount
- **Security:** Bearer Authentication

##### Parameters

- **Name:** identifier
- **In:** query
- **Required:** true
- **Schema:** string
- **Description:** Username or email of the user to lock

##### Responses

- **200:** User account locked successfully
- **400:** Bad request (User not found or already locked)
- **403:** Forbidden (Insufficient permissions)
- **500:** Internal server error
- **Default:** Unexpected Error

#### Unlock a user account

- **Path:** `/auth/unlock-user`
- **Method:** POST
- **Summary:** Unlock a user account
- **Operation ID:** unlockUserAccount
- **Security:** Bearer Authentication

##### Parameters

- **Name:** identifier
- **In:** query
- **Required:** true
- **Schema:** string
- **Description:** Username or email of the user to unlock

##### Responses

- **200:** User account unlocked successfully
- **400:** Bad request (User not found or not locked)
- **403:** Forbidden (Insufficient permissions)
- **500:** Internal server error
- **Default:** Unexpected Error

### Products

#### Create a new product

- **Path:** `/api/products/admin`
- **Method:** POST
- **Summary:** Create a new product
- **Operation ID:** Admin Can Create a new product
- **Security:** Bearer Authentication

##### Request Body

- **Content Type:** application/json
- **Schema:** ProductDTO

##### Responses

- **201:** Created
  - **Content Type:** application/json
  - **Schema:** ProductDTO
- **400:** Bad Request

#### Get list of products

- **Path:** `/api/products`
- **Method:** GET
- **Summary:** Get list of products

##### Parameters

- **Name:** page
  - **In:** query
  - **Schema:** integer
  - **Description:** Page number
- **Name:** pageSize
  - **In:** query
  - **Schema:** integer
  - **Description:** Number of items per page

##### Responses

- **200:** OK
  - **Content Type:** application/json
  - **Schema:**
    - **Type:** object
    - **Properties:**
      - content: array of ProductDTO
      - totalElements: integer
      - totalPages: integer
      - number: integer
- **400:** Bad Request

## Components

### Schemas

#### UserDTO

- **Type:** object
- **Properties:**
  - id: integer
  - username: string
  - email: string (format: email)
  - role: string (enum: [CLIENT, ADMIN])
- **Required:** username, email, role

#### LoginRequestDTO

- **Type:** object
- **Properties:**
  - username: string
  - password: string (format: password)
- **Required:** username, password

#### AuthResponseDTO

- **Type:** object
- **Properties:**
  - token: string
  - expiresAt: string (format: date-time)
- **Required:** token, expiresAt

#### ProductDTO

- **Type:** object
- **Properties:**
  - id: integer
  - name: string
  - unit: string
  - price: number (format: double)
  - description: string
  - base64ImageData: string
  - createdAt: string (format: date-time)
  - updatedAt: string (format: date-time)

#### Error

- **Type:** object
- **Properties:**
  - code: integer (format: int32)
  - message: string
- **Required:** code, message

### Responses

#### UnexpectedError

- **Description:** Unexpected error
- **Content Type:** application/json
- **Schema:** Error

### Security Schemes

#### Bearer Authentication

- **Type:** http
- **Scheme:** bearer
- **Bearer Format:** JWT