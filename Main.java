import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.io.*;
import java.nio.file.Files;

import static javax.management.remote.JMXConnectorFactory.connect;

// MAIN CLASS
 class HotelManagementSystem {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        DBHandler.connect();
        while(true) {
            System.out.println("\n=== HOTEL MANAGEMENT SYSTEM 2025 ===");
            System.out.println("1. Admin");
            System.out.println("2. Manager");
            System.out.println("3. Front Desk");
            System.out.println("4. Employee");
            System.out.println("5. Customer");
            System.out.println("6. Exit");
            System.out.print("Choice: ");
            int role = sc.nextInt(); sc.nextLine();
            switch(role) {
                case 1: Admin.loginPanel(); break;
                case 2: Manager.loginPanel(); break;
                case 3: FrontDesk.loginPanel(); break;
                case 4: Employee.loginPanel(); break;
                case 5: Customer.menu(); break;
                case 6: System.out.println("Exiting..."); return;
                default: System.out.println("Invalid Choice!");
            }
        }
    }
}

// DATABASE HANDLER
class DBHandler {
    static Connection con;
    static Scanner sc = new Scanner(System.in);

    public static void connect() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_management","root","");
            System.out.println("Database connected!");
        } catch(Exception e) {
            System.out.println("DB Error: "+e.getMessage());
        }
    }
}

// ================== ADMIN CLASS =================
class Admin {
    static Scanner sc = new Scanner(System.in);

    public static void loginPanel() {
        System.out.print("Admin Username: ");
        String user = sc.nextLine();
        System.out.print("Admin Password: ");
        String pass = sc.nextLine();
        try {
            PreparedStatement ps = DBHandler.con.prepareStatement(
                    "SELECT * FROM admin WHERE username=? AND password=?"
            );
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                System.out.println("Welcome Admin!");
                menu();
            } else {
                System.out.println("Invalid credentials!");
            }
        } catch(Exception e) { System.out.println(e.getMessage()); }
    }

    static void menu() throws Exception {
        while(true) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. Add Manager");
            System.out.println("2. Add Employee");
            System.out.println("3. View All Users");
            System.out.println("4. Generate Report");
            System.out.println("5. Add Front Desk");
            System.out.println("6. Add Customer");
            System.out.println("7. Back");
            System.out.print("Choice: ");
            int ch = sc.nextInt(); sc.nextLine();
            switch(ch) {
                case 1: addManager(); break;
                case 2: addEmployee(); break;
                case 3: viewAllUsers(); break;
                case 4: generateReport(); break;
                case 5: addFrontDesk(); break;
                case 6:{
                    FrontDesk d=new FrontDesk();
                    d.bookRoom();


                }
                case 7: return;
                default: System.out.println("Invalid!");
            }
        }
    }

    static void addManager() {
        try {
            System.out.print("Manager Username: "); String uname = sc.nextLine();
            System.out.print("Manager Password: "); String pass = sc.nextLine();
            PreparedStatement ps = DBHandler.con.prepareStatement(
                    "INSERT INTO manager (username,password) VALUES(?,?)"
            );
            ps.setString(1, uname); ps.setString(2, pass);
            ps.executeUpdate();
            System.out.println("Manager added successfully!");
        } catch(Exception e){ System.out.println(e.getMessage()); }
    }

    static void addEmployee() {
        try {
            System.out.print("Employee Name: "); String name = sc.nextLine();
            System.out.print("Employee Username: "); String uname = sc.nextLine();
            System.out.print("Employee Password: "); String pass = sc.nextLine();
            PreparedStatement ps = DBHandler.con.prepareStatement(
                    "INSERT INTO employee (name,username,password) VALUES(?,?,?)"
            );
            ps.setString(1, name); ps.setString(2, uname); ps.setString(3, pass);
            ps.executeUpdate();
            System.out.println("Employee added successfully!");
        } catch(Exception e){ System.out.println(e.getMessage()); }
    }

    static void viewAllUsers() {
        try {
            Statement st = DBHandler.con.createStatement();
            System.out.println("\n--- Managers ---");
            ResultSet rs = st.executeQuery("SELECT * FROM manager");
            while(rs.next()) System.out.println(rs.getInt("manager_id")+" | "+rs.getString("username"));
            System.out.println("\n--- Employees ---");
            rs = st.executeQuery("SELECT * FROM employee");
            while(rs.next()) System.out.println(rs.getInt("employee_id")+" | "+rs.getString("name")+" | "+rs.getString("username"));
            System.out.println("\n--- Customers ---");
            rs = st.executeQuery("SELECT * FROM customers");
            while(rs.next()) System.out.println(rs.getInt("customer_id")+" | "+rs.getString("username")+" | "+rs.getString("name"));
        } catch(Exception e){ System.out.println(e.getMessage()); }
    }

    // Add Front Desk User
     static void addFrontDesk() throws SQLException {

            System.out.print("Enter Front Desk Username: ");
            String username = sc.nextLine();
            System.out.print("Enter Password: ");
            String password = sc.nextLine();

            String sql = "INSERT INTO frontdesk(username, password) VALUES (?, ?)";
            PreparedStatement ps = DBHandler.con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            System.out.println("Front Desk user added successfully!");

    }

    static void generateReport() {
        try {
            FileWriter fw = new FileWriter("Admin_Report.txt");
            Statement st = DBHandler.con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM customers");

            fw.write("=============== 🏨 ADMIN REPORT - CUSTOMER BOOKINGS 🏨 ===============\n");
            fw.write("ID | NAME | USERNAME | ROOM TYPE | CHARGES | ROOM(S) | PAYMENT STATUS\n");
            fw.write("---------------------------------------------------------------------\n");

            int totalRevenue = 0;
            int totalBookings = 0;
            int checkedInCount = 0;
            int futureBookingsCount = 0;
            int cancelledCount = 0;

            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

            while(rs.next()) {
                totalBookings++;

                int id = rs.getInt("customer_id");
                String name = rs.getString("name");
                String username = rs.getString("username");
                String roomType = rs.getString("room_type");
                int charges = rs.getInt("charges");
                String roomNumbers = rs.getString("room_number");
                String paymentStatus = rs.getString("payment_status");
                String status = rs.getString("status"); // ACTIVE / CANCELLED
                String checkinTime = rs.getString("checkin_time");
                String checkoutTime = rs.getString("checkout_time");
                String plannedCheckinDate = rs.getString("checkin_date");

                // Sum revenue only for active or paid bookings
                if(status == null || !status.equalsIgnoreCase("CANCELLED")) {
                    totalRevenue += charges;
                } else {
                    cancelledCount++;
                }

                // Count checked-in guests
                if(checkinTime != null && (checkoutTime == null || checkoutTime.trim().isEmpty())) {
                    checkedInCount++;
                }

                // Count future bookings
                if(plannedCheckinDate != null && java.sql.Date.valueOf(plannedCheckinDate).after(today)) {
                    futureBookingsCount++;
                }

                // Beautify payment info
                String paymentInfo;
                if(paymentStatus == null || paymentStatus.trim().isEmpty() || paymentStatus.equalsIgnoreCase("Pending")) {
                    paymentInfo = "PENDING";
                } else {
                    paymentInfo = "PAID";
                }

                fw.write(id + " | " + name + " | " + username + " | " + roomType +
                        " | ₹" + charges + " | " + roomNumbers + " | " + paymentInfo + "\n");
            }

            // 🔹 Add simple analytics at the end
            fw.write("\n=============== 📊 SUMMARY 📊 ===============\n");
            fw.write("Total Bookings       : " + totalBookings + "\n");
            fw.write("Total Revenue        : ₹" + totalRevenue + "\n");
            fw.write("Checked-in Guests    : " + checkedInCount + "\n");
            fw.write("Future Bookings      : " + futureBookingsCount + "\n");
            fw.write("Cancelled Bookings   : " + cancelledCount + "\n");

            fw.close();
            System.out.println("✅ Admin Report generated: Admin_Report.txt");

        } catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }


}

