package jaewanlee.android_project.com.map_memo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by jaewan on 2016-12-02.
 */

public class Home extends AppCompatActivity {

    //레이아웃 셋팅
    TextView title_tv;//전체 타이틀
    ImageButton addMemo_bt;//새로운 메모 바로 추가하기
    TextView subtitle1_tv;//부제목 1
    ListView memo_listView;//최신 메모 리스트 뷰
    TextView subtitle2_tv;//부제목 2
    ListView messeage_listView;//최신 메세지 리스트 뷰
    ImageButton map_bt;//지도화면 이동 버튼
    ImageButton list_bt;//리스트 화면 이동버튼
    ImageButton chat_bt;//메세지 화면 이동 버튼
    ImageButton setting_bt;//세팅 화면 이동버튼

    //프로그램 내에서 사용되는 인텐트
    Intent intent;
    //유저 아이디
    String user_id;
    //리스트뷰에 들어갈 어뎁터
    Category_view_Adapter listAdapter;
    //리스트뷰에 사용되는 DB
    DBManager dbManager;
    ArrayList<MarkerItem> getMarker;
    ArrayList<Category_view_Data> getDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //레이아웃 연동하기
        title_tv = (TextView) findViewById(R.id.home_title_textView);
        addMemo_bt = (ImageButton) findViewById(R.id.home_add_button);
        subtitle1_tv = (TextView) findViewById(R.id.home_subtitle1_textView);
        memo_listView = (ListView) findViewById(R.id.home_memo_listView);
        subtitle2_tv = (TextView) findViewById(R.id.home_subtitle2_textView);
        messeage_listView = (ListView) findViewById(R.id.home_messege_listView);
        map_bt = (ImageButton) findViewById(R.id.home_map_ImageButton);
        list_bt = (ImageButton) findViewById(R.id.home_list_Imagebutton);
        chat_bt = (ImageButton) findViewById(R.id.home_chat_Imagebutton);
        setting_bt = (ImageButton) findViewById(R.id.home_setting_Imagebutton);

        //폰트 변경하기
        title_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        subtitle1_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        subtitle2_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));

        //접속 정보 가져오기
        intent = getIntent();
        user_id = intent.getStringExtra("user_id");

        //친구찾기 서비스 실행
         Intent serviceStart=new Intent(getApplicationContext(),Friend_service.class);
        serviceStart.putExtra("user_id",user_id);
        startService(serviceStart);


        //메모 클릭 버튼
        addMemo_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //팝업창 설정
                Intent moveToMap = new Intent(getApplicationContext(), HomePopUp.class);
                moveToMap.putExtra("user_id", user_id);
                startActivity(moveToMap);
            }
        });

        //메모 리스트뷰 어뎁터 세팅 및 DB 가져오기
        listAdapter = new Category_view_Adapter(this.getApplicationContext());
        dbManager = new DBManager(getApplicationContext(), "MEMO.db", null, 1);
        getMarker = dbManager.listViewDB();
        getDb = addrIntToString(getMarker);
        listAdapter.setData(getDb);
        memo_listView.setAdapter(listAdapter);
        //리스트뷰 클릭 이벤트
        memo_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(((Category_view_Data)listAdapter.getItem(position)).getChat().equals("1")){
                    Intent moveToInput=new Intent(getApplicationContext(),Direct_input.class);
                    moveToInput.putExtra("user_id",user_id);
                    moveToInput.putExtra("fromMap",0);
                    moveToInput.putExtra("DBId",String.valueOf(listAdapter.getDBId(position)));
                    startActivity(moveToInput);
                }
                else if(((Category_view_Data)listAdapter.getItem(position)).getChat().equals("-1")){
                    Intent moveToInput=new Intent(getApplicationContext(),Chat_input.class);
                    moveToInput.putExtra("user_id",user_id);
                    moveToInput.putExtra("fromMap",0);
                    moveToInput.putExtra("DBId",String.valueOf(listAdapter.getDBId(position)));
                    startActivity(moveToInput);
                }

            }
        });

        map_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToMap = new Intent(getApplicationContext(), MapMain.class);
                moveToMap.putExtra("user_id", user_id);
                startActivity(moveToMap);
            }
        });
        list_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToList = new Intent(getApplicationContext(), Category_view.class);
                moveToList.putExtra("user_id", user_id);
                startActivity(moveToList);
            }
        });
        chat_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToChat=new Intent(getApplicationContext(),Chat_main.class);
                moveToChat.putExtra("user_id",user_id);
                startActivity(moveToChat);
            }
        });
        setting_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToSet=new Intent(getApplicationContext(),Setting.class);
                moveToSet.putExtra("user_id",user_id);
                startActivity(moveToSet);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getMarker = dbManager.listViewDB();
        getDb = addrIntToString(getMarker);
        listAdapter.setData(getDb);
        listAdapter.notifyDataSetChanged();
    }

    public ArrayList<Category_view_Data> addrIntToString (ArrayList<MarkerItem> markerItems){
        ArrayList<Category_view_Data> categoryViewDatas=new ArrayList<>();

        for(int i=0;i<markerItems.size();i++){
            MarkerItem tempMarkerItem=markerItems.get(i);
            Category_view_Data tempCategory_db=new Category_view_Data();
            tempCategory_db.setId(tempMarkerItem.getMemo_id());
            tempCategory_db.setTitle(tempMarkerItem.getTitle());
            tempCategory_db.setChat(tempMarkerItem.getChat());
            //주소설정
            String address="";
            //1차 카테고리
            String[] addrCategory1=getResources().getStringArray(R.array.citiy);
            address+=addrCategory1[tempMarkerItem.getAddrCategory1()]+" ";
            //두번쨰 지역 string은 2차배열로 만들자
            //*********************************************************************************************************************************************
            GetAreaData getAreaData=new GetAreaData(getApplicationContext(),tempMarkerItem.getAddrCategory1());
            String[] addrCategory2=getAreaData.getCategory2Array();
            address+=addrCategory2[tempMarkerItem.getAddrCategory2()]+" ";
            address+=tempMarkerItem.getAddrDetail();
            tempCategory_db.setAddress(address);
            categoryViewDatas.add(tempCategory_db);
        }
        return categoryViewDatas;
    }
}


