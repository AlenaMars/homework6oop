package org.example.Refactoring;

import java.util.ArrayList;

public class HoldRequestOperations {

    static ArrayList <HoldRequest> holdRequests;

    public HoldRequestOperations()
    {
        holdRequests= new ArrayList<>();
    }
    // Добавить запрос на резерв
    public void addHoldRequest(HoldRequest hr)
    {
        holdRequests.add(hr);
    }
    // удалить запрос на резерв
    public void removeHoldRequest()
    {
        if(!holdRequests.isEmpty())
        {
            holdRequests.remove(0);
        }
    }
}