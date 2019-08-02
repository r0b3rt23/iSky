package com.example.edgepoint.checqr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;
    private static String[] columnsPresent = {"VotersName" ,"Timestamp" ,"ActivityName"};
    private static String[] columnsBatch = { "id" ,"batch_name","batch_count"};
    private static String[] columnsActivty = { "id" ,"Activity_name","Target_attendees"};
    private static String attendance_table = "attendance_table";
    private static String present_table = "present_table";
    private static String activity_table = "activity_table";
    private static String finish_activity_table = "finish_activity_table";
    private static String subactivity_table = "subactivity_table";
    private static String list_activity_table = "list_activity_table";
    private static String passwords_table = "passwords_table";
    private static String solicitation_table = "solicitation_table";
    private static String admin_table = "admin_table";
    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    /**
     * Read all voters from the database.
     *
     * @return a List of voters
     */

    public boolean loginInfo(String username,String password, String table) {
        Cursor cursor = database.query(table, null , "username=? AND password=?",new String[] {username,password}, null, null, null, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public boolean userAccess(String userAccess) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_access",userAccess);
        database.update("access_table", contentValues, "id=?",new String[] {"1"});
        return true;
    }

    public String getUserAccess() {
        String userAccess = "";
        Cursor cursor = database.query("access_table", null , "id=?",new String[] {"1"}, null, null, null, null);
        cursor.moveToFirst();
        userAccess = cursor.getString(cursor.getColumnIndex("user_access"));
        cursor.moveToNext();
        cursor.close();
        return userAccess;
    }

    public Integer getCountCityList() {
        int count = 0;
        Cursor mCount= database.rawQuery("select count(*) from attendance_table", null);
        mCount.moveToFirst();
        count=mCount.getInt(0);
        mCount.close();
        return count;
    }

    public Integer getCountBarangayList(String Barangay) {
        int count = 0;
        String[] selectionArg = {Barangay};
        Cursor mCount= database.rawQuery("select count(*) from attendance_table where Barangay = ?", selectionArg);
        mCount.moveToFirst();
        count=mCount.getInt(0);
        mCount.close();
        return count;
    }

    public Integer getCountPrecinctList(String Precinct) {
        int count = 0;
        String[] selectionArg = {Precinct};
        Cursor mCount= database.rawQuery("select count(*) from attendance_table where Precinct = ?", selectionArg);
        mCount.moveToFirst();
        count=mCount.getInt(0);
        mCount.close();
        return count;
    }

    public Integer getAttendanceCountCity(String ActivityCompare) {
        int count = 0;
        String[] selectionArg = {ActivityCompare};
        Cursor mCount= database.rawQuery("select count(Activity) from list_activity_table where Activity = ? ", selectionArg);
        mCount.moveToFirst();
        count=mCount.getInt(0);
        mCount.close();
        return count;
    }

    public Integer getAttendanceCountBarangay(String ActivityCompare, String barangay) {
        int count = 0;
        String[] selectionArg = {barangay, ActivityCompare};
        String selectQuery = "SELECT count(list_activity_table.Activity) " +
                "FROM attendance_table " +
                "JOIN list_activity_table ON attendance_table.ID = list_activity_table.Voters_ID "+
                "WHERE attendance_table.Barangay = ? AND list_activity_table.Activity = ?";
        Cursor mCount = database.rawQuery(selectQuery, selectionArg);

        mCount.moveToFirst();
        count=mCount.getInt(0);
        mCount.close();
        return count;
    }

    public Integer getAttendanceCountPrecinct(String ActivityCompare, String precinct) {
        int count = 0;
        String[] selectionArg = {precinct, ActivityCompare};
        String selectQuery = "SELECT count(list_activity_table.Activity) " +
                "FROM attendance_table " +
                "JOIN list_activity_table ON attendance_table.ID = list_activity_table.Voters_ID "+
                "WHERE attendance_table.Precinct = ? AND list_activity_table.Activity = ?";
        Cursor mCount = database.rawQuery(selectQuery, selectionArg);

        mCount.moveToFirst();
        count=mCount.getInt(0);
        mCount.close();
        return count;
    }

    public boolean insertAttendanceCount(int distinctcount, String labels){
        ContentValues cv = new ContentValues();
        cv.put("yAxis", distinctcount);
        database.update("AttendanceGraph", cv, "Label=?", new String[]{labels});
        return true;
    }

    public ArrayList<BarEntry> getQRBarEntries(String limitRow) {
        ArrayList<BarEntry> rv = new ArrayList<>();
        String rowlimit = limitRow;
        Cursor csr = database.query("AttendanceGraph",new String[]{"xAxis","yAxis","Label"},null,null,null,null,null, rowlimit);
        while(csr.moveToNext()) {
            rv.add(new BarEntry(csr.getInt(0), csr.getInt(1)));
        }
        csr.close();
        return rv;
    }

    public List<Integer> getEntriesQRY() {
        List<Integer> list = new ArrayList<>();
        Cursor cursor = database.query("AttendanceGraph", new String[]{"xAxis", "yAxis", "Label"}, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getInt(1));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }
//--------------------------LIST ATTENDEES---------------------------------------------------------------------------//
    public ArrayList<AttendeesModel> Attendees(String groupColumn,String activity) {
        String[] selectionArg = {activity};
        ArrayList<AttendeesModel> AttendeesModelArrayList = new ArrayList<>();
        String selectQuery = "SELECT attendance_table.VotersName, list_activity_table.Timestamp " +
                "FROM attendance_table " +
                "JOIN list_activity_table ON attendance_table.ID = list_activity_table.Voters_ID "+
                "WHERE list_activity_table.Activity = ?";
        Cursor cursor = database.rawQuery(selectQuery, selectionArg);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AttendeesModel attendeesModel = new AttendeesModel();
            attendeesModel.setName(cursor.getString(cursor.getColumnIndex("VotersName")));
            attendeesModel.setTime(cursor.getString(cursor.getColumnIndex("Timestamp")));
            AttendeesModelArrayList.add(attendeesModel);
            cursor.moveToNext();
        }
        cursor.close();
        return AttendeesModelArrayList;
    }

    public List<String> getQRDatabase(String columnGroup) {
        List<String> list = new ArrayList<>();
        Cursor cursor = database.query(true,attendance_table, null , null,null, columnGroup, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(cursor.getColumnIndex(columnGroup)));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public List<String> getQR_2_Database(String v ,String sel,String columnGroup) {
        List<String> list = new ArrayList<>();
        Cursor cursor = database.query(attendance_table, null , sel, new String[] {v}, columnGroup, null, columnGroup + " ASC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(cursor.getColumnIndex(columnGroup)));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

//    public List<String> getPrecinct_List_Database(String v ,String group,int x) {
//        List<String> list = new ArrayList<>();
//        Cursor cursor = database.query(attendance_table, null , "Barangay = ? AND ActivityName != ?", new String[] {v,""}, group, null, group + " ASC", null);
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            list.add(cursor.getString(x));
//            cursor.moveToNext();
//        }
//        cursor.close();
//        return list;
//    }

    public List<String> getPrecinct_List(String bgy) {
        String[] selectionArg = {bgy};
        List<String> list = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT attendance_table.Precinct " +
                "FROM attendance_table " +
                "JOIN list_activity_table ON attendance_table.ID = list_activity_table.Voters_ID "+
                "WHERE attendance_table.Barangay = ?";
        Cursor cursor = database.rawQuery(selectQuery, selectionArg);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(cursor.getColumnIndex("Precinct")));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

//    public List<String> getName_List_Database(String v ,String group,int x) {
//        List<String> list = new ArrayList<>();
//        Cursor cursor = database.query(attendance_table, null , "Precinct = ? AND ActivityName = ?", new String[] {v,"Activity 1"}, group, null, group + " ASC", null);
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            list.add(cursor.getString(x));
//            cursor.moveToNext();
//        }
//        cursor.close();
//        return list;
//    }

    public List<String> getName_List(String pct) {
        String[] selectionArg = {pct};
        List<String> list = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT attendance_table.VotersName " +
                "FROM attendance_table " +
                "JOIN list_activity_table ON attendance_table.ID = list_activity_table.Voters_ID "+
                "WHERE attendance_table.Precinct = ? GROUP BY attendance_table.VotersName";
        Cursor cursor = database.rawQuery(selectQuery, selectionArg);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(cursor.getColumnIndex("VotersName")));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }


//--------------------------SCANNER---------------------------------------------------------------------------//

    public List<String> checker(String v) {
        List<String> list = new ArrayList<>();
        Cursor cursor = database.query(attendance_table, null , "VotersName=?",new String[] {v}, null, null, null, null);
        cursor.moveToFirst();
        for(int i=0; i<cursor.getColumnCount();i++)
        {
            list.add(cursor.getString(i));
        }
        cursor.close();
        return list;
    }

    public boolean checkPrecinct(String Voters, String[] whereArgs, String questionMarks) {

//        String selectQuery = "SELECT VotersName FROM attendance_table WHERE Precinct IN ("+whereArgs+") AND VotersName = 'VILLARINO, ELENOR BALIGNASA'";
//        Cursor cursor = database.rawQuery(selectQuery, null);
        String[] tableColumns = new String[] {"VotersName"};
        String whereClause =  "Precinct IN ("+ questionMarks +") AND VotersName = ?";

        String[] whereArgs2 = new String[] {"0037A","0038A","0039A","VILLARINO, ELENOR BALIGNASA"};
        Cursor cursor = database.query(attendance_table,tableColumns,whereClause,whereArgs,null,null,null);

        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        // return all notes
        return false;
    }

    public List<String> activity() {
        List<String> list = new ArrayList<>();
        Cursor cursor = database.query(activity_table, columnsActivty , "ID=?",new String[] {String.valueOf(1)},null, null, null, null);
        cursor.moveToFirst();
        for(int i=0; i<cursor.getColumnCount();i++)
        {
            list.add(cursor.getString(i));
        }
        cursor.close();
        return list;
    }

    public List<String> getLocalActivity() {
        List<String> list = new ArrayList<>();
        Cursor cursor = database.query(activity_table, null , null,null,null, null, "Activity_name ASC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(1));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public List<String> getFinishActivity() {
        List<String> list = new ArrayList<>();
        Cursor cursor = database.query(finish_activity_table, null , null,null,null, null, "Activity_name_finish ASC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(1));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public List<String> getSubActivity() {
        List<String> list = new ArrayList<>();
        Cursor cursor = database.query(subactivity_table, null , "sub_ID=?",new String[] {String.valueOf(1)},null, null, null, null);
        cursor.moveToFirst();
        for(int i=1; i<cursor.getColumnCount();i++)
        {
            list.add(cursor.getString(i));
        }
        cursor.close();
        return list;
    }

    public List<String> getPrecinctPassword(String password) {
        List<String> list = new ArrayList<>();
        Cursor cursor = database.query(passwords_table, null , "password=?",new String[] {password},null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(1));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public String getActivity(String activity_name) {
        String activity = "";
        Cursor cursor = database.query(activity_table, null , "Activity_name=?",new String[] {activity_name},null, null, null, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                activity = cursor.getString(1);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return activity;
    }

    public String getFinishActivity(String activity_name) {
        String activity = "";
        Cursor cursor = database.query(finish_activity_table, null , "Activity_name_finish=?",new String[] {activity_name},null, null, null, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                activity = cursor.getString(1);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return activity;
    }

    public int getVotersID(String VotersName) {
        int voters_id = 0;
        Cursor cursor = database.query(attendance_table, null , "VotersName=?",new String[] {VotersName},null, null, null, null);
        cursor.moveToFirst();
        voters_id = cursor.getInt(cursor.getColumnIndex("ID"));
        cursor.moveToNext();
        return voters_id;
    }

    public String getVotersName(String ID, String VIN) {
        String voters_name = "";
        Cursor cursor = database.query(attendance_table, null , "ID=? AND VIN=?",new String[] {ID,VIN},null, null, null, null);
        cursor.moveToFirst();
        voters_name = cursor.getString((cursor.getColumnIndex("VotersName")));
        cursor.moveToNext();
        return voters_name;
    }

    public ArrayList<ViewInfoRecycler> getRecyclerVoterName(){
        ArrayList<ViewInfoRecycler> voters = new ArrayList<>();
        Cursor csr = database.query(attendance_table,new String[]{"VotersName"},null,null,null,null,"VotersName ASC");
        while(csr.moveToNext()) {
            voters.add(new ViewInfoRecycler(csr.getString(csr.getColumnIndex("VotersName"))));
        }
        csr.close();
        return voters;
    }


    public ArrayList<List_Activity_Data> getRowActivity(String Votname) {
        String[] selectionArg = {Votname};
        ArrayList<List_Activity_Data> dataList = new ArrayList<>();
        String selectQuery = "SELECT list_activity_table.Timestamp,list_activity_table.Activity " +
                             "FROM attendance_table " +
                             "JOIN list_activity_table ON attendance_table.ID = list_activity_table.Voters_ID "+
                             "WHERE attendance_table.VotersName = ?";
        Cursor cursor = database.rawQuery(selectQuery, selectionArg);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            List_Activity_Data list_Activity_Data = new List_Activity_Data();
            list_Activity_Data.setActivityname(cursor.getString(cursor.getColumnIndex("Activity")));
            list_Activity_Data.setTimestamp(cursor.getString(cursor.getColumnIndex("Timestamp")));
            dataList.add(list_Activity_Data);
            cursor.moveToNext();
        }

        cursor.close();
        // return all notes
        return dataList;
    }

    public ArrayList<List_Activity_Data> getAll_Activity() {
        ArrayList<List_Activity_Data> dataList = new ArrayList<>();
        String selectQuery = "SELECT list_activity_table.Timestamp,list_activity_table.Activity,list_activity_table.Voters_ID,list_activity_table.UploadTime " +
                "FROM attendance_table " +
                "JOIN list_activity_table ON attendance_table.ID = list_activity_table.Voters_ID ";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            List_Activity_Data list_Activity_Data = new List_Activity_Data();
            list_Activity_Data.setActivityname(cursor.getString(cursor.getColumnIndex("Activity")));
            list_Activity_Data.setTimestamp(cursor.getString(cursor.getColumnIndex("Timestamp")));
            list_Activity_Data.setUploadtime(cursor.getString(cursor.getColumnIndex("UploadTime")));
            list_Activity_Data.setVotersID(cursor.getInt(cursor.getColumnIndex("Voters_ID")));
            dataList.add(list_Activity_Data);
            cursor.moveToNext();
        }

        cursor.close();
        // return all notes
        return dataList;
    }

    public ArrayList<Solicitation_Data> getVotersSolicitation(String Votname) {
        String[] selectionArg = {Votname};
        ArrayList<Solicitation_Data> dataList = new ArrayList<>();
        String selectQuery = "SELECT solicitation_table.solicite_name,solicitation_table.solicite_amount,solicitation_table.solicite_voter_id " +
                "FROM attendance_table " +
                "JOIN solicitation_table ON attendance_table.ID = solicitation_table.solicite_voter_id "+
                "WHERE attendance_table.VotersName = ?";
        Cursor cursor = database.rawQuery(selectQuery, selectionArg);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Solicitation_Data solicitation_Data = new Solicitation_Data();
            solicitation_Data.setSolicitename(cursor.getString(cursor.getColumnIndex("solicite_name")));
            solicitation_Data.setAmount(cursor.getInt(cursor.getColumnIndex("solicite_amount")));
            solicitation_Data.setSolicitevotersid(cursor.getInt(cursor.getColumnIndex("solicite_voter_id")));
            dataList.add(solicitation_Data);
            cursor.moveToNext();
        }
        cursor.close();
        // return all notes
        return dataList;
    }

    public ArrayList<Solicitation_Data> getAll_Solicitation() {
        ArrayList<Solicitation_Data> dataList = new ArrayList<>();
        String selectQuery = "SELECT solicitation_table.solicite_name,solicitation_table.solicite_amount,solicitation_table.solicite_voter_id " +
                "FROM attendance_table " +
                "JOIN solicitation_table ON attendance_table.ID = solicitation_table.solicite_voter_id ";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Solicitation_Data solicitation_Data = new Solicitation_Data();
            solicitation_Data.setSolicitename(cursor.getString(cursor.getColumnIndex("solicite_name")));
            solicitation_Data.setAmount(cursor.getInt(cursor.getColumnIndex("solicite_amount")));
            solicitation_Data.setSolicitevotersid(cursor.getInt(cursor.getColumnIndex("solicite_voter_id")));
            dataList.add(solicitation_Data);
            cursor.moveToNext();
        }
        cursor.close();
        // return all notes
        return dataList;
    }

    public boolean CheckList(String Votname, String Actname){
        String[] selectionArg = {Votname, Actname};
        String selectQuery = "SELECT attendance_table.VotersName,list_activity_table.Activity " +
                "FROM attendance_table " +
                "JOIN list_activity_table ON attendance_table.ID = list_activity_table.Voters_ID " +
                "WHERE attendance_table.VotersName = ? AND list_activity_table.Activity = ?";
        Cursor cursor = database.rawQuery(selectQuery, selectionArg);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        // return all notes
        return false;
    }

    //--------------------------Updating Database---------------------------------------------------------------------------//

    public boolean insertAttendance(String activity, String timestamp,int voters_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Activity",activity);
        contentValues.put("Voters_ID",voters_id);
        contentValues.put("Timestamp",timestamp);
        database.insert(list_activity_table, null, contentValues);
        return true;
    }

    public boolean updateUploadTime(String update_uploadtime) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("UploadTime",update_uploadtime);
        database.update(list_activity_table, contentValues, "UploadTime IS NULL",null);
        return true;
    }

    public boolean insertSolicitation(int voters_id,String solicite_name,int amount) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("solicite_voter_id",voters_id);
        contentValues.put("solicite_name",solicite_name);
        contentValues.put("solicite_amount",amount);
        database.insert(solicitation_table, null, contentValues);
        return true;
    }

    public boolean insertActivityTable(String update_name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Activity_name",update_name);
        database.insertWithOnConflict(activity_table, null, contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        return true;
    }

    public boolean insertFinishActivity(String insert_name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Activity_name_finish",insert_name);
        database.insertWithOnConflict(finish_activity_table, null, contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        return true;
    }

    public boolean updateSubActivity(String update_subActivity) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("sub_activity",update_subActivity);
        database.update(subactivity_table, contentValues, "sub_ID=?",new String[] {String.valueOf(1)});
        return true;
    }

    public boolean updateSolicitation(String update_solicitation) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("solicitation",update_solicitation);
        database.update(subactivity_table, contentValues, "sub_ID=?",new String[] {String.valueOf(1)});
        return true;
    }

    public boolean updateSubScan(String update_subCoverage,String update_subPassword) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("sub_scancoverage",update_subCoverage);
        contentValues.put("sub_scanpassword",update_subPassword);
        database.update(subactivity_table, contentValues, "sub_ID=?",new String[] {String.valueOf(1)});
        return true;
    }

    public boolean updateLogin(int id,String username,String password) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);
        contentValues.put("username",username);
        contentValues.put("password",password);
        database.update(admin_table, contentValues, "id="+id,null);
        return true;
    }

    public boolean deleteActivity(String Activity){
        database.delete(activity_table,"Activity_name=?",new String[]{Activity});
        database.close();
        return true;
    }

    public boolean deleteFinishActivity(String Activity){
        database.delete(finish_activity_table,"Activity_name_finish=?",new String[]{Activity});
        database.close();
        return true;
    }

    public boolean delete(){
        database.delete(activity_table,null,null);
        database.delete(finish_activity_table,null,null);
        database.delete(list_activity_table,null,null);
        database.delete(present_table,null,null);
        database.delete(solicitation_table,null,null);
        ContentValues contentValues = new ContentValues();
        contentValues.put("sub_activity","Choose Activity");
        contentValues.put("sub_scancoverage","Set Coverage");
        contentValues.put("sub_scanpassword","set");
        contentValues.put("solicitation","false");
        database.update(subactivity_table, contentValues, "sub_ID=?",new String[] {String.valueOf(1)});
        database.close();
        return true;
    }

    public boolean addColumn(){

        database.execSQL("ALTER TABLE attendance_table ADD Activity TEXT NOT NULL DEFAULT ''");
        return true;
    }

}
