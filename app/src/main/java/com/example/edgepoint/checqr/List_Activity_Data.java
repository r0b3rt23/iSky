package com.example.edgepoint.checqr;

public class List_Activity_Data {
    private String activityname,timestamp,uploadtime;
    private int votersid;

    public List_Activity_Data(){
    }

    public List_Activity_Data(String activityname,String timestamp,String uploadtime,int votersid){
        this.activityname = activityname;
        this.timestamp = timestamp;
        this.uploadtime = uploadtime;
        this.votersid = votersid;
    }
    public String getActivityname() {
        return activityname;
    }

    public void setActivityname(String activityname) {
        this.activityname = activityname;
    }

    public String getUploadtime() {
        return uploadtime;
    }

    public void setUploadtime(String uploadtime) {
        this.uploadtime = uploadtime;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getVotersID() {
        return votersid;
    }

    public void setVotersID(int votersid) {
        this.votersid = votersid;
    }


}
