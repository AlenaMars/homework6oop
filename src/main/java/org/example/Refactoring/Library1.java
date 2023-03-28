package org.example.Refactoring;

// Including Header Files.
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Types;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Library {

    private String name;
    public static Librarian librarian;
    public static ArrayList <Person> persons;
    private ArrayList <Book> booksInLibrary;

    private ArrayList <Loan> loans;

    public int book_return_deadline;
    public double per_day_fine;

    public int hold_request_expiry;
    //Created object of the hold request operations
    private HoldRequestOperations holdRequestsOperations =new HoldRequestOperations();


    /*----ПО Singleton Design Pattern------------*/
    private static Library obj;

    public static Library getInstance()
    {
        if(obj==null)
        {
            obj = new Library();
        }

        return obj;
    }
    /*---------------------------------------------------------------------*/

    private Library()
    {
        name = null;
        librarian = null;
        persons = new ArrayList();

        booksInLibrary = new ArrayList();
        loans = new ArrayList();
    }


    /*------------Setter FUNCs.------------*/

    public void setReturnDeadline(int deadline)
    {
        book_return_deadline = deadline;
    }

    public void setFine(double perDayFine)
    {
        per_day_fine = perDayFine;
    }

    public void setRequestExpiry(int hrExpiry)
    {
        hold_request_expiry = hrExpiry;
    }
    /*--------------------------------------*/




    public void setName(String n)
    {
        name = n;
    }



    public int getHoldRequestExpiry()
    {
        return hold_request_expiry;
    }

    public ArrayList<Person> getPersons()
    {
        return persons;
    }

    public Librarian getLibrarian()
    {
        return librarian;
    }

    public String getLibraryName()
    {
        return name;
    }

    public ArrayList<Book> getBooks()
    {
        return booksInLibrary;
    }

    /*---------------------------------------*/

    /*-----Добавляем других людей в библиотеку----*/

    public void addClerk(Clerk c)
    {
        persons.add(c);
    }

    public void addBorrower(Borrower b)
    {
        persons.add(b);
    }


    public void addLoan(Loan l)
    {
        loans.add(l);
    }

    /*----------------------------------------------*/

    /*-----------Поиск людей в библиотеке--------------*/
    public Borrower findBorrower()
    {
        System.out.println("\nEnter Borrower's ID: ");

        int id = 0;

        Scanner scanner = new Scanner(System.in);

        try{
            id = scanner.nextInt();
        }
        catch (java.util.InputMismatchException e)
        {
            System.out.println("\nInvalid Input");
        }

        for (int i = 0; i < persons.size(); i++)
        {
            if (persons.get(i).getID() == id && persons.get(i).getClass().getSimpleName().equals("Borrower"))
                return (Borrower)(persons.get(i));
        }

        System.out.println("\nИзвините, этот ID не соответствует ни дному из ID взявших книгу .");
        return null;
    }

    public Clerk findClerk()
    {
        System.out.println("\nВведите ID клерка: ");

        int id = 0;

        Scanner scanner = new Scanner(System.in);

        try{
            id = scanner.nextInt();
        }
        catch (java.util.InputMismatchException e)
        {
            System.out.println("\nНеправильный ввод");
        }

        for (int i = 0; i < persons.size(); i++)
        {
            if (persons.get(i).getID() == id && persons.get(i).getClass().getSimpleName().equals("Clerk"))
                return (Clerk)(persons.get(i));
        }

        System.out.println("\nИзвините, этот ID не сооветствует ни одному из ID служащих");
        return null;
    }

    /*------- FUNCS. на книгах в библиотеке--------------*/
    public void addBookinLibrary(Book b)
    {
        booksInLibrary.add(b);
    }

    //Когда эта функция вызывается, только указатель книги в библиотеке удаляется. Но реальный объект книги
    // остаётся в памяти, потому что указатели книги,установленные в IssuedBooks и ReturnedBooks до сих пор указывают на эту книгу.И мы
    // сохраняем эти указатеи таким образом,что мы можем сохранить историю.
    //Но если мы не хотим сохранять историю, мы можем удалить эти указатели,установленные в IssuedBooks и ReturnedBooks также
    //В этом случае книга будет удалена из памяти
    public void removeBookfromLibrary(Book b)
    {
        boolean delete = true;

        //Проверяем взята ли книга
        for (int i = 0; i < persons.size() && delete; i++)
        {
            if (persons.get(i).getClass().getSimpleName().equals("Borrower"))
            {
                ArrayList<Loan> borBooks = ((Borrower)(persons.get(i))).getBorrowedBooks();

                for (int j = 0; j < borBooks.size() && delete; j++)
                {
                    if (borBooks.get(j).getBook() == b)
                    {
                        delete = false;
                        System.out.println("This particular book is currently borrowed by some borrower.");
                    }
                }
            }
        }

        if (delete)
        {
            System.out.println("\nCurrently this book is not borrowed by anyone.");
            ArrayList<HoldRequest> hRequests = b.getHoldRequests();

            if(!hRequests.isEmpty())
            {
                System.out.println("\nThis book might be on hold requests by some borrowers. Deleting this book will delete the relevant hold requests too.");
                System.out.println("Do you still want to delete the book? (y/n)");

                Scanner sc = new Scanner(System.in);

                while (true)
                {
                    String choice = sc.next();

                    if(choice.equals("y") || choice.equals("n"))
                    {
                        if(choice.equals("n"))
                        {
                            System.out.println("\nDelete Unsuccessful.");
                            return;
                        }
                        else
                        {

                            for (int i = 0; i < hRequests.size() && delete; i++)
                            {
                                HoldRequest hr = hRequests.get(i);
                                hr.getBorrower().removeHoldRequest(hr);
                                holdRequestsOperations.removeHoldRequest();
                            }
                        }
                    }
                    else
                        System.out.println("Invalid Input. Enter (y/n): ");
                }

            }
            else
                System.out.println("This book has no hold requests.");

            booksInLibrary.remove(b);
            System.out.println("The book is successfully removed.");
        }
        else
            System.out.println("\nDelete Unsuccessful.");
    }



    // Поиск
    public ArrayList<Book> searchForBooks() throws IOException
    {
        String choice;
        String title = "", subject = "", author = "";

        Scanner sc = new Scanner(System.in);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true)
        {
            System.out.println("\nEnter either '1' or '2' or '3' for search by Title, Subject or Author of Book respectively: ");
            choice = sc.next();

            if (choice.equals("1") || choice.equals("2") || choice.equals("3"))
                break;
            else
                System.out.println("\nWrong Input!");
        }

        if (choice.equals("1"))
        {
            System.out.println("\nEnter the Title of the Book: ");
            title = reader.readLine();
        }

        else if (choice.equals("2"))
        {
            System.out.println("\nEnter the Subject of the Book: ");
            subject = reader.readLine();
        }

        else
        {
            System.out.println("\nEnter the Author of the Book: ");
            author = reader.readLine();
        }

        ArrayList<Book> matchedBooks = new ArrayList();

        //Retrieving all the books which matched the user's search query
        for(int i = 0; i < booksInLibrary.size(); i++)
        {
            Book b = booksInLibrary.get(i);

            if (choice.equals("1"))
            {
                if (b.getTitle().equals(title))
                    matchedBooks.add(b);
            }
            else if (choice.equals("2"))
            {
                if (b.getSubject().equals(subject))
                    matchedBooks.add(b);
            }
            else
            {
                if (b.getAuthor().equals(author))
                    matchedBooks.add(b);
            }
        }

        //Printing all the matched Books
        if (!matchedBooks.isEmpty())
        {
            System.out.println("\nThese books are found: \n");

            System.out.println("------------------------------------------------------------------------------");
            System.out.println("No.\t\tTitle\t\t\tAuthor\t\t\tSubject");
            System.out.println("------------------------------------------------------------------------------");

            for (int i = 0; i < matchedBooks.size(); i++)
            {
                System.out.print(i + "-" + "\t\t");
                matchedBooks.get(i).printInfo();
                System.out.print("\n");
            }

            return matchedBooks;
        }
        else
        {
            System.out.println("\nSorry. No Books were found related to your query.");
            return null;
        }
    }



    // Показать информацию о всех книгах
    public void viewAllBooks()
    {
        if (!booksInLibrary.isEmpty())
        {
            System.out.println("\nBooks are: ");

            System.out.println("------------------------------------------------------------------------------");
            System.out.println("Номер.\t\tНазвание\t\t\tАвтор\t\t\tПредмет");
            System.out.println("------------------------------------------------------------------------------");

            for (int i = 0; i < booksInLibrary.size(); i++)
            {
                System.out.print(i + "-" + "\t\t");
                booksInLibrary.get(i).printInfo();
                System.out.print("\n");
            }
        }
        else
            System.out.println("\nНа данный момент, в библиотеке нет книг");
    }


    //Высчитываем штраф
    public double computeFine2(Borrower borrower)
    {
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("Номер.\t\tНазвание книги\t\tИмя взявшего\t\t\tДата выдачи\t\t\tДата возврата\t\t\t\tШтраф(Rs)");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        double totalFine = 0;
        double per_loan_fine = 0;

        for (int i = 0; i < loans.size(); i++)
        {
            Loan l = loans.get(i);

            if ((l.getBorrower() == borrower))
            {
                per_loan_fine = l.computeFine1();
                System.out.print(i + "-" + "\t\t" + loans.get(i).getBook().getTitle() + "\t\t\t" + loans.get(i).getBorrower().getName() + "\t\t" + loans.get(i).getIssuedDate() +  "\t\t\t" + loans.get(i).getReturnDate() + "\t\t\t\t" + per_loan_fine  + "\n");

                totalFine += per_loan_fine;
            }
        }

        return totalFine;
    }


    public void createPerson(char x)
    {
        Scanner sc = new Scanner(System.in);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\nВведите имя");
        String n = "";
        try {
            n = reader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Введите адрес ");
        String address = "";
        try {
            address = reader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
        }

        int phone = 0;

        try{
            System.out.println("Введите номер телефона: ");
            phone = sc.nextInt();
        }
        catch (java.util.InputMismatchException e)
        {
            System.out.println("\nНеправильный ввод");
        }

        //Если нужно создать служащего
        if (x == 'c')
        {
            double salary = 0;

            try{
                System.out.println("Введите зарплату ");
                salary = sc.nextDouble();
            }
            catch (java.util.InputMismatchException e)
            {
                System.out.println("\nНеправильный ввод");
            }

            Clerk c = new Clerk(-1,n,address,phone,salary,-1);
            addClerk(c);

            System.out.println("\nСлужащий по имени " + n + "создан успешно");
            System.out.println("\nВаш ID: " + c.getID());
            System.out.println("Ваш ID : " + c.getPassword());
        }


        else if (x == 'l')
        {
            double salary = 0;
            try{
                System.out.println("Введите зарплату");
                salary = sc.nextDouble();
            }
            catch (java.util.InputMismatchException e)
            {
                System.out.println("\nНеправильный ввод");
            }

            Librarian l = new Librarian(-1,n,address,phone,salary,-1);
            if(Librarian.addLibrarian(l))
            {
                System.out.println("\nБиблиотекарь по имени" + n + "создан успешно");
                System.out.println("\nВаш ID" + l.getID());
                System.out.println("Ваш пароль" + l.getPassword());
            }
        }


        else
        {
            Borrower b = new Borrower(-1,n,address,phone);
            addBorrower(b);
            System.out.println("\nВзявший книгу по имени " + n + "успешно создан");

            System.out.println("\nВаш ID " + b.getID());
            System.out.println("Ваш пароль: " + b.getPassword());
        }
    }



    public void createBook(String title, String subject, String author)
    {
        Book b = new Book(-1,title,subject,author,false);

        addBookinLibrary(b);

        System.out.println("\nКнига с названием " + b.getTitle() + " успешно создана");
    }



    //При доступе к порталу
    public Person login()
    {
        Scanner input = new Scanner(System.in);

        int id = 0;
        String password = "";

        System.out.println("\nВведите ID");

        try{
            id = input.nextInt();
        }
        catch (java.util.InputMismatchException e)
        {
            System.out.println("\nНеправильный ввод");
        }

        System.out.println("Введите пароль: ");
        password = input.next();

        for (int i = 0; i < persons.size(); i++)
        {
            if (persons.get(i).getID() == id && persons.get(i).getPassword().equals(password))
            {
                System.out.println("\nЛогин удачный");
                return persons.get(i);
            }
        }

        if(librarian!=null)
        {
            if (librarian.getID() == id && librarian.getPassword().equals(password))
            {
                System.out.println("\nЛогин удачный");
                return librarian;
            }
        }

        System.out.println("\nИзвините,неправильный пароль или ID");
        return null;
    }


    // История взятия и возврата
    public void viewHistory()
    {
        if (!loans.isEmpty())
        {
            System.out.println("\nВыданные книги ");

            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("Номер\tНазвание книги\tИмя взявшего\tИмя выдавшего\t\tДата выдачи\t\t\tИмя получившего\t\tДата возврата\t\tШтраф оплачен");
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------");

            for (int i = 0; i < loans.size(); i++)
            {
                if(loans.get(i).getIssuer()!=null)
                    System.out.print(i + "-" + "\t" + loans.get(i).getBook().getTitle() + "\t\t\t" + loans.get(i).getBorrower().getName() + "\t\t" + loans.get(i).getIssuer().getName() + "\t    " + loans.get(i).getIssuedDate());

                if (loans.get(i).getReceiver() != null)
                {
                    System.out.print("\t" + loans.get(i).getReceiver().getName() + "\t\t" + loans.get(i).getReturnDate() +"\t   " + loans.get(i).getFineStatus() + "\n");
                }
                else
                    System.out.print("\t\t" + "--" + "\t\t\t" + "--" + "\t\t" + "--" + "\n");
            }
        }
        else
            System.out.println("\nНет выданных книг");
    }









    public Connection makeConnection()
    {
        try
        {
            String host = "jdbc:derby://localhost:1527/LMS";
            String uName = "haris";
            String uPass= "123";
            Connection con = DriverManager.getConnection( host, uName, uPass );
            return con;
        }
        catch ( SQLException err )
        {
            System.out.println( err.getMessage( ) );
            return null;
        }
    }


    // Загружаем всю информацию через базу данных
    public void populateLibrary(Connection con) throws SQLException, IOException
    {
        Library lib = this;
        Statement stmt = con.createStatement( );


        String SQL = "SELECT * FROM BOOK";
        ResultSet rs = stmt.executeQuery( SQL );

        if(!rs.next())
        {
            System.out.println("\nВ библиотеке книг не найдено");
        }
        else
        {
            int maxID = 0;

            do
            {
                if(rs.getString("TITLE") !=null && rs.getString("AUTHOR")!=null && rs.getString("SUBJECT")!=null && rs.getInt("ID")!=0)
                {
                    String title=rs.getString("TITLE");
                    String author=rs.getString("AUTHOR");
                    String subject=rs.getString("SUBJECT");
                    int id= rs.getInt("ID");
                    boolean issue=rs.getBoolean("IS_ISSUED");
                    Book b = new Book(id,title,subject,author,issue);
                    addBookinLibrary(b);

                    if (maxID < id)
                        maxID = id;
                }
            }while(rs.next());


            Book.setIDCount(maxID);
        }



        SQL="SELECT ID,PNAME,ADDRESS,PASSWORD,PHONE_NO,SALARY,DESK_NO FROM PERSON INNER JOIN CLERK ON ID=C_ID INNER JOIN STAFF ON S_ID=C_ID";

        rs=stmt.executeQuery(SQL);

        if(!rs.next())
        {
            System.out.println("No clerks Found in Library");
        }
        else
        {
            do
            {
                int id=rs.getInt("ID");
                String cname=rs.getString("PNAME");
                String adrs=rs.getString("ADDRESS");
                int phn=rs.getInt("PHONE_NO");
                double sal=rs.getDouble("SALARY");
                int desk=rs.getInt("DESK_NO");
                Clerk c = new Clerk(id,cname,adrs,phn,sal,desk);

                addClerk(c);
            }
            while(rs.next());

        }

        /*-----Populating Librarian---*/
        SQL="SELECT ID,PNAME,ADDRESS,PASSWORD,PHONE_NO,SALARY,OFFICE_NO FROM PERSON INNER JOIN LIBRARIAN ON ID=L_ID INNER JOIN STAFF ON S_ID=L_ID";

        rs=stmt.executeQuery(SQL);
        if(!rs.next())
        {
            System.out.println("No Librarian Found in Library");
        }
        else
        {
            do
            {
                int id=rs.getInt("ID");
                String lname=rs.getString("PNAME");
                String adrs=rs.getString("ADDRESS");
                int phn=rs.getInt("PHONE_NO");
                double sal=rs.getDouble("SALARY");
                int off=rs.getInt("OFFICE_NO");
                Librarian l= new Librarian(id,lname,adrs,phn,sal,off);

                Librarian.addLibrarian(l);

            }while(rs.next());

        }



        SQL="SELECT ID,PNAME,ADDRESS,PASSWORD,PHONE_NO FROM PERSON INNER JOIN BORROWER ON ID=B_ID";

        rs=stmt.executeQuery(SQL);

        if(!rs.next())
        {
            System.out.println("No Borrower Found in Library");
        }
        else
        {
            do
            {
                int id=rs.getInt("ID");
                String name=rs.getString("PNAME");
                String adrs=rs.getString("ADDRESS");
                int phn=rs.getInt("PHONE_NO");

                Borrower b= new Borrower(id,name,adrs,phn);
                addBorrower(b);

            }while(rs.next());

        }

        /*----Populating Loan----*/

        SQL="SELECT * FROM LOAN";

        rs=stmt.executeQuery(SQL);
        if(!rs.next())
        {
            System.out.println("No Books Issued Yet!");
        }
        else
        {
            do
            {
                int borid=rs.getInt("BORROWER");
                int bokid=rs.getInt("BOOK");
                int iid=rs.getInt("ISSUER");
                Integer rid=(Integer)rs.getObject("RECEIVER");
                int rd=0;
                Date rdate;

                Date idate=new Date (rs.getTimestamp("ISS_DATE").getTime());

                if(rid!=null)    // if there is a receiver
                {
                    rdate=new Date (rs.getTimestamp("RET_DATE").getTime());
                    rd=(int)rid;
                }
                else
                {
                    rdate=null;
                }

                boolean fineStatus = rs.getBoolean("FINE_PAID");

                boolean set=true;

                Borrower bb = null;


                for(int i=0;i<getPersons().size() && set;i++)
                {
                    if(getPersons().get(i).getID()==borid)
                    {
                        set=false;
                        bb=(Borrower)(getPersons().get(i));
                    }
                }

                set =true;
                Staff s[]=new Staff[2];

                if(iid==getLibrarian().getID())
                {
                    s[0]=getLibrarian();
                }

                else
                {
                    for(int k=0;k<getPersons().size() && set;k++)
                    {
                        if(getPersons().get(k).getID()==iid && getPersons().get(k).getClass().getSimpleName().equals("Clerk"))
                        {
                            set=false;
                            s[0]=(Clerk)(getPersons().get(k));
                        }
                    }
                }

                set=true;
                // Если ещё не возвращена
                if(rid==null)
                {
                    s[1]=null;  // нет получателя
                    rdate=null;
                }
                else
                {
                    if(rd==getLibrarian().getID())
                        s[1]=getLibrarian();

                    else
                    {
                        for(int k=0;k<getPersons().size() && set;k++)
                        {
                            if(getPersons().get(k).getID()==rd && getPersons().get(k).getClass().getSimpleName().equals("Clerk"))
                            {
                                set=false;
                                s[1]=(Clerk)(getPersons().get(k));
                            }
                        }
                    }
                }

                set=true;

                ArrayList<Book> books = getBooks();

                for(int k=0;k<books.size() && set;k++)
                {
                    if(books.get(k).getID()==bokid)
                    {
                        set=false;
                        Loan l = new Loan(bb,books.get(k),s[0],s[1],idate,rdate,fineStatus);
                        loans.add(l);
                    }
                }

            }while(rs.next());
        }



        SQL="SELECT * FROM ON_HOLD_BOOK";

        rs=stmt.executeQuery(SQL);
        if(!rs.next())
        {
            System.out.println("Книги ещё не зарезервированы");
        }
        else
        {
            do
            {
                int borid=rs.getInt("BORROWER");
                int bokid=rs.getInt("BOOK");
                Date off=new Date (rs.getDate("REQ_DATE").getTime());

                boolean set=true;
                Borrower bb =null;

                ArrayList<Person> persons = lib.getPersons();

                for(int i=0;i<persons.size() && set;i++)
                {
                    if(persons.get(i).getID()==borid)
                    {
                        set=false;
                        bb=(Borrower)(persons.get(i));
                    }
                }

                set=true;

                ArrayList<Book> books = lib.getBooks();

                for(int i=0;i<books.size() && set;i++)
                {
                    if(books.get(i).getID()==bokid)
                    {
                        set=false;
                        HoldRequest hbook= new HoldRequest(bb,books.get(i),off);
                        holdRequestsOperations.addHoldRequest(hbook);
                        bb.addHoldRequest(hbook);
                    }
                }
            }while(rs.next());
        }

            /


        SQL="SELECT ID,BOOK FROM PERSON INNER JOIN BORROWER ON ID=B_ID INNER JOIN BORROWED_BOOK ON B_ID=BORROWER ";

        rs=stmt.executeQuery(SQL);

        if(!rs.next())
        {
            System.out.println("No Borrower has borrowed yet from Library");
        }
        else
        {

            do
            {
                int id=rs.getInt("ID");      // borrower
                int bid=rs.getInt("BOOK");   // book

                Borrower bb=null;
                boolean set=true;
                boolean okay=true;

                for(int i=0;i<lib.getPersons().size() && set;i++)
                {
                    if(lib.getPersons().get(i).getClass().getSimpleName().equals("Borrower"))
                    {
                        if(lib.getPersons().get(i).getID()==id)
                        {
                            set =false;
                            bb=(Borrower)(lib.getPersons().get(i));
                        }
                    }
                }

                set=true;

                ArrayList<Loan> books = loans;

                for(int i=0;i<books.size() && set;i++)
                {
                    if(books.get(i).getBook().getID()==bid &&books.get(i).getReceiver()==null )
                    {
                        set=false;
                        Loan bBook= new Loan(bb,books.get(i).getBook(),books.get(i).getIssuer(),null,books.get(i).getIssuedDate(),null,books.get(i).getFineStatus());
                        bb.addBorrowedBook(bBook);
                    }
                }

            }while(rs.next());
        }

        ArrayList<Person> persons = lib.getPersons();

        /* Устанавливаем счётчик ID человека */
        int max=0;

        for(int i=0;i<persons.size();i++)
        {
            if (max < persons.get(i).getID())
                max=persons.get(i).getID();
        }

        Person.setIDCount(max);
    }


    // Заполняем изменения обратно в базу данных
    public void fillItBack(Connection con) throws SQLException,SQLIntegrityConstraintViolationException
    {


        String template = "DELETE FROM LIBRARY.LOAN";
        PreparedStatement stmts = con.prepareStatement(template);

        stmts.executeUpdate();



        template = "DELETE FROM LIBRARY.BORROWED_BOOK";
        stmts = con.prepareStatement(template);

        stmts.executeUpdate();



        template = "DELETE FROM LIBRARY.ON_HOLD_BOOK";
        stmts = con.prepareStatement(template);

        stmts.executeUpdate();



        template = "DELETE FROM LIBRARY.BOOK";
        stmts = con.prepareStatement(template);

        stmts.executeUpdate();



        template = "DELETE FROM LIBRARY.CLERK";
        stmts = con.prepareStatement(template);

        stmts.executeUpdate();



        template = "DELETE FROM LIBRARY.LIBRARIAN";
        stmts = con.prepareStatement(template);

        stmts.executeUpdate();



        template = "DELETE FROM LIBRARY.BORROWER";
        stmts = con.prepareStatement(template);

        stmts.executeUpdate();



        template = "DELETE FROM LIBRARY.STAFF";
        stmts = con.prepareStatement(template);

        stmts.executeUpdate();



        template = "DELETE FROM LIBRARY.PERSON";
        stmts = con.prepareStatement(template);

        stmts.executeUpdate();

        Library lib = this;


        for(int i=0;i<lib.getPersons().size();i++)
        {
            template = "INSERT INTO LIBRARY.PERSON (ID,PNAME,PASSWORD,ADDRESS,PHONE_NO) values (?,?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);

            stmt.setInt(1, lib.getPersons().get(i).getID());
            stmt.setString(2, lib.getPersons().get(i).getName());
            stmt.setString(3,  lib.getPersons().get(i).getPassword());
            stmt.setString(4, lib.getPersons().get(i).getAddress());
            stmt.setInt(5, lib.getPersons().get(i).getPhoneNumber());

            stmt.executeUpdate();
        }


        for(int i=0;i<lib.getPersons().size();i++)
        {
            if (lib.getPersons().get(i).getClass().getSimpleName().equals("Clerk"))
            {
                template = "INSERT INTO LIBRARY.STAFF (S_ID,TYPE,SALARY) values (?,?,?)";
                PreparedStatement stmt = con.prepareStatement(template);

                stmt.setInt(1,lib.getPersons().get(i).getID());
                stmt.setString(2,"Clerk");
                stmt.setDouble(3, ((Clerk)(lib.getPersons().get(i))).getSalary());

                stmt.executeUpdate();

                template = "INSERT INTO LIBRARY.CLERK (C_ID,DESK_NO) values (?,?)";
                stmt = con.prepareStatement(template);

                stmt.setInt(1,lib.getPersons().get(i).getID());
                stmt.setInt(2, ((Clerk)(lib.getPersons().get(i))).deskNo);

                stmt.executeUpdate();
            }

        }

        if(lib.getLibrarian()!=null)
        {
            template = "INSERT INTO LIBRARY.STAFF (S_ID,TYPE,SALARY) values (?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);

            stmt.setInt(1, lib.getLibrarian().getID());
            stmt.setString(2,"Librarian");
            stmt.setDouble(3,lib.getLibrarian().getSalary());

            stmt.executeUpdate();

            template = "INSERT INTO LIBRARY.LIBRARIAN (L_ID,OFFICE_NO) values (?,?)";
            stmt = con.prepareStatement(template);

            stmt.setInt(1,lib.getLibrarian().getID());
            stmt.setInt(2, lib.getLibrarian().officeNo);

            stmt.executeUpdate();
        }

        /* Filling Borrower's Table*/
        for(int i=0;i<lib.getPersons().size();i++)
        {
            if (lib.getPersons().get(i).getClass().getSimpleName().equals("Borrower"))
            {
                template = "INSERT INTO LIBRARY.BORROWER(B_ID) values (?)";
                PreparedStatement stmt = con.prepareStatement(template);

                stmt.setInt(1, lib.getPersons().get(i).getID());

                stmt.executeUpdate();
            }
        }

        ArrayList<Book> books = lib.getBooks();


        for(int i=0;i<books.size();i++)
        {
            template = "INSERT INTO LIBRARY.BOOK (ID,TITLE,AUTHOR,SUBJECT,IS_ISSUED) values (?,?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);

            stmt.setInt(1,books.get(i).getID());
            stmt.setString(2,books.get(i).getTitle());
            stmt.setString(3, books.get(i).getAuthor());
            stmt.setString(4, books.get(i).getSubject());
            stmt.setBoolean(5, books.get(i).getIssuedStatus());
            stmt.executeUpdate();

        }


        for(int i=0;i<loans.size();i++)
        {
            template = "INSERT INTO LIBRARY.LOAN(L_ID,BORROWER,BOOK,ISSUER,ISS_DATE,RECEIVER,RET_DATE,FINE_PAID) values (?,?,?,?,?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);

            stmt.setInt(1,i+1);
            stmt.setInt(2,loans.get(i).getBorrower().getID());
            stmt.setInt(3,loans.get(i).getBook().getID());
            stmt.setInt(4,loans.get(i).getIssuer().getID());
            stmt.setTimestamp(5,new java.sql.Timestamp(loans.get(i).getIssuedDate().getTime()));
            stmt.setBoolean(8,loans.get(i).getFineStatus());
            if(loans.get(i).getReceiver()==null)
            {
                stmt.setNull(6,Types.INTEGER);
                stmt.setDate(7,null);
            }
            else
            {
                stmt.setInt(6,loans.get(i).getReceiver().getID());
                stmt.setTimestamp(7,new java.sql.Timestamp(loans.get(i).getReturnDate().getTime()));
            }

            stmt.executeUpdate();

        }

        /* Filling On_Hold_ Table*/

        int x=1;
        for(int i=0;i<lib.getBooks().size();i++)
        {
            for(int j=0;j<lib.getBooks().get(i).getHoldRequests().size();j++)
            {
                template = "INSERT INTO LIBRARY.ON_HOLD_BOOK(REQ_ID,BOOK,BORROWER,REQ_DATE) values (?,?,?,?)";
                PreparedStatement stmt = con.prepareStatement(template);

                stmt.setInt(1,x);
                stmt.setInt(3,lib.getBooks().get(i).getHoldRequests().get(j).getBorrower().getID());
                stmt.setInt(2,lib.getBooks().get(i).getHoldRequests().get(j).getBook().getID());
                stmt.setDate(4,new java.sql.Date(lib.getBooks().get(i).getHoldRequests().get(j).getRequestDate().getTime()));

                stmt.executeUpdate();
                x++;

            }
        }
        /*for(int i=0;i<lib.getBooks().size();i++)
        {
            for(int j=0;j<lib.getBooks().get(i).getHoldRequests().size();j++)
            {
            template = "INSERT INTO LIBRARY.ON_HOLD_BOOK(REQ_ID,BOOK,BORROWER,REQ_DATE) values (?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);

            stmt.setInt(1,i+1);
            stmt.setInt(3,lib.getBooks().get(i).getHoldRequests().get(j).getBorrower().getID());
            stmt.setInt(2,lib.getBooks().get(i).getHoldRequests().get(j).getBook().getID());
            stmt.setDate(4,new java.sql.Date(lib.getBooks().get(i).getHoldRequests().get(j).getRequestDate().getTime()));

            stmt.executeUpdate();
            }
        }*/


        for(int i=0;i<lib.getBooks().size();i++)
        {
            if(lib.getBooks().get(i).getIssuedStatus()==true)
            {
                boolean set=true;
                for(int j=0;j<loans.size() && set ;j++)
                {
                    if(lib.getBooks().get(i).getID()==loans.get(j).getBook().getID())
                    {
                        if(loans.get(j).getReceiver()==null)
                        {
                            template = "INSERT INTO LIBRARY.BORROWED_BOOK(BOOK,BORROWER) values (?,?)";
                            PreparedStatement stmt = con.prepareStatement(template);
                            stmt.setInt(1,loans.get(j).getBook().getID());
                            stmt.setInt(2,loans.get(j).getBorrower().getID());

                            stmt.executeUpdate();
                            set=false;
                        }
                    }

                }

            }
        }
    } // Заполнение закончено
