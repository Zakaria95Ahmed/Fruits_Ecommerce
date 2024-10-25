package com.fruits.ecommerce.configuration.SecurityConfig.constants;

public class SecurityConstants {

    // Public APIs URLs (No Authentication Required )
    public static final String[] PUBLIC_URLS_APIS = {
            // Auth URLs
            "/api/v1/auth/register",                   // POST - register
            "/api/v1/auth/login",                      // POST - login

            // Products Public APIs
            "/api/v1/products",                        // GET - list products
            "/api/v1/products/{id}",                   // GET - product details
            "/api/v1/products/images/id/{id}",         // GET - image by id
            "/api/v1/products/images/file/{filename:.+}", // GET - image by filename
            "/api/v1/products/byCategory/{categoryId}",           // GET - products by category
            "/api/v1/products/search",                 // GET - search products

            // Categories Public APIs
            "/api/v1/categories",                   // GET - Get All category
            "/api/v1/categories/{id}",              // GET - Get category by id


            // General Public APIs
            "/api/public/**",                       // All public APIs

            // Swagger/OpenAPI Documentation
            "/swagger-ui/**",                       // Swagger UI resources
            "/v3/api-docs/**",                      // OpenAPI docs
            "/swagger-ui.html"                      // Swagger UI main page
    };

    // Admin APIs URLs - Requires ADMIN Role
    public static final String[] ADMIN_URLS_APIS = {
            // Auth Admin APIs
            "/api/v1/auth/lock-user",                  // POST - lock user
            "/api/v1/auth/unlock-user",                // POST - unlock user
            "/api/v1/auth/{userId}/add",               // POST - add role
            "/api/v1/auth/{userId}/remove",            // DELETE - remove role
            "/api/v1/auth/customers",                  // GET - all customers
            "/api/v1/auth/users",                      // GET - all users
            "/api/v1/auth/{userId}/reset-password",    // PUT - reset password
            "/api/v1/auth/{userId}",                   // DELETE - delete user

            // Products Admin APIs
            "/api/v1/products/admin",                  // POST - create product
            "/api/v1/products/admin/images/upload",    // POST - upload images
            "/api/v1/products/admin/{productId}/images/link", // PUT - link images
            "/api/v1/products/admin/{productId}/images",      // POST - add images
            "/api/v1/products/admin/images/{id}",      // DELETE - delete image
            "/api/v1/products/admin/{id}",             // PUT - update product
            "/api/v1/products/admin/{id}",             // DELETE - delete product

            // Categories Admin APIs
            "/api/v1/categories/admin",                   // POST - create category
            "/api/v1/categories/admin/{id}",              // PUT - update category
            "/api/v1/categories/admin/{id}"               // DELETE - delete category
    };

    // Customer/User URLs
    public static final String[] CUSTOMER_USER_URLS_APIS = {
            "/api/v1/customer/cart/add"                // POST - add to cart
    };

    // Customer Only URLs
    public static final String[] CUSTOMER_URLS_APIS = {
            "/api/v1/customer/cart"  ,                  // GET - cart details
            "/api/v1/customer/cart/addresses"           // PUT - update Addresses for Customer
    };
}
