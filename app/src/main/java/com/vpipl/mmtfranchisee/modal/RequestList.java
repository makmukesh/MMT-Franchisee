package com.vpipl.mmtfranchisee.modal;

public class RequestList {

    private String RequestId = "";
    private String BillNo = "";
    private String BillDate = "";
    private String CustomerName = "";
    private String CustomerIdno = "";
    private String CustomerFormNo = "";
    private String BillAmount = "";
    private String CommissionAmount = "";
    private String Status = "";
    private String ApproveDate = "";


    public RequestList(String RequestId,String BillNo,String BillDate,  String CustomerName,String CustomerIdno,  String CustomerFormNo, String BillAmount, String CommissionAmount, String Status, String ApproveDate)
    {
        this.RequestId = RequestId;
        this.BillNo = BillNo;
        this.BillDate = BillDate;
        this.CustomerName = CustomerName;
        this.CustomerIdno = CustomerIdno;
        this.CustomerFormNo = CustomerFormNo;
        this.BillAmount = BillAmount;
        this.CommissionAmount = CommissionAmount;
        this.Status = Status;
        this.ApproveDate = ApproveDate;
    }

    public String getRequestId() {
        return this.RequestId;
    }

    public String getBillNo() {
        return this.BillNo;
    }

    public String getBillDate() {
        return this.BillDate;
    }

    public String getCustomerName() {
        return this.CustomerName;
    }

    public String getCustomerIdno() {
        return this.CustomerIdno;
    }

    public String getCustomerFormNo() {
        return this.CustomerFormNo;
    }

    public String getBillAmount() {
        return this.BillAmount;
    }

    public String getApproveDate() {
        return this.ApproveDate;
    }

    public String getStatus() {
        return this.Status;
    }

    public String getCommissionAmount() {
        return this.CommissionAmount;
    }
}