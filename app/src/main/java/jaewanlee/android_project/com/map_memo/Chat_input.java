package jaewanlee.android_project.com.map_memo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by jaewan on 2017-01-04.
 */

public class Chat_input extends AppCompatActivity {
    //뷰
    TextView title_tv;
    TextView subtitle_tv;
    TextView location_tv;
    Spinner addrCategory1_sp;
    Spinner addrCategory2_sp;
    TextView addrDetail_tv;
    //    Button addrCheck_bt;
    EditText addrDetail_et;
    TextView memo_title_tv;
    EditText memo_title_et;
    TextView date_tv;
    Button date_bt;
    TextView category_tv;
    Spinner category_sp;
    TextView image_tv;
    TextView image_addr_tv;
    Button image_bt;
    TextView memo_tv;
    EditText memo_et;
    Button save_bt;
    Button cancel_bt;
    ImageButton map_bt;
    ImageButton list_bt;
    ImageButton chat_bt;
    ImageButton setting_bt;

    //스피너로부터 값 가져오기
    String[] category1_array;
    String[] category2_array;
    //저장시 사용되는 데이터
    static int GETIMAGE = 1;

    //넘겨받는 인텐트 내용
    String user_id;
    String map_id;//현재 활성화되어 있는 메모의 아이디
    String img_addr;//해당 맵의 이미지 위치
    String friend;
    String roomNumber;

    //넘겨받은 lat,lng값
    String lat;
    String lng;
    //주소값을 정확히 확인 했는지 확인하는 변수
    int flag;
    int initial_checker;

    //사용되는 DB아이디
    String DBId;

    AdapterView.OnItemSelectedListener onItemSelectedListener;

    private StorageReference mStorageRef;//firebase 이미지 업로드
    String change_img;//이미지에 변화가 있었는지 확인하는것 처음에는 0, 디비로부터 받아오면 1, 바뀌면 -1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_input);
        mStorageRef= FirebaseStorage.getInstance().getReference();
        //부들 연결하기
        title_tv = (TextView) findViewById(R.id.chatinput_title_textView);
        subtitle_tv = (TextView) findViewById(R.id.chatinput_subtitle_textView);
        location_tv = (TextView) findViewById(R.id.chatinput_addrCategory_TextView);
        addrCategory1_sp = (Spinner) findViewById(R.id.chatinput_addrCategory1_Spinner);
        addrCategory2_sp = (Spinner) findViewById(R.id.chatinput_addrCategory2_Spinner);
        addrDetail_tv = (TextView) findViewById(R.id.chatinput_addrDetail_TextView);
        addrDetail_et = (EditText) findViewById(R.id.chatinput_addrDetail_EditText);
        memo_title_tv = (TextView) findViewById(R.id.chatinput_memoTitle_TextView);
        memo_title_et = (EditText) findViewById(R.id.chatinput_memotitle_EditText);
        date_tv = (TextView) findViewById(R.id.chatinput_date_TextView);
        date_bt = (Button) findViewById(R.id.chatinput_year_Edittext);
        category_tv = (TextView) findViewById(R.id.chatinput_category_TextView);
        category_sp = (Spinner) findViewById(R.id.chatinput_category_Spinner);
        image_tv = (TextView) findViewById(R.id.chatinput_image_TextView);
        image_addr_tv = (TextView) findViewById(R.id.chatinput_image_addr_TextView);
        image_bt = (Button) findViewById(R.id.chatinput_imageAdd_Button);
        memo_tv = (TextView) findViewById(R.id.chatinput_memo_TextView);
        memo_et = (EditText) findViewById(R.id.chatinput_memo_EditText);
        save_bt = (Button) findViewById(R.id.chatinput_save_Button);
        cancel_bt = (Button) findViewById(R.id.chatinput_cancel_Button);
        map_bt=(ImageButton)findViewById(R.id.chatinput_map_ImageButton);
        list_bt=(ImageButton)findViewById(R.id.chatinput_list_Imagebutton);
        chat_bt=(ImageButton)findViewById(R.id.chatinput_chat_Imagebutton);
        setting_bt=(ImageButton)findViewById(R.id.chatinput_setting_Imagebutton);
        map_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveTomap=new Intent(getApplicationContext(),MapMain.class);
                moveTomap.putExtra("user_id",user_id);
                startActivity(moveTomap);
                finish();
            }
        });
        list_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToList=new Intent(getApplicationContext(),Category_view.class);
                moveToList.putExtra("user_id",user_id);
                startActivity(moveToList);
                finish();
            }
        });
        chat_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToChat=new Intent(getApplicationContext(),Chat_main.class);
                moveToChat.putExtra("user_id",user_id);
                startActivity(moveToChat);
                finish();

            }
        });
        setting_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToSetting=new Intent(getApplicationContext(),Setting.class);
                moveToSetting.putExtra("user_id",user_id);
                startActivity(moveToSetting);
                finish();
            }
        });

        //글꼴 설정
        title_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        subtitle_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        location_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        addrDetail_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        addrDetail_et.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        memo_title_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        memo_title_et.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        date_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        date_bt.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        category_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        image_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        image_addr_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        image_bt.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        memo_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        memo_et.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        save_bt.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        cancel_bt.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));

        //데이터를 클릭시 캘린더 위젯이 호출되도록
        date_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Chat_input.this, onDateSetListener, 2016, 11, 14);
                datePickerDialog.show();
            }
        });


        //대지역 스피너 데이터 연동
        ArrayAdapter addrCategory1_adapter = ArrayAdapter.createFromResource(this, R.array.citiy, android.R.layout.simple_spinner_item);
        addrCategory1_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addrCategory1_sp.setAdapter(addrCategory1_adapter);
        addrCategory1_sp.setSelection(0);
        category1_array = getResources().getStringArray(R.array.citiy);
        change_img="0";


        //대지역 스피너 아이템 클릭 이벤트
