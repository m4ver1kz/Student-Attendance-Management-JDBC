import java.sql.*;
import java.util.Scanner;

public class AttendanceManagement {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

            Connection con = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521/XE",
                "system",
                "admin123"
            );
            con.setAutoCommit(false);

            int choice;

            do {
                System.out.println("\n--- STUDENT ATTENDANCE MANAGEMENT ---");
                System.out.println("1. Mark Attendance");
                System.out.println("2. Display Student Attendance");
                System.out.println("3. Calculate Attendance Percentage");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");

                choice = sc.nextInt();

                switch (choice) {

                    case 1:
                        System.out.print("Enter Student ID: ");
                        int id = sc.nextInt();

                        sc.nextLine();

                        System.out.print("Enter Date (yyyy-mm-dd): ");
                        String date = sc.nextLine();

                        System.out.print("Enter Status (Present/Absent): ");
                        String status = sc.nextLine();

                        String sql = "INSERT INTO ATTENDANCE " +
                            "(STUDENT_ID, ATTENDANCE_DATE, STATUS) " +
                            "VALUES (?, TO_DATE(?, 'YYYY-MM-DD'), ?)";

                        PreparedStatement ps = con.prepareStatement(sql);

                        ps.setInt(1, id);
                        ps.setString(2, date);
                        ps.setString(3, status);

                        int rows = ps.executeUpdate();

                        if (rows > 0) {
                            con.commit();
                            System.out.println("Attendance marked successfully!");
                        }

                        ps.close();
                        break;

                    case 2:
                        System.out.print("Enter Student ID: ");
                        id = sc.nextInt();

                        sql = "SELECT STUDENT_ID, " +
                            "TO_CHAR(ATTENDANCE_DATE, 'YYYY-MM-DD') AS ATT_DATE, " +
                            "STATUS FROM ATTENDANCE " +
                            "WHERE STUDENT_ID = ? ORDER BY ATTENDANCE_DATE";

                        ps = con.prepareStatement(sql);
                        ps.setInt(1, id);

                        ResultSet rs = ps.executeQuery();

                        System.out.println("\nDate\t\tStatus");

                        while (rs.next()) {
                            System.out.println(
                                rs.getString("ATT_DATE") + "\t" +
                                rs.getString("STATUS")
                            );
                        }

                        rs.close();
                        ps.close();
                        break;

                    case 3:
                        System.out.print("Enter Student ID: ");
                        id = sc.nextInt();

                        sql = "SELECT COUNT(*) AS TOTAL, " +
                            "SUM(CASE WHEN STATUS = 'Present' THEN 1 ELSE 0 END) " +
                            "AS PRESENT_COUNT FROM ATTENDANCE WHERE STUDENT_ID = ?";

                        ps = con.prepareStatement(sql);
                        ps.setInt(1, id);

                        rs = ps.executeQuery();

                        if (rs.next()) {
                            int total = rs.getInt("TOTAL");
                            int present = rs.getInt("PRESENT_COUNT");

                            if (total > 0) {
                                double percentage = (present * 100.0) / total;

                                System.out.println("Total Classes: " + total);
                                System.out.println("Classes Attended: " + present);
                                System.out.printf(
                                    "Attendance Percentage: %.2f%%\n",
                                    percentage
                                );
                            } else {
                                System.out.println("No attendance records found.");
                            }
                        }

                        rs.close();
                        ps.close();
                        break;

                    case 4:
                        System.out.println("Exiting program...");
                        break;

                    default:
                        System.out.println("Invalid choice!");
                }

            } while (choice != 4);

            con.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        sc.close();
    }
}
