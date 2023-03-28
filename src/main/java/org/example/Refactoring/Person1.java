package org.example.Refactoring;

public abstract class Person
{
    protected int id;           // ID всех людей, связаннных с библиотекой
    protected String password;  // Пароль всех людей,связанных с библиотекой
    protected String name;      // Имена всех людей,связанных с библиотекой
    protected String address;   //Адрес всех людей, связанных с библиотекой
    protected int phoneNo;      //Телефонный номер всех людей,связанных с библиотекой


    static int currentIdNumber = 0;

    public Person(int idNum, String name, String address, int phoneNum)
    {
        currentIdNumber++;

        if(idNum==-1)
        {
            id = currentIdNumber;
        }
        else
            id = idNum;

        password = Integer.toString(id);
        this.name = name;
        this.address = address;
        phoneNo = phoneNum;
    }


    public void printInfo()
    {
        System.out.println("-----------------------------------------");
        System.out.println("\nДетали\n");
        System.out.println("ID " + id);
        System.out.println("Имя: " + name);
        System.out.println("Адрес: " + address);
        System.out.println("Номер телефона: " + phoneNo + "\n");
    }


    public void setAddress(String a)
    {
        address = a;
    }

    public void setPhone(int p)
    {
        phoneNo = p;
    }

    public void setName(String n)
    {
        name = n;
    }

    public String getName()
    {
        return name;
    }

    public String getPassword()
    {
        return password;
    }

    public String getAddress()
    {
        return address;
    }

    public int getPhoneNumber()
    {
        return phoneNo;
    }
    public int getID()
    {
        return id;
    }


    public static void setIDCount(int n)
    {
        currentIdNumber=n;
    }

}