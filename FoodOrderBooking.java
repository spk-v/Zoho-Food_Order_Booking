import java.util.*;

public class FoodOrderBooking {
    static Scanner sc = new Scanner(System.in);
    static int tripNo = 0;
    static int bookingId = 0;
    // Map of trips for storing trip details, treemap is used to get the trip
    // details in order
    static Map<Integer, Trip> Trips = new TreeMap<>();
    // list of delivery excutives to store their earnings
    static List<DeliveryExecutive> DE = new ArrayList<>();

    // function to convert time to milliseconds
    static long convertToMilli(String time) {
        long millis = (Integer.parseInt(time.substring(0, time.indexOf(":"))) * 60 * 60 * 1000)
                + (Integer.parseInt(time.substring(time.indexOf(":") + 1)) * 60 * 1000);
        return millis;
    }

    // function to convert milli seconds to time
    static String convertMillisToTime(long millis) {
        String time = "";
        time += (millis / 1000 / 60 / 60) + ":" + ((millis / 1000 / 60) % 60);
        return time;
    }

    // function to convert entered 12 hrs format time to 24hrs format
    static String convertTo24Hrs(String time) {
        String res = "";
        int hh = Integer.parseInt(time.substring(0, 2));
        if (time.charAt(5) == 'A') {
            if (hh == 12) {
                res += "00:" + time.substring(3, 5);
            } else {
                res += hh + ":" + time.substring(3, 5);
            }
        } else {
            if (hh == 12) {
                res += hh + ":" + time.substring(3, 5);
            } else {
                res += (hh + 12) + ":" + time.substring(3, 5);
            }
        }
        return res;
    }

    // function to add 30 mins to time in order to get delivery time
    static String add30ToTime(String time) {
        String res = "";
        long millis = convertToMilli(time) + (30 * 60 * 1000);
        res = convertMillisToTime(millis);
        return res;
    }

    // function to add 15 mins to time inorder to get pickup time
    static String add15ToTime(String time) {
        String res = "";
        long millis = convertToMilli(time) + (15 * 60 * 1000);
        res = convertMillisToTime(millis);
        return res;
    }

    // function to find minutes difference between two time peiods
    static long timeDiff(String prevTime, String time) {
        long diff = -1;
        long prevMillis = convertToMilli(prevTime);
        long Millis = convertToMilli(time);
        diff = (Millis - prevMillis) / 1000 / 60;
        return diff;
    }

    // function to assign and update delivery executive and add and update trip
    // details
    static String assignDeliveryExecutive(char restaurant, char destination, String time) {
        String allottedDeliveryExecutive = "";
        List<DeliveryExecutive> availableDeliveryExecutives = new ArrayList<>();
        int min = 2147483647;// assignming integers maximum value
        int DEidx = -1;// index of the alloted delivery Executive
        boolean laTrip = false;// variable defines that the order can connected to previous trip or not
        boolean tripConfirmed = false;
        for (int i = 0; i < DE.size(); i++) {
            DeliveryExecutive de = DE.get(i);
            Trip lt = de.lastTrip;
            if (!tripConfirmed) {
                // condition to check that the orders restaurant and destination are same as
                // previous order and the time is in between the 15 mins of previuos orders
                // pickuptime
                if (lt.tripNo != 0 && lt.restaurant == restaurant && lt.destination == destination
                        && timeDiff(time, lt.pickupTime) >= 0 && timeDiff(time, lt.pickupTime) <= 15
                        && lt.orders <= 5) {
                    allottedDeliveryExecutive = de.name;
                    availableDeliveryExecutives.add(de);
                    laTrip = true;
                    DEidx = i;
                    tripConfirmed = true;
                } else if (lt.tripNo == 0) {// condition to check that this is the first order for the executive
                    allottedDeliveryExecutive = de.name;
                    availableDeliveryExecutives.add(de);
                    DEidx = i;
                    tripConfirmed = true;
                } else if (timeDiff(lt.deliveryTime, add15ToTime(time)) > 0) {// condition to check that the oreders
                                                                              // pickuptime is later of previous order's
                                                                              // delivery time or not
                    availableDeliveryExecutives.add(de);
                    if (de.deliveryCharge < min) {
                        allottedDeliveryExecutive = de.name;
                        DEidx = i;
                        min = de.deliveryCharge;
                    }
                }
            } else {
                if (lt.tripNo == 0 || timeDiff(lt.deliveryTime, add15ToTime(time)) > 0) {
                    availableDeliveryExecutives.add(de);
                }
            }
        } // printing the available delivery executives
        if (availableDeliveryExecutives.size() > 0) {
            bookingId++;
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Available Executives:");
            System.out.println("Executive   Delivery_Charge_Earned");
            for (int i = 0; i < availableDeliveryExecutives.size(); i++) {
                DeliveryExecutive de = availableDeliveryExecutives.get(i);
                System.out.println(de.name + "                " + (de.deliveryCharge));
            }
            if (laTrip) {// updating the details of delivery executive and trips
                DeliveryExecutive de = DE.get(DEidx);
                Trip t = de.lastTrip;
                t.setDeliveryCharge(t.deliveryCharge + 5);
                t.setOrders(t.orders + 1);
                Trips.replace(tripNo, t);
                de.lastTrip = t;
                de.setDeliveryCharge(de.deliveryCharge + 5);
                de.setTotal();
                DE.set(DEidx, de);
            } else {
                tripNo++;
                String pickupTime = add15ToTime(time);
                Trip t = new Trip(tripNo, allottedDeliveryExecutive, restaurant, destination, 1, pickupTime,
                        add30ToTime(pickupTime), 50);
                Trips.put(tripNo, t);
                DeliveryExecutive de = DE.get(DEidx);
                de.setAllowance(de.allowance + 10);
                de.setDeliveryCharge(de.deliveryCharge + 50);
                de.setTotal();
                de.lastTrip = t;
                DE.set(DEidx, de);
            }
        }

        return allottedDeliveryExecutive;
    }

