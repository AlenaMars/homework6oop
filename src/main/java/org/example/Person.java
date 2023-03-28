package org.example;

public abstract class Person {
    protected int id;
    protected String password;
    protected String name;
    protected String address;
    protected int phoneNo;
    static int currentIdNumber = 0;

    public Person(int dd, String n, String a, int p) {
        ++currentIdNumber;
        if (dd == -1) {
            this.id = currentIdNumber;
        } else {
            this.id = dd;
        }

        this.password = Integer.toString(this.id);
        this.name = n;
        this.address = a;
        this.phoneNo = p;
    }

    public void printInfo() {
        System.out.println("-----------------------------------------");
        System.out.println("\nДетали\n");
        System.out.println("ID " + this.id);
        System.out.println("Имя: " + this.name);
        System.out.println("Адрес " + this.address);
        System.out.println("Нет номера: " + this.phoneNo + "\n");
    }

    public void setAddress(String a) {
        this.address = a;
    }

    public void setPhone(int p) {
        this.phoneNo = p;
    }

    public void setName(String n) {
        this.name = n;
    }

    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }

    public String getAddress() {
        return this.address;
    }

    public int getPhoneNumber() {
        return this.phoneNo;
    }

    public int getID() {
        return this.id;
    }

    public static void setIDCount(int n) {
        currentIdNumber = n;
    }
}
