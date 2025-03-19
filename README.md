# MoneyMe - Virtual Wallet (Spring Boot Project) - Team 2

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
  - Transfer funds to other users via phone number, username, or email.
  - View and filter transaction history by date, recipient, sender, etc.
  - Set up and manage multiple virtual wallets, one wallet for each support currency.

- **Security Features**
  - Email verification for registration completion.
  - Identity verification by uploading a photo of Identity card.
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
- **Cloudinary API for Identity Verification**
- **Starter Mail for Email Verification**

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
- Users must verify their identity before completing high-value transactions. 

---

## Database Diagram

(image or a link to the database schema)

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

## Access Our Project

We have deployed our project using **Heroku**. Also, we provide **Swagger API Documentation** to facilitate testing and exploration of the wallet’s endpoints.

- **App URL**: [`https://your-heroku-app.herokuapp.com`](https://your-heroku-app.herokuapp.com)  
- **Swagger URL**: [`https://your-heroku-app.herokuapp.com/swagger-ui/index.html`](https://your-heroku-app.herokuapp.com/swagger-ui/index.html)

