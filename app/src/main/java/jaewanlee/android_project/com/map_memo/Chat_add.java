package jaewanlee.android_project.com.map_memo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.Manifest;

/**
 * Created by jaewan on 2017-01-04.
 */

public class Chat_add extends AppCompatActivity{
    Button okButton_bt;
//    LinearLayout chatLocationInfo_lv;
//    TextView chatTitle_tv;
//    TextView chatLocation_tv;
//    TextView subTitleMemo_tv;
    TextView subTitlechat_tv;
    EditText findFriend_et;
    ListView friend_lv;
//    ImageView imageView_iv;

    String user_id;
    String memo_id;//새롭게 추가되는 채팅방에 종속되어 있는 메모의 아이디
    String title;
    String img_addr;
    String fileName;//프로필 이미지 다운받았을때 경로로 사용

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;//마쉬멜로 이상 퍼미션 사용

    Friend_DB_Manager friend_db_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_add);

        //뷰연결하기
        okButton_bt=(Button)findViewById(R.id.chatAdd_ok_Button);
//        chatLocationInfo_lv=(LinearLayout)findViewById(R.id.chatAdd_memo_LinearLayout);
//        chatTitle_tv=(TextView)findViewById(R.id.chatAdd_memoTitle_TextView);
//        chatLocation_tv=(TextView)findViewById(R.id.chatAdd_memoLocation_TextView);
//        subTitleMemo_tv=(TextView)findViewById(R.id.chatAdd_subtitleMemo_textView);
        subTitlechat_tv=(TextView)findViewById(R.id.chatAdd_subtitlefriend_textView);
        findFriend_et=(EditText)findViewById(R.id.chatAdd_friend_editText);
        friend_lv=(ListView)findViewById(R.id.chatAdd_friend_listView);
//        imageView_iv=(ImageView)findViewById(R.id.chatAdd_image_ImageView);

        //글꼴설정
        okButton_bt.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
//        chatTitle_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
//        chatLocation_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
//        subTitleMemo_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        subTitlechat_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        findFriend_et.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));


        //초기 인텐트 설정
        final Intent getintent=getIntent();
        user_id=getintent.getStringExtra("user_id");



        //ok버튼 클릭
        okButton_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(img_addr==null){
                    img_addr="null";
                }
                Intent moveToChatMap=new Intent(getApplicationContext(),Chat_map.class);
                moveToChatMap.putExtra("user_id",user_id);
                moveToChatMap.putExtra("friend",user_id+","+findFriend_et.getText().toString());
                moveToChatMap.putExtra("roomNumber","-1");
                startActivity(moveToChatMap);

            }
        });
//        //회원 정보 가져오기
//        String result=GetUserContactList(this.getApplication());
//
//        //이걸로 in 해서 서버 데이터베이스 확인하기
//        GetFriend getFriend=new GetFriend(result);
//        getFriend.execute();

        //리스트뷰 형태로 출력하기
        //기존의 친구 정보 가져오기
         friend_db_manager=new Friend_DB_Manager(getApplicationContext(),"FRIEND.db",null,1);
        ArrayList<Friend>  friends=friend_db_manager.getMyFriend(user_id);
        final Friend_adapter friendAdapter=new Friend_adapter();
        friendAdapter.setData(friends);
        friend_lv.setAdapter(friendAdapter);

        //리스트 뷰 클릭 이벤트
        friend_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!findFriend_et.getText().toString().contains(((Friend)friendAdapter.getItem(position)).getUser_id())){
                    if(findFriend_et.getText().toString().equals("")){
                        findFriend_et.setText(((Friend)friendAdapter.getItem(position)).getUser_id());
                    }
                    else{
                        findFriend_et.append(","+((Friend)friendAdapter.getItem(position)).getUser_id());
                    }
                }
            }
        });
        Log.d("result",String.valueOf(isServiceRunningCheck()));


    }
    //서비스가 작동중인지 확인하는 메소드
    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("jaewanlee.android_project.com.map_memo.Friend_service".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    //반복문
    //서버에서 같은 아이디가 있는지 확인
    //없으면 패스
    //있으면 유저 정보와 이미지를 다운함
    //해당 데이터를 sqlite에 저장함

    //이메일 정보랑 이름을 받아옴
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
    public class GetFriend extends AsyncTask<String,Void,String>{
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
                                    fileName=friendInfo.getString("photo");
                                    String url="http://allucanbuy.vps.phps.kr/map_memo/uploads/";
                                    url+=fileName;
//                            Picasso.with(getApplicationContext()).load(url).into(target);
                                    Gliding gliding=new Gliding(url);
                                    gliding.execute();
                                }
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

                            }
                        }
                        else{
                            //새롭게 등록된 친구
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
                            //실제 이미지 다운로드
                            fileName=tempUser.getPhoto();
                            String url="http://allucanbuy.vps.phps.kr/map_memo/uploads/";
                            url+=tempUser.getPhoto();
//                            Picasso.with(getApplicationContext()).load(url).into(target);
                            Gliding gliding=new Gliding(url);
                            gliding.execute();
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
                        with(Chat_add.this).
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
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            };
        }
    }
    com.squareup.picasso.Target target=new com.squareup.picasso.Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            FileOutputStream fos = null;
            try {
                fos = openFileOutput(fileName, 0);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("error",e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("error",e.toString());
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Toast.makeText(Chat_add.this, "친구 프로필 사진 출력에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
    @Override
    public void onDestroy() {  // could be in onPause or onStop
        Picasso.with(this).cancelRequest(target);
        super.onDestroy();
    }





//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==1){
//            if (resultCode==RESULT_OK){
//                chatTitle_tv.setText(data.getStringExtra("title"));
//                chatLocation_tv.setText(data.getStringExtra("location"));
//                memo_id=data.getStringExtra("map_id");
//                img_addr=data.getStringExtra("img_addr");
//            }
//        }
//    }
}
