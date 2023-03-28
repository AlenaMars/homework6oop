package org.example;

import java.util.Date;

public class HoldRequest {
    Borrower borrower;
    Book book;
    Date requestDate;

    public HoldRequest(Borrower bor, Book b, Date reqDate) {
        this.borrower = bor;
        this.book = b;
        this.requestDate = reqDate;
    }

    public Borrower getBorrower() {
        return this.borrower;
    }

    public Book getBook() {
        return this.book;
    }

    public Date getRequestDate() {
        return this.requestDate;
    }

    public void print() {
        System.out.print(this.book.getTitle() + "\t\t\t\t" + this.borrower.getName() + "\t\t\t\t" + this.requestDate + "\n");
    }
}