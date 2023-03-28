package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Library {
    private String name = null;
    private Librarian librarian = null;
    private ArrayList<Person> persons = new ArrayList();
    private ArrayList<Book> booksInLibrary = new ArrayList();
    private ArrayList<Loan> loans = new ArrayList();
    public int book_return_deadline;
    public double per_day_fine;
    public int hold_request_expiry;
    private static Library obj;

    public static Library getInstance() {
        if (obj == null) {
            obj = new Library();
        }

        return obj;
    }

    private Library() {
    }

    public void setReturnDeadline(int deadline) {
        this.book_return_deadline = deadline;
    }

    public void setFine(double perDayFine) {
        this.per_day_fine = perDayFine;
    }

    public void setRequestExpiry(int hrExpiry) {
        this.hold_request_expiry = hrExpiry;
    }

    public void setName(String n) {
        this.name = n;
    }

    public int getHoldRequestExpiry() {
        return this.hold_request_expiry;
    }

    public ArrayList<Person> getPersons() {
        return this.persons;
    }

    public Librarian getLibrarian() {
        return this.librarian;
    }

    public String getLibraryName() {
        return this.name;
    }

    public ArrayList<Book> getBooks() {
        return this.booksInLibrary;
    }

    public boolean addLibrarian(Librarian lib) {
        if (this.librarian == null) {
            this.librarian = lib;
            this.persons.add(this.librarian);
            return true;
        } else {
            System.out.println("\nИзвините, в библиотеке уже есть библиотекарь.Новый библиотекарь не может быть добавлен");
            return false;
        }
    }

    public void addClerk(Clerk c) {
        this.persons.add(c);
    }

    public void addBorrower(Borrower b) {
        this.persons.add(b);
    }

    public void addLoan(Loan l) {
        this.loans.add(l);
    }

    public Borrower findBorrower() {
        System.out.println("\nВведите ID одолжившего книгу");
        int id = 0;
        Scanner scanner = new Scanner(System.in);

        try {
            id = scanner.nextInt();
        } catch (InputMismatchException var4) {
            System.out.println("\nНеправильный ввод");
        }

        for(int i = 0; i < this.persons.size(); ++i) {
            if (((Person)this.persons.get(i)).getID() == id && ((Person)this.persons.get(i)).getClass().getSimpleName().equals("Borrower")) {
                return (Borrower)this.persons.get(i);
            }
        }

        System.out.println("\nИзвините, этот ID не подходит ни одному отдолжившему");
        return null;
    }

    public Clerk findClerk() {
        System.out.println("\n Введите ID служащего");
        int id = 0;
        Scanner scanner = new Scanner(System.in);

        try {
            id = scanner.nextInt();
        } catch (InputMismatchException var4) {
            System.out.println("\nНеправилльный ввод");
        }

        for(int i = 0; i < this.persons.size(); ++i) {
            if (((Person)this.persons.get(i)).getID() == id && ((Person)this.persons.get(i)).getClass().getSimpleName().equals("Clerk")) {
                return (Clerk)this.persons.get(i);
            }
        }

        System.out.println("\nИзвиите, этот ID не соответствует ни одному ID служащего");
        return null;
    }

    public void addBookinLibrary(Book b) {
        this.booksInLibrary.add(b);
    }

    public void removeBookfromLibrary(Book b) {
        boolean delete = true;

        for(int i = 0; i < this.persons.size() && delete; ++i) {
            if (((Person)this.persons.get(i)).getClass().getSimpleName().equals("Borrower")) {
                ArrayList<Loan> borBooks = ((Borrower)this.persons.get(i)).getBorrowedBooks();

                for(int j = 0; j < borBooks.size() && delete; ++j) {
                    if (((Loan)borBooks.get(j)).getBook() == b) {
                        delete = false;
                        System.out.println("Эта конкретная книга, на данный момент,отдолжена");
                    }
                }
            }
        }

        if (delete) {
            System.out.println("\nНа данный момент,книга не отдолжена");
            ArrayList<HoldRequest> hRequests = b.getHoldRequests();
            if (!hRequests.isEmpty()) {
                System.out.println("\nЭта книга может быть зареервирована. Удаление книги удалит всю связанные с ней резервы");
                System.out.println("Вы всё ещё хотите удалить книгу?(да/нет)");
                Scanner sc = new Scanner(System.in);

                while(true) {
                    while(true) {
                        String choice = sc.next();
                        if (!choice.equals("y") && !choice.equals("n")) {
                            System.out.println("Неправильный ввод.Ввести (да/нет)");
                        } else {
                            if (choice.equals("n")) {
                                System.out.println("\nУдалить не получилось");
                                return;
                            }

                            for(int i = 0; i < hRequests.size() && delete; ++i) {
                                HoldRequest hr = (HoldRequest)hRequests.get(i);
                                hr.getBorrower().removeHoldRequest(hr);
                                b.removeHoldRequest();
                            }
                        }
                    }
                }
            }

            System.out.println("По этой книге нет запросов на резервирование");
            this.booksInLibrary.remove(b);
            System.out.println("Книга успешно удалена");
        } else {
            System.out.println("\nУдалить не получилось");
        }

    }

    public ArrayList<Book> searchForBooks() throws IOException {
        String title = "";
        String subject = "";
        String author = "";
        Scanner sc = new Scanner(System.in);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while(true) {
            System.out.println("\nВвести 1, 2, 3 для поиска названия, предмета,авора или книги,соответственно");
            String choice = sc.next();
            if (choice.equals("1") || choice.equals("2") || choice.equals("3")) {
                if (choice.equals("1")) {
                    System.out.println("\nВведите название книги ");
                    title = reader.readLine();
                } else if (choice.equals("2")) {
                    System.out.println("\nВведите предмет книги");
                    subject = reader.readLine();
                } else {
                    System.out.println("\nВведите автора книги");
                    author = reader.readLine();
                }

                ArrayList<Book> matchedBooks = new ArrayList();

                int i;
                for(i = 0; i < this.booksInLibrary.size(); ++i) {
                    Book b = (Book)this.booksInLibrary.get(i);
                    if (choice.equals("1")) {
                        if (b.getTitle().equals(title)) {
                            matchedBooks.add(b);
                        }
                    } else if (choice.equals("2")) {
                        if (b.getSubject().equals(subject)) {
                            matchedBooks.add(b);
                        }
                    } else if (b.getAuthor().equals(author)) {
                        matchedBooks.add(b);
                    }
                }

                if (matchedBooks.isEmpty()) {
                    System.out.println("\nИзвините, книг,связанных с вашим запросом не найдено");
                    return null;
                } else {
                    System.out.println("\nЭти книги найдены\n");
                    System.out.println("------------------------------------------------------------------------------");
                    System.out.println("Нет\t\tНазвания\t\t\tАвтора\t\t\tПредмета");
                    System.out.println("------------------------------------------------------------------------------");

                    for(i = 0; i < matchedBooks.size(); ++i) {
                        System.out.print(i + "-\t\t");
                        ((Book)matchedBooks.get(i)).printInfo();
                        System.out.print("\n");
                    }

                    return matchedBooks;
                }
            }

            System.out.println("\nНеправильный ввод!");
        }
    }

    public void viewAllBooks() {
        if (!this.booksInLibrary.isEmpty()) {
            System.out.println("\nКниги: ");
            System.out.println("------------------------------------------------------------------------------");
            System.out.println("Нет\t\tНазвания\t\t\tАвтора\t\t\tПредмета");
            System.out.println("------------------------------------------------------------------------------");

            for(int i = 0; i < this.booksInLibrary.size(); ++i) {
                System.out.print(i + "-\t\t");
                ((Book)this.booksInLibrary.get(i)).printInfo();
                System.out.print("\n");
            }
        } else {
            System.out.println("\nНа данный момент,в библиотеке нет книг");
        }

    }

    public double computeFine2(Borrower borrower) {
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("No.\t\tНазвание книги\t\tИмя одолжившего\t\t\tДата выдачи\t\t\tДата возврата\t\t\t\tШтраф(Rs)");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        double totalFine = 0.0;
        double per_loan_fine = 0.0;

        for(int i = 0; i < this.loans.size(); ++i) {
            Loan l = (Loan)this.loans.get(i);
            if (l.getBorrower() == borrower) {
                per_loan_fine = l.computeFine1();
                System.out.print(i + "-\t\t" + ((Loan)this.loans.get(i)).getBook().getTitle() + "\t\t\t" + ((Loan)this.loans.get(i)).getBorrower().getName() + "\t\t" + ((Loan)this.loans.get(i)).getIssuedDate() + "\t\t\t" + ((Loan)this.loans.get(i)).getReturnDate() + "\t\t\t\t" + per_loan_fine + "\n");
                totalFine += per_loan_fine;
            }
        }

        return totalFine;
    }

    public void createPerson(char x) {
        Scanner sc = new Scanner(System.in);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\nВведите имя ");
        String n = "";

        try {
            n = reader.readLine();
        } catch (IOException var14) {
            Logger.getLogger(Library.class.getName()).log(Level.SEVERE, (String)null, var14);
        }

        System.out.println("Ввести адрес ");
        String address = "";

        try {
            address = reader.readLine();
        } catch (IOException var13) {
            Logger.getLogger(Library.class.getName()).log(Level.SEVERE, (String)null, var13);
        }

        int phone = 0;

        try {
            System.out.println("Введите телефонный номер");
            phone = sc.nextInt();
        } catch (InputMismatchException var12) {
            System.out.println("\nНеправильный ввод");
        }

        double salary;
        if (x == 'c') {
            salary = 0.0;

            try {
                System.out.println("Введите зарплату ");
                salary = sc.nextDouble();
            } catch (InputMismatchException var11) {
                System.out.println("\nНеверный ввод");
            }

            Clerk c = new Clerk(-1, n, address, phone, salary, -1);
            this.addClerk(c);
            System.out.println("\nИмя служащего" + n + "создано успешно");
            System.out.println("\nYour ID is : " + c.getID());
            System.out.println("Ваш пароль" + c.getPassword());
        } else if (x == 'l') {
            salary = 0.0;

            try {
                System.out.println("Введите зарплату");
                salary = sc.nextDouble();
            } catch (InputMismatchException var10) {
                System.out.println("\nНеправильный ввод");
            }

            Librarian l = new Librarian(-1, n, address, phone, salary, -1);
            if (this.addLibrarian(l)) {
                System.out.println("\nИмя библиотекаря" + n + " создано успешно");
                System.out.println("\nВаш ID" + l.getID());
                System.out.println("Ваш пароль" + l.getPassword());
            }
        } else {
            Borrower b = new Borrower(-1, n, address, phone);
            this.addBorrower(b);
            System.out.println("\Имя одолжившего" + n + "создано успешно");
            System.out.println("\nВаш ID" + b.getID());
            System.out.println("Ваш пароль" + b.getPassword());
        }

    }

    public void createBook(String title, String subject, String author) {
        Book b = new Book(-1, title, subject, author, false);
        this.addBookinLibrary(b);
        System.out.println("\nКнига с названием" + b.getTitle() + "успешно создана");
    }

    public Person login() {
        Scanner input = new Scanner(System.in);
        int id = 0;
        String password = "";
        System.out.println("\nВведите ID");

        try {
            id = input.nextInt();
        } catch (InputMismatchException var5) {
            System.out.println("\Неправильный ввод");
        }

        System.out.println("Введите пароль");
        password = input.next();

        for(int i = 0; i < this.persons.size(); ++i) {
            if (((Person)this.persons.get(i)).getID() == id && ((Person)this.persons.get(i)).getPassword().equals(password)) {
                System.out.println("\nУспешный логин");
                return (Person)this.persons.get(i);
            }
        }

        if (this.librarian != null && this.librarian.getID() == id && this.librarian.getPassword().equals(password)) {
            System.out.println("\nУспешный логин");
            return this.librarian;
        } else {
            System.out.println("\nИзвините,неверный пароль или ID");
            return null;
        }
    }

    public void viewHistory() {
        if (!this.loans.isEmpty()) {
            System.out.println("\nВыданные книги");
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("Нет\tНазвание книги\tИмя отдолжившего\t  Имя выдавшего\t\tДата выдачи\t\t\tИмя получившего\t\tДата возврата\t\tОплаченный штраф");
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------");

            for(int i = 0; i < this.loans.size(); ++i) {
                if (((Loan)this.loans.get(i)).getIssuer() != null) {
                    System.out.print(i + "-\t" + ((Loan)this.loans.get(i)).getBook().getTitle() + "\t\t\t" + ((Loan)this.loans.get(i)).getBorrower().getName() + "\t\t" + ((Loan)this.loans.get(i)).getIssuer().getName() + "\t    " + ((Loan)this.loans.get(i)).getIssuedDate());
                }

                if (((Loan)this.loans.get(i)).getReceiver() != null) {
                    System.out.print("\t" + ((Loan)this.loans.get(i)).getReceiver().getName() + "\t\t" + ((Loan)this.loans.get(i)).getReturnDate() + "\t   " + ((Loan)this.loans.get(i)).getFineStatus() + "\n");
                } else {
                    System.out.print("\t\t--\t\t\t--\t\t--\n");
                }
            }
        } else {
            System.out.println("\nНет выданных книг");
        }

    }

    public Connection makeConnection() {
        try {
            String host = "jdbc:derby://localhost:1527/LMS";
            String uName = "haris";
            String uPass = "123";
            Connection con = DriverManager.getConnection(host, uName, uPass);
            return con;
        } catch (SQLException var5) {
            System.out.println(var5.getMessage());
            return null;
        }
    }

    public void populateLibrary(Connection con) throws SQLException, IOException {
        Library lib = this;
        Statement stmt = con.createStatement();
        String SQL = "SELECT * FROM BOOK";
        ResultSet rs = stmt.executeQuery(SQL);
        int id;
        String name;
        String adrs;
        int rd;
        if (!rs.next()) {
            System.out.println("\nКниги не найдены в библиотеке");
        } else {
            id = 0;

            do {
                if (rs.getString("TITLE") != null && rs.getString("AUTHOR") != null && rs.getString("SUBJECT") != null && rs.getInt("ID") != 0) {
                    name = rs.getString("TITLE");
                    adrs = rs.getString("AUTHOR");
                    String subject = rs.getString("SUBJECT");
                    rd = rs.getInt("ID");
                    boolean issue = rs.getBoolean("IS_ISSUED");
                    Book b = new Book(rd, name, subject, adrs, issue);
                    this.addBookinLibrary(b);
                    if (id < rd) {
                        id = rd;
                    }
                }
            } while(rs.next());

            Book.setIDCount(id);
        }

        SQL = "SELECT ID,PNAME,ADDRESS,PASSWORD,PHONE_NO,SALARY,DESK_NO FROM PERSON INNER JOIN CLERK ON ID=C_ID INNER JOIN STAFF ON S_ID=C_ID";
        rs = stmt.executeQuery(SQL);
        int phn;
        double sal;
        int i;
        if (!rs.next()) {
            System.out.println("Служащие не найдены");
        } else {
            do {
                id = rs.getInt("ID");
                name = rs.getString("PNAME");
                adrs = rs.getString("ADDRESS");
                phn = rs.getInt("PHONE_NO");
                sal = rs.getDouble("SALARY");
                i = rs.getInt("DESK_NO");
                Clerk c = new Clerk(id, name, adrs, phn, sal, i);
                this.addClerk(c);
            } while(rs.next());
        }

        SQL = "SELECT ID,PNAME,ADDRESS,PASSWORD,PHONE_NO,SALARY,OFFICE_NO FROM PERSON INNER JOIN LIBRARIAN ON ID=L_ID INNER JOIN STAFF ON S_ID=L_ID";
        rs = stmt.executeQuery(SQL);
        if (!rs.next()) {
            System.out.println("Библиотекари не найдены");
        } else {
            do {
                id = rs.getInt("ID");
                name = rs.getString("PNAME");
                adrs = rs.getString("ADDRESS");
                phn = rs.getInt("PHONE_NO");
                sal = rs.getDouble("SALARY");
                i = rs.getInt("OFFICE_NO");
                Librarian l = new Librarian(id, name, adrs, phn, sal, i);
                this.addLibrarian(l);
            } while(rs.next());
        }

        SQL = "SELECT ID,PNAME,ADDRESS,PASSWORD,PHONE_NO FROM PERSON INNER JOIN BORROWER ON ID=B_ID";
        rs = stmt.executeQuery(SQL);
        Borrower bb;
        if (!rs.next()) {
            System.out.println("Одолжившие не найдены в библиотеке");
        } else {
            do {
                id = rs.getInt("ID");
                name = rs.getString("PNAME");
                adrs = rs.getString("ADDRESS");
                phn = rs.getInt("PHONE_NO");
                bb = new Borrower(id, name, adrs, phn);
                this.addBorrower(bb);
            } while(rs.next());
        }

        SQL = "SELECT * FROM LOAN";
        rs = stmt.executeQuery(SQL);
        int bokid;
        int i;
        if (!rs.next()) {
            System.out.println("Книги ещё не выданы!");
        } else {
            do {
                id = rs.getInt("BORROWER");
                bokid = rs.getInt("BOOK");
                i = rs.getInt("ISSUER");
                Integer rid = (Integer)rs.getObject("RECEIVER");
                rd = 0;
                Date idate = new Date(rs.getTimestamp("ISS_DATE").getTime());
                Date rdate;
                if (rid != null) {
                    rdate = new Date(rs.getTimestamp("RET_DATE").getTime());
                    rd = rid;
                } else {
                    rdate = null;
                }

                boolean fineStatus = rs.getBoolean("FINE_PAID");
                boolean set = true;
                Borrower bb = null;

                for(int i = 0; i < this.getPersons().size() && set; ++i) {
                    if (((Person)this.getPersons().get(i)).getID() == id) {
                        set = false;
                        bb = (Borrower)this.getPersons().get(i);
                    }
                }

                set = true;
                Staff[] s = new Staff[2];
                int k;
                if (i == this.getLibrarian().getID()) {
                    s[0] = this.getLibrarian();
                } else {
                    for(k = 0; k < this.getPersons().size() && set; ++k) {
                        if (((Person)this.getPersons().get(k)).getID() == i && ((Person)this.getPersons().get(k)).getClass().getSimpleName().equals("Clerk")) {
                            set = false;
                            s[0] = (Clerk)this.getPersons().get(k);
                        }
                    }
                }

                set = true;
                if (rid == null) {
                    s[1] = null;
                    rdate = null;
                } else if (rd == this.getLibrarian().getID()) {
                    s[1] = this.getLibrarian();
                } else {
                    for(k = 0; k < this.getPersons().size() && set; ++k) {
                        if (((Person)this.getPersons().get(k)).getID() == rd && ((Person)this.getPersons().get(k)).getClass().getSimpleName().equals("Clerk")) {
                            set = false;
                            s[1] = (Clerk)this.getPersons().get(k);
                        }
                    }
                }

                set = true;
                ArrayList<Book> books = this.getBooks();

                for(int k = 0; k < books.size() && set; ++k) {
                    if (((Book)books.get(k)).getID() == bokid) {
                        set = false;
                        Loan l = new Loan(bb, (Book)books.get(k), s[0], s[1], idate, rdate, fineStatus);
                        this.loans.add(l);
                    }
                }
            } while(rs.next());
        }

        SQL = "SELECT * FROM ON_HOLD_BOOK";
        rs = stmt.executeQuery(SQL);
        boolean set;
        ArrayList books;
        if (!rs.next()) {
            System.out.println("Книги ещё не зарезервированы");
        } else {
            do {
                id = rs.getInt("BORROWER");
                bokid = rs.getInt("BOOK");
                Date off = new Date(rs.getDate("REQ_DATE").getTime());
                set = true;
                bb = null;
                books = lib.getPersons();

                for(i = 0; i < books.size() && set; ++i) {
                    if (((Person)books.get(i)).getID() == id) {
                        set = false;
                        bb = (Borrower)books.get(i);
                    }
                }

                set = true;
                ArrayList<Book> books = lib.getBooks();

                for(int i = 0; i < books.size() && set; ++i) {
                    if (((Book)books.get(i)).getID() == bokid) {
                        set = false;
                        HoldRequest hbook = new HoldRequest(bb, (Book)books.get(i), off);
                        ((Book)books.get(i)).addHoldRequest(hbook);
                        bb.addHoldRequest(hbook);
                    }
                }
            } while(rs.next());
        }

        SQL = "SELECT ID,BOOK FROM PERSON INNER JOIN BORROWER ON ID=B_ID INNER JOIN BORROWED_BOOK ON B_ID=BORROWER ";
        rs = stmt.executeQuery(SQL);
        if (!rs.next()) {
            System.out.println("Никто не отдолжил из библиотеки");
        } else {
            do {
                id = rs.getInt("ID");
                bokid = rs.getInt("BOOK");
                Borrower bb = null;
                set = true;
                boolean okay = true;

                for(int i = 0; i < lib.getPersons().size() && set; ++i) {
                    if (((Person)lib.getPersons().get(i)).getClass().getSimpleName().equals("Borrower") && ((Person)lib.getPersons().get(i)).getID() == id) {
                        set = false;
                        bb = (Borrower)lib.getPersons().get(i);
                    }
                }

                set = true;
                books = this.loans;

                for(i = 0; i < books.size() && set; ++i) {
                    if (((Loan)books.get(i)).getBook().getID() == bokid && ((Loan)books.get(i)).getReceiver() == null) {
                        set = false;
                        Loan bBook = new Loan(bb, ((Loan)books.get(i)).getBook(), ((Loan)books.get(i)).getIssuer(), (Staff)null, ((Loan)books.get(i)).getIssuedDate(), (Date)null, ((Loan)books.get(i)).getFineStatus());
                        bb.addBorrowedBook(bBook);
                    }
                }
            } while(rs.next());
        }

        ArrayList<Person> persons = lib.getPersons();
        bokid = 0;

        for(i = 0; i < persons.size(); ++i) {
            if (bokid < ((Person)persons.get(i)).getID()) {
                bokid = ((Person)persons.get(i)).getID();
            }
        }

        Person.setIDCount(bokid);
    }

    public void fillItBack(Connection con) throws SQLException, SQLIntegrityConstraintViolationException {
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

        int i;
        PreparedStatement stmt;
        for(i = 0; i < lib.getPersons().size(); ++i) {
            template = "INSERT INTO LIBRARY.PERSON (ID,PNAME,PASSWORD,ADDRESS,PHONE_NO) values (?,?,?,?,?)";
            stmt = con.prepareStatement(template);
            stmt.setInt(1, ((Person)lib.getPersons().get(i)).getID());
            stmt.setString(2, ((Person)lib.getPersons().get(i)).getName());
            stmt.setString(3, ((Person)lib.getPersons().get(i)).getPassword());
            stmt.setString(4, ((Person)lib.getPersons().get(i)).getAddress());
            stmt.setInt(5, ((Person)lib.getPersons().get(i)).getPhoneNumber());
            stmt.executeUpdate();
        }

        for(i = 0; i < lib.getPersons().size(); ++i) {
            if (((Person)lib.getPersons().get(i)).getClass().getSimpleName().equals("Clerk")) {
                template = "INSERT INTO LIBRARY.STAFF (S_ID,TYPE,SALARY) values (?,?,?)";
                stmt = con.prepareStatement(template);
                stmt.setInt(1, ((Person)lib.getPersons().get(i)).getID());
                stmt.setString(2, "Служащий");
                stmt.setDouble(3, ((Clerk)lib.getPersons().get(i)).getSalary());
                stmt.executeUpdate();
                template = "INSERT INTO LIBRARY.CLERK (C_ID,DESK_NO) values (?,?)";
                stmt = con.prepareStatement(template);
                stmt.setInt(1, ((Person)lib.getPersons().get(i)).getID());
                stmt.setInt(2, ((Clerk)lib.getPersons().get(i)).deskNo);
                stmt.executeUpdate();
            }
        }

        if (lib.getLibrarian() != null) {
            template = "INSERT INTO LIBRARY.STAFF (S_ID,TYPE,SALARY) values (?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);
            stmt.setInt(1, lib.getLibrarian().getID());
            stmt.setString(2, "Библиотекарь");
            stmt.setDouble(3, lib.getLibrarian().getSalary());
            stmt.executeUpdate();
            template = "INSERT INTO LIBRARY.LIBRARIAN (L_ID,OFFICE_NO) values (?,?)";
            stmt = con.prepareStatement(template);
            stmt.setInt(1, lib.getLibrarian().getID());
            stmt.setInt(2, lib.getLibrarian().officeNo);
            stmt.executeUpdate();
        }

        for(i = 0; i < lib.getPersons().size(); ++i) {
            if (((Person)lib.getPersons().get(i)).getClass().getSimpleName().equals("Borrower")) {
                template = "INSERT INTO LIBRARY.BORROWER(B_ID) values (?)";
                stmt = con.prepareStatement(template);
                stmt.setInt(1, ((Person)lib.getPersons().get(i)).getID());
                stmt.executeUpdate();
            }
        }

        ArrayList<Book> books = lib.getBooks();

        PreparedStatement stmt;
        int x;
        for(x = 0; x < books.size(); ++x) {
            template = "INSERT INTO LIBRARY.BOOK (ID,TITLE,AUTHOR,SUBJECT,IS_ISSUED) values (?,?,?,?,?)";
            stmt = con.prepareStatement(template);
            stmt.setInt(1, ((Book)books.get(x)).getID());
            stmt.setString(2, ((Book)books.get(x)).getTitle());
            stmt.setString(3, ((Book)books.get(x)).getAuthor());
            stmt.setString(4, ((Book)books.get(x)).getSubject());
            stmt.setBoolean(5, ((Book)books.get(x)).getIssuedStatus());
            stmt.executeUpdate();
        }

        for(x = 0; x < this.loans.size(); ++x) {
            template = "INSERT INTO LIBRARY.LOAN(L_ID,BORROWER,BOOK,ISSUER,ISS_DATE,RECEIVER,RET_DATE,FINE_PAID) values (?,?,?,?,?,?,?,?)";
            stmt = con.prepareStatement(template);
            stmt.setInt(1, x + 1);
            stmt.setInt(2, ((Loan)this.loans.get(x)).getBorrower().getID());
            stmt.setInt(3, ((Loan)this.loans.get(x)).getBook().getID());
            stmt.setInt(4, ((Loan)this.loans.get(x)).getIssuer().getID());
            stmt.setTimestamp(5, new Timestamp(((Loan)this.loans.get(x)).getIssuedDate().getTime()));
            stmt.setBoolean(8, ((Loan)this.loans.get(x)).getFineStatus());
            if (((Loan)this.loans.get(x)).getReceiver() == null) {
                stmt.setNull(6, 4);
                stmt.setDate(7, (java.sql.Date)null);
            } else {
                stmt.setInt(6, ((Loan)this.loans.get(x)).getReceiver().getID());
                stmt.setTimestamp(7, new Timestamp(((Loan)this.loans.get(x)).getReturnDate().getTime()));
            }

            stmt.executeUpdate();
        }

        x = 1;

        int i;
        for(i = 0; i < lib.getBooks().size(); ++i) {
            for(int j = 0; j < ((Book)lib.getBooks().get(i)).getHoldRequests().size(); ++j) {
                template = "INSERT INTO LIBRARY.ON_HOLD_BOOK(REQ_ID,BOOK,BORROWER,REQ_DATE) values (?,?,?,?)";
                PreparedStatement stmt = con.prepareStatement(template);
                stmt.setInt(1, x);
                stmt.setInt(3, ((HoldRequest)((Book)lib.getBooks().get(i)).getHoldRequests().get(j)).getBorrower().getID());
                stmt.setInt(2, ((HoldRequest)((Book)lib.getBooks().get(i)).getHoldRequests().get(j)).getBook().getID());
                stmt.setDate(4, new java.sql.Date(((HoldRequest)((Book)lib.getBooks().get(i)).getHoldRequests().get(j)).getRequestDate().getTime()));
                stmt.executeUpdate();
                ++x;
            }
        }

        for(i = 0; i < lib.getBooks().size(); ++i) {
            if (((Book)lib.getBooks().get(i)).getIssuedStatus()) {
                boolean set = true;

                for(int j = 0; j < this.loans.size() && set; ++j) {
                    if (((Book)lib.getBooks().get(i)).getID() == ((Loan)this.loans.get(j)).getBook().getID() && ((Loan)this.loans.get(j)).getReceiver() == null) {
                        template = "INSERT INTO LIBRARY.BORROWED_BOOK(BOOK,BORROWER) values (?,?)";
                        PreparedStatement stmt = con.prepareStatement(template);
                        stmt.setInt(1, ((Loan)this.loans.get(j)).getBook().getID());
                        stmt.setInt(2, ((Loan)this.loans.get(j)).getBorrower().getID());
                        stmt.executeUpdate();
                        set = false;
                    }
                }
            }
        }

    }
}