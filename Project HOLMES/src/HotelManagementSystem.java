import java.io.*;
import java.util.*;

public class HotelManagementSystem {
    private static final String ROOM_FILE = "rooms.txt";
    private static final String GUEST_FILE = "guests.txt";
    private static final String ADMIN_CREDENTIALS_FILE = "admin_credentials.txt";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String GUEST_CREDENTIALS_FILE = "guest_credentials.txt";

    private List<Room> rooms = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private List<Guest> guests = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HotelManagementSystem system = new HotelManagementSystem();

        system.loadRooms();
        system.loadBookings();
        system.loadGuests();

        System.out.println("Welcome to Hotel Management System!");

        String role = system.userLogin(scanner);

        if (role == null) {
            System.out.println("Invalid login. Exiting...");
            return;
        }

        while (true) {
            if (role.equals("admin")) {
                system.showAdminMenu(scanner);
            } else if (role.equals("guest")) {
                system.showGuestMenu(scanner);
            }
        }
    }

    private String userLogin(Scanner scanner) {
        System.out.println("Login as:");
        System.out.println("1.Admin");
        System.out.println("2.Guest");
        System.out.println("3.Register as Admin");
        System.out.println("4.Register as Guest");
        System.out.print("Choose option: ");
        int loginChoice = scanner.nextInt();
        scanner.nextLine();

        if (loginChoice == 1) {
            return adminLogin(scanner) ? "admin" : null;
        } else if (loginChoice == 2) {
            return guestLogin(scanner) ? "guest" : null;
        } else if (loginChoice == 3) {
            return registerAdmin(scanner) ? "admin" : null;
        } else if (loginChoice == 4) {
            return registerGuest(scanner) ? "guest" : null;
        } else {
            System.out.println("Invalid choice, exiting...");
            return null;
        }
    }

    private boolean adminLogin(Scanner scanner) {
        System.out.println("Admin Login");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try (BufferedReader reader = new BufferedReader(new FileReader(ADMIN_CREDENTIALS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials.length == 2 && credentials[0].equals(username) && credentials[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading admin credentials file.");
        }

        System.out.println("Invalid admin credentials.");
        return false;
    }

    private boolean guestLogin(Scanner scanner) {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        for (Guest guest : guests) {
            if (guest.getUsername().equals(username) && guest.getPassword().equals(password)) {
                return true;
            }
        }

        System.out.println("Invalid credentials. Would you like to register? (Y/N)");
        String registerChoice = scanner.nextLine();
        if (registerChoice.equalsIgnoreCase("Y")) {
            registerGuest(scanner);
            return true;
        }

        return false;
    }

    private boolean registerAdmin(Scanner scanner) {
        System.out.println("Admin Registration");
        System.out.print("Enter the admin registration password: ");
        String adminPassword = scanner.nextLine();

        if (ADMIN_PASSWORD.equals(adminPassword)) {
            System.out.print("Enter Admin Username: ");
            String username = scanner.nextLine();
            System.out.print("Enter Admin Password: ");
            String password = scanner.nextLine();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ADMIN_CREDENTIALS_FILE, true))) {
                writer.write(username + "," + password + "\n");
                System.out.println("Admin registration successful!");
                userLogin(scanner);
                return true;
            } catch (IOException e) {
                System.out.println("Error registering admin.");
                return false;
            }
        } else {
            System.out.println("Incorrect password. Admin registration failed.");
            return false;
        }
    }

    private boolean registerGuest(Scanner scanner) {
        System.out.println("Guest Registration");
        System.out.print("Enter Guest Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Guest Password: ");
        String password = scanner.nextLine();

        Guest newGuest = new Guest(username, password);
        guests.add(newGuest);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GUEST_CREDENTIALS_FILE, true))) {
            writer.write(username + "," + password + "\n");
            System.out.println("Guest registration successful!");
            return true;
        } catch (IOException e) {
            System.out.println("Error registering guest.");
            return false;
        }
    }

    private void loadRooms() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ROOM_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] roomData = line.split(",");
                if (roomData.length == 2) {
                    int roomNumber = Integer.parseInt(roomData[0]);
                    boolean isAvailable = Boolean.parseBoolean(roomData[1]);
                    rooms.add(new Room(roomNumber, isAvailable));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading rooms data.");
        }
    }

    private void loadBookings() {
        try (BufferedReader reader = new BufferedReader(new FileReader(GUEST_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] guestData = line.split(",");
                if (guestData.length == 3) {
                    String name = guestData[0];
                    int roomNumber = Integer.parseInt(guestData[1]);
                    String checkInDate = guestData[2];
                    bookings.add(new Booking(name, roomNumber, checkInDate));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading bookings data.");
        }
    }

    private void loadGuests() {
        try (BufferedReader reader = new BufferedReader(new FileReader(GUEST_CREDENTIALS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] guestData = line.split(",");
                if (guestData.length == 2) {
                    String username = guestData[0];
                    String password = guestData[1];
                    guests.add(new Guest(username, password));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading guest data.");
        }
    }

    private void saveRooms() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ROOM_FILE))) {
            for (Room room : rooms) {
                writer.write(room.getRoomNumber() + "," + room.isAvailable() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving rooms data.");
        }
    }

    private void saveBookings() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GUEST_FILE))) {
            for (Booking booking : bookings) {
                writer.write(booking.getName() + "," + booking.getRoomNumber() + "," + booking.getCheckInDate() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving bookings data.");
        }
    }


    private void saveGuests() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GUEST_CREDENTIALS_FILE))) {
            for (Guest guest : guests) {
                writer.write(guest.getUsername() + "," + guest.getPassword() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving guest data.");
        }
    }

    private void showAdminMenu(Scanner scanner) {
        System.out.println("\n----- Admin Menu -----");
        System.out.println("1. Manage Rooms");
        System.out.println("2. Manage Bookings");
        System.out.println("3. Manage Guest Accounts");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                manageRooms(scanner);
                break;
            case 2:
                manageBookings(scanner);
                break;
            case 3:
                manageGuestAccounts(scanner);
                break;
            case 4:
                System.out.println("Logging out...");
                userLogin(scanner);
        }
    }

    private void manageRooms(Scanner scanner) {
        System.out.println("\n----- Managing Rooms -----");
        System.out.println("1. Add Room");
        System.out.println("2. Remove Room");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            addRoom(scanner);
        } else if (choice == 2) {
            removeRoom(scanner);
        } else {
            return;
        }
    }

    private void addRoom(Scanner scanner) {
        System.out.print("Enter room number: ");
        int roomNumber = scanner.nextInt();
        scanner.nextLine();
        rooms.add(new Room(roomNumber, true));
        System.out.println("Room added successfully.");
    }

    private void removeRoom(Scanner scanner) {
        System.out.print("Enter room number to remove: ");
        int roomNumber = scanner.nextInt();
        scanner.nextLine();
        rooms.removeIf(room -> room.getRoomNumber() == roomNumber);
        System.out.println("Room removed successfully.");
    }

    private void manageBookings(Scanner scanner) {
        System.out.println("\n----- Manage Bookings -----");
        System.out.println("1. View All Bookings");
        System.out.println("2. Cancel Booking");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            viewBookings();
        } else if (choice == 2) {
            cancelBooking(scanner);
        } else {
            return;
        }
    }

    private void cancelBooking(Scanner scanner) {
        System.out.print("Enter booking name to cancel: ");
        String name = scanner.nextLine();
        bookings.removeIf(booking -> booking.getName().equals(name));
        System.out.println("Booking cancelled successfully.");
    }

    private void manageGuestAccounts(Scanner scanner) {
        System.out.println("\n----- Manage Guest Accounts -----");
        System.out.println("1. View All Guests");
        System.out.println("2. Exit");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            viewGuests();
        } else {
            return;
        }
    }

    private void viewGuests() {
        System.out.println("\nGuest Accounts:");
        for (Guest guest : guests) {
            System.out.println("Username: " + guest.getUsername());
        }
    }

    private void showGuestMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n----- Guest Menu -----");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Book Room");
            System.out.println("3. View Bookings");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                viewAvailableRooms();
            } else if (choice == 2) {
                bookRoom(scanner);
            } else if (choice == 3) {
                viewBookings();
            } else if (choice == 4) {
                System.out.println("Logging out...");
                userLogin(scanner);
            }
        }
    }

    private void viewAvailableRooms() {
        System.out.println("\nAvailable Rooms:");
        for (Room room : rooms) {
            if (room.isAvailable()) {
                System.out.println("Room " + room.getRoomNumber() + " is available.");
            }
        }
    }

    private void bookRoom(Scanner scanner) {
        System.out.print("Enter room number to book: ");
        int roomNumber = scanner.nextInt();
        scanner.nextLine();
        Room roomToBook = findRoom(roomNumber);

        if (roomToBook != null && roomToBook.isAvailable()) {
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            System.out.print("Enter check-in date (YYYY-MM-DD): ");
            String checkInDate = scanner.nextLine();
            bookings.add(new Booking(name, roomNumber, checkInDate));
            roomToBook.setAvailable(false);
            System.out.println("Booking successful.");
        } else {
            System.out.println("Room is not available.");
        }
    }

    private void viewBookings() {
        System.out.println("\nYour Bookings:");
        for (Booking booking : bookings) {
            System.out.println("Room " + booking.getRoomNumber() + " booked by " + booking.getName() + " on " + booking.getCheckInDate());
        }
    }

    private Room findRoom(int roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }
}

