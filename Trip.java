class Trip {
    int tripNo;
    String deliveryExecutive;
    char restaurant;
    char destination;
    int orders;
    String pickupTime;
    String deliveryTime;
    int deliveryCharge;

    Trip(int tn, String de, char r, char d, int o, String pt, String dt, int dc) {
        tripNo = tn;
        deliveryExecutive = de;
        restaurant = r;
        destination = d;
        orders = o;
        pickupTime = pt;
        deliveryTime = dt;
        deliveryCharge = dc;
    }

    void setOrders(int o) {
        orders = o;
    }

    void setDeliveryCharge(int dc) {
        deliveryCharge = dc;
    }
}