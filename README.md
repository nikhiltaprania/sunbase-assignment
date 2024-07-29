# Customer Management System

## Overview

The Customer Management System is a comprehensive web application designed to efficiently manage customer data. It provides functionalities for user authentication, CRUD operations for customer information, and advanced search capabilities. Additionally, it supports synchronization of customer data with an external API.

## Features

- **Authentication**: Secure login and token-based session management.
- **CRUD Operations**: Add, edit, delete, and view customer information.
- **Search**: Search for customers by various criteria such as first name, city, email, and phone.
- **Pagination**: View customer lists with pagination support.
- **Sync**: Synchronize customer data with an external API and update the database accordingly.

## Technologies Used

- **Frontend**: HTML, CSS, JavaScript
- **Backend**: Spring Boot
- **Database**: MySQL

## Getting Started

### Prerequisites

- Java JDK 17+
- Node.js
- MySQL

### Installation

1. **Clone the Repository**

   ```bash
   https://github.com/nikhiltaprania/sunbase-assignment
2. **Set Up the Database**
- Create a MySQL database and update the database credentials in the `application.properties` file.

3. **Build and Run the Application**

- Open the project in your IDE (e.g., IntelliJ IDEA, Eclipse).
- Configure application.properties with your MySQL database credentials.
- Build and run the Spring Boot application.
4. **Access the Application**
- Open your web browser and navigate to http://localhost:8080 to access the application.

## Usage
### Register a New User

**POST** `http://localhost:8080/auth/register`

**Headers:**
- Content-Type: application/json
- Accept: application/json

**Body:**
```json
{
  "email": "test@sunbasedata.com",
  "password": "Test@123",
  "firstName": "Nikhil",
  "lastName": "Taprania",
  "phone": "9719768185",
  "customerAddress": {
    "street": "Street-1",
    "address": "House No. 1",
    "city": "MZN",
    "state": "UP"
  }
}
```
![Registration](/SunbaseAssignment/src/main/resources/static/project-images/registration.png)

### Login
**POST** `http://localhost:8080/auth/login`

**Headers:**
- Content-Type: application/json
- Accept: application/json

**Body:**
```json
{
  "email": "test@sunbasedata.com",
  "password": "Test@123"
}
```
![Login](/SunbaseAssignment/src/main/resources/static/project-images/login.png)

### Add New Customer

**POST** `http://localhost:8080/customers/save`

**Headers:**
- Content-Type: application/json
- Accept: application/json
- Authorization: Bearer [Your_JWT_Token_Here]

**Body:**
```json
{
  "email": "l@gmail.com",
  "password": "1234",
  "firstName": "Nikhil",
  "lastName": "Taprania",
  "phone": "9719768185",
  "customerAddress": {
    "street": "Street-1",
    "address": "House No. 1",
    "city": "MZN",
    "state": "UP"
  }
}
```
![Save new Customer](/SunbaseAssignment/src/main/resources/static/project-images/add.png)

### Edit a Customer

**PUT** `http://localhost:8080/customers/update`

**Headers:**
- Content-Type: application/json
- Accept: application/json
- Authorization: Bearer [Your_JWT_Token_Here]

**Body:**
```json
{
  "customerId": 2,
  "email": "shani@gmail.com",
  "password": "1234",
  "firstName": "Shani",
  "lastName": "Taprania",
  "phone": "9719768185",
  "customerAddress": {
    "street": "Street-1",
    "address": "House No. 1",
    "city": "MZN",
    "state": "UP"
  }
}
```
![Update Customer with customerId](/SunbaseAssignment/src/main/resources/static/project-images/update.png)

### Delete a Customer

**DELETE** `http://localhost:8080/customers/delete?customerId=3`

**Headers:**
- Accept: application/json
- Authorization: Bearer [Your_JWT_Token_Here]

![Delete Customer with customerId](/SunbaseAssignment/src/main/resources/static/project-images/delete.png)

### Search
- Use the search functionality to filter customers based on criteria like first name, city, email, and phone. Designed in front-end

![Search](/SunbaseAssignment/src/main/resources/static/project-images/search.png)

### Sync Button And Pagination
- Located on the customer list screen, this button fetches customer data from a remote API and updates your database. If a customer already exists, their details are updated rather than creating a duplicate entry.
- Pagination functionality add

![Sync & Pagination](/SunbaseAssignment/src/main/resources/static/project-images/sync.png)

## Contact
For further suggestions, inquiries, or issues, please contact `nikhiltaprania@gmail.com` or visit my portfolio at https://nikhiltaprania.github.io.