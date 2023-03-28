package org.example;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Scanner;

public class Loan {
    private Borrower borrower;
    private Book book;
    private Staff issuer;
    private Date issuedDate;
    private Date dateReturned;
    private Staff receiver;
    private boolean finePaid;

    public Loan(Borrower bor, Book b, Staff i, Staff r, Date iDate, Date rDate, boolean fPaid) {
        this.borrower = bor;
        this.book = b;
        this.issuer = i;
        this.receiver = r;
        this.issuedDate = iDate;
        this.dateReturned = rDate;
        this.finePaid = fPaid;
    }

    public Book getBook() {
        return this.book;
    }

    public Staff getIssuer() {
        return this.issuer;
    }

    public Staff getReceiver() {
        return this.receiver;
    }

    public Date getIssuedDate() {
        return this.issuedDate;
    }

    public Date getReturnDate() {
        return this.dateReturned;
    }

    public Borrower getBorrower() {
        return this.borrower;
    }

    public boolean getFineStatus() {
        return this.finePaid;
    }

    public void setReturnedDate(Date dReturned) {
        this.dateReturned = dReturned;
    }

    public void setFineStatus(boolean fStatus) {
        this.finePaid = fStatus;
    }

    public void setReceiver(Staff r) {
        this.receiver = r;
    }

    public double computeFine1() {
        double totalFine = 0.0;
        if (!this.finePaid) {
            Date iDate = this.issuedDate;
            Date rDate = new Date();
            long days = ChronoUnit.DAYS.between(rDate.toInstant(), iDate.toInstant());
            days = 0L - days;
            days -= (long)Library.getInstance().book_return_deadline;
            if (days > 0L) {
                totalFine = (double)days * Library.getInstance().per_day_fine;
            } else {
                totalFine = 0.0;
            }
        }

        return totalFine;
    }

    public void payFine() {
        double totalFine = this.computeFine1();
        if (totalFine > 0.0) {
            System.out.println("\nОбщий штраф сгенерирован " + totalFine);
            System.out.println("Хотите оплатить? (да/нет)");
            Scanner input = new Scanner(System.in);
            String choice = input.next();
            if (choice.equals("y") || choice.equals("Y")) {
                this.finePaid = true;
            }

            if (choice.equals("n") || choice.equals("N")) {
                this.finePaid = false;
            }
        } else {
            System.out.println("\nШтраф не сгенерирован");
            this.finePaid = true;
        }

    }

    public void renewIssuedBook(Date iDate) {
        this.issuedDate = iDate;
        System.out.println("\nКрайний срок сдачи книги" + this.getBook().getTitle() + "был продлён");
        System.out.println("Выданная книга успешно возобновлена\n");
    }
}