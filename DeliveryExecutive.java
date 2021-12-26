class DeliveryExecutive {
    String name;
    int allowance;
    int deliveryCharge;
    int total;
    Trip lastTrip;

    DeliveryExecutive(String n, int a, int dc, Trip lt) {
        name = n;
        allowance = a;
        deliveryCharge = dc;
        total = allowance + deliveryCharge;
        lastTrip = lt;
    }

    void setAllowance(int a) {
        allowance = a;
    }

    void setDeliveryCharge(int dc) {
        deliveryCharge = dc;
    }

    void setTotal() {
        total = allowance + deliveryCharge;
    }
}