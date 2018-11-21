package com.flowrithm.daangdiaries.Model;

import io.realm.RealmObject;

public class RUser extends RealmObject {
    private int UId;
    private String Name;
    private String Address;
    private String City;
    private String ContactNo;
    private String Email;
    private String Payment;
    private String Role;
    private int PaymentStatus;
    private int IsActive;
    private int ScanningRights;
    private int UserCreativeRights;

    public int getUId() {
        return UId;
    }

    public void setUId(int UId) {
        this.UId = UId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getContactNo() {
        return ContactNo;
    }

    public void setContactNo(String contactNo) {
        ContactNo = contactNo;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPayment() {
        return Payment;
    }

    public void setPayment(String payment) {
        Payment = payment;
    }

    public int getPaymentStatus() {
        return PaymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        PaymentStatus = paymentStatus;
    }

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
    }

    public int getScanningRights() {
        return ScanningRights;
    }

    public void setScanningRights(int scanningRights) {
        ScanningRights = scanningRights;
    }

    public int getUserCreativeRights() {
        return UserCreativeRights;
    }

    public void setUserCreativeRights(int userCreativeRights) {
        UserCreativeRights = userCreativeRights;
    }
}
