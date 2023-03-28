package org.example;

public class Clerk extends Staff {
    int deskNo;
    public static int currentdeskNumber = 0;

    public Clerk(int id, String n, String a, int ph, double s, int dk) {
        super(id, n, a, ph, s);
        if (dk == -1) {
            this.deskNo = currentdeskNumber;
        } else {
            this.deskNo = dk;
        }

        ++currentdeskNumber;
    }

    public void printInfo() {
        super.printInfo();
        System.out.println("Номер столика" + this.deskNo);
    }
4}
