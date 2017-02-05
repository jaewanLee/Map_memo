package jaewanlee.android_project.com.map_memo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

/**
 * Created by jaewan on 2017-01-15.
 */

public class Friend_service extends Service{

    Friend_DB_Manager friend_db_manager;
    String fileName;
    String user_id;
    int semapore;
    int flag=-1;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent==null){
            return super.onStartCommand(intent,flags,startId);
        }
        else{
            user_id=intent.getStringExtra("user_id");
            friend_db_manager=new Friend_DB_Manager(getApplicationContext(),"FRIEND.db",null,1);
            //회원 정보 가져오기
            String result=GetUserContactList(this.getApplication());
            //이걸로 in 해서 서버 데이터베이스 확인하기
            GetFriend getFriend=new GetFriend(result);
            getFriend.execute();
            return super.onStartCommand(intent, flags, startId);
        }

    }

    public String GetUserContactList(Context ctx){

        Uri uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;

        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Email.DATA }; // 연락처 이름.
        String result = "'email'";

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";
        Cursor contactCursor = ctx.getContentResolver().query(uri,projection,null,null,null);
        while(contactCursor.moveToNext()){
            result+=",'"+contactCursor.getString(0)+"'";
        }
        return result;
    }
    public class GetFriend extends AsyncTask<String,Void,String> {
        String querySet;
        public GetFriend(String querySet){
            this.querySet=querySet;
        }

        @Override
        protected String doInBackground(String... params) {
            //디비 주소
            String link = "http://allucanbuy.vps.phps.kr/map_memo/getFriend.php";
            //데이터 보내기 위해 urlEncoder로 변경하기
            String data = null;
            try {
                data = URLEncoder.encode("where", "UTF-8") + "=" + URLEncoder.encode(querySet, "UTF-8");
                //연결
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                //값을 쓰기위한 stream
                conn.setDoOutput(true);
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                //값을 보내고 끝내기
                osw.write(data);
                osw.close();
                //return 값 읽기
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                Log.d("result",sb.toString().trim());
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line+"\n");
                }
                return sb.toString().trim();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("work",result);
            super.onPostExecute(result);
            if(result!=null){
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray jsonArray=jsonObject.getJSONArray("result");
                    semapore=jsonArray.length();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject friendInfo=jsonArray.getJSONObject(i);
                        //아니야 초기값으로 다 떄려 넣자! ㅋㅋ
                        Friend tempUser=friend_db_manager.getUserProfile(friendInfo.getString("id"),user_id);
                        //업데이트는 다음에 확인하는 걸로...
                        //업데이트는 id먼저 확인하고 변환값 두번쨰로 확인하는 형태로
                        if(tempUser!=null){
                            //중복값인경우, 업데이트 부분을 확인해서 바꿔줌
                            if(!tempUser.getUpdate().equals(friendInfo.getString("update_date"))){
                                //업데이트 날짜가 다른경우, 조건문 바꿔주기
                                if(!friendInfo.getString("photo").equals(tempUser.getPhoto())){
                                    //여긴 프로필 이미지까지 변한경우, 이경우에는 다운로드까지 해줘야함
                                    if(!tempUser.getPhoto().equals("null")){
                                        //프로필 이미지가 이미 존재할 경우 삭제해주기
                                        String oldFile="/data/data/jaewanlee.android_project.com.map_memo/files/";
                                        oldFile+=tempUser.getPhoto();
                                        File f= new File(oldFile);
                                        if(f.delete()){
                                            Log.i("banana", "file remove = " + f.getName() + ", 삭제 성공"); }
                                        else { Log.i("banana", "file remove = " + f.getName() + ", 삭제 실패"); }
                                    }
                                    fileName=friendInfo.getString("photo");
                                    String url="http://allucanbuy.vps.phps.kr/map_memo/uploads/";
                                    url+=fileName;
//                            Picasso.with(getApplicationContext()).load(url).into(target);
                                    Gliding gliding=new Gliding(url);
                                    gliding.execute();
                                    //일반적인 업데이트 전부 진행하기
                                    tempUser.setPhone(friendInfo.getString("phone"));
                                    tempUser.setPhoto(friendInfo.getString("photo"));
                                    tempUser.setDate(friendInfo.getString("date"));
                                    tempUser.setUpdate(friendInfo.getString("update_date"));
                                    String query="update FRIEND set phone=";
                                    query+="'"+tempUser.getPhone()+"',";
                                    query+="photo='"+tempUser.getPhoto()+"',";
                                    query+="date='"+tempUser.getDate()+"',";
                                    query+="update_date='"+tempUser.getUpdate()+"' where user_id='"+tempUser.getUser_id()+"';";
                                    friend_db_manager.update(query);
                                }
                                else{
                                    //일반적인 업데이트 전부 진행하기
                                    tempUser.setPhone(friendInfo.getString("phone"));
                                    tempUser.setPhoto(friendInfo.getString("photo"));
                                    tempUser.setDate(friendInfo.getString("date"));
                                    tempUser.setUpdate(friendInfo.getString("update_date"));
                                    String query="update FRIEND set phone=";
                                    query+="'"+tempUser.getPhone()+"',";
                                    query+="photo='"+tempUser.getPhoto()+"',";
                                    query+="date='"+tempUser.getDate()+"',";
                                    query+="update_date='"+tempUser.getUpdate()+"' where user_id='"+tempUser.getUser_id()+"';";
                                    friend_db_manager.update(query);
                                    semapore--;
                                    if(semapore==0){
                                        Log.d("result","finish1");
                                        stopSelf();
                                    }
                                }
                            }
                            else{
                                semapore--;
                                if(semapore==0){
                                    Log.d("result","finish2");
                                    stopSelf();
                                }
                            }
                        }
                        else{
                            //새롭게 등록된 친구
                            tempUser=new Friend();
                            tempUser.setUser_id(friendInfo.getString("id"));
                            tempUser.setUpdate(friendInfo.getString("update_date"));
                            tempUser.setUser_no(-1);
                            tempUser.setPhone(friendInfo.getString("phone"));
                            tempUser.setPhoto(friendInfo.getString("photo"));
                            tempUser.setName(friendInfo.getString("name"));
                            tempUser.setType(friendInfo.getString("type"));
                            tempUser.setDate(friendInfo.getString("date"));
                            tempUser.setUpdate(friendInfo.getString("update_date"));
                            tempUser.setFriend(user_id);
                            //친구 등록 및 이미지 다운로드
//                            friend_db_manager=new Friend_DB_Manager(getApplicationContext(),"FRIEND.db",null,1);
                            String query="insert into FRIEND values(";
                            query+="null,";
                            query+="'"+tempUser.getUser_id()+"',";
                            query+="'"+tempUser.getPhone()+"',";
                            query+="'"+tempUser.getPhoto()+"',";
                            query+="'"+tempUser.getName()+"',";
                            query+="'"+tempUser.getType()+"',";
                            query+="'"+tempUser.getDate()+"',";
                            query+="'"+tempUser.getUpdate()+"',";
                            query+="'"+tempUser.getFriend()+"');";
                            friend_db_manager.insert(query);
                            //이미지 다운로드 뒤 처리
                            if(!tempUser.getPhoto().equals("null")){
                                //실제 이미지 다운로드
                                fileName=tempUser.getPhoto();
                                String url="http://allucanbuy.vps.phps.kr/map_memo/uploads/";
                                url+=tempUser.getPhoto();
//                            Picasso.with(getApplicationContext()).load(url).into(target);
                                Gliding gliding=new Gliding(url);
                                gliding.execute();
                            }
                            else{
                                semapore--;
                                if(semapore==0){
                                    Log.d("result","finish3");
                                    stopSelf();
                                }
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    class Gliding extends  AsyncTask<Void,Void,Void>{
        String url;
        Bitmap theBitmap;
        Gliding(String url){
            this.url=url;
        }
        @Override
        protected Void doInBackground(Void... params) {
//            Looper.prepare();
            try {
                theBitmap = Glide.
                        with(Friend_service.this).
                        load(url).
                        asBitmap().
                        into(-1,-1).
                        get();
            } catch (final ExecutionException e) {
                Log.d("error",e.toString());

            } catch (final InterruptedException e) {
                Log.d("error",e.toString());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void dummy) {
            if (null != theBitmap) {
                // The full bitmap should be available here
                FileOutputStream fos = null;
                try {
                    fos = openFileOutput(fileName, 0);
//                    imageView_iv.setImageBitmap(theBitmap);
                    theBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    Log.d("result","sucess");
                    semapore--;
                    if(semapore==0){
                        Log.d("result","finish3");
                        stopSelf();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            };
        }
    }
}
