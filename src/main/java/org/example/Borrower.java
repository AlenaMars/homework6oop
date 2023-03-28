package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Borrower extends Person {
    private ArrayList<Loan> borrowedBooks = new ArrayList();
    private ArrayList<HoldRequest> onHoldBooks = new ArrayList();

    public Borrower(int id, String n, String a, int p) {
        super(id, n, a, p);
    }

    public void printInfo() {
        super.printInfo();
        this.printBorrowedBooks();
        this.printOnHoldBooks();
    }

    public void printBorrowedBooks() {
        if (!this.borrowedBooks.isEmpty()) {
            System.out.println("\nВзятые книги: ");
            System.out.println("------------------------------------------------------------------------------");
            System.out.println("Нет.\t\tНазвание\t\t\tАвтор\t\t\tПредмет");
            System.out.println("------------------------------------------------------------------------------");

            for(int i = 0; i < this.borrowedBooks.size(); ++i) {
                System.out.print(i + "-\t\t");
                ((Loan)this.borrowedBooks.get(i)).getBook().printInfo();
                System.out.print("\n");
            }
        } else {
            System.out.println("\nНет взятых книг");
        }

    }

    public void printOnHoldBooks() {
        if (!this.onHoldBooks.isEmpty()) {
            System.out.println("\nЗарезервированные книги ");
            System.out.println("------------------------------------------------------------------------------");
            System.out.println("Нет.\t\tНазвание\t\t\tАвтор\t\t\tПредмет");
            System.out.println("------------------------------------------------------------------------------");

            for(int i = 0; i < this.onHoldBooks.size(); ++i) {
                System.out.print(i + "-\t\t");
                ((HoldRequest)this.onHoldBooks.get(i)).getBook().printInfo();
                System.out.print("\n");
            }
        } else {
            System.out.println("\nНет зарезервированных книг");
        }

    }

    public void updateBorrowerInfo() throws IOException {
        Scanner sc = new Scanner(System.in);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\nХотите изменить " + this.getName() + "имя ? (да/нет)");
        String choice = sc.next();
        if (choice.equals("да")) {
            System.out.println("\nНапишите новое имя");
            this.setName(reader.readLine());
            System.out.println("\nИмя успешно изменено");
        }

        System.out.println("\nХотите изменить" + this.getName() + "адрес ? (да/нет)");
        choice = sc.next();
        if (choice.equals("да")) {
            System.out.println("\n Написать новый адрес ");
            this.setAddress(reader.readLine());
            System.out.println("\nАдрес успешно изменён");
        }

        System.out.println("\n Хотите изменить" + this.getName() + "телефонный номер ? (да/нет)");
        choice = sc.next();
        if (choice.equals("да")) {
            System.out.println("\nНаписать новый телефонный номер");
            this.setPhone(sc.nextInt());
            System.out.println("\nТелефонный номер успешно именён");
        }

        System.out.println("\n Отдолживший успешно изменён");
    }

    public void addBorrowedBook(Loan iBook) {
        this.borrowedBooks.add(iBook);
    }

    public void removeBorrowedBook(Loan iBook) {
        this.borrowedBooks.remove(iBook);
    }

    public void addHoldRequest(HoldRequest hr) {
        this.onHoldBooks.add(hr);
    }

    public void removeHoldRequest(HoldRequest hr) {
        this.onHoldBooks.remove(hr);
    }

    public ArrayList<Loan> getBorrowedBooks() {
        return this.borrowedBooks;
    }

    public ArrayList<HoldRequest> getOnHoldBooks() {
        return this.onHoldBooks;
    }
}