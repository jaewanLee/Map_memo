package jaewanlee.android_project.com.map_memo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jaewan on 2017-01-11.
 */

public class User_DB_Manager extends SQLiteOpenHelper {
    public User_DB_Manager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE USER ( user_no INTEGER PRIMARY KEY AUTOINCREMENT, user_id TEXT, phone TEXT, photo TEXT, name TEXT, type TEXT, date TEXT, update_date TEXT);");
        //페이스북의 회원가입 타입은 1
        //일반 회원가입의 타입은 0
        //그외는 -1
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
    public User getUserProfile(String user_id){
        SQLiteDatabase db=getReadableDatabase();
        String query="select * from user where user_id='";
        query+=user_id+"';";
        Cursor cursor=db.rawQuery(query,null);
        User result=null;
        while(cursor.moveToNext()){
            result=new User(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7));
        }
        db.close();
        return result;
    }
    public String getUserPhoto(String user_id){
        SQLiteDatabase db=getReadableDatabase();
        String query="select photo from user where user_id='";
        query+=user_id+"';";
        Cursor cursor=db.rawQuery(query,null);
        String result=null;
        while(cursor.moveToNext()){
            result=cursor.getString(0);
        }
        db.close();
        return  result;
    }
}
