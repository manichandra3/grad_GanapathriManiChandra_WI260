package org.example;

import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.internal.bulk.DeleteRequest;
import org.bson.Document;

import javax.print.Doc;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

enum TYPE{
    VILLA1,
    OPEN_SITE,
    INDEPENDENT_HOUSE,

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
                System.out.print("Enter Option: ");
                int option = scanner.hasNextInt() ? scanner.nextInt() : 5;
                if (scanner.hasNextLine()) scanner.nextLine();

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
            e.printStackTrace();
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
            System.out.println("1. View Pending Payments\n2. Generate Monthly Bills (Admin)");
            int choice = scanner.nextInt(); scanner.nextLine();
            if (choice == 1) {
                for (Document m : maintCol.find(eq("status", "PENDING"))) {
                    System.out.println("Record ID: " + m.getObjectId("_id") + " | Amount: ₹" + m.getInteger("amount") + " | Owner: " + m.getObjectId("ownerId"));
                }
            } else {

                for (Document s : sitesCol.find()) {
                    int rate = s.getString("status").equalsIgnoreCase("Open") ? 6 : 9;
                    int amount = s.getInteger("sqft") * rate;
                    maintCol.insertOne(new Document("ownerId", s.getObjectId("currentOwnerId"))
                            .append("siteId", s.getObjectId("_id"))
                            .append("amount", amount)
                            .append("status", "PENDING"));
                }
                System.out.println(">>> Monthly bills generated successfully.");
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
                System.out.println("Request from Owner " + r.getObjectId("requesterId") + " for Site " + r.getObjectId("siteId"));
                System.out.print("Approve (a) or Reject (r)? ");
                String action = scanner.nextLine();
                if (action.equalsIgnoreCase("a")) {
                    reqCol.updateOne(eq("_id", r.getObjectId("_id")), Updates.set("status", "APPROVED"));
                    Document data = (Document) r.get("proposedData");
                    sitesCol.updateOne(eq("_id", r.getObjectId("siteId")),
                            Updates.combine(Updates.set("status", data.getString("status")), Updates.set("type", data.getString("type"))));
                    System.out.println(">>> Approved.");
                } else {
                    reqCol.updateOne(eq("_id", r.getObjectId("_id")), Updates.set("status", "REJECTED"));
                }
            }
        } else {
            System.out.print("Enter Site Number to request change: ");
            int sNum = scanner.nextInt(); scanner.nextLine();
            Document site = sitesCol.find(and(eq("siteNumber", sNum), eq("currentOwnerId", currentUser.getObjectId("_id")))).first();
            if (site != null) {
                System.out.print("Enter New Status (Open/Occupied): ");
                String stat = scanner.nextLine();
                System.out.print("Enter New Type (Villa/Apartment/Independent House/Open Site): ");
                String type = scanner.nextLine();
                reqCol.insertOne(new Document("requesterId", currentUser.getObjectId("_id"))
                        .append("siteId", site.getObjectId("_id"))
                        .append("status", "PENDING")
                        .append("proposedData", new Document("status", stat).append("type", type)));
                System.out.println(">>> Request Sent to Admin.");
            }
        }
    }

    private static void manageUsersAndSites(MongoCollection<Document> usersCol, MongoCollection<Document> sitesCol, MongoCollection<Document> maintenanceCol, MongoCollection<Document> requestCol) {
        if (!isAdmin()) return;
        System.out.println("\n--- ADMIN TOOLS ---");
        System.out.println("1. Add New Owner");
        System.out.println("2. Add New Site");
        System.out.println("3. Update Site Details");
        System.out.println("4. Delete Site");
        System.out.println("5. Back");

        System.out.print("Enter Choice: ");
        int choice = scanner.nextInt(); scanner.nextLine();

        switch (choice) {
            case 1 -> { // ADD NEW OWNER
                System.out.print("Name: "); String n = scanner.nextLine();
                System.out.print("Email: "); String e = scanner.nextLine();
                usersCol.insertOne(new Document("name", n).append("email", e).append("role", "OWNER"));
                System.out.println(">>> Owner Added successfully.");
            }
            case 2 -> { // ADD NEW SITE
                System.out.print("Site Number (int): "); int sn = scanner.nextInt(); scanner.nextLine();
                System.out.print("Dimension (e.g., 30x40): "); String dim = scanner.nextLine();
                System.out.print("Total Sqft: "); int sqft = scanner.nextInt(); scanner.nextLine();
                System.out.print("Type (Villa/Open Site): "); String type = scanner.nextLine();
                System.out.print("Status (Open/Occupied): "); String status = scanner.nextLine();

                System.out.print("Owner Email (or leave blank for none): ");
                String ownerEmail = scanner.nextLine();
                Object ownerId = null;

                if (!ownerEmail.isEmpty()) {
                    Document owner = usersCol.find(eq("email", ownerEmail)).first();
                    if (owner != null) {
                        ownerId = owner.getObjectId("_id");
                    } else {
                        System.out.println(">>> Owner not found. Creating site without owner.");
                    }
                }

                Document newSite = new Document("siteNumber", sn)
                        .append("dimension", dim)
                        .append("sqft", sqft)
                        .append("type", type)
                        .append("status", status)
                        .append("currentOwnerId", ownerId);

                sitesCol.insertOne(newSite);
                System.out.println(">>> Site #" + sn + " added successfully.");
            }
            case 3 -> { // UPDATE SITE
                System.out.print("Site Number: "); int sn = scanner.nextInt(); scanner.nextLine();
                System.out.print("New Dimension: "); String dim = scanner.nextLine();
                sitesCol.updateOne(eq("siteNumber", sn), Updates.set("dimension", dim));
                System.out.println(">>> Site Updated.");
            }
            case 4 -> { // DELETE SITE
                System.out.print("Enter Site Number to Delete: ");
                int sn = scanner.nextInt(); scanner.nextLine();
                Document site = sitesCol.find(eq("siteNumber", sn)).first();
                if (site != null) {
                    Object siteId = site.getObjectId("_id");
                    sitesCol.deleteOne(eq("siteNumber", sn));
                    // TODO: Cleanup hanging refs after deleting site
                    maintenanceCol.deleteMany(eq("_id", siteId));
                    requestCol.deleteMany(eq("_id",siteId));
                    System.out.println(">>> Successfully Deleted Site #" + sn);

                } else {
                    System.out.println(">>> Site not found!");
                }
            }
            default -> System.out.println("Returning to menu...");
        }
    }
    private static void printMenu() {
        System.out.println("\n--- LAYOUT MAINTENANCE SYSTEM ---");
        if (currentUser == null) {
            System.out.println("0. Login | 5. Exit");
        } else {
            System.out.print("User: " + currentUser.getString("name") + "\nOptions: ");
            System.out.println("\n0. Switch User \n1. Dashboard \n2. Maintenance \n3. Requests" + (isAdmin() ? " \n4. Admin Tools" : "") + " \n5. Exit");
        }
    }

    private static boolean isLoggedIn() { return currentUser != null; }
    private static boolean isAdmin() { return currentUser != null && "ADMIN".equals(currentUser.getString("role")); }
}