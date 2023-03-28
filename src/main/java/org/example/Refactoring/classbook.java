package org.example.Refactoring;

import java.io .*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public  classBook {

private int bookID; // ID выдаётся в библиотеке книге чтобы отличать её от других
private  Stringtitle; // Название книги
private  Stringsubject; // Предмет, к которому книга относится
private String author; // автор книги
private boolean isIssued; // Это будет правдой, если книга, на данный момент выдана пользователю
private HoldRequestOperations holdRequestsOperations =new HoldRequestOperations();
static int currentIdNumber = 0; //Это будет уникльным дял каждой книги
//когда книга создана


public Book(int , String t, String s, String a, boolean issued)
        {
        currentIdNumber++;
        if (id==-1)
        {
        bookID = currentIdNumber;
        }
        else
        bookID=id;

        title = t;
        subject = s;
        author = a;
        isIssued = issued;

        }


// printing all hold req on a book.
public void printHoldRequests()
        {
        if (!holdRequestsOperations.holdRequests.isEmpty())
        {
        System.out.println("\nHold Requests are: ");

        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("Номер\t\tНазвание книги\t\t\t Имя пользователя\t\t\tДта запроса");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < holdRequestsOperations.holdRequests.size(); i++)
        {
        System.out.print(i + "-" + "\t\t");
        holdRequestsOperations.holdRequests.get(i).print();
        }
        }
        else
        System.out.println("\nНет резерввов");
        }

// printing book's Info
public void printInfo()
        {
        System.out.println(title + "\t\t\t" + author + "\t\t\t" + subject);
        }

// changign Info of a Book
public void changeBookInfo() throws IOException
        {
        Scanner scanner = new Scanner(System.in);
        String input;

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\nUpdate Author? (y/n)");
        input = scanner.next();

        if(input.equals("y"))
        {
        System.out.println("\nВвести нового оавтора");
        author = reader.readLine();
        }

        System.out.println("\nИзменить предмет? (да/нет)");
        input = scanner.next();

        if(input.equals("да"))
        {
        System.out.println("\nВведите новый предмет");
        subject = reader.readLine();
        }

        System.out.println("\nИзменить название (да/нет)");
        input = scanner.next();

        if(input.equals("да"))
        {
        System.out.println("\nВведите новое название ");
        title = reader.readLine();
        }

        System.out.println("\nКнига успешно изменена");

        }

/*------------Getter FUNCs.---------*/

public String getTitle()
        {
        return title;
        }

public String getSubject()
        {
        return subject;
        }

public String getAuthor()
        {
        return author;
        }

public boolean getIssuedStatus()
        {
        return isIssued;
        }

public void setIssuedStatus(boolean s)
        {
        isIssued = s;
        }

public int getID()
        {
        return bookID;
        }

public ArrayList<HoldRequest> getHoldRequests()
        {
        return holdRequestsOperations.holdRequests;
        }
/*-----------------------------------*/

// Setter Static Func.
public static void setIDCount(int n)
        {
        currentIdNumber = n;
        }




//-------------------------------------------------------------------//

// Placing book on Hold
public void placeBookOnHold(Borrower bor)
        {
        HoldRequest hr = new HoldRequest(bor,this, new Date());

        holdRequestsOperations.addHoldRequest(hr);        //Add this hold request to holdRequests queue of this book
        bor.addHoldRequest(hr);      //Add this hold request to that particular borrower's class as well

        System.out.println("\nКнига" + title + " была успешно зарезервирована" + bor.getName() + ".\n");
        }




