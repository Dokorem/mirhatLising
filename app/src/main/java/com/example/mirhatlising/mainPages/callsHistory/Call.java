package com.example.mirhatlising.mainPages.callsHistory;

public class Call {

    private String numberOfCaller, dateOfCall, typeOfTruck;

    public Call(String numberOfCaller, String dateOfCall, String typeOfTruck) {
        this.numberOfCaller = numberOfCaller;
        this.dateOfCall = dateOfCall;
        this.typeOfTruck = typeOfTruck;
    }

    public String getDateOfCall() {
        return dateOfCall;
    }

    public void setDateOfCall(String dateOfCall) {
        this.dateOfCall = dateOfCall;
    }

    public String getNumberOfCaller() {
        return numberOfCaller;
    }

    public void setNumberOfCaller(String numberOfCaller) {
        this.numberOfCaller = numberOfCaller;
    }

    public String getTypeOfTruck() {
        return typeOfTruck;
    }

    public void setTypeOfTruck(String typeOfTruck) {
        this.typeOfTruck = typeOfTruck;
    }
}
