# Hotel-Management-System-using-Java-MySQL
A complete Java-based Hotel Management System developed as a Semester 2 project. The system uses JDBC for MySQL database connectivity and supports admin login, customer management, room booking, payment tracking, check-in/check-out handling, and automated room availability updates.



## 📖 Project Overview
The **Hotel Management System** is a fully functional, database-driven Java application developed as an **individual Semester 2 project**.  
It automates hotel operations such as **room booking, customer management, payments, and check-in/check-out handling** using a structured backend system.

The project is built using **Core Java with JDBC** and connected to a **MySQL (MariaDB) database**, demonstrating real-world application development concepts.

---

## 🎯 Project Objectives
- Automate hotel booking and record management
- Maintain accurate real-time room availability
- Ensure structured and persistent data storage
- Apply Java and SQL concepts in a real-world project

---

## 🚀 Features
- Secure **Admin Login**
- Customer registration and booking management
- Room allocation based on room type and availability
- Automated room count updates using stored procedures
- Check-in and check-out handling
- Payment tracking (status & method)
- Planned vs actual booking dates
- Relational database with constraints

---

## 🛠️ Technologies Used
- **Language:** Java  
- **Database:** MySQL / MariaDB  
- **Connectivity:** JDBC  
- **Database Tool:** phpMyAdmin  
- **IDE:** Eclipse / IntelliJ IDEA / NetBeans  

---


---

## 🗄️ Database Design
- **Database Name:** `hotel_management`

### Tables
- `admin` – Admin authentication details  
- `customers` – Customer data, bookings, payments, status  
- `rooms` – Room types and availability  

### Stored Procedure
- `updateRoomCount`
  - Automatically updates available rooms during booking and checkout

---

## ⚙️ How to Run the Project

### ✅ Prerequisites
- Java JDK 8 or higher
- MySQL Server / MariaDB
- MySQL JDBC Connector (`mysql-connector-j`)
- Java IDE (Eclipse / IntelliJ / NetBeans)

---

### 🟢 Step 1: Setup Database
1. Open **phpMyAdmin**
2. Create a new database:
```

hotel_management

```
3. Import the SQL file provided:
```

database/hotel_management.sql

````
4. Ensure tables and stored procedures are created successfully

---

### 🟢 Step 2: Configure Java Project
1. Open the project in your Java IDE
2. Add **MySQL JDBC Connector** to the project libraries
3. Update database connection details in `Main.java`:
```java
String url = "jdbc:mysql://localhost:3306/hotel_management";
String user = "root";
String password = "";
````

4. Save the file

---

### 🟢 Step 3: Run the Application

1. Run `Main.java`
2. The application will start in the console
3. Login using admin credentials
4. Begin managing hotel bookings and customers

---

## 🔐 Default Admin Credentials

```
Username: admin
Password: admin123
```

---

## 📘 Academic Information

* **Course:** Engineering (Computer Science – AI Specialization)
* **Semester:** 2
* **Project Type:** Individual Project
* **Developed By:** Ammar Husain Gheewala
* **Purpose:** Academic and practical learning

---

## 🧠 Concepts & Skills Applied

* Core Java programming
* JDBC database connectivity
* SQL schema design
* Stored procedures
* CRUD operations
* Backend logic design
* Software project structuring

---

## 🔮 Future Enhancements

* GUI using JavaFX or Swing
* Role-based access control
* Billing & invoice generation
* Search and reporting modules
* Enhanced security (password hashing)

---

## 📌 Disclaimer

This project is developed **strictly for educational purposes**.
It demonstrates backend development, database connectivity, and real-world logic implementation.

---

## ⭐ Feedback & Support

If you found this project useful, please ⭐ star the repository.
Suggestions and feedback are always welcome.

```

---

```