//        addrCategory1_sp.setOnItemSelectedListener(onItemSelectedListener);
        addrCategory1_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (initial_checker == 1) {
                    initial_checker = -1;
                } else {
                    //포지션 값에 따른 소지역 스피너 데이터 연동
                    if (position == 2) {
                        ArrayAdapter addrCategory2_adapter = ArrayAdapter.createFromResource(Chat_input.this, R.array.경기도, android.R.layout.simple_spinner_item);
                        addrCategory2_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        addrCategory2_sp.setAdapter(addrCategory2_adapter);
//                    addrCategory2_sp.setSelection(0);
                        category2_array = getResources().getStringArray(R.array.경기도);
                    }
                    //기타 값에따른 소지역스피터 데이터 연동
                    else {
                        ArrayAdapter addrCategory2_adapter = ArrayAdapter.createFromResource(Chat_input.this, R.array.서울특별시, android.R.layout.simple_spinner_item);
                        addrCategory2_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        addrCategory2_sp.setAdapter(addrCategory2_adapter);
//                    addrCategory2_sp.setSelection(0);
                        category2_array = getResources().getStringArray(R.array.서울특별시);
                        Log.d("spinner", "select listenr");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //카테고리 스피너 어뎁터 연결
        ArrayAdapter category_adapter = ArrayAdapter.createFromResource(Chat_input.this, R.array.분류, android.R.layout.simple_spinner_item);
        category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_sp.setAdapter(category_adapter);
        category_sp.setSelection(0);


        //이미지 버튼 가져오기를 클릭해서 가져오는 과정
        image_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getImage = new Intent(Intent.ACTION_PICK);
                getImage.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(getImage, GETIMAGE);
            }
        });

        //saveButton 클릭 이벤트
        save_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //직접 입력일 경우 받은 주소
                if (memo_title_et.getText().toString().equals("")) {
                    Toast.makeText(Chat_input.this, "메모의 제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
                } else if (flag == -1) {
                    int addrCategory_big = addrCategory1_sp.getSelectedItemPosition();
                    int addrCategory_small = addrCategory2_sp.getSelectedItemPosition();
                    String str = category1_array[addrCategory_big] + " " + category2_array[addrCategory_small] + " " + addrDetail_et.getText().toString();
                    List<Address> geoResult = null;
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    //list<address>형태로 결과값 받아옥.
                    try {
                        geoResult = geocoder.getFromLocationName(str, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (geoResult.size() > 0) {
                        //첫번째 결과값 선택
                        Address address = geoResult.get(0);
                        //해당 지점으로 카메라 이동 및 마커 추가
                        lat = String.valueOf(address.getLatitude());
                        lng = String.valueOf(address.getLongitude());
                    }
                    //db추가 쿼리문
                    DBManager dbManager = new DBManager(getApplicationContext(), "MEMO.db", null, 1);
                    String query = "insert into MEMO values(";
                    query += "null, ";
                    map_id = user_id + System.currentTimeMillis();
                    query += "'" + map_id + "', ";
                    query += String.valueOf(addrCategory1_sp.getSelectedItemPosition()) + ", ";
                    query += String.valueOf(addrCategory2_sp.getSelectedItemPosition()) + ", ";
                    query += "'" + addrDetail_et.getText().toString() + "', ";
                    query += "'" + memo_title_et.getText().toString() + "', ";
                    query += "'" + date_bt.getText().toString() + "', ";
                    query += String.valueOf(category_sp.getSelectedItemPosition()) + ", ";
                    if (image_addr_tv.getText().toString().equals("이미지 없음")) {
                        img_addr = "null";
                        query += "'" + img_addr + "', ";
                    } else {
                        img_addr = image_addr_tv.getText().toString();
                        query += "'" + img_addr + "', ";
                    }
                    query += "'" + memo_et.getText().toString() + "', ";
                    query += "'" + friend + "', ";
                    query += "'" + lat + "', ";
                    query += "'" + lng + "',";
                    query += "'" + "-1" + "');";
                    dbManager.insert(query);

                    Toast.makeText(Chat_input.this, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    //결과값 돌려주기
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("title", memo_title_et.getText().toString());
                    returnIntent.putExtra("location", addrDetail_et.getText().toString());
                    returnIntent.putExtra("map_id", map_id);
                    returnIntent.putExtra("img_addr", img_addr);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
                //지도상에 찍어서 새롭게 입력되는 경우
                else if (flag == 1) {
                    //db추가 쿼리문
                    DBManager dbManager = new DBManager(getApplicationContext(), "MEMO.db", null, 1);
                    String query = "insert into MEMO values(";
                    query += "null, ";
                    map_id = user_id + System.currentTimeMillis();
                    query += "'" + map_id + "', ";
                    query += String.valueOf(addrCategory1_sp.getSelectedItemPosition()) + ", ";
                    query += String.valueOf(addrCategory2_sp.getSelectedItemPosition()) + ", ";
                    query += "'" + addrDetail_et.getText().toString() + "', ";
                    query += "'" + memo_title_et.getText().toString() + "', ";
                    query += "'" + date_bt.getText().toString() + "', ";
                    query += String.valueOf(category_sp.getSelectedItemPosition()) + ", ";
                    String onlyFriend = friend.replace(user_id + ",", "");
                    if (image_addr_tv.getText().toString().equals("이미지 없음")) {
                        img_addr = "null";
                        query += "'" + img_addr + "', ";
                    } else {
                        img_addr = image_addr_tv.getText().toString();
                        String filepath="/data/data/jaewanlee.android_project.com.map_memo/files/";
                        HashMap<String,String> hashMap=new HashMap<String, String>();
                        hashMap.put("friend",onlyFriend);
                        fileUpLoad(filepath+img_addr,img_addr,hashMap);
                        query += "'" + img_addr + "', ";
                    }
                    query += "'" + memo_et.getText().toString() + "', ";
                    query += "'" + friend + "', ";
                    query += "'" + lat + "', ";
                    query += "'" + lng + "',";
                    query += "'" + "-1" + "');";
                    dbManager.insert(query);
                    HashMap<String,String> hashMap=new HashMap<String, String>();
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("query",query);
                        jsonObject.put("type","map_memo");
                        hashMap.put("friend",onlyFriend);
                        hashMap.put("message",jsonObject.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Send send=new Send(hashMap);
                    send.execute();
                    Toast.makeText(Chat_input.this, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent moveToChatRoom = new Intent(getApplicationContext(), Chat_room.class);
//                //인텐트에 데이터 넣기
                    moveToChatRoom.putExtra("user_id", user_id);
                    moveToChatRoom.putExtra("friend", friend);
                    moveToChatRoom.putExtra("roomNumber", roomNumber);
                    moveToChatRoom.putExtra("map_id", map_id);
                    moveToChatRoom.putExtra("title", memo_title_et.getText().toString());
                    moveToChatRoom.putExtra("img_addr", img_addr);
                    startActivity(moveToChatRoom);
                    finish();
                }
                //이미 저장되어 있는 DB일 경우
                else if (flag == 0) {
                    DBManager dbManager = new DBManager(getApplicationContext(), "MEMO.db", null, 1);
                    String query = "update MEMO set title='";
                    query += memo_title_et.getText().toString() + "',DATE='";
                    query+=date_bt.getText().toString()+"',category=";
                    query+=String.valueOf(category_sp.getSelectedItemPosition())+",img=";
                    query+="'"+image_addr_tv.getText().toString()+"',memo='";
                    query+=memo_et.getText().toString()+"' where map_id='";
                    query += map_id + "';";
                    dbManager.update(query);
                    String onlyFriend = friend.replace(user_id + ",", "");
                    HashMap<String,String> hashMap=new HashMap<String, String>();
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("query",query);
                        jsonObject.put("type","map_memo");
                        hashMap.put("friend",onlyFriend);
                        hashMap.put("message",jsonObject.toString());

                        Send send=new Send(hashMap);
                        send.execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(change_img.equals("-1")){
                        String filepath="/data/data/jaewanlee.android_project.com.map_memo/files/";
                        HashMap<String,String> hashMap2=new HashMap<String, String>();
                        hashMap2.put("friend",onlyFriend);
                        fileUpLoad(filepath+img_addr,img_addr,hashMap2);
                    }

                    Toast.makeText(Chat_input.this, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    //결과값 돌려주기
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("title", memo_title_et.getText().toString());
                    returnIntent.putExtra("location", addrDetail_et.getText().toString());
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
        cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //인텐트에서 값 받아오기
        initial_checker = -1;
        //초기설정
        setIntent();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    //켈린더 위젯 클릭 이벤트
    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            //클릭된 값을 date_bt에 넣어줌
            date_bt.setText(String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(dayOfMonth));
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //이미지를 가져온뒤 처리하는 과정
        if (requestCode == GETIMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri mImageUri = data.getData();
                int[] maxTexturesize = new int[1];
                GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTexturesize, 0);

                try {//get bitmap file from gallery and resize it to fit ImageView. after then setting the imageView to getted bitmap
//                    Bitmap btm = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                    AssetFileDescriptor afd = getContentResolver().openAssetFileDescriptor(mImageUri, "r");
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inSampleSize = 4;
                    Bitmap btm = BitmapFactory.decodeFileDescriptor(afd.getFileDescriptor(), null, opt);
                    //save bitmap file to sdCard(inner),
//                    String filePath="/data/data/jaewanlee.android_project.com.map_memo/files/";
                    String filePath = user_id+String.valueOf(System.currentTimeMillis());
                    filePath += ".png";
                    FileOutputStream fos = openFileOutput(filePath, 0);
                    btm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    image_addr_tv.setText(filePath);
                    if (change_img.equals("1")) {
                        change_img="-1";
                    }
                } catch (IOException e) {
                    Log.d("", "i/oexception");
                } catch (IndexOutOfBoundsException e) {
                    Log.d("somethig wrong!", "wrong!");
                } catch (SecurityException e) {
                    Log.d("w", "security");
                } catch (RuntimeException e) {
                    Log.d("somethig wrong!", "runtime!");
                }
            } else {
                Log.d("no resultCode", "no resultCode");
            }
        } else {
            Log.d("no requesetCode", "no requeset");
        }
    }

    public List<Address> setIntent() {

        //인텐트 값 넘겨받기
        Intent getIntent = getIntent();
        user_id = getIntent.getStringExtra("user_id");
        List<Address> tempAddr = null;

        //진입 방식에 따른 설정
        flag = getIntent.getIntExtra("fromMap", -1);//어디서 왔는지 확인하기->map을 통해 왔으면 몇몇 예외처리하기
        //직접입력일 경우, 확인하기
        if (flag == -1) {
            lat = "-1";
            lng = "-1";
            Toast.makeText(Chat_input.this, "직접 주소를 입력할  경우, 지도상에 정확한 표시가 안될 수 있습니다", Toast.LENGTH_SHORT).show();

        }
        //사전에 저장되어있는 DB일 경우
        else if (flag == 0) {
            //DB의 id값을 가져옴
            DBId = getIntent.getStringExtra("DBId");
            Log.d("DBID", DBId);
            DBManager dbManager = new DBManager(getApplicationContext(), "MEMO.db", null, 1);

            ArrayList<String> result = dbManager.wholeDB(Integer.valueOf(DBId));
            if (result.size() > 0) {
                map_id = result.get(0);//맵 아이디
                //주소 카테고리 설정
                setAddrCategory(Integer.valueOf(result.get(1)), Integer.valueOf(result.get(2)));
                //상세 주소 설정
                addrDetail_et.setText(result.get(3));
                memo_title_et.setText(result.get(4));
                date_bt.setText(result.get(5));
                category_sp.setSelection(Integer.valueOf(result.get(6)));
                image_addr_tv.setText(result.get(7));
                memo_et.setText(result.get(8));
                //채팅방 디비에서 받아오거나....아예 첨부터 추가하거나..
                friend=result.get(9);
                change_img="1";
            }
            //중주소값 초기화 방지
            initial_checker = 1;
            addrCategory1_sp.setEnabled(false);
            addrCategory1_sp.setFocusable(false);
            addrCategory2_sp.setEnabled(false);
            addrCategory2_sp.setFocusable(false);
            addrDetail_et.setClickable(false);
            addrDetail_et.setFocusable(false);
        }
        //지도를 통해 새롭게 등록되는 경우
        else if (flag == 1) {
            lat = getIntent.getStringExtra("lat");
            lng = getIntent.getStringExtra("lon");
            roomNumber = getIntent.getStringExtra("roomNumber");
            friend = getIntent.getStringExtra("friend");

            LatLng tempLatLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
            Geocoder geocoder = new Geocoder(this, Locale.KOREA);
            try {
                tempAddr = geocoder.getFromLocation(Double.valueOf(lat), Double.valueOf(lng), 1);
                if (tempAddr != null && tempAddr.size() > 0) {
                    //여기서 해당 값 확인해보기
                    Log.d("address2", tempAddr.get(0).toString());
                    Log.d("address2", tempAddr.get(0).getAdminArea());
                    Log.d("address2", tempAddr.get(0).getLocality().toString());
                    Log.d("address2", tempAddr.get(0).getThoroughfare());
                    Log.d("address2", tempAddr.get(0).getFeatureName());
                    //주소 카테고리 설정
                    setAddrCategory(tempAddr.get(0).getAdminArea(), tempAddr.get(0).getLocality());
                    //대주소 잡기
                    String getAddrCategory1 = tempAddr.get(0).getAdminArea();
                    addrDetail_et.setText(tempAddr.get(0).getThoroughfare() + " " + tempAddr.get(0).getFeatureName());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "위도 경도 설정 오류", Toast.LENGTH_SHORT).show();
            }
            //상세 주소
            initial_checker = 1;
            addrCategory1_sp.setEnabled(false);
            addrCategory1_sp.setFocusable(false);
            addrCategory2_sp.setEnabled(false);
            addrCategory2_sp.setFocusable(false);
            addrDetail_et.setClickable(false);
            addrDetail_et.setFocusable(false);
        } else {
            Toast.makeText(Chat_input.this, "잘못된 경로로 접속하셨습니다", Toast.LENGTH_SHORT).show();
        }

        return tempAddr;
    }

    public void setAddrCategory(int addrCategory1, int addrCategory2) {
        addrCategory1_sp.setSelection(addrCategory1);
        GetAreaData getAreaData = new GetAreaData(getApplicationContext(), addrCategory1);
        String[] addrCategory2Array = getAreaData.getCategory2Array();
        ArrayAdapter addrCategory2_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, addrCategory2Array);
        addrCategory2_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addrCategory2_sp.setAdapter(addrCategory2_adapter);
        addrCategory2_sp.setSelection(addrCategory2);
    }

    public void setAddrCategory(String addrCategory1, String addrCategory2) {

        //대주소 잡기
        String getAddrCategory1 = addrCategory1;
        for (int i = 0; i < category1_array.length; i++) {
            if (category1_array[i].equals(getAddrCategory1)) {
                addrCategory1_sp.setSelection(i);
                //중주소 잡기
                GetAreaData getAreaData = new GetAreaData(getApplicationContext(), i);
                String[] addrCategory2Array = getAreaData.getCategory2Array();
                ArrayAdapter addrCategory2_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, addrCategory2Array);
                addrCategory2_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                addrCategory2_sp.setAdapter(addrCategory2_adapter);
                for (int j = 0; j < addrCategory2Array.length; j++) {
                    if (addrCategory2Array[j].contains(addrCategory2)) {
                        addrCategory2_sp.setSelection(j);
                    }
                }
                break;
            }
        }
    }
    class Send extends AsyncTask<String, Void, String> {
        HashMap<String,String> map;
        Send(HashMap map){
            this.map=map;
        }
        @Override
        protected String doInBackground(String... strings) {
            String response=send(map,"http://allucanbuy.vps.phps.kr/map_memo/push_notification.php");
            Log.d("result",response);
            return null;
        }
    }
    private String send(HashMap<String, String> map, String addr) {
        String response = ""; // DB 서버의 응답을 담는 변수

        try {
            URL url = new URL(addr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // 해당 URL에 연결

            conn.setConnectTimeout(10000); // 타임아웃: 10초
            conn.setUseCaches(false); // 캐시 사용 안 함
            conn.setRequestMethod("POST"); // POST로 연결
            conn.setDoInput(true);
            conn.setDoOutput(true);

            if (map != null) { // 웹 서버로 보낼 매개변수가 있는 경우우
                OutputStream os = conn.getOutputStream(); // 서버로 보내기 위한 출력 스트림
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); // UTF-8로 전송
                bw.write(getPostString(map)); // 매개변수 전송
                bw.flush();
                bw.close();
                os.close();
            }

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) { // 연결에 성공한 경우
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림

                while ((line = br.readLine()) != null) // 서버의 응답을 읽어옴
                    response += line;
            }

            conn.disconnect();
        } catch (MalformedURLException me) {
            me.printStackTrace();
            return me.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
        return response;
    }
    private String getPostString(HashMap<String, String> map) {
        StringBuilder result = new StringBuilder();
        boolean first = true; // 첫 번째 매개변수 여부

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (first)
                first = false;
            else // 첫 번째 매개변수가 아닌 경우엔 앞에 &를 붙임
                result.append("&");
            try { // UTF-8로 주소에 키와 값을 붙임
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException ue) {
                ue.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result.toString();
    }
    //업로딩 파일
    public void fileUpLoad(String filePath, String fileName, final HashMap<String,String> hashMap){
        FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
        StorageReference storageRef=firebaseStorage.getReferenceFromUrl("gs://sunny-truth-151708.appspot.com/map_memo_img/").child(fileName);
        Bitmap bitmap=BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] data=outputStream.toByteArray();
        UploadTask uploadTask=storageRef.putBytes(data);
//        UploadTask uploadTask=storageRef.putFile(Uri.fromFile(file));
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Chat_input.this, "이미지 전송에 실패하였습니다", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("type","img_addr");
                    jsonObject.put("img_addr",img_addr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hashMap.put("message",jsonObject.toString());
                Send send=new Send(hashMap);
                send.execute();
                Toast.makeText(Chat_input.this,"이미지 전송이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
