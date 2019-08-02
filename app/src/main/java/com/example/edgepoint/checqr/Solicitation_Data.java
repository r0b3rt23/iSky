package com.example.edgepoint.checqr;

public class Solicitation_Data {
    private String solicitename;
    private int amount;
    private int solicitevotersid;

    public Solicitation_Data(String solicitename,int amount ,int solicitevotersid){
        this.solicitename = solicitename;
        this.amount = amount;
        this.solicitevotersid = solicitevotersid;
    }

    public Solicitation_Data(){
    }


    public void setSolicitename(String solicitename) {
        this.solicitename = solicitename;
    }

    public String getSolicitename() {
        return solicitename;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setSolicitevotersid(int solicitevotersid) {
        this.solicitevotersid = solicitevotersid;
    }

    public int getSolicitevotersid() {
        return solicitevotersid;
    }
}