// Request for Holding a Book
public void makeHoldRequest(Borrower borrower)
        {
        boolean makeRequest = true;

        //If that borrower has already borrowed that particular book. Then he isn't allowed to make request for that book. He will have to renew the issued book in order to extend the return deadline.
        for(int i=0;i<borrower.getBorrowedBooks().size();i++)
        {
        if(borrower.getBorrowedBooks().get(i).getBook()==this)
        {
        System.out.println("\n" + "Вы уже одолжили" + title);
        return;
        }
        }


        //If that borrower has already requested for that particular book. Then he isn't allowed to make the same request again.
        for (int i = 0; i < holdRequestsOperations.holdRequests.size(); i++)
        {
        if ((holdRequestsOperations.holdRequests.get(i).getBorrower() == borrower))
        {
        makeRequest = false;
        break;
        }
        }

        if (makeRequest)
        {
        placeBookOnHold(borrower);
        }
        else
        System.out.println("\nУ вас уже есть один резерв дял этой книги\n");
        }


// Getting Info of a Hold Request
public void serviceHoldRequest(HoldRequest hr)
        {
        holdRequestsOperations.removeHoldRequest();
        hr.getBorrower().removeHoldRequest(hr);
        }



// Выдача книги
public void issueBook(Borrower borrower, Staff staff)
        {
        //Сначала удаляем истёкшие резервы
        Date today = new Date();

        ArrayList<HoldRequest> hRequests = holdRequestsOperations.holdRequests;

        for (int i = 0; i < hRequests.size(); i++)
        {
        HoldRequest hr = hRequests.get(i);

        //Удалить истёкшие резервы
        long days =  ChronoUnit.DAYS.between(today.toInstant(), hr.getRequestDate().toInstant());
        days = 0-days;

        if(days>Library.getInstance().getHoldRequestExpiry())
        {
        holdRequestsOperations.removeHoldRequest();
        hr.getBorrower().removeHoldRequest(hr);
        }
        }

        if (isIssued)
        {
        System.out.println("\nКнига" + title + "уже выдана");
        System.out.println("Вы хотели бы арезервировать книгу (да/нет)");

        Scanner sc = new Scanner(System.in);
        String choice = sc.next();

        if (choice.equals("y"))
        {
        makeHoldRequest(borrower);
        }
        }

        else
        {
        if (!holdRequestsOperations.holdRequests.isEmpty())
        {
        boolean hasRequest = false;

        for (int i = 0; i < holdRequestsOperations.holdRequests.size() && !hasRequest;i++)
        {
        if (holdRequestsOperations.holdRequests.get(i).getBorrower() == borrower)
        hasRequest = true;

        }

        if (hasRequest)
        {
        //Если этот конкртеный полльзователь имеет наиюолее ранний резерв
        if (holdRequestsOperations.holdRequests.get(0).getBorrower() == borrower)
        serviceHoldRequest(holdRequestsOperations.holdRequests.get(0));

        else
        {
        System.out.println("\nИзвините, неокторые пользователи зарезервировали книгу раньше вас и вам придётся подождать пока их резервы будут обработаны");
        return;
        }
        }
        else
        {
        System.out.println("\nНекоторые аользователи уже зарезервировали эьу книгу и она не может быть вам выдана");

        System.out.println("Вы хотели бы зарезервировать книгу? (да/нет)");

        Scanner
        String

        if (choice.equals("да"))
        {
        makeHoldRequest(borrower);
        }

        return;
        }
        }

        //Если нет резервов,то просто выдавать книгу
        setIssuedStatus(true);

        Loan iHistory = new Loan(borrower,this,staff,null,new Date(),null,false);

        Library.getInstance().addLoan(iHistory);
        borrower.addBorrowedBook(iHistory);

        System.out.println("\nКнига" + title + "успешно выдана" + borrower.getName() + ".");
        System.out.println("\nВыдана: " + staff.getName());
        }
        }


// Returning a Book
public void returnBook(Borrower borrower, Loan l, Staff staff)
        {
        l.getBook().setIssuedStatus(false);
        l.setReturnedDate(new Date());
        l.setReceiver(staff);

        borrower.removeBorrowedBook(l);

        l.payFine();

        System.out.println("\nКнига" + l.getBook().getTitle() + "успешно возвращена" + borrower.getName() + ".");
        System.out.println("\nПолучена: " + staff.getName());
        }

        }