    static String handleBooking() {// function to get and validate order details
        System.out.print("Customer Id: ");
        String customerId = sc.nextLine().strip();
        System.out.print("Restaurant: ");
        char restaurant = sc.next().charAt(0);
        System.out.print("Destination: ");
        char destination = sc.next().charAt(0);
        while (restaurant < 'A' || restaurant > 'E' || destination < 'A' || destination > 'E') {
            System.out.println();
            System.out.println("Enter a valid restaurant and destination..");
            System.out.print("Restaurant: ");
            restaurant = sc.next().charAt(0);
            System.out.print("Destination: ");
            destination = sc.next().charAt(0);
        }
        boolean invalidTime = true;
        System.out.println("Time (hh.mm AM): ");
        sc.nextLine();
        String time = sc.nextLine();
        while (invalidTime) {
            try {
                time = convertTo24Hrs(time.replace(" ", ""));
                invalidTime = false;
            } catch (Exception e) {
                System.out.println("Invalid time format. Enter the time in 12 hrs format like 'hh.mm AM'.");
                System.out.print("Time (hh.mm AM): ");
                time = sc.nextLine();
            }
        }

        return assignDeliveryExecutive(restaurant, destination, time);
    }

    // function to display trip details
    static void displayTripDetails() {
        System.out.println();
        System.out.println("Delivery History:");
        System.out.println(
                "Trip   Delivery_Executive   Restaurant   Destination   Orders   Pickup_Time   Delivery_Time   Delivery_Charge");
        for (Map.Entry<Integer, Trip> e : Trips.entrySet()) {
            Trip t = e.getValue();
            System.out.println(e.getKey() + "        " + t.deliveryExecutive + "                      " + t.restaurant
                    + "             " + t.destination
                    + "          " + t.orders + "          " + t.pickupTime + "           " + t.deliveryTime
                    + "              " + t.deliveryCharge);
        }
    }

    // function to display delivery executives earnings
    static void displayTotalEarnings() {
        System.out.println();
        System.out.println("Total Earned:");
        System.out.println("Delivery_Executive   Allowance   Delivery_Charge   Total");
        for (int i = 0; i < DE.size(); i++) {
            DeliveryExecutive de = DE.get(i);
            System.out.println(de.name + "                      " + de.allowance + "              " + de.deliveryCharge
                    + "           " + de.total);
        }
    }

    public static void main(String[] args) {// main function
        int numberOfDeliveryExecutives = 5;
        // creating delivery executives
        for (int i = 1; i <= numberOfDeliveryExecutives; i++) {
            Trip lastTrip = new Trip(0, "", '0', '0', 0, "", "", 0);
            DeliveryExecutive de = new DeliveryExecutive("DE" + i, 0, 0, lastTrip);
            DE.add(de);
        }

        while (true) {// home page code starts here
            System.out.println();
            System.out.println("-------------------Home_Page-------------------");
            System.out.println("1)Make Order.");
            System.out.println("2)Show Delivery Details.");
            System.out.println("3)Exit.");
            System.out.println();
            System.out.println("Choose an option from above: ");
            String option = sc.nextLine();
            switch (option.charAt(0)) {
                case '1':
                    String allottedDeliveryExecutive = handleBooking();
                    if (allottedDeliveryExecutive.length() == 0) {
                        System.out.println("Booking Rejected.\nNo Delivery Executive Available.");
                        break;
                    }
                    System.out.println("Allotted Delivery Executive: " + allottedDeliveryExecutive);
                    break;

                case '2':
                    displayTripDetails();
                    displayTotalEarnings();
                    break;

                case '3':
                    System.out.println("Closing Application...");
                    sc.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid Option.");
                    break;
            }
        }
    }
}
