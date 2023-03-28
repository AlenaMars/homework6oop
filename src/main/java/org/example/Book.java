package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Book {
    private int bookID;
    private String title;
    private String subject;
    private String author;
    private boolean isIssued;
    private ArrayList<HoldRequest> holdRequests;
    static int currentIdNumber = 0;

    public Book(int id, String t, String s, String a, boolean issued) {
        ++currentIdNumber;
        if (id == -1) {
            this.bookID = currentIdNumber;
        } else {
            this.bookID = id;
        }

        this.title = t;
        this.subject = s;
        this.author = a;
        this.isIssued = issued;
        this.holdRequests = new ArrayList();
    }

    public void addHoldRequest(HoldRequest hr) {
        this.holdRequests.add(hr);
    }

    public void removeHoldRequest() {
        if (!this.holdRequests.isEmpty()) {
            this.holdRequests.remove(0);
        }

    }

    public void printHoldRequests() {
        if (!this.holdRequests.isEmpty()) {
            System.out.println("\nЗапросы на резервирование");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("Нет\t\tНазвание книги\t\t\t Имя отдолжившего\t\t\tДата запроса");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------");

            for(int i = 0; i < this.holdRequests.size(); ++i) {
                System.out.print(i + "-\t\t");
                ((HoldRequest)this.holdRequests.get(i)).print();
            }
        } else {
            System.out.println("\nНет запросов о резервировании");
        }

    }

    public void printInfo() {
        System.out.println(this.title + "\t\t\t" + this.author + "\t\t\t" + this.subject);
    }

    public void changeBookInfo() throws IOException {
        Scanner scanner = new Scanner(System.in);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\nИзменить автора? (да/нет)");
        String input = scanner.next();
        if (input.equals("да")) {
            System.out.println("\nВвести новго автора ");
            this.author = reader.readLine();
        }

        System.out.println("\nИзменить предмет? (да/нет)");
        input = scanner.next();
        if (input.equals("да")) {
            System.out.println("\nВведите новый предмет");
            this.subject = reader.readLine();
        }

        System.out.println("\nИзменить название? (да/нет)");
        input = scanner.next();
        if (input.equals("y")) {
            System.out.println("\nВвести новое название");
            this.title = reader.readLine();
        }

        System.out.println("\nКнига успешно изменена");
    }

    public String getTitle() {
        return this.title;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getAuthor() {
        return this.author;
    }

    public boolean getIssuedStatus() {
        return this.isIssued;
    }

    public void setIssuedStatus(boolean s) {
        this.isIssued = s;
    }

    public int getID() {
        return this.bookID;
    }

    public ArrayList<HoldRequest> getHoldRequests() {
        return this.holdRequests;
    }

    public static void setIDCount(int n) {
        currentIdNumber = n;
    }

    public void placeBookOnHold(Borrower bor) {
        HoldRequest hr = new HoldRequest(bor, this, new Date());
        this.addHoldRequest(hr);
        bor.addHoldRequest(hr);
        System.out.println("\nКнига " + this.title + "была успешно зарезервирована отдалживающим" + bor.getName() + ".\n");
    }

    public void makeHoldRequest(Borrower borrower) {
        boolean makeRequest = true;

        int i;
        for(i = 0; i < borrower.getBorrowedBooks().size(); ++i) {
            if (((Loan)borrower.getBorrowedBooks().get(i)).getBook() == this) {
                System.out.println("\nВы уже одолжили" + this.title);
                return;
            }
        }

        for(i = 0; i < this.holdRequests.size(); ++i) {
            if (((HoldRequest)this.holdRequests.get(i)).getBorrower() == borrower) {
                makeRequest = false;
                break;
            }
        }

        if (makeRequest) {
            this.placeBookOnHold(borrower);
        } else {
            System.out.println("\nУ вас уже есть запрос на резервирование этой книги\n");
        }

    }

    public void serviceHoldRequest(HoldRequest hr) {
        this.removeHoldRequest();
        hr.getBorrower().removeHoldRequest(hr);
    }

    public void issueBook(Borrower borrower, Staff staff) {
        Date today = new Date();
        ArrayList<HoldRequest> hRequests = this.holdRequests;

        for(int i = 0; i < hRequests.size(); ++i) {
            HoldRequest hr = (HoldRequest)hRequests.get(i);
            long days = ChronoUnit.DAYS.between(today.toInstant(), hr.getRequestDate().toInstant());
            days = 0L - days;
            if (days > (long)Library.getInstance().getHoldRequestExpiry()) {
                this.removeHoldRequest();
                hr.getBorrower().removeHoldRequest(hr);
            }
        }

        if (this.isIssued) {
            System.out.println("\nКнига" + this.title + "уже выдана");
            System.out.println("Хотели бы вы зарезервировать книгу? (да/нет)");
            Scanner sc = new Scanner(System.in);
            String choice = sc.next();
            if (choice.equals("y")) {
                this.makeHoldRequest(borrower);
            }
        } else {
            if (!this.holdRequests.isEmpty()) {
                boolean hasRequest = false;

                for(int i = 0; i < this.holdRequests.size() && !hasRequest; ++i) {
                    if (((HoldRequest)this.holdRequests.get(i)).getBorrower() == borrower) {
                        hasRequest = true;
                    }
                }

                if (!hasRequest) {
                    System.out.println("\nНекоторые пользователи уже сделали запрос на эту книгу,а вы-нет,поэтому книга не может быть вам выдана");
                    System.out.println("Хотели бы вы зарезервировать? (да/нет)");
                    Scanner sc = new Scanner(System.in);
                    String choice = sc.next();
                    if (choice.equals("y")) {
                        this.makeHoldRequest(borrower);
                    }

                    return;
                }

                if (((HoldRequest)this.holdRequests.get(0)).getBorrower() != borrower) {
                    System.out.println("\nИзвините,некоторые другие пользователи запросили эту книгур раньше вас,поэтому придётся подождать пока их запросы на резервирование будут обработаны");
                    return;
                }

                this.serviceHoldRequest((HoldRequest)this.holdRequests.get(0));
            }

            this.setIssuedStatus(true);
            Loan iHistory = new Loan(borrower, this, staff, (Staff)null, new Date(), (Date)null, false);
            Library.getInstance().addLoan(iHistory);
            borrower.addBorrowedBook(iHistory);
            System.out.println("\nКнига " + this.title + " is successfully issued to " + borrower.getName() + ".");
            System.out.println("\nВыдана" + staff.getName());
        }

    }

    public void returnBook(Borrower borrower, Loan l, Staff staff) {
        l.getBook().setIssuedStatus(false);
        l.setReturnedDate(new Date());
        l.setReceiver(staff);
        borrower.removeBorrowedBook(l);
        l.payFine();
        System.out.println("\n Книга " + l.getBook().getTitle() + " is successfully returned by " + borrower.getName() + ".");
        System.out.println("\nПолучена " + staff.getName());
    }
}