package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Main {
    public Main() {
    }

    public static void clrscr() {
        for(int i = 0; i < 20; ++i) {
            System.out.println();
        }

    }

    public static int takeInput(int min, int max) {
        Scanner input = new Scanner(System.in);

        while(true) {
            System.out.println("\nВведите выбор ");
            String choice = input.next();
            if (!choice.matches(".*[a-zA-Z]+.*") && Integer.parseInt(choice) > min && Integer.parseInt(choice) < max) {
                return Integer.parseInt(choice);
            }

            System.out.println("\nНеправильный ввод");
        }
    }

    public static void allFunctionalities(Person person, int choice) throws IOException {
        Library lib = Library.getInstance();
        Scanner scanner = new Scanner(System.in);
        int input = false;
        if (choice == 1) {
            lib.searchForBooks();
        } else {
            ArrayList books;
            Book b;
            Borrower bor;
            int input;
            if (choice == 2) {
                books = lib.searchForBooks();
                if (books != null) {
                    input = takeInput(-1, books.size());
                    b = (Book)books.get(input);
                    if (!"Служащий".equals(person.getClass().getSimpleName()) && !"Библиотекарь".equals(person.getClass().getSimpleName())) {
                        b.makeHoldRequest((Borrower)person);
                    } else {
                        bor = lib.findBorrower();
                        if (bor != null) {
                            b.makeHoldRequest(bor);
                        }
                    }
                }
            } else {
                Borrower bor;
                if (choice == 3) {
                    if (!"Служащий".equals(person.getClass().getSimpleName()) && !"Библиотекарь".equals(person.getClass().getSimpleName())) {
                        person.printInfo();
                    } else {
                        bor = lib.findBorrower();
                        if (bor != null) {
                            bor.printInfo();
                        }
                    }
                } else if (choice == 4) {
                    if (!"Clerk".equals(person.getClass().getSimpleName()) && !"Librarian".equals(person.getClass().getSimpleName())) {
                        double totalFine = lib.computeFine2((Borrower)person);
                        System.out.println("\nВаш общий штраф" + totalFine);
                    } else {
                        bor = lib.findBorrower();
                        if (bor != null) {
                            double totalFine = lib.computeFine2(bor);
                            System.out.println("\n Ваш общий штраф" + totalFine);
                        }
                    }
                } else if (choice == 5) {
                    books = lib.searchForBooks();
                    if (books != null) {
                        input = takeInput(-1, books.size());
                        ((Book)books.get(input)).printHoldRequests();
                    }
                } else if (choice == 6) {
                    books = lib.searchForBooks();
                    if (books != null) {
                        input = takeInput(-1, books.size());
                        b = (Book)books.get(input);
                        bor = lib.findBorrower();
                        if (bor != null) {
                            b.issueBook(bor, (Staff)person);
                        }
                    }
                } else {
                    ArrayList loans;
                    if (choice == 7) {
                        bor = lib.findBorrower();
                        if (bor != null) {
                            bor.printBorrowedBooks();
                            loans = bor.getBorrowedBooks();
                            if (!loans.isEmpty()) {
                                input = takeInput(-1, loans.size());
                                Loan l = (Loan)loans.get(input);
                                l.getBook().returnBook(bor, l, (Staff)person);
                            } else {
                                System.out.println("\nThis borrower " + bor.getName() + " has no book to return.");
                            }
                        }
                    } else if (choice == 8) {
                        bor = lib.findBorrower();
                        if (bor != null) {
                            bor.printBorrowedBooks();
                            loans = bor.getBorrowedBooks();
                            if (!loans.isEmpty()) {
                                input = takeInput(-1, loans.size());
                                ((Loan)loans.get(input)).renewIssuedBook(new Date());
                            } else {
                                System.out.println("\nЭтот одолживший" + bor.getName() + " не имеет выданной книги<которая может быть продлена");
                            }
                        }
                    } else if (choice == 9) {
                        lib.createPerson('b');
                    } else if (choice == 10) {
                        bor = lib.findBorrower();
                        if (bor != null) {
                            bor.updateBorrowerInfo();
                        }
                    } else if (choice == 11) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                        System.out.println("\nВведите название");
                        String title = reader.readLine();
                        System.out.println("\nВведите предмет");
                        String subject = reader.readLine();
                        System.out.println("\nВведите автора");
                        String author = reader.readLine();
                        lib.createBook(title, subject, author);
                    } else if (choice == 12) {
                        books = lib.searchForBooks();
                        if (books != null) {
                            input = takeInput(-1, books.size());
                            lib.removeBookfromLibrary((Book)books.get(input));
                        }
                    } else if (choice == 13) {
                        books = lib.searchForBooks();
                        if (books != null) {
                            input = takeInput(-1, books.size());
                            ((Book)books.get(input)).changeBookInfo();
                        }
                    } else if (choice == 14) {
                        Clerk clerk = lib.findClerk();
                        if (clerk != null) {
                            clerk.printInfo();
                        }
                    }
                }
            }
        }

        System.out.println("\nНажмите любую клавишу чтобы проодолжить\n");
        scanner.next();
    }

    public static void main(String[] args) {
        Scanner admin = new Scanner(System.in);
        Library lib = Library.getInstance();
        lib.setFine(20.0);
        lib.setRequestExpiry(7);
        lib.setReturnDeadline(5);
        lib.setName("FAST Library");
        Connection con = lib.makeConnection();
        if (con == null) {
            System.out.println("\nОшибка соединения с базой даных.Выход.");
        } else {
            try {
                lib.populateLibrary(con);
                boolean stop = false;

                while(!stop) {
                    clrscr();
                    System.out.println("--------------------------------------------------------");
                    System.out.println("\Добро пожаловать в систему управления библиотекой!");
                    System.out.println("--------------------------------------------------------");
                    System.out.println("Следующие функции доступны\n");
                    System.out.println("1- Логин");
                    System.out.println("2- Выход");
                    System.out.println("3- Административыне функции");
                    System.out.println("-----------------------------------------\n");
                    int choice = false;
                    int choice = takeInput(0, 4);
                    if (choice == 3) {
                        System.out.println("\nВведите пароль");
                        String aPass = admin.next();
                        if (aPass.equals("lib")) {
                            while(true) {
                                clrscr();
                                System.out.println("--------------------------------------------------------");
                                System.out.println("\tДобро пожаловать на портал администратора");
                                System.out.println("--------------------------------------------------------");
                                System.out.println("Следующие функции доступны\n");
                                System.out.println("1- Добавить служащего");
                                System.out.println("2- Добавить библиотекаря");
                                System.out.println("3- Показать историю выданных книг");
                                System.out.println("4- Показать все книги в библиотеке");
                                System.out.println("5- Выйти из аккаунта");
                                System.out.println("---------------------------------------------");
                                choice = takeInput(0, 6);
                                if (choice == 5) {
                                    break;
                                }

                                if (choice == 1) {
                                    lib.createPerson('c');
                                } else if (choice == 2) {
                                    lib.createPerson('l');
                                } else if (choice == 3) {
                                    lib.viewHistory();
                                } else if (choice == 4) {
                                    lib.viewAllBooks();
                                }

                                System.out.println("\nНажмите любую клавишу для продолжения\n");
                                admin.next();
                            }
                        } else {
                            System.out.println("\nИзвините!Неправильный пароль");
                        }
                    } else if (choice == 1) {
                        Person person = lib.login();
                        if (person != null) {
                            if (person.getClass().getSimpleName().equals("Одолживший")) {
                                while(true) {
                                    clrscr();
                                    System.out.println("--------------------------------------------------------");
                                    System.out.println("\tДобро пожаловать на портал пользователя библиотеки!");
                                    System.out.println("--------------------------------------------------------");
                                    System.out.println("Доступные функции\n");
                                    System.out.println("1- Найти книгу");
                                    System.out.println("2-Зарезервировать книгу");
                                    System.out.println("3- Проверить лчную информацию пользователя");
                                    System.out.println("4- Проверить общий штраф пользователя");
                                    System.out.println("5- Проверить запросы на резервирование книг");
                                    System.out.println("6- Выйти из аккаунта");
                                    System.out.println("--------------------------------------------------------");
                                    choice = takeInput(0, 7);
                                    if (choice == 6) {
                                        break;
                                    }

                                    allFunctionalities(person, choice);
                                }
                            } else if (person.getClass().getSimpleName().equals("Служащий")) {
                                while(true) {
                                    clrscr();
                                    System.out.println("--------------------------------------------------------");
                                    System.out.println("\tДобро пожаловать на портал служащего!");
                                    System.out.println("--------------------------------------------------------");
                                    System.out.println("Следующие функциии доступны\n");
                                    System.out.println("1- Найти книгу");
                                    System.out.println("2- Зарезервировать книгу");
                                    System.out.println("3- Проверить персональную информацию пользователя");
                                    System.out.println("4- ПРоверить общий штраф пользователя");
                                    System.out.println("5- Проверить резервы книг в очереди");
                                    System.out.println("6- ПРоверить книгу");
                                    System.out.println("7- Записать книгу");
                                    System.out.println("8- Продлить книгу");
                                    System.out.println("9- Добавить нового пользователя");
                                    System.out.println("10- Обновить информацию о пользователе");
                                    System.out.println("11- Выйти из аккаунта");
                                    System.out.println("--------------------------------------------------------");
                                    choice = takeInput(0, 12);
                                    if (choice == 11) {
                                        break;
                                    }

                                    allFunctionalities(person, choice);
                                }
                            } else if (person.getClass().getSimpleName().equals("Библиотекарь")) {
                                while(true) {
                                    clrscr();
                                    System.out.println("--------------------------------------------------------");
                                    System.out.println("\tДобро пожаловать на портал библиотекаря!");
                                    System.out.println("--------------------------------------------------------");
                                    System.out.println("Следующие функции доступны \n");
                                    System.out.println("1- Найти книгу");
                                    System.out.println("2- Зарезервировать книгу");
                                    System.out.println("3- ПРоверить преоснальную информацию о пользователе");
                                    System.out.println("4- Проверить общий штраф пользователя");
                                    System.out.println("5- Првоерить резервы книги");
                                    System.out.println("6- ПРоверить книгу");
                                    System.out.println("7- Записать книгу");
                                    System.out.println("8- Продлить книгу");
                                    System.out.println("9- Добавить нового пользователя");
                                    System.out.println("10- Обновить информацию о пользователе");
                                    System.out.println("11- Добавить новую книгу");
                                    System.out.println("12- Удалить книгу");
                                    System.out.println("13- Изменить информацию о книге");
                                    System.out.println("14- Проверить личную информацию служащего");
                                    System.out.println("15- Выйти из аккаунта");
                                    System.out.println("--------------------------------------------------------");
                                    choice = takeInput(0, 16);
                                    if (choice == 15) {
                                        break;
                                    }

                                    allFunctionalities(person, choice);
                                }
                            }
                        }
                    } else {
                        stop = true;
                    }

                    System.out.println("\nНажмите любую клавишу дял продолжения\n");
                    Scanner scanner = new Scanner(System.in);
                    scanner.next();
                }

                lib.fillItBack(con);
            } catch (Exception var7) {
                System.out.println("\nExiting...\n");
            }

        }
    }
}