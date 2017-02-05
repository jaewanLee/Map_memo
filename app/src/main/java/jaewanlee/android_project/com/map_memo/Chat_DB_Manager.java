package jaewanlee.android_project.com.map_memo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by jaewan on 2017-01-04.
 */

public class Chat_DB_Manager extends SQLiteOpenHelper {
    public Chat_DB_Manager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table CHATDB (\n" +
                "db_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +//DB 기본값
                "roomNumber TEXT,\n" +//채팅방 번호
                "friend TEXT,\n" +//친구 목록(,)로 구분되어 있음
                "user_id TEXT,\n" +//해당 디비의 주인
                "text TEXT,\n" +//대화 내용(\n으로 각 대화가 구분되어 있음, user_id:aaa (:)로 구분하면 아이디랑 대화내용 구분 가능)
                "title TEXT,\n" +//해당 대화의 타이틀  memo의, 타이틀과 일치함
                "img_addr TEXT,\n"+//이미지의 위치
                "map_id INTEGER,\n" +//맵 메모의 아이디
                "update_date TEXT);");//최근 업데이트 한 날짜
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
    public Chat_DB getChatDB(String user_id,String roomNumber){
        Chat_DB result=null;
        String query="select roomNumber,friend,user_id,text,title,img_addr,map_id from CHATDB where roomNumber='"+roomNumber+"';";
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);

        while(cursor.moveToNext()){
            if(cursor.getString(2).equals(user_id)){
                result=new Chat_DB(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));
            }
        }
        db.close();
        return result;
    }
    public ArrayList<Chat_DB> getWholeDB(String user_id){
        ArrayList<Chat_DB> chat_dbArrayList=new ArrayList<>();
        String query="select roomNumber,friend,user_id,text,title,img_addr,map_id from CHATDB where user_id='"+user_id+"';";
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        while(cursor.moveToNext()){
            chat_dbArrayList.add(new Chat_DB(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6)));
        }
        db.close();
        return chat_dbArrayList;
    }
}
