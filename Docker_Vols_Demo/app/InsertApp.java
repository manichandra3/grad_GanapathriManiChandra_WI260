import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class InsertApp {

    public static void main(String[] args) {

        String host = env("DB_HOST", "db");
        String port = env("DB_PORT", "5432");
        String dbName = env("DB_NAME", "appdb");
        String user = env("DB_USER", "appuser");
        String password = env("DB_PASSWORD", "apppass");

        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;

        Connection conn = null;

        // Retry loop – the DB container may still be starting up
        for (int attempt = 1; attempt <= 10; attempt++) {
            try {
                conn = DriverManager.getConnection(url, user, password);
                System.out.println("Connected to PostgreSQL successfully!");
                break;
            } catch (Exception e) {
                System.out.println("Waiting for database... (attempt " + attempt + "/10)");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
            }
        }

        if (conn == null) {
            System.err.println("Could not connect to the database. Exiting.");
            System.exit(1);
        }

        try {
            // 1. Create table if it doesn't exist
            Statement stmt = conn.createStatement();
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS employees ("
                            + "  id SERIAL PRIMARY KEY,"
                            + "  name VARCHAR(100) NOT NULL,"
                            + "  department VARCHAR(100),"
                            + "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                            + ")");
            System.out.println("Table 'employees' is ready.");

            // 2. Insert a record
            String insertSQL = "INSERT INTO employees (name, department) VALUES (?, ?) RETURNING id";
            PreparedStatement pstmt = conn.prepareStatement(insertSQL);
            pstmt.setString(1, "Alice Johnson");
            pstmt.setString(2, "Engineering");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("Inserted record with ID: " + rs.getInt("id"));
            }

            // 3. Read back all records
            rs = stmt.executeQuery("SELECT id, name, department, created_at FROM employees ORDER BY id");
            System.out.println("\n--- All Records in 'employees' table ---");
            while (rs.next()) {
                System.out.printf("  ID: %d | Name: %-20s | Dept: %-15s | Created: %s%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getTimestamp("created_at"));
            }
            System.out.println("-----------------------------------------");

            rs.close();
            pstmt.close();
            stmt.close();
            conn.close();

            System.out.println("Application finished successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static String env(String key, String fallback) {
        String val = System.getenv(key);
        return (val != null && !val.isEmpty()) ? val : fallback;
    }
}
