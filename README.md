# MoneyMe - Virtual Wallet (Spring Boot Project) - Team 2

## Access Our Project

We have deployed our project using **Heroku** and **AWS RDB**. Also, we provide **Swagger API Documentation** to facilitate testing and exploration of the REST API’s endpoints.

- **App URL**: [`https://money-me-84fb9ba46b45.herokuapp.com/`](https://money-me-84fb9ba46b45.herokuapp.com/)  
- **Swagger URL**: [`https://money-me-84fb9ba46b45.herokuapp.com/swagger-ui/index.html`](https://money-me-84fb9ba46b45.herokuapp.com/swagger-ui/index.html)

## Overview

**MoneyMe** is a secure and efficient digital wallet solution designed to help users manage their finances effortlessly. Users can deposit funds, transfer money, and exchange currencies, while administrators oversee platform activities to maintain security and efficiency.

---

## Features

- **User Management**
  - Register and authenticate using JWT authentication.
  - Manage user profiles and account settings.
  
- **Wallet & Transactions**
  - Add and manage credit/debit cards securely.
  - Deposit money into the virtual wallet from linked bank cards.
  - Transfer funds to other users via phone number, username, or email (after account verification).
  - Exchange money with live exchange rates.
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
- **ExchangeRate API for Live Exchange Rate Update**
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
   - Use the schema.sql script to create the database and data.sql to populate it.

3. **Create .env file in root directory and configure variables**  
   Create a .env file in the root directory of the project and add the following environment variables:
   ```sh
   MAIL_USERNAME=your.email@gmail.com
   MAIL_PASSWORD=mail.password

   CLOUDINARY_NAME=cloudinary.api.name
   CLOUDINARY_KEY=cloudinary.api.key
   CLOUDINARY_SECRET=cloudinary.api.secret

   TWILIO_SID=twilio.sid
   TWILIO_TOKEN=twilio.token
   TWILIO_PHONE=twilio.phone

   DB_URL=your.db.url
   DB_USERNAME=db.username
   DB_PASSWORD=db.password

   EXCHANGE_RATE_API_URL=exchangerate.api.url
   EXCHANGE_RATE_API_KEY=exchangerate.api.key
   ```
 ⚠️ We recommend creating your own accounts for each service. If needed, we can provide shared credentials for development or demo purposes.

 ⚠️ If you want CI/CD to work using `main.yml` you must create github secrets in your github repository.

4. **Run the Application**  
   Execute `VirtualWalletApplication.class` to start the project.

---

## **IMPORTANT NOTES**

⚠️ **For Testing Purposes**  
- The dummy data script uses the same encrypted password for all users: `"12345678"`, hashed as:  
  `$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m`
- The JWT token is set to expire after **100 days** to facilitate testing.

⚠️ **Security Disclaimer**  
- Do **not** use actual credit card details and personal information!  

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

