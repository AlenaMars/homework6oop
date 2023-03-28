package org.example.Refactoring;

import java.io.*;
import java.util.*;

public class Borrower extends Person
{
    private ArrayList<Loan> borrowedBooks;          //Книги,которые на данный момент взяты
    private ArrayList<HoldRequest> onHoldBooks;  //Те, на которые есть резерв



    public Borrower(int id,String name, String address, int phoneNum)
    {
        super(id,name,address,phoneNum);

        borrowedBooks = new ArrayList();
        onHoldBooks = new ArrayList();
    }



    public void printInfo()
    {
        super.printInfo();

        printBorrowedBooks();
        printOnHoldBooks();
    }


    public void printBorrowedBooks()
    {
        if (!borrowedBooks.isEmpty())
        {
            System.out.println("\nBorrowed Books are: ");

            System.out.println("------------------------------------------------------------------------------");
            System.out.println("Номер.\t\tНазвание\t\t\tАвтор\t\t\tПредмет");
            System.out.println("------------------------------------------------------------------------------");

            for (int i = 0; i < borrowedBooks.size(); i++)
            {
                System.out.print(i + "-" + "\t\t");
                borrowedBooks.get(i).getBook().printInfo();
                System.out.print("\n");
            }
        }
        else
            System.out.println("\nНет взятых книг");
    }


    public void printOnHoldBooks()
    {
        if (!onHoldBooks.isEmpty())
        {
            System.out.println("\nЗарезервированные книги: ");

            System.out.println("------------------------------------------------------------------------------");
            System.out.println("Номер.\t\tНазвание\t\t\tАвтор\t\t\tПРедмет");
            System.out.println("------------------------------------------------------------------------------");

            for (int i = 0; i < onHoldBooks.size(); i++)
            {
                System.out.print(i + "-" + "\t\t");
                onHoldBooks.get(i).getBook().printInfo();
                System.out.print("\n");
            }
        }
        else
            System.out.println("\nНет зарезервированных книг");
    }

    // Updating Borrower's Info
    public void updateBorrowerInfo() throws IOException
    {
        String choice;

        Scanner sc = new Scanner(System.in);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


        System.out.println("\nХотите изменить" + getName() + "'Имя ? (да/нет)");
        choice = sc.next();

        updateBorrowerName(choice, reader);


        System.out.println("\nХотите изменить" + getName() + "Адрес ? (да/нет)");
        choice = sc.next();

        updateBorrowerAddress(choice, reader);

        System.out.println("\Хотите обновить" + getName() + "номер телефона ? (да/нет)");
        choice = sc.next();

        updateBorrowerPhoneNumber(choice, sc);

        System.out.println("\nПОльзователь успешно изменён");

    }

    private void updateBorrowerPhoneNumber(String choice, Scanner sc) {
        if(choice.equals("да"))
        {
            System.out.println("\nНаптшите новый телефон");
            setPhone(sc.nextInt());
            System.out.println("\nНомер успешно изменён");
        }
    }

    private void updateBorrowerAddress(String choice, BufferedReader reader) throws IOException {
        if(choice.equals("да"))
        {
            System.out.println("\nНапечатать новый адрес ");
            setAddress(reader.readLine());
            System.out.println("\nАдрес успешно изменён");
        }
    }

    private void updateBorrowerName(String choice, BufferedReader reader) throws IOException {
        if(choice.equals("y"))
        {
            System.out.println("\nНапишите новое имя");
            setName(reader.readLine());
            System.out.println("\nИмя успешно изменено");
        }
    }

    /*-- Добавляем и удаляем взятые книги---*/
    public void addBorrowedBook(Loan iBook)
    {
        borrowedBooks.add(iBook);
    }

    public void removeBorrowedBook(Loan iBook)
    {
        borrowedBooks.remove(iBook);
    }

    /*-------------------------------------------*/

    /*-- Добавляем и удаляем зарезервированные книги---*/
    public void addHoldRequest(HoldRequest hr)
    {
        onHoldBooks.add(hr);
    }

    public void removeHoldRequest(HoldRequest hr)
    {
        onHoldBooks.remove(hr);
    }


    public ArrayList<Loan> getBorrowedBooks()
    {
        return borrowedBooks;
    }

    public ArrayList<HoldRequest> getOnHoldBooks()
    {
        return onHoldBooks;
    }

}