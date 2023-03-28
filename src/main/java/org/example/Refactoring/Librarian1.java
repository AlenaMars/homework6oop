package org.example.Refactoring;

import static LMS.Library.librarian;
import static LMS.Library.persons;

public class Librarian extends Staff {

    int officeNo;     //Офисный номер библиотекаря
    public static int currentOfficeNumber = 0;

    public Librarian(int id, String n, String a, int p, double s, int of) // para cons.
    {
        super(id, n, a, p, s);

        if (of == -1)
            officeNo = currentOfficeNumber;
        else
            officeNo = of;

        currentOfficeNumber++;
    }

    // Печатем информацию о библиотекаре
    public void printInfo() {
        super.printInfo();
        System.out.println("Office Number: " + officeNo);
    }

    public static boolean addLibrarian(Librarian lib) {
        //Одна библиотека может иметь только одного библиотекаря
        if (librarian == null) {
            librarian = lib;
            persons.add(librarian);
            return true;
        } else
            System.out.println("\nИзвините,в библиотеке уже есть библиотекарь.Новый библиотекарь не может быть добавлен");
        return false;
    }
}