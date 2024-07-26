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
   git clone https://github.com/yourusername/customer-management-system.git
   cd customer-management-system
2. **Set Up the Database**
- Create a MySQL database and update the database credentials in the `application.properties` file.

3. **Build and Run the Application**

- Open the project in your IDE (e.g., IntelliJ IDEA, Eclipse).
- Configure application.properties with your MySQL database credentials.
- Build and run the Spring Boot application.
4. **Access the Application**
- Open your web browser and navigate to http://localhost:8080 to access the application.

### Usage
- Login/Register: Use the login screen to authenticate users or register new accounts.
- Customer List: View and manage customer data. Use the sync button to fetch and update customer data from a remote API.
- Add Customer: Click on the "Add Customer" button to open a form for adding new customers.
- Edit Customer: Click on the "Edit" button next to a customer to modify their details.
- Delete Customer: Click on the "Delete" button to remove a customer from the database.
- Search: Use the search functionality to filter customers based on criteria like first name, city, email, and phone.
- Syncing Customer Data
- Sync Button: Located on the customer list screen, this button fetches customer data from a remote API and updates your database. If a customer already exists, their details are updated rather than creating a duplicate entry.
## Contact
For further suggestions, inquiries, or issues, please contact `nikhiltaprania@gmail.com` or visit my portfolio at https://nikhiltaprania.github.io.