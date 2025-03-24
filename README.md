# MoneyMe - Virtual Wallet (Spring Boot Project) - Team 2

## Access Our Project

We have deployed our project using **Heroku** and **AWS RDB**. Also, we provide **Swagger API Documentation** to facilitate testing and exploration of the wallet’s endpoints.

- **App URL**: [`https://money-me-84fb9ba46b45.herokuapp.com/`](https://money-me-84fb9ba46b45.herokuapp.com/)  
- **Swagger URL**: [`https://money-me-84fb9ba46b45.herokuapp.com/swagger-ui/index.html`](https://money-me-84fb9ba46b45.herokuapp.com/swagger-ui/index.html)

## Overview

**MoneyMe** is a secure and efficient digital wallet solution designed to help users manage their finances effortlessly. Users can deposit funds, transfer money, and exchange currencies, while administrators oversee platform activities to maintain security and efficiency.

---

## Features

- **User Management**
  - Register and authenticate using JWT authentication.
  - Manage user profiles and account settings.
  - Identity verification for enhanced security.
  
- **Wallet & Transactions**
  - Add and manage credit/debit cards securely.
  - Deposit money into the virtual wallet from linked bank cards.
  - Transfer funds to other users via phone number, username, or email (after account verification).
  - View and filter transaction history by date, recipient, sender, etc.
  - Set up and manage multiple virtual wallets, one wallet for each supported currency.

- **Security Features**
  - Email verification for registration completion.
  - Admin-controlled user blocking/unblocking.
  
- **Administrative Controls**
  - Search, view, and manage users.
  - Monitor and filter transactions.
  - Adjust platform settings - adjust exchange rates.

---

## Technologies Used

- **JDK 17**
- **Spring Boot framework**
- **MariaDB**
- **JPA**
- **JWT Authentication**
- **Swagger API Documentation**
- **Cloudinary API for Profile Picture Upload**
- **Starter Mail for Email Verification**
- **FormSpree for contact forms**
- **Heroku for application hosting platform**
- **AWS MARIADB RDB for database hosting**

---

## Installation

Follow these steps to set up and run the application:

1. **Clone the Repository**  
   Download the project folder:
   ```sh
   git clone https://github.com/TelerikAcademyJavaA68Team2/VirtualWallet.git
   cd virtual-wallet
   ```

2. **Set Up the Database**  
   - Create and configure a **MariaDB** database.  
   - Use the provided database scripts to populate it.

3. **Configure Properties**  
   Edit `application.properties` in `src/main/resources/` to match your database settings.

4. **Run the Application**  
   Execute `VirtualWalletApplication.class` to start the project.

---

## **IMPORTANT NOTES**

⚠️ **For Testing Purposes**  
- The dummy data script uses the same encrypted password for all users: `"12345678"`, hashed as:  
  `$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m`
- The JWT token is set to expire after **100 days** to facilitate testing.

⚠️ **Security Disclaimer**  
- Do **not** use actual credit card details.  

---

## Database Diagram

![database_schema](https://github.com/user-attachments/assets/d12b1914-566e-4c80-a0e1-50751e88afe9)

<br>

---

## Solution Structure

- **Controllers**: Handle HTTP requests and responses for user, transaction, and wallet-related endpoints.
- **Services**: Implement business logic for user management, transactions, and wallet operations.
- **Repositories**: Interface with the database for CRUD operations using JPA.
- **Models**: Represent entities such as Users, Transactions, Wallets, and Credit/Debit Cards.
- **Utilities**: Provide helper methods for security, validation, and transaction management.

---

## Contributors

For further information, please feel free to contact us:

| Authors               | Emails                       | GitHub                                           |
|-----------------------|------------------------------|--------------------------------------------------|
| **Georgi Benchev**    | gega4321@gmail.com           | [GitHub Link](https://github.com/Georgi-Benchev) |
| **Ivan Ivanov**       | ivanovivanbusiness@gmail.com | [GitHub Link](https://github.com/ivanoffcode)    |

---

