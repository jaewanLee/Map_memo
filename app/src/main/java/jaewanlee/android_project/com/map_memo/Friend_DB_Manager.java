package jaewanlee.android_project.com.map_memo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by jaewan on 2017-01-13.
 */

public class Friend_DB_Manager extends SQLiteOpenHelper {
    public Friend_DB_Manager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE FRIEND ( user_no INTEGER PRIMARY KEY AUTOINCREMENT, user_id TEXT, phone TEXT, photo TEXT, name TEXT, type TEXT, date TEXT, update_date TEXT,friend TEXT);");
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
    public Friend getUserProfile(String user_id,String friend_id){
        SQLiteDatabase db=getReadableDatabase();
        String query="select * from FRIEND where user_id='";
        query+=user_id+"' and friend='"+friend_id+"';";
        Cursor cursor=db.rawQuery(query,null);
        Friend result=null;
        while(cursor.moveToNext()){
            result=new Friend(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8));
        }
        db.close();
        return result;
    }
    public ArrayList<Friend> getMyFriend(String user_id){
        ArrayList<Friend> result=new ArrayList<>();
        SQLiteDatabase db=getReadableDatabase();
        String query="select * from FRIEND where friend='";
        query+=user_id+"';";
        Cursor cursor=db.rawQuery(query,null);
        while(cursor.moveToNext()){
            result.add(new Friend(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8)));
        }
        return  result;
    }
    public String getImg(String user_id){
        String result=null;
        SQLiteDatabase db=getReadableDatabase();
        String query="select photo from FRIEND where user_id='";
        query+=user_id+"';";
        Cursor cursor=db.rawQuery(query,null);
        while(cursor.moveToNext()){
            result=cursor.getString(0);
        }
        return result;
    }
}
