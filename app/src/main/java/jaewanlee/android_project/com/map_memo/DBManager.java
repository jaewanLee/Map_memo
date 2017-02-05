package jaewanlee.android_project.com.map_memo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jaewan on 2016-12-14.
 */

public class DBManager extends SQLiteOpenHelper {
    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE MEMO ( memo_id INTEGER PRIMARY KEY AUTOINCREMENT, map_id TEXT,addrCategory1 INTEGER, addrCategory2 INTEGER, addrDetail TEXT, title TEXT, DATE TEXT, category INTEGER, img TEXT,memo TEXT,user_id TEXT,lat TEXT,lng TEXT,chat TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insert(String query){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL(query);
        db.close();
    }
    public void delete(String query){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL(query);
        db.close();
    }
    public void update(String query){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL(query);
        db.close();
    }
    public ArrayList<MarkerItem> listViewDB(){
        ArrayList<MarkerItem> result=new ArrayList();
        String query="select memo_id,title,addrCategory1,addrCategory2,addrDetail,chat from MEMO order by memo_id DESC LIMIT 10;";
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        while(cursor.moveToNext()){
            result.add(new MarkerItem(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getInt(3),cursor.getString(4),cursor.getString(5)));
        }
        return result;
    }
    public ArrayList<MarkerItem> listViewDB(int category1){
        ArrayList<MarkerItem> result=new ArrayList();
        String query="select memo_id,title,addrCategory1,addrCategory2,addrDetail,chat from MEMO where addrCategory1="+String.valueOf(category1)+";";
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        while(cursor.moveToNext()){
            result.add(new MarkerItem(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getInt(3),cursor.getString(4),cursor.getString(5)));
        }
        return result;
    }
    public ArrayList<MarkerItem> listViewDB(int category1,int category2){
        ArrayList<MarkerItem> result=new ArrayList();
        String query="select memo_id,title,addrCategory1,addrCategory2,addrDetail,chat from MEMO where addrCategory1="+String.valueOf(category1)+" and addrcategory2="+String.valueOf(category2)+";";
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        while(cursor.moveToNext()){
            result.add(new MarkerItem(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getInt(3),cursor.getString(4),cursor.getString(5)));
        }
        return result;
    }

    public MarkerItem  layoutDB(int memo_id){
        MarkerItem result=null;
        String query="select memo_id,title,addrCategory1,addrCategory2,addrDetail,img,chat from MEMO where memo_id="+memo_id+";";
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        while(cursor.moveToNext()){
            result=new MarkerItem(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getInt(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));
        }
        return result;
    }
    public HashMap<Integer,MarkerCluster> markerDB(){
        HashMap<Integer,MarkerCluster> result=new HashMap<Integer, MarkerCluster>();
        String query="select memo_id,title,lat,lng,chat from MEMO;";
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        while(cursor.moveToNext()){
            int memo_id=cursor.getInt(0);
            result.put(cursor.getInt(0),new MarkerCluster(new LatLng(Double.valueOf(cursor.getString(2)),Double.valueOf(cursor.getString(3))),cursor.getString(1),cursor.getInt(0),cursor.getString(4)));
        }
        return result;
    }
    public ArrayList<String> wholeDB(int memo_id){
        String query="select * from MEMO where memo_id="+memo_id+";";
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        ArrayList<String> result=new ArrayList<String>();
        while(cursor.moveToNext()){
            result.add(cursor.getString(1));
            result.add(String.valueOf(cursor.getInt(2)));
            result.add(String.valueOf(cursor.getInt(3)));
            result.add(cursor.getString(4));
            result.add(cursor.getString(5));
            result.add(cursor.getString(6));
            result.add(String.valueOf(cursor.getInt(7)));
            result.add(cursor.getString(8));
            result.add(cursor.getString(9));
            result.add(cursor.getString(10));
            Log.d("curosr",String.valueOf(cursor.getInt(2)));
            Log.d("curosr",String.valueOf(cursor.getInt(3)));
        }

        return result;
    }
}
