# MoneyMe - Virtual Wallet (Spring Boot Project)
![MoneyMeLogo](https://github.com/user-attachments/assets/98547bea-8307-417d-b036-dfc0c4cf4f18)

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
  - Exchange money with daily live exchange rates that update every 24 hours.
  - View and filter transaction history by date, recipient, sender, etc.
  - Set up and manage multiple virtual wallets, one wallet for each supported currency.

- **Security Features**
  - Email verification for registration completion.
  - Admin-controlled user blocking/unblocking.
  
- **Administrative Controls**
  - Search, view, and manage users.
  - Monitor and filter transactions.
  - Adjust platform settings.

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
- **OAuth2 Google Login via Google Cloud**
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

![database_schema](https://github.com/user-attachments/assets/f8ef7cf4-9b54-4b7e-82f1-1fb523fcc047)

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

## Application Photos

![1](https://github.com/user-attachments/assets/ef2eefac-4c7d-47f9-b838-c0d2c75bf5e1)

![2](https://github.com/user-attachments/assets/eb912be2-08ea-42db-b8a7-0e85223d484d)

![3](https://github.com/user-attachments/assets/062d826f-2fec-4879-b469-dcfc769a154d)

![4](https://github.com/user-attachments/assets/032bd218-d231-4a8e-b3e7-c303c4f3de78)

![5](https://github.com/user-attachments/assets/0e910417-4fb6-4d27-9641-dd40ff5fa8ed)

![6](https://github.com/user-attachments/assets/49a24029-8931-4a5d-ba56-6ff7a1afc2c4)

![7](https://github.com/user-attachments/assets/6d2679a3-2037-4bbc-94f1-a12ba32b8ca0)

![8](https://github.com/user-attachments/assets/ac46aed4-1254-425e-b33a-bfdfc72fbe28)

![9](https://github.com/user-attachments/assets/22951ac8-82c1-46f8-8904-b25dcc8b876f)

![10](https://github.com/user-attachments/assets/d15b47e5-d1fa-47aa-a589-5115d6dd83b9)

![11](https://github.com/user-attachments/assets/1a6e9731-9f55-450d-b527-9f43d6194d3c)

![12](https://github.com/user-attachments/assets/8e0993e1-3009-47f2-932e-6de0a4b8ef89)

![13](https://github.com/user-attachments/assets/10964fae-36d1-4692-831e-518d65345120)

![14](https://github.com/user-attachments/assets/6a6eaa05-f52f-4888-9817-999baa4af7fa)

![15](https://github.com/user-attachments/assets/657bc443-000a-4277-9301-c85bc0caaa2e)

![16](https://github.com/user-attachments/assets/08c9969d-2e24-4324-a649-193e7a21eae9)

![17](https://github.com/user-attachments/assets/7a3495f6-1807-47d2-b426-54eeb26574c5)

![18](https://github.com/user-attachments/assets/7b6f3f20-da7a-48a7-946a-581b9ed4a85e)


