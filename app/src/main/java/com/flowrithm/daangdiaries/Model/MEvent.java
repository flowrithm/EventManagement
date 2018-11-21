package com.flowrithm.daangdiaries.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class MEvent implements Serializable {
    public int EventId;
    public String Name;
    public String StartDate;
    public String EndDate;
    public String Venue;
    public String ContactPerson;
    public String ContactNumber;
    public String Image;
    public String PassPrice;
    public ArrayList<MTax> Tax;
    public boolean IsPassAvailable;
    public boolean IsTicketAvailable;
    public int Status;
}
