package jaewanlee.android_project.com.map_memo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by jaewan on 2016-12-13.
 */

public class Direct_input extends AppCompatActivity {
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
//    int addrCategory1;
//    int addrCategory2;
//    int category;
    static int GETIMAGE=1;

    //사용자 아이디
    String user_id;
    //넘겨받은 lat,lng값
    String lat;
    String lng;
    //주소값을 정확히 확인 했는지 확인하는 변수
    int flag;
    int initial_checker;

    //사용되는 DB아이디
    String DBId;
    String map_id;

    AdapterView.OnItemSelectedListener onItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_input);

        //부들 연결하기
        title_tv=(TextView)findViewById(R.id.input_title_textView);
        subtitle_tv=(TextView)findViewById(R.id.input_subtitle_textView);
        location_tv=(TextView)findViewById(R.id.input_addrCategory_TextView);
        addrCategory1_sp=(Spinner)findViewById(R.id.input_addrCategory1_Spinner);
        addrCategory2_sp=(Spinner)findViewById(R.id.input_addrCategory2_Spinner);
        addrDetail_tv=(TextView)findViewById(R.id.input_addrDetail_TextView);
        addrDetail_et=(EditText)findViewById(R.id.input_addrDetail_EditText);
//        addrCheck_bt=(Button)findViewById(R.id.input_addrCheck_Button);
        memo_title_tv=(TextView)findViewById(R.id.input_memoTitle_TextView);
        memo_title_et=(EditText)findViewById(R.id.input_memotitle_EditText);
        date_tv=(TextView)findViewById(R.id.input_date_TextView);
        date_bt=(Button)findViewById(R.id.input_year_Edittext);
        category_tv=(TextView)findViewById(R.id.input_category_TextView);
        category_sp=(Spinner)findViewById(R.id.input_category_Spinner);
        image_tv=(TextView)findViewById(R.id.input_image_TextView);
        image_addr_tv=(TextView)findViewById(R.id.input_image_addr_TextView);
        image_bt=(Button)findViewById(R.id.input_imageAdd_Button);
        memo_tv=(TextView)findViewById(R.id.input_memo_TextView);
        memo_et=(EditText) findViewById(R.id.input_memo_EditText);
        save_bt=(Button)findViewById(R.id.input_save_Button);
        cancel_bt=(Button)findViewById(R.id.input_cancel_Button);
        //사용데이터 값 초기화
//        addrCategory1=0;
//        addrCategory2=0;
//        category=0;

        //글꼴 설정
        title_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        subtitle_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        location_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        addrDetail_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        addrDetail_et.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
//        addrCheck_bt.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        memo_title_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        memo_title_et.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        date_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        date_bt.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        category_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        image_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        image_addr_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        image_bt.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        memo_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        memo_et.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        save_bt.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        cancel_bt.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));

        map_bt=(ImageButton)findViewById(R.id.input_map_ImageButton);
        list_bt=(ImageButton)findViewById(R.id.input_list_Imagebutton);
        chat_bt=(ImageButton)findViewById(R.id.input_chat_Imagebutton);
        setting_bt=(ImageButton)findViewById(R.id.input_setting_Imagebutton);
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

        //데이터를 클릭시 캘린더 위젯이 호출되도록
        date_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(Direct_input.this,onDateSetListener,2016,11,14);
                datePickerDialog.show();
            }
        });


        //대지역 스피너 데이터 연동
        ArrayAdapter addrCategory1_adapter=ArrayAdapter.createFromResource(this,R.array.citiy,android.R.layout.simple_spinner_item);
        addrCategory1_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addrCategory1_sp.setAdapter(addrCategory1_adapter);
        addrCategory1_sp.setSelection(0);
        category1_array=getResources().getStringArray(R.array.citiy);


        //대지역 스피너 아이템 클릭 이벤트
