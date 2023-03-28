package org.example;

public class Staff extends Person {
    protected double salary;

    public Staff(int id, String n, String a, int p, double s) {
        super(id, n, a, p);
        this.salary = s;
    }

    public void printInfo() {
        super.printInfo();
        System.out.println("Зарплата " + this.salary + "\n");
    }

    public double getSalary() {
        return this.salary;
    }
}