// ================== MANAGER CLASS =================
class Manager {
    static Scanner sc = new Scanner(System.in);

    public static void loginPanel() {
        System.out.print("Manager Username: "); String user = sc.nextLine();
        System.out.print("Manager Password: "); String pass = sc.nextLine();
        try {
            PreparedStatement ps = DBHandler.con.prepareStatement(
                    "SELECT * FROM manager WHERE username=? AND password=?"
            );
            ps.setString(1,user); ps.setString(2,pass);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) { System.out.println("Welcome Manager!"); menu(); }
            else System.out.println("Invalid credentials!");
        } catch(Exception e){ System.out.println(e.getMessage()); }
    }

    static void menu()  throws Exception{
        while(true) {
            System.out.println("\n--- MANAGER MENU ---");
            System.out.println("1. View Active Customers");
            System.out.println("2. Search Customer By Username");
            System.out.println("3. View Room Status");
            System.out.println("4. Generate Report");
            System.out.println("5. Assign Task");
            System.out.println("6. View Task");
            System.out.println("7. View All Customers");
            System.out.println("8. All Customers (Sorted by Username)");
            System.out.println("9. Back");
            System.out.print("Choice: ");
            int ch = sc.nextInt(); sc.nextLine();
            switch(ch) {
                case 1: viewActiveCustomers(); break;
                case 2: searchCustomerBST(); break;
                case 3: viewRoomStatus(); break;
                case 4: generateManagerReport(); break;
                case 5: assignTask(); break;
                case 6: viewTaskStatus(); break;
                case 7:viewCustomersLinkedList(); break;
                case 8: buildAndDisplayBST(); break;
                case 9: return;
                default: System.out.println("Invalid!");
            }
        }
    }
    static void buildAndDisplayBST() {
        try {
            SimpleBST customerBST = new SimpleBST();

            Statement st = DBHandler.con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM customers");

            while (rs.next()) {
                int id = rs.getInt("customer_id");
                String name = rs.getString("name");
                String username = rs.getString("username"); // ✅ unique
                int charges = rs.getInt("charges");
                String rooms = rs.getString("room_number");

                customerBST.insert(id, name, username, charges, rooms);
            }

            System.out.println("\n--- All Customers (Sorted by Username) ---");
            customerBST.inorder();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    // View  All customers using LinkedList
    static void viewCustomersLinkedList() {
        try {
            Statement st = DBHandler.con.createStatement();
            ResultSet rs = st.executeQuery(" SELECT customer_id, name,room_type,charges,room_number FROM customers");

            System.out.println("\n--- All Customers ---");

            // create linkedlist
            CustomerLinkedList list = new CustomerLinkedList();

            while (rs.next()) {
                int id = rs.getInt("customer_id");
                String name = rs.getString("name");
                String roomType = rs.getString("room_type");
                int charges = rs.getInt("charges");
                String roomNumbers = rs.getString("room_number"); // can be multiple

                // split if multiple rooms
                String[] rooms = roomNumbers.split(",");
                for (String room : rooms) {
                    list.insert(id, name, roomType, charges, room.trim());
                }
            }

            // Display all customers
            list.display();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void viewActiveCustomers() {
        try {
            Statement st = DBHandler.con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM customers WHERE checkout_time IS NULL");

            System.out.println("\n--- Active Customers ---");

            // create linkedlist
            CustomerLinkedList list = new CustomerLinkedList();

            while (rs.next()) {
                int id = rs.getInt("customer_id");
                String name = rs.getString("name");
                String roomType = rs.getString("room_type");
                int charges = rs.getInt("charges");
                String roomNumbers = rs.getString("room_number"); // can be multiple

                // split if multiple rooms
                String[] rooms = roomNumbers.split(",");
                for (String room : rooms) {
                    list.insert(id, name, roomType, charges, room.trim());
                }
            }

            // Display all customers
            list.display();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }



    // BST search by customer name
    static void searchCustomerBST() {
        try {
            // Create a new BST
            SimpleBST customerBST = new SimpleBST();

            // Fetch all customers from the database
            Statement st = DBHandler.con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM customers");

            while (rs.next()) {
                int id = rs.getInt("customer_id");
                String name = rs.getString("name");
                String username = rs.getString("username");   // ✅ get username
                int charges = rs.getInt("charges");
                String roomnos = rs.getString("room_number");

                // Insert each customer into the BST (based on username)
                customerBST.insert(id, name, username, charges, roomnos);
            }

            // Ask user for the username to search
            System.out.print("Enter customer username to search: ");
            String searchUsername = sc.nextLine();

            // Search in BST
            CustomerNode found = customerBST.search(searchUsername);
            if (found != null) {
                System.out.println("\n✅ Found Customer:");
                System.out.println("ID        : " + found.id);
                System.out.println("Name      : " + found.name);
                System.out.println("Username  : " + found.username);
                System.out.println("Charges   : ₹" + found.charges);
                System.out.println("Room No(s): " + found.rooms);
            } else {
                System.out.println("❌ Customer not found.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void viewRoomStatus() {
        try {
            Statement st = DBHandler.con.createStatement();

            // Deluxe
            ResultSet rs1 = st.executeQuery(
                    "SELECT COUNT(*) AS total FROM customers WHERE room_type='Deluxe' AND checkout_time IS NULL");
            rs1.next();
            int deluxe = rs1.getInt("total");

            // Premium
            ResultSet rs2 = st.executeQuery(
                    "SELECT COUNT(*) AS total FROM customers WHERE room_type='Premium' AND checkout_time IS NULL");
            rs2.next();
            int premium = rs2.getInt("total");

            // Super
            ResultSet rs3 = st.executeQuery(
                    "SELECT COUNT(*) AS total FROM customers WHERE room_type='Super' AND checkout_time IS NULL");
            rs3.next();
            int superRoom = rs3.getInt("total");

            System.out.println("\n--- Room Status ---");
            System.out.println("Deluxe rooms booked : " + deluxe);
            System.out.println("Premium rooms booked: " + premium);
            System.out.println("Super rooms booked  : " + superRoom);

            int total = deluxe + premium + superRoom;
            System.out.println("---------------------------------");
            System.out.println("Total rooms booked  : " + total);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public static void assignTask() throws SQLException {

            System.out.print("Enter Employee ID to assign task: ");
            int empID = sc.nextInt(); sc.nextLine();
            System.out.print("Enter Task Description: ");
            String task = sc.nextLine();
            System.out.print("Enter Due Date (YYYY-MM-DD): ");
            String due = sc.nextLine();

            String sql = "INSERT INTO tasks(employee_id, task, status, due_date) VALUES (?, ?, 'Pending', ?)";
            PreparedStatement ps = DBHandler.con.prepareStatement(sql);
            ps.setInt(1, empID);
            ps.setString(2, task);
            ps.setString(3, due);
            ps.executeUpdate();
            System.out.println("Task assigned successfully!");

    }

    public static void viewTaskStatus() throws SQLException {
        String sql = "SELECT t.task_id, e.name, t.task, t.status, t.due_date " +
                "FROM tasks t JOIN employee e ON t.employee_id = e.employee_id";
        ResultSet rs = DBHandler.con.createStatement().executeQuery(sql);

        System.out.println("--- Task Status ---");
        boolean hasTasks = false;  // flag to check if tasks exist

        while (rs.next()) {
            hasTasks = true; // at least one task found
            System.out.println("Task ID: " + rs.getInt("task_id") +
                    " | Employee: " + rs.getString("name") +
                    " | Task: " + rs.getString("task") +
                    " | Status: " + rs.getString("status") +
                    " | Due: " + rs.getString("due_date"));
        }

        if (!hasTasks) {
            System.out.println("No tasks available.");
        }
    }



    static void generateManagerReport() {
        try (FileWriter fw = new FileWriter("Manager_Report.txt")) {

            // 🔹 Section 1: Customer Bookings
            fw.write("=============== 🏨 MANAGER REPORT - CUSTOMER BOOKINGS 🏨 ===============\n");
            fw.write("ID | NAME | ROOM TYPE | ROOM(S) | CHARGES | CHECK-IN | CHECK-OUT | STATUS\n");
            fw.write("--------------------------------------------------------------------------------\n");

            Statement st = DBHandler.con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM customers");

            int totalIncome = 0, totalPending = 0, totalPenalties = 0;
            int cashIncome = 0, cardIncome = 0, upiIncome = 0;

            ArrayList<String> pendingPayments = new ArrayList<>();
            ArrayList<String> stillCheckedIn = new ArrayList<>();
            ArrayList<String> futureBookings = new ArrayList<>();

            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

            while (rs.next()) {
                int id = rs.getInt("customer_id");
                String name = rs.getString("name");
                String roomType = rs.getString("room_type");
                int charges = rs.getInt("charges");
                String roomNumbers = rs.getString("room_number");
                String paymentStatus = rs.getString("payment_status");
                String paymentMethod = rs.getString("payment_method");
                String checkin = rs.getString("checkin_time");
                String checkout = rs.getString("checkout_time");
                String plannedCheckinDate = rs.getString("planned_checkin_date");
                String status = rs.getString("status");

                // 🔹 If cancelled, treat charges as penalty
                if ("CANCELLED".equalsIgnoreCase(status)) {
                    totalPenalties += charges;
                    fw.write(id + " | " + name + " | " + roomType +
                            " | " + roomNumbers + " | ₹" + charges +
                            " | " + (checkin != null ? checkin : "-") +
                            " | " + (checkout != null ? checkout : "-") +
                            " | ❌ CANCELLED (Penalty ₹" + charges + ")\n");
                    continue; // skip payment/income logic
                }

                // ✅ Payment info for non-cancelled bookings
                String paymentInfo;
                if (paymentStatus == null || paymentStatus.trim().isEmpty() || paymentStatus.equalsIgnoreCase("Pending")) {
                    paymentInfo = "⚠️ PENDING";
                    totalPending += charges;
                    pendingPayments.add("Room(s): " + roomNumbers + " | Customer: " + name + " | Amount: ₹" + charges);
                } else {
                    paymentInfo = "✅ PAID (" + paymentMethod + ")";
                    totalIncome += charges;

                    if (paymentMethod != null) {
                        switch (paymentMethod.toLowerCase()) {
                            case "cash": cashIncome += charges; break;
                            case "card": cardIncome += charges; break;
                            case "upi": upiIncome += charges; break;
                        }
                    }
                }

                // ✅ Still checked-in
                if (checkout == null || checkout.trim().isEmpty()) {
                    stillCheckedIn.add("Room(s): " + roomNumbers + " | Customer: " + name + " | Since: " + checkin);
                }

                // ✅ Future bookings
                if (plannedCheckinDate != null && java.sql.Date.valueOf(plannedCheckinDate).after(today)) {
                    futureBookings.add("Customer: " + name + " | Room(s): " + roomNumbers + " | Check-in: " + plannedCheckinDate);
                }

                fw.write(id + " | " + name + " | " + roomType +
                        " | " + roomNumbers + " | ₹" + charges +
                        " | " + (checkin != null ? checkin : "-") +
                        " | " + (checkout != null ? checkout : "-") +
                        " | " + paymentInfo + "\n");
            }

            // 🔹 Section 2: Room Availability & Occupancy
            fw.write("\n=============== 🏨 ROOM AVAILABILITY & OCCUPANCY 🏨 ===============\n");
            String[] types = {"Deluxe", "Premium", "Super"};

            for (String type : types) {
                fw.write("\n--- " + type.toUpperCase() + " ROOMS ---\n");

                int totalRooms = getCount("SELECT COUNT(*) FROM rooms WHERE room_type=?", type);
                int bookedRooms = getCount("SELECT COUNT(*) FROM rooms WHERE room_type=? AND is_booked=TRUE", type);

                fw.write("Total Rooms    : " + totalRooms + "\n");
                fw.write("Booked Rooms   : " + bookedRooms + " (" + (totalRooms > 0 ? (bookedRooms * 100 / totalRooms) : 0) + "%)\n");
                fw.write("Available Rooms: " + (totalRooms - bookedRooms) + "\n");
            }

            // 🔹 Section 3: Payment Summary
            fw.write("\n=============== 💰 PAYMENT SUMMARY 💰 ===============\n");
            fw.write("Cash Received : ₹" + cashIncome + "\n");
            fw.write("Card Received : ₹" + cardIncome + "\n");
            fw.write("UPI Received  : ₹" + upiIncome + "\n");
            fw.write("Total Income  : ₹" + totalIncome + "\n");
            fw.write("Pending Total : ₹" + totalPending + "\n");
            fw.write("Total Penalties Collected: ₹" + totalPenalties + "\n");
            fw.write("--------------------------------------\n");
            fw.write("Grand Total (Paid + Pending + Penalties): ₹" + (totalIncome + totalPending + totalPenalties) + "\n");

            // 🔹 Section 4: Pending Payments
            fw.write("\n=============== ⚠️ PENDING PAYMENTS ⚠️ ===============\n");
            if (pendingPayments.isEmpty()) fw.write("✅ No pending payments.\n");
            else for (String p : pendingPayments) fw.write(p + "\n");

            // 🔹 Section 5: Guests Still Checked-in
            fw.write("\n=============== ⏳ STILL CHECKED-IN (NO CHECKOUT) ⏳ ===============\n");
            if (stillCheckedIn.isEmpty()) fw.write("✅ No guests remaining.\n");
            else for (String s : stillCheckedIn) fw.write(s + "\n");

            // 🔹 Section 6: Future Bookings
            fw.write("\n=============== 📅 FUTURE BOOKINGS 📅 ===============\n");
            if (futureBookings.isEmpty()) fw.write("✅ No upcoming bookings.\n");
            else for (String f : futureBookings) fw.write(f + "\n");

            System.out.println("✅ Manager Report generated: Manager_Report.txt");
            System.out.println("Summary: Total Income: ₹" + totalIncome + ", Pending: ₹" + totalPending +
                    ", Penalties: ₹" + totalPenalties + ", Guests Checked-in: " + stillCheckedIn.size() +
                    ", Future Bookings: " + futureBookings.size());

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Helper method for counting rooms
    private static int getCount(String query, String roomType) throws SQLException {
        PreparedStatement ps = DBHandler.con.prepareStatement(query);
        ps.setString(1, roomType);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }


}


// Node for LinkedList
class CustomerNodeLL {
    int id;
    String name, roomType, roomNumber;
    int charges;
    CustomerNodeLL next;

    CustomerNodeLL(int id, String name, String roomType, int charges, String roomNumber) {
        this.id = id;
        this.name = name;
        this.roomType = roomType;
        this.charges = charges;
        this.roomNumber = roomNumber;
        this.next = null;
    }
}

// Custom LinkedList
class CustomerLinkedList {
    CustomerNodeLL head;

    // Insert at end
    void insert(int id, String name, String roomType, int charges, String roomNumber) {
        CustomerNodeLL newNode = new CustomerNodeLL(id, name, roomType, charges, roomNumber);
        if (head == null) {
            head = newNode;
            return;
        }
        CustomerNodeLL temp = head;
        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = newNode;
    }

    // Display all
    void display() {
        if (head == null) {
            System.out.println("No  customers at the moment.");
            return;
        }
        CustomerNodeLL temp = head;
        while (temp != null) {
            System.out.println(temp.id + " | " + temp.name + " | " + temp.roomType +
                    " | ₹" + temp.charges + " | Room No: " + temp.roomNumber);
            temp = temp.next;
        }
    }
}




class SimpleBST {
    CustomerNode root;

    // Insert a customer into the BST
    public void insert(int id, String name, String username, int charges, String rooms) {
        root = insertRec(root, id, name, username, charges, rooms);
    }

    private CustomerNode insertRec(CustomerNode node, int id, String name, String username, int charges, String rooms) {
        if (node == null) {
            return new CustomerNode(id, name, username, charges, rooms);
        }
        if (username.compareToIgnoreCase(node.username) < 0) {
            node.left = insertRec(node.left, id, name, username, charges, rooms);
        } else {
            node.right = insertRec(node.right, id, name, username, charges, rooms);
        }
        return node;
    }

    // Search a customer by username
    public CustomerNode search(String username) {
        return searchRec(root, username);
    }

    private CustomerNode searchRec(CustomerNode node, String username) {
        if (node == null) return null;
        int cmp = username.compareToIgnoreCase(node.username);
        if (cmp == 0) return node;
        else if (cmp < 0) return searchRec(node.left, username);
        else return searchRec(node.right, username);
    }

    // Inorder traversal to display all customers
    public void inorder() {
        inorderRec(root);
    }

    private void inorderRec(CustomerNode node) {
        if (node != null) {
            inorderRec(node.left);
            System.out.println("ID: " + node.id
                    + " | Name: " + node.name
                    + " | Username: " + node.username
                    + " | Charges: ₹" + node.charges
                    + " | Rooms: " + node.rooms);
            inorderRec(node.right);
        }
    }
}

// Node class for customers
class CustomerNode {
    int id, charges;
    String name, username, rooms;   // added username
    CustomerNode left, right;

    CustomerNode(int id, String name, String username, int charges, String rooms) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.charges = charges;
        this.rooms = rooms;
        left = right = null;
    }
}

// ================== FRONT DESK CLASS =================
class FrontDesk {
    static Scanner sc = new Scanner(System.in);

    public static void loginPanel() {
        System.out.print("Front Desk Username: "); String user = sc.nextLine();
        System.out.print("Password: "); String pass = sc.nextLine();
        try {
            PreparedStatement ps = DBHandler.con.prepareStatement(
                    "SELECT * FROM frontdesk WHERE username=? AND password=?"
            );
            ps.setString(1,user); ps.setString(2,pass);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) { System.out.println("Welcome Front Desk!"); menu(); }
            else System.out.println("Invalid credentials!");
        } catch(Exception e){ System.out.println(e.getMessage()); }
    }

    static void menu() {
        while(true) {
            System.out.println("\n--- FRONT DESK MENU ---");
            System.out.println("1. Book Room for Customer");
            System.out.println("2. Cancel Room For Advanced Booking");
            System.out.println("3. Check-in Customer For Advance Booking ");
            System.out.println("4. Check-out Customer");
            System.out.println("5. Back");
            System.out.print("Choice: "); int ch=sc.nextInt(); sc.nextLine();
            switch(ch) {
                case 1: bookRoom(); break;
                case 2: cancelBooking(); break;
                case 3: checkInCustomer(); break;
                case 4: checkout(); break;
                case 5: return;
                default: System.out.println("Invalid!");
            }
        }
    }

    static public void bookRoom() {
        try {
            System.out.println("Do you want to:");
            System.out.println("1. Check-in Now");
            System.out.println("2. Advance Booking");
            int option;
            while (true) {
                if (sc.hasNextInt()) {
                    option = sc.nextInt();
                    sc.nextLine();
                    if (option == 1 || option == 2) break;
                }
                System.out.print("❌ Invalid choice! Enter 1 or 2: ");
                sc.nextLine();
            }
            boolean isAdvance = option == 2;

            ArrayList<String> selectedRooms = new ArrayList<>();
            ArrayList<String> selectedTypes = new ArrayList<>();

            // --- Customer details ---
            String name;
            while (true) {
                System.out.print("Customer Name: ");
                name = sc.nextLine().trim();
                if (name.matches("[a-zA-Z ]+")) break;
                System.out.println("❌ Invalid name! Only alphabets allowed. Try again.");
            }

            String uname;
            while (true) {
                System.out.print("Username: ");
                uname = sc.nextLine().trim();
                if (!uname.matches("[a-zA-Z0-9_]+")) {
                    System.out.println("❌ Invalid username! Use only letters, digits, or underscore.");
                    continue;
                }
                PreparedStatement psCheck = DBHandler.con.prepareStatement(
                        "SELECT COUNT(*) FROM customers WHERE username=?");
                psCheck.setString(1, uname);
                ResultSet rsCheck = psCheck.executeQuery();
                rsCheck.next();
                if (rsCheck.getInt(1) > 0) {
                    System.out.println("❌ Username already exists! Try another.");
                } else break;
            }

            int age;
            while (true) {
                System.out.print("Age: ");
                if (sc.hasNextInt()) {
                    age = sc.nextInt();
                    sc.nextLine();
                    if (age >= 18 && age <= 100) break;
                    else System.out.println("❌ Age must be between 18 and 100.");
                } else {
                    System.out.println("❌ Invalid age! Enter a number.");
                    sc.nextLine();
                }
            }

            String mobile;
            while (true) {
                System.out.print("Mobile (10 digits): ");
                mobile = sc.nextLine().trim();
                if (mobile.matches("\\d{10}")) break;
                System.out.println("❌ Invalid mobile! Try again.");
            }

            // Aadhaar proof
            File aadharFile = null;
            FileInputStream fis = null;
            while (true) {
                System.out.print("Enter Aadhaar card image file path: ");
                String aadharPath = sc.nextLine().trim();
                aadharFile = new File(aadharPath);
                try {
                    fis = new FileInputStream(aadharFile);
                    break;
                } catch (Exception e) {
                    System.out.println("❌ Invalid file path! Try again.");
                }
            }

            // Guests
            int adults, children;
            while (true) {
                System.out.print("Adults: ");
                adults = sc.nextInt();
                System.out.print("Children: ");
                children = sc.nextInt();
                sc.nextLine();
                if (adults <= 0 && children > 0) {
                    System.out.println("❌ Children cannot stay without at least one adult. Try again.");
                } else {
                    break;
                }
            }
            int totalGuests = adults + children;
            int requiredRooms = (int) Math.ceil(totalGuests / 4.0);

            // Check hotel room capacity
            Statement stCap = DBHandler.con.createStatement();
            ResultSet rsCap = stCap.executeQuery("SELECT COUNT(*) FROM rooms");
            rsCap.next();
            int totalRoomsInHotel = rsCap.getInt(1);
            if (requiredRooms > totalRoomsInHotel) {
                System.out.println("❌ Not enough rooms available for " + totalGuests + " guests.");
                return;
            }
            Timestamp checkInTime = null;
            Timestamp checkOutTime = null;

            // --- Dates ---
          //  java.sql.Date checkInDate = null, checkOutDate = null;
            if (isAdvance) {
                while (checkInTime == null) {
                    try {
                        System.out.print("Planned Check-In Time (yyyy-mm-dd): ");
                        checkInTime = java.sql.Timestamp.valueOf(String.valueOf(LocalTime.parse(sc.nextLine().trim())));
                        if (!checkInTime.after(new java.sql.Date(System.currentTimeMillis()))) {
                            System.out.println("❌ Must be after today!");
                            checkInTime = null;
                        }
                    } catch (Exception e) {
                        System.out.println("❌ Invalid date! Format yyyy-mm-dd.");
                    }
                }
            } else {
                checkInTime = new Timestamp(System.currentTimeMillis());
                System.out.println("✔ Check-In Time set to Today: " + checkInTime);
            }
            while (checkOutTime == null) {
                try {
                    System.out.print("Planned Check-Out Date (yyyy-mm-dd): ");
                    checkOutTime = java.sql.Timestamp.valueOf(sc.nextLine().trim());
                    if (!checkOutTime.after(checkInTime)) {
                        System.out.println("❌ Must be after check-in!");
                        checkOutTime = null;
                    } else if (ChronoUnit.DAYS.between(checkInTime.toLocalDateTime(), checkOutTime.toLocalDateTime()) > 30) {
                        System.out.println("❌ Max stay = 30 days!");
                        checkOutTime = null;
                    }
                } catch (Exception e) {
                    System.out.println("❌ Invalid date! Format yyyy-mm-dd.");
                }
            }

            // --- Room selection ---
            for (int i = 0; i < requiredRooms; i++) {
                boolean roomAssigned = false;
                while (!roomAssigned) {
                    System.out.println("\nAvailable Rooms:");
                    String[] types = {"Super", "Premium", "Deluxe"};
                    for (String type : types) {
                        PreparedStatement psAvail;
                        if (isAdvance) {
                            psAvail = DBHandler.con.prepareStatement(
                                    "SELECT room_number FROM rooms WHERE room_type=? AND room_number NOT IN (" +
                                            " SELECT room_number FROM customers " +
                                            " WHERE (? < planned_checkout_Time AND ? > planned_checkin_Time))");
                            psAvail.setString(1, type);
                            psAvail.setTimestamp(2, checkOutTime);
                            psAvail.setTimestamp(3, checkInTime);
                        } else {
                            psAvail = DBHandler.con.prepareStatement(
                                    "SELECT room_number FROM rooms WHERE room_type=? AND is_booked=0");
                            psAvail.setString(1, type);
                        }
                        ResultSet rs = psAvail.executeQuery();
                        ArrayList<String> available = new ArrayList<>();
                        while (rs.next()) available.add(rs.getString("room_number"));
                        System.out.println(type + ": " + available);
                    }

                    System.out.print("Enter room type (Super/Premium/Deluxe): ");
                    String chosenType = sc.nextLine().trim();
                    if (!(chosenType.equalsIgnoreCase("Super") ||
                            chosenType.equalsIgnoreCase("Premium") ||
                            chosenType.equalsIgnoreCase("Deluxe"))) {
                        System.out.println("❌ Invalid type! Try again.");
                        continue;
                    }

                    System.out.print("Enter room number: ");
                    String chosenRoom = sc.nextLine().trim();

                    PreparedStatement psRoomCheck;
                    if (isAdvance) {
                        psRoomCheck = DBHandler.con.prepareStatement(
                                "SELECT room_number FROM rooms WHERE room_number=? AND room_type=? AND room_number NOT IN (" +
                                        " SELECT room_number FROM customers " +
                                        " WHERE (? < planned_checkout_date AND ? > planned_checkin_date))");
                        psRoomCheck.setString(1, chosenRoom);
                        psRoomCheck.setString(2, chosenType);
                        psRoomCheck.setTimestamp(3, checkOutTime);
                        psRoomCheck.setTimestamp(4, checkInTime);
                    } else {
                        psRoomCheck = DBHandler.con.prepareStatement(
                                "SELECT room_number FROM rooms WHERE room_number=? AND room_type=? AND is_booked=0");
                        psRoomCheck.setString(1, chosenRoom);
                        psRoomCheck.setString(2, chosenType);
                    }
                    ResultSet rsCheckRoom = psRoomCheck.executeQuery();
                    if (rsCheckRoom.next()) {
                        if (!isAdvance) {
                            PreparedStatement reserveRoom = DBHandler.con.prepareStatement(
                                    "UPDATE rooms SET is_booked=1 WHERE room_number=?");
                            reserveRoom.setString(1, chosenRoom);
                            reserveRoom.executeUpdate();
                        }
                        selectedRooms.add(chosenRoom);
                        selectedTypes.add(chosenType);
                        roomAssigned = true;
                    } else {
                        System.out.println("❌ Room not available! Try again.");
                    }
                }
            }

            // --- Calculate charges ---
            long Timestay;
            Timestay = ChronoUnit.HOURS.between(checkInTime.toLocalDateTime(), checkOutTime.toLocalDateTime());
            int totalCharges = 0;
            for (String type : selectedTypes) totalCharges += getRoomRate(type) * (int) Timestay;

            // --- Save booking ---
            PreparedStatement psCust = DBHandler.con.prepareStatement(
                    "INSERT INTO customers (username,name,age,mobile,room_type,adults,children,room_count,charges,room_number,proof_image,checkin_date,planned_checkin_date,planned_checkout_date,is_checkedin) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            psCust.setString(1, uname);
            psCust.setString(2, name);
            psCust.setInt(3, age);
            psCust.setString(4, mobile);
            psCust.setString(5, String.join(",", selectedTypes));
            psCust.setInt(6, adults);
            psCust.setInt(7, children);
            psCust.setInt(8, requiredRooms);
            psCust.setInt(9, totalCharges);
            psCust.setString(10, String.join(",", selectedRooms));
            psCust.setBinaryStream(11, fis, (int) aadharFile.length());
            if (isAdvance) {
                psCust.setNull(12, java.sql.Types.DATE);
                psCust.setTimestamp(13, checkInTime);
                psCust.setTimestamp(14, checkOutTime);
                psCust.setBoolean(15, false);
            } else {
                psCust.setTimestamp(12, checkInTime);
                psCust.setTimestamp(13, checkInTime);
                psCust.setTimestamp(14, checkOutTime);
                psCust.setBoolean(15, true);
            }
            psCust.executeUpdate();
            fis.close();

            System.out.println("\n✅ Booking Successful!");
            System.out.println("Guests: " + totalGuests + " (Adults: " + adults + ", Children: " + children + ")");
            System.out.println("Rooms: " + selectedRooms);
            System.out.println("Types: " + selectedTypes);
            System.out.println("Check-In: " + checkInTime);
            System.out.println("Check-Out: " + checkOutTime);
            System.out.println("Total Charges (for " + Timestay + " hours): ₹" + totalCharges);

        } catch (Exception e) {
            System.out.println("⚠ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }





    public static void updateRoomStatus(int roomNo, int bookFlag) {
        try {
            CallableStatement cs = DBHandler.con.prepareCall("{call updateRoomStatus(?, ?)}");
            cs.setInt(1, roomNo);
            cs.setInt(2, bookFlag);
            cs.execute();
            cs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static void checkout() {
        try {
            System.out.print("Enter Customer ID to check-out: ");
            int id = sc.nextInt(); sc.nextLine();

            PreparedStatement ps = DBHandler.con.prepareStatement(
                    "SELECT status FROM customers WHERE customer_id=?"
            );
            ps.setInt(1, id);
            ResultSet rst = ps.executeQuery();
            if (rst.next()) {
                String status = rst.getString("status");
                if ("CANCELLED".equalsIgnoreCase(status)) {
                    System.out.println("❌ This booking is cancelled. Cannot proceed.");
                    return;
                }
            }




            PreparedStatement getCustomer = DBHandler.con.prepareStatement(
                    "SELECT name, room_number, room_type, charges, payment_status, payment_method, " +
                            "checkin_date, planned_checkin_date, planned_checkout_date, is_checkedin " +
                            "FROM customers WHERE customer_id=?"
            );
            getCustomer.setInt(1, id);
            ResultSet rs = getCustomer.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String roomTypesStr = rs.getString("room_type");
                String[] roomTypes = roomTypesStr.split(",");
                String roomNumbersStr = rs.getString("room_number");
                String[] roomNumbers = roomNumbersStr.replaceAll("[\\[\\] ]", "").split(",");
                int originalCharges = rs.getInt("charges");
                String paymentStatus = rs.getString("payment_status");
                String paymentMethod = rs.getString("payment_method");
                java.sql.Date checkinDate = rs.getDate("checkin_date");
                java.sql.Date plannedCheckinDate = rs.getDate("planned_checkin_date");
                java.sql.Date plannedCheckoutDate = rs.getDate("planned_checkout_date");
                boolean isCheckedIn = rs.getBoolean("is_checkedin");

                // --- If never checked in ---
                if (!isCheckedIn || checkinDate == null) {
                    System.out.println("❌ This customer has an advance booking and has not checked in yet. Checkout not allowed.");
                    return;
                }

                // --- Calculate stay days ---
                java.sql.Date actualCheckoutDate = new java.sql.Date(System.currentTimeMillis());
                long diff = actualCheckoutDate.getTime() - checkinDate.getTime();
                int actualDays = (int) Math.ceil(diff / (1000.0 * 60 * 60 * 24));
                int totalCharges = 0;
                for (String type : roomTypes) {
                    totalCharges += getRoomRate(type) * actualDays;
                }

                // --- Late checkout fee ---
                if (actualCheckoutDate.after(plannedCheckoutDate)) {
                    long lateDays = (actualCheckoutDate.getTime() - plannedCheckoutDate.getTime()) / (1000 * 60 * 60 * 24);
                    lateDays = Math.max(lateDays, 1); // at least 1 day if late
                    int lateFee = 0;
                    for (String type : roomTypes) {
                        lateFee += getRoomRate(type) * lateDays;
                    }
                    totalCharges += lateFee;
                    System.out.println("⚠️ Late checkout! Additional charges: ₹" + lateFee);
                }

                // --- Payment ---
                if (!"Done".equalsIgnoreCase(paymentStatus)) {
                    System.out.println("Total bill: ₹" + totalCharges);
                    System.out.println("Select Payment Method: ");
                    System.out.println("1. Cash");
                    System.out.println("2. Card");
                    System.out.println("3. UPI");
                    int choice = sc.nextInt(); sc.nextLine();
                    switch(choice) {
                        case 1: paymentMethod="Cash"; break;
                        case 2: paymentMethod="Card"; break;
                        case 3: paymentMethod="UPI"; break;
                        default: paymentMethod="Unknown"; break;
                    }

                    PreparedStatement updatePayment = DBHandler.con.prepareStatement(
                            "UPDATE customers SET payment_status='Done', payment_method=?, charges=? WHERE customer_id=?"
                    );
                    updatePayment.setString(1, paymentMethod);
                    updatePayment.setInt(2, totalCharges);
                    updatePayment.setInt(3, id);
                    updatePayment.executeUpdate();
                    System.out.println("✅ Payment done successfully via " + paymentMethod);
                } else {
                    // If already paid, just update charges if late fee applied
                    PreparedStatement updateCharges = DBHandler.con.prepareStatement(
                            "UPDATE customers SET charges=? WHERE customer_id=?"
                    );
                    updateCharges.setInt(1, totalCharges);
                    updateCharges.setInt(2, id);
                    updateCharges.executeUpdate();
                }

                // --- Update checkout time ---
                PreparedStatement updateCheckout = DBHandler.con.prepareStatement(
                        "UPDATE customers SET checkout_time=CURRENT_TIMESTAMP WHERE customer_id=?"
                );
                updateCheckout.setInt(1, id);
                updateCheckout.executeUpdate();

                // --- Free allocated rooms ---
                for (String roomNum : roomNumbers) {
                    PreparedStatement freeRoom = DBHandler.con.prepareStatement(
                            "UPDATE rooms SET is_booked=FALSE WHERE room_number=?"
                    );
                    freeRoom.setInt(1, Integer.parseInt(roomNum));
                    freeRoom.executeUpdate();
                }

                // --- Build invoice ---
                String invoice =
                        "\n============================================\n" +
                                "         🏨 WELCOME TO GRAND HOTEL 🏨\n" +
                                "============================================\n" +
                                "Invoice for : " + name.toUpperCase() + "\n" +
                                "--------------------------------------------\n" +
                                "Room Type(s)   : " + roomTypesStr + "\n" +
                                "Room Number(s) : " + roomNumbersStr + "\n" +
                                "Check-in Date  : " + checkinDate + "\n" +
                                "Check-out Date : " + actualCheckoutDate + "\n" +
                                "Total Days     : " + actualDays + "\n" +
                                "Total Charges  : ₹" + totalCharges + "\n" +
                                "Payment Status : PAID (" + paymentMethod + ")\n" +
                                "--------------------------------------------\n" +
                                "   ✅ Thank you " + name + " for staying with us!\n" +
                                "============================================\n";

                // --- Save invoice ---
                File f = new File("Invoice_" + id + ".txt");
                FileWriter fw = new FileWriter(f);
                fw.write(invoice);
                fw.flush();
                fw.close();

                System.out.println(invoice);
                System.out.println("📄 Invoice saved as file: " + f.getName());

            } else {
                System.out.println("❌ Customer not found.");
            }

        } catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static void checkInCustomer() {
        try {
            System.out.print("Enter Customer ID to check-in: ");
            int id = sc.nextInt(); sc.nextLine();

            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

            PreparedStatement ps = DBHandler.con.prepareStatement(
                    "SELECT status, is_checkedin, planned_checkin_date, room_number " +
                            "FROM customers WHERE customer_id=?"
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                boolean alreadyCheckedIn = rs.getBoolean("is_checkedin");
                java.sql.Date plannedDate = rs.getDate("planned_checkin_date");
                String roomNumbers = rs.getString("room_number");

                // 1. Cancelled check
                if ("CANCELLED".equalsIgnoreCase(status)) {
                    System.out.println("❌ This booking was cancelled. Check-in not allowed.");
                    return;
                }

                // 2. Already checked-in check
                if (alreadyCheckedIn) {
                    System.out.println("⚠️ Customer already checked in.");
                    return;
                }

                // 3. Early check-in
                if (today.before(plannedDate)) {
                    System.out.println("⚠️ Early check-in attempt. Checking room availability...");

                    String[] rooms = roomNumbers.replace("[","").replace("]","").split(",\\s*");
                    boolean allAvailable = true;

                    for (String roomNum : rooms) {
                        PreparedStatement chkRoom = DBHandler.con.prepareStatement(
                                "SELECT is_booked FROM rooms WHERE room_number=?"
                        );
                        chkRoom.setInt(1, Integer.parseInt(roomNum.trim()));
                        ResultSet rsRoom = chkRoom.executeQuery();
                        if (rsRoom.next() && rsRoom.getBoolean("is_booked")) {
                            allAvailable = false;
                            break;
                        }
                    }

                    if (!allAvailable) {
                        System.out.println("❌ Rooms not available for early check-in. Please come on planned date: " + plannedDate);
                        return;
                    }

                    System.out.println("✅ Rooms are free. Proceeding with early check-in...");
                }

                // ✅ Perform check-in
                PreparedStatement update = DBHandler.con.prepareStatement(
                        "UPDATE customers SET checkin_date=?, is_checkedin=TRUE, status='CHECKEDIN' WHERE customer_id=?"
                );
                update.setDate(1, today);
                update.setInt(2, id);
                update.executeUpdate();

                // ✅ Mark rooms as occupied
                String[] rooms = roomNumbers.replace("[","").replace("]","").split(",\\s*");
                for (String roomNum : rooms) {
                    PreparedStatement updRoom = DBHandler.con.prepareStatement(
                            "UPDATE rooms SET is_booked=TRUE WHERE room_number=?"
                    );
                    updRoom.setInt(1, Integer.parseInt(roomNum.trim()));
                    updRoom.executeUpdate();
                }

                System.out.println("✅ Customer checked-in successfully on: " + today);

            } else {
                System.out.println("❌ Customer not found!");
            }
        } catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static void cancelBooking() {
        System.out.print("Enter Customer ID to cancel booking: ");
        int id = sc.nextInt();
        sc.nextLine();

        try {
            PreparedStatement ps = DBHandler.con.prepareStatement(
                    "SELECT planned_checkin_date, is_checkedin, room_number, charges FROM customers WHERE customer_id=?"
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Invalid Customer ID!");
                return;
            }

            String checkinDateStr = rs.getString("planned_checkin_date");
            boolean checkedIn = rs.getBoolean("is_checkedin");
            String roomNumbers = rs.getString("room_number"); // e.g., "[301,101]"
            int charges = rs.getInt("charges");

            if (checkedIn) {
                System.out.println("⚠️ Cannot cancel: Customer has already checked in!");
                return;
            }

            // Clean room numbers
            String[] rooms = roomNumbers.replace("[","").replace("]","").split(",\\s*");

            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
            java.sql.Date bookingDate = java.sql.Date.valueOf(checkinDateStr);

            int penalty = 0;

            // Apply penalty if cancelled before check-in date
            if (today.before(bookingDate)) {
                penalty = 500 * rooms.length;
                charges = penalty; // For cancellation, we only keep penalty as "charges"
                System.out.println("💰 Cancellation penalty applied: ₹" + penalty);
            } else {
                charges = 0; // No penalty if cancelling after or on check-in date
            }

            // Release rooms back
            for (String r : rooms) {
                PreparedStatement psUpdateRoom = DBHandler.con.prepareStatement(
                        "UPDATE rooms SET is_booked=FALSE WHERE room_number=?"
                );
                psUpdateRoom.setInt(1, Integer.parseInt(r.trim()));
                psUpdateRoom.executeUpdate();
            }

            // Mark booking as cancelled (charges = penalty)
            PreparedStatement psCancel = DBHandler.con.prepareStatement(
                    "UPDATE customers SET checkout_time=CURRENT_TIMESTAMP, charges=?, status='CANCELLED' WHERE customer_id=?"
            );
            psCancel.setInt(1, charges);
            psCancel.setInt(2, id);
            psCancel.executeUpdate();

            System.out.println("✅ Booking cancelled successfully!");
            System.out.println("➡️ Penalty Charged: ₹" + charges);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static int getRoomRate(String roomType) {
        switch(roomType.toLowerCase()) {
            case "deluxe": return 2000;
            case "premium": return 4000;
            case "super": return 6000;
            default: return 0;
        }
    }

    static int getAvailableRooms(String roomType) throws SQLException {
        PreparedStatement ps = DBHandler.con.prepareStatement(
                "SELECT SUM(room_count) AS booked FROM customers WHERE room_type=? AND checkout_time IS NULL"
        );
        ps.setString(1, roomType);
        ResultSet rs = ps.executeQuery();
        int booked = 0;
        if(rs.next()) booked = rs.getInt("booked");

        int totalRooms = 10; // Example total per room type
        return totalRooms - booked;
    }


}

// ================== EMPLOYEE CLASS =================
class Employee {
    static Scanner sc = new Scanner(System.in);

    public static void loginPanel() {
        System.out.print("Employee Username: "); String user = sc.nextLine();
        System.out.print("Password: "); String pass = sc.nextLine();
        try {
            PreparedStatement ps = DBHandler.con.prepareStatement(
                    "SELECT * FROM employee WHERE username=? AND password=?"
            );
            ps.setString(1,user); ps.setString(2,pass);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) { System.out.println("Welcome Employee!"); menu(rs.getInt("employee_id")); }
            else System.out.println("Invalid credentials!");
        } catch(Exception e){ System.out.println(e.getMessage()); }
    }

    static void menu(int empId) {
        while(true) {
            System.out.println("\n--- EMPLOYEE MENU ---");
            System.out.println("1. View My Tasks");
            System.out.println("2. Mark Task Done");
            System.out.println("3. Back");
            System.out.print("Choice: "); int ch=sc.nextInt(); sc.nextLine();
            switch(ch) {
                case 1: viewTasks(empId); break;
                case 2: markTaskDone(empId); break;
                case 3: return;
                default: System.out.println("Invalid!");
            }
        }
    }

    static void viewTasks(int empId) {
        try {
            Statement st = DBHandler.con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM tasks WHERE employee_id=" + empId);

            LinkedList<Task> taskList = new LinkedList<>();
            while (rs.next()) {
                taskList.add(new Task(
                        rs.getInt("task_id"),
                        rs.getString("task"),
                        rs.getString("status")
                ));
            }

            if (taskList.isEmpty()) {
                System.out.println("No tasks assigned to you yet.");
            } else {
                System.out.println("--- My Tasks ---");
                for (Task t : taskList) {
                    System.out.println(t.id + " | " + t.task + " | " + t.status);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void markTaskDone(int empId) {
        System.out.print("Enter Task ID to mark done: ");
        int tId = sc.nextInt(); sc.nextLine();

        try {
            // 1. Check if task exists and belongs to the employee
            PreparedStatement checkPs = DBHandler.con.prepareStatement(
                    "SELECT status FROM tasks WHERE task_id=? AND employee_id=?"
            );
            checkPs.setInt(1, tId);
            checkPs.setInt(2, empId);
            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ No such task found for you.");
                return;
            }

            String status = rs.getString("status");
            if ("Done".equalsIgnoreCase(status)) {
                System.out.println("⚠️ Task is already completed!");
                return;
            }

            // 2. Update status to Done
            PreparedStatement ps = DBHandler.con.prepareStatement(
                    "UPDATE tasks SET status='Done' WHERE task_id=? AND employee_id=?"
            );
            ps.setInt(1, tId);
            ps.setInt(2, empId);

            int updated = ps.executeUpdate();
            if (updated > 0) {
                System.out.println("✅ Task marked as Done!");
            } else {
                System.out.println("❌ Failed to update task.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}

class Task {
    int id; String task,status;
    Task(int id,String task,String status){this.id=id; this.task=task; this.status=status;}
}

// ================== CUSTOMER CLASS =================
class Customer {
    static Scanner sc = new Scanner(System.in);

    public static void menu() {
        while(true) {
            System.out.println("\n--- CUSTOMER MENU ---");
            System.out.println("1. Login To Check Room Status And Bill Payement ");
            System.out.println("2. Back");
            System.out.print("Choice: "); int ch=sc.nextInt(); sc.nextLine();
            switch(ch) {
                case 1:  login(); break;
                case 2: return;
                default: System.out.println("Invalid!");
            }
        }
    }



    static void login() {
        System.out.print("Username: "); String uname = sc.nextLine();
        try {
            PreparedStatement ps = DBHandler.con.prepareStatement("SELECT * FROM customers WHERE username=?");
            ps.setString(1, uname);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                System.out.println("Welcome "+rs.getString("name")+"!");
                viewBookingStatus(rs.getInt("customer_id"));
            } else System.out.println("Username not found!");
        } catch(Exception e){ System.out.println(e.getMessage()); }
    }

    static void viewBookingStatus(int custId) {
        try {
            PreparedStatement ps = DBHandler.con.prepareStatement(
                    "SELECT * FROM customers WHERE customer_id=?"
            );
            ps.setInt(1, custId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ No booking found for Customer ID: " + custId);
                return;
            }

            System.out.println("\n===== Booking Details =====");
            System.out.println("Customer Name: " + rs.getString("name"));
            System.out.println("Username: " + rs.getString("username"));
            System.out.println("Room Type: " + rs.getString("room_type"));
            System.out.println("Adults: " + rs.getInt("adults") + " | Children: " + rs.getInt("children"));
            System.out.println("Room Count: " + rs.getInt("room_count"));
            System.out.println("Room Numbers: " + rs.getString("room_number"));
            System.out.println("Charges: ₹" + rs.getInt("charges"));
            System.out.println("Payment Status: " + rs.getString("payment_status"));
            System.out.println("Planned Check-in: " + rs.getDate("planned_checkin_date")
                    + " | Planned Check-out: " + rs.getDate("planned_checkout_date"));
            System.out.println("Actual Check-in: " + rs.getTimestamp("checkin_time")
                    + " | Actual Check-out: " + rs.getTimestamp("checkout_time"));
            System.out.println("Booking Status: " + rs.getString("status"));
            System.out.println("============================\n");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}


//CREATE OR REPLACE FUNCTION check_room_availability()
//RETURNS TRIGGER
//AS $$
//BEGIN
//    IF NEW.is_booked = 1 AND OLD.is_booked = 1 THEN
//        RAISE EXCEPTION 'Room is already booked!';
//    END IF;
//    RETURN NEW;
//END;
//$$ LANGUAGE plpgsql;
//
//
//CREATE TRIGGER trg_check_room_booking
//BEFORE UPDATE ON rooms
//FOR EACH ROW
//EXECUTE FUNCTION check_room_availability();
