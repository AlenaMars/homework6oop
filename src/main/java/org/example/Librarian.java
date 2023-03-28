package org.example;

public class Librarian extends Staff {
    int officeNo;
    public static int currentOfficeNumber = 0;

    public Librarian(int id, String n, String a, int p, double s, int of) {
        super(id, n, a, p, s);
        if (of == -1) {
            this.officeNo = currentOfficeNumber;
        } else {
            this.officeNo = of;
        }

        ++currentOfficeNumber;
    }

    public void printInfo() {
        super.printInfo();
        System.out.println("Номер офиса" + this.officeNo);
    }
}