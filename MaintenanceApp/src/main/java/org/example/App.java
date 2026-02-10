package org.example;

import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

enum SiteType {
    VILLA, OPEN_SITE, INDEPENDENT_HOUSE, APARTMENT;

    public static SiteType fromString(String val) {
        try {
            return SiteType.valueOf(val.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

enum SiteStatus {
    OPEN, OCCUPIED;

    public static SiteStatus fromString(String val) {
        try {
            return SiteStatus.valueOf(val.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

public class App {
    private static Document currentUser = null;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("LMA");

            MongoCollection<Document> sitesCol = database.getCollection("sites");
            MongoCollection<Document> usersCol = database.getCollection("users");
            MongoCollection<Document> maintCol = database.getCollection("maintenance_records");
            MongoCollection<Document> requestsCol = database.getCollection("change_requests");

            while (true) {
                printMenu();
                int option = readInt("Enter Option: ");

                switch (option) {
                    case 0 -> handleLogin(usersCol);
                    case 1 -> viewDashboard(sitesCol);
                    case 2 -> handleMaintenance(maintCol, sitesCol);
                    case 3 -> handleRequests(requestsCol, sitesCol);
                    case 4 -> manageUsersAndSites(usersCol, sitesCol, maintCol, requestsCol);
                    case 5 -> {
                        System.out.println("Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            }
        } catch (Exception e) {
            System.err.println("Critical System Error: " + e.getMessage());
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int val = Integer.parseInt(scanner.nextLine());
                return val;
            } catch (NumberFormatException e) {
                System.out.println(">>> Error: Please enter a valid number.");
            }
        }
    }

    private static String readEnumInput(String prompt, Object[] values) {
        while (true) {
            System.out.print(prompt + " " + Arrays.toString(values) + ": ");
            String input = scanner.nextLine().toUpperCase().replace(" ", "_");
            for (Object v : values) {
                if (v.toString().equals(input)) return input;
            }
            System.out.println(">>> Invalid choice. Please pick from the list.");
        }
    }

    private static void handleLogin(MongoCollection<Document> usersCol) {
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        currentUser = usersCol.find(eq("email", email)).first();
        if (currentUser != null) {
            System.out.println(">>> Welcome, " + currentUser.getString("name") + " [" + currentUser.getString("role") + "]");
        } else {
            System.out.println(">>> User not found!");
        }
    }

    private static void viewDashboard(MongoCollection<Document> sitesCol) {
        if (!isLoggedIn()) return;
        System.out.println("\n--- SITE DASHBOARD ---");
        FindIterable<Document> sites = isAdmin() ? sitesCol.find() : sitesCol.find(eq("currentOwnerId", currentUser.getObjectId("_id")));

        for (Document s : sites) {
            System.out.printf("Site #%d | Size: %s (%d sqft) | Type: %s | Status: %s%n",
                    s.getInteger("siteNumber"), s.getString("dimension"), s.getInteger("sqft"),
                    s.getString("type"), s.getString("status"));
        }
    }

    private static void handleMaintenance(MongoCollection<Document> maintCol, MongoCollection<Document> sitesCol) {
        if (!isLoggedIn()) return;

        if (isAdmin()) {
            System.out.println("1. View Pending Payments\n2. Generate Monthly Bills");
            int choice = readInt("Choice: ");
            if (choice == 1) {
                for (Document m : maintCol.find(eq("status", "PENDING"))) {
                    System.out.println("ID: " + m.getObjectId("_id") + " | Amount: ₹" + m.getInteger("amount") + " | OwnerID: " + m.getObjectId("ownerId"));
                }
            } else {
                for (Document s : sitesCol.find()) {
                    if (s.getObjectId("currentOwnerId") == null) continue; // Skip sites without owners

                    int rate = SiteStatus.OPEN.name().equals(s.getString("status")) ? 6 : 9;
                    int amount = s.getInteger("sqft") * rate;
                    
                    maintCol.insertOne(new Document("ownerId", s.getObjectId("currentOwnerId"))
                            .append("siteId", s.getObjectId("_id"))
                            .append("amount", amount)
                            .append("status", "PENDING"));
                }
                System.out.println(">>> Monthly bills generated.");
            }
        } else {
            System.out.println("--- Your Unpaid Bills ---");
            for (Document m : maintCol.find(and(eq("ownerId", currentUser.getObjectId("_id")), eq("status", "PENDING")))) {
                System.out.print("Due: ₹" + m.getInteger("amount") + ". Pay now? (y/n): ");
                if (scanner.nextLine().equalsIgnoreCase("y")) {
                    maintCol.updateOne(eq("_id", m.getObjectId("_id")), Updates.set("status", "PAID"));
                    System.out.println(">>> Payment Successful!");
                }
            }
        }
    }

    private static void handleRequests(MongoCollection<Document> reqCol, MongoCollection<Document> sitesCol) {
        if (!isLoggedIn()) return;

        if (isAdmin()) {
            for (Document r : reqCol.find(eq("status", "PENDING"))) {
                System.out.println("Request for Site ID: " + r.getObjectId("siteId"));
                System.out.print("Approve (a) or Reject (r)? ");
                String action = scanner.nextLine();
                if (action.equalsIgnoreCase("a")) {
                    Document data = (Document) r.get("proposedData");
                    sitesCol.updateOne(eq("_id", r.getObjectId("siteId")),
                            Updates.combine(Updates.set("status", data.getString("status")), Updates.set("type", data.getString("type"))));
                    reqCol.updateOne(eq("_id", r.getObjectId("_id")), Updates.set("status", "APPROVED"));
                    System.out.println(">>> Approved.");
                } else {
                    reqCol.updateOne(eq("_id", r.getObjectId("_id")), Updates.set("status", "REJECTED"));
                }
            }
        } else {
            int sNum = readInt("Enter Site Number for change: ");
            Document site = sitesCol.find(and(eq("siteNumber", sNum), eq("currentOwnerId", currentUser.getObjectId("_id")))).first();
            if (site != null) {
                String stat = readEnumInput("New Status", SiteStatus.values());
                String type = readEnumInput("New Type", SiteType.values());

                reqCol.insertOne(new Document("requesterId", currentUser.getObjectId("_id"))
                        .append("siteId", site.getObjectId("_id"))
                        .append("status", "PENDING")
                        .append("proposedData", new Document("status", stat).append("type", type)));
                System.out.println(">>> Request Sent.");
            }
        }
    }

    private static void manageUsersAndSites(MongoCollection<Document> usersCol, MongoCollection<Document> sitesCol, MongoCollection<Document> maintCol, MongoCollection<Document> reqCol) {
        if (!isAdmin()) return;
        System.out.println("\n1. Add Owner | 2. Add Site | 3. Update Site | 4. Delete Site");
        int choice = readInt("Choice: ");

        switch (choice) {
            case 1 -> {
                System.out.print("Name: "); String n = scanner.nextLine();
                System.out.print("Email: "); String e = scanner.nextLine();
                usersCol.insertOne(new Document("name", n).append("email", e).append("role", "OWNER"));
            }
            case 2 -> {
                int sn = readInt("Site Number: ");
                System.out.print("Dimension: "); String dim = scanner.nextLine();
                int sqft = readInt("Total Sqft: ");
                String type = readEnumInput("Type", SiteType.values());
                String status = readEnumInput("Status", SiteStatus.values());

                System.out.print("Owner Email (or blank): ");
                String oEmail = scanner.nextLine();
                ObjectId oId = null;
                if (!oEmail.isEmpty()) {
                    Document owner = usersCol.find(eq("email", oEmail)).first();
                    if (owner != null) oId = owner.getObjectId("_id");
                }

                sitesCol.insertOne(new Document("siteNumber", sn).append("dimension", dim)
                        .append("sqft", sqft).append("type", type).append("status", status).append("currentOwnerId", oId));
                System.out.println(">>> Site added.");
            }
            case 4 -> {
                int sn = readInt("Site Number to Delete: ");
                Document site = sitesCol.find(eq("siteNumber", sn)).first();
                if (site != null) {
                    ObjectId id = site.getObjectId("_id");
                    sitesCol.deleteOne(eq("_id", id));
                    maintCol.deleteMany(eq("siteId", id)); // Clean up references
                    reqCol.deleteMany(eq("siteId", id));
                    System.out.println(">>> Site and related records deleted.");
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- LAYOUT MAINTENANCE SYSTEM ---");
        if (currentUser == null) {
            System.out.println("0. Login | 5. Exit");
        } else {
            System.out.println("User: " + currentUser.getString("name") + " (" + currentUser.getString("role") + ")");
            System.out.println("0. Switch User | 1. Dashboard | 2. Maintenance | 3. Requests" + (isAdmin() ? " | 4. Admin Tools" : "") + " | 5. Exit");
        }
    }

    private static boolean isLoggedIn() { return currentUser != null; }
    private static boolean isAdmin() { return isLoggedIn() && "ADMIN".equals(currentUser.getString("role")); }
}