//        addrCategory1_sp.setOnItemSelectedListener(onItemSelectedListener);
        addrCategory1_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              if(initial_checker==1){
                  initial_checker=-1;
              }
                else{
                //포지션 값에 따른 소지역 스피너 데이터 연동
                if(position==2){
                    ArrayAdapter addrCategory2_adapter=ArrayAdapter.createFromResource(Direct_input.this,R.array.경기도,android.R.layout.simple_spinner_item);
                    addrCategory2_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    addrCategory2_sp.setAdapter(addrCategory2_adapter);
//                    addrCategory2_sp.setSelection(0);
                    category2_array=getResources().getStringArray(R.array.경기도);
                }
                //기타 값에따른 소지역스피터 데이터 연동
                else{
                    ArrayAdapter addrCategory2_adapter=ArrayAdapter.createFromResource(Direct_input.this,R.array.서울특별시,android.R.layout.simple_spinner_item);
                    addrCategory2_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    addrCategory2_sp.setAdapter(addrCategory2_adapter);
//                    addrCategory2_sp.setSelection(0);
                    category2_array=getResources().getStringArray(R.array.서울특별시);
                    Log.d("spinner","select listenr");
                }}
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //카테고리 스피너 어뎁터 연결
        ArrayAdapter category_adapter=ArrayAdapter.createFromResource(Direct_input.this,R.array.분류,android.R.layout.simple_spinner_item);
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
                if(memo_title_et.getText().toString().equals("")){
                    Toast.makeText(Direct_input.this, "메모의 제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
                }
                else if(flag==-1){
                    int addrCategory_big=addrCategory1_sp.getSelectedItemPosition();
                    int addrCategory_small=addrCategory2_sp.getSelectedItemPosition();
                    String str=category1_array[addrCategory_big]+" "+category2_array[addrCategory_small]+" "+addrDetail_et.getText().toString();
                    List<Address> geoResult=null;
                    Geocoder geocoder=new Geocoder(getApplicationContext());
                    //list<address>형태로 결과값 받아옥.
                    try {
                        geoResult=geocoder.getFromLocationName(str,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(geoResult.size()>0){
                        //첫번째 결과값 선택
                        Address address=geoResult.get(0);
                        //해당 지점으로 카메라 이동 및 마커 추가
                        lat=String.valueOf(address.getLatitude());
                        lng=String.valueOf(address.getLongitude());
                    }
                    //db추가 쿼리문
                    DBManager dbManager=new DBManager(getApplicationContext(),"MEMO.db",null,1);
                    String query="insert into MEMO values(";
                    query+="null, ";
                    query+="'"+user_id+System.currentTimeMillis()+"', ";
                    query+=String.valueOf(addrCategory1_sp.getSelectedItemPosition())+", ";
                    query+=String.valueOf(addrCategory2_sp.getSelectedItemPosition())+", ";
                    query+="'"+addrDetail_et.getText().toString()+"', ";
                    query+="'"+memo_title_et.getText().toString()+"', ";
                    query+="'"+date_bt.getText().toString()+"', ";
                    query+=String.valueOf(category_sp.getSelectedItemPosition())+", ";
                    if (image_addr_tv.getText().toString().equals("이미지 없음")){
                        query+="'null', ";
                    }
                    else{
                        query+="'"+image_addr_tv.getText().toString()+"', ";
                    }query+="'"+memo_et.getText().toString()+"', ";
                    query+="'"+user_id+"', ";
                    query+="'"+lat+"', ";
                    query+="'"+lng+"',";
                    query+="'"+"1"+"');";
                    dbManager.insert(query);
                    Toast.makeText(Direct_input.this, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                //지도상에 찍어서 새롭게 입력되는 경우
                else if(flag==1){
                    //db추가 쿼리문
                    DBManager dbManager=new DBManager(getApplicationContext(),"MEMO.db",null,1);
                    String query="insert into MEMO values(";
                    query+="null, ";
                    query+="'"+user_id+System.currentTimeMillis()+"', ";
                    query+=String.valueOf(addrCategory1_sp.getSelectedItemPosition())+", ";
                    query+=String.valueOf(addrCategory2_sp.getSelectedItemPosition())+", ";
                    query+="'"+addrDetail_et.getText().toString()+"', ";
                    query+="'"+memo_title_et.getText().toString()+"', ";
                    query+="'"+date_bt.getText().toString()+"', ";
                    query+=String.valueOf(category_sp.getSelectedItemPosition())+", ";
                    if (image_addr_tv.getText().toString().equals("이미지 없음")){
                        query+="'null', ";
                    }
                    else{
                        query+="'"+image_addr_tv.getText().toString()+"', ";
                    }
                    query+="'"+memo_et.getText().toString()+"', ";
                    query+="'"+user_id+"', ";
                    query+="'"+lat+"', ";
                    query+="'"+lng+"',";
                    query+="'"+"1"+"');";
                    dbManager.insert(query);
                    Toast.makeText(Direct_input.this, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                //이미 저장되어 있는 DB일 경우
                else if(flag==0){
                    DBManager dbManager=new DBManager(getApplicationContext(),"MEMO.db",null,1);
                    String query="update MEMO set title='";
                    query+=memo_title_et.getText().toString()+"',DATE='";
                    query+=date_bt.getText().toString()+"',category=";
                    query+=String.valueOf(category_sp.getSelectedItemPosition())+",img=";
                    query+="'"+image_addr_tv.getText().toString()+"',memo='";
                    query+=memo_et.getText().toString()+"' where memo_id=";
                    query+=DBId+";";
                    dbManager.update(query);
                    Toast.makeText(Direct_input.this, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
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
        initial_checker=-1;
        //초기설정
        setIntent();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    //켈린더 위젯 클릭 이벤트
    DatePickerDialog.OnDateSetListener onDateSetListener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            //클릭된 값을 date_bt에 넣어줌
            date_bt.setText(String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(dayOfMonth));
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
                    String filePath =String.valueOf(System.currentTimeMillis());
                    filePath += ".png";
                    FileOutputStream fos = openFileOutput(filePath, 0);
                    btm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    image_addr_tv.setText(filePath);
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
    public List<Address> setIntent(){

        //인텐트 값 넘겨받기
        Intent getIntent=getIntent();
        user_id=getIntent.getStringExtra("user_id");
        List<Address> tempAddr=null;

        //진입 방식에 따른 설정
        flag=getIntent.getIntExtra("fromMap",-1);//어디서 왔는지 확인하기->map을 통해 왔으면 몇몇 예외처리하기
        //직접입력일 경우, 확인하기
        if(flag==-1){
            lat="-1";
            lng="-1";
            Toast.makeText(Direct_input.this, "직접 주소를 입력할  경우, 지도상에 정확한 표시가 안될 수 있습니다", Toast.LENGTH_SHORT).show();

        }
        //사전에 저장되어있는 DB일 경우
        else if(flag==0){
            //DB의 id값을 가져옴
            DBId=getIntent.getStringExtra("DBId");
            Log.d("DBID",DBId);
            DBManager dbManager=new DBManager(getApplicationContext(), "MEMO.db", null, 1);

            ArrayList<String> result=dbManager.wholeDB(Integer.valueOf(DBId));
            if(result.size()>0){
                map_id=result.get(0);
                //주소 카테고리 설정
                setAddrCategory(Integer.valueOf(result.get(1)),Integer.valueOf(result.get(2)));
                //상세 주소 설정
                addrDetail_et.setText(result.get(3));
                memo_title_et.setText(result.get(4));
                date_bt.setText(result.get(5));
                category_sp.setSelection(Integer.valueOf(result.get(6)));
                image_addr_tv.setText(result.get(7));
                memo_et.setText(result.get(8));
            }
            //중주소값 초기화 방지
            initial_checker=1;
            addrCategory1_sp.setEnabled(false);
            addrCategory1_sp.setFocusable(false);
            addrCategory2_sp.setEnabled(false);
            addrCategory2_sp.setFocusable(false);
            addrDetail_et.setFocusable(false);
            addrDetail_et.setClickable(false);
        }
        //지도를 통해 새롭게 등록되는 경우
        else if(flag==1){
            lat=getIntent.getStringExtra("lat");
            lng=getIntent.getStringExtra("lon");
            Geocoder geocoder=new Geocoder(this, Locale.KOREA);
            try {
                tempAddr=geocoder.getFromLocation(Double.valueOf(lat),Double.valueOf(lng),1);
                if(tempAddr!=null && tempAddr.size()>0){
                    //여기서 해당 값 확인해보기
                    Log.d("address2",tempAddr.get(0).toString());
                    Log.d("address2",tempAddr.get(0).getAdminArea());
                    Log.d("address2",tempAddr.get(0).getLocality().toString());
                    Log.d("address2",tempAddr.get(0).getThoroughfare());
                    Log.d("address2",tempAddr.get(0).getFeatureName());
                    //주소 카테고리 설정
                    setAddrCategory(tempAddr.get(0).getAdminArea(),tempAddr.get(0).getLocality());
                    //대주소 잡기
                    String getAddrCategory1=tempAddr.get(0).getAdminArea();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //상세 주소
            addrDetail_et.setText(tempAddr.get(0).getThoroughfare()+" "+tempAddr.get(0).getFeatureName());
            initial_checker=1;
            addrCategory1_sp.setEnabled(false);
            addrCategory1_sp.setFocusable(false);
            addrCategory2_sp.setEnabled(false);
            addrCategory2_sp.setFocusable(false);
            addrDetail_et.setFocusable(false);
            addrDetail_et.setClickable(false);
        }
        else{
            Toast.makeText(Direct_input.this, "잘못된 경로로 접속하셨습니다", Toast.LENGTH_SHORT).show();
        }
        return tempAddr;
    }
    public void setAddrCategory(int addrCategory1,int addrCategory2){
        addrCategory1_sp.setSelection(addrCategory1);
        GetAreaData getAreaData=new GetAreaData(getApplicationContext(),addrCategory1);
        String[] addrCategory2Array=getAreaData.getCategory2Array();
        ArrayAdapter addrCategory2_adapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,addrCategory2Array);
        addrCategory2_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addrCategory2_sp.setAdapter(addrCategory2_adapter);
        addrCategory2_sp.setSelection(addrCategory2);
    }
    public void setAddrCategory(String addrCategory1,String addrCategory2){

        //대주소 잡기
        String getAddrCategory1=addrCategory1;
        for(int i=0;i<category1_array.length;i++){
            if(category1_array[i].equals(getAddrCategory1)){
                addrCategory1_sp.setSelection(i);
                //중주소 잡기
                GetAreaData getAreaData=new GetAreaData(getApplicationContext(),i);
                String[] addrCategory2Array=getAreaData.getCategory2Array();
                ArrayAdapter addrCategory2_adapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,addrCategory2Array);
                addrCategory2_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                addrCategory2_sp.setAdapter(addrCategory2_adapter);
                for(int j=0;j<addrCategory2Array.length;j++) {
                    if(addrCategory2Array[j].contains(addrCategory2)) {
                        addrCategory2_sp.setSelection(j);
                    }
                }
                break;
            }
        }
    }

}
