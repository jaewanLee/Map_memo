package jaewanlee.android_project.com.map_memo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by jaewan on 2016-12-22.
 */

public class Category_view extends AppCompatActivity {
    //뷰들
    TextView title_tv;
    TextView subTitle_tv;
    Spinner addrCategory1_sp;
    Spinner addrCategory2_sp;
    TextView num_tv;
    TextView listTitle_tv;
    TextView address_tv;
    ListView listView;
    ImageButton map_bt;
    ImageButton list_bt;
    ImageButton chat_bt;
    ImageButton set_bt;

    //데이터 베이스
    DBManager dbManager;
    ArrayList<MarkerItem> getMarker;
    ArrayList<Category_view_Data> getDb;
    Category_view_Adapter adapter;

    int addrFlag1;
    int addrFlag2;

    String user_id;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        //뷰들 연결하기
        title_tv=(TextView)findViewById(R.id.category_title_textView);
        subTitle_tv=(TextView)findViewById(R.id.category_subtitle_textView);
        addrCategory1_sp=(Spinner)findViewById(R.id.category_addrCategory1_Spinner);
        addrCategory2_sp=(Spinner)findViewById(R.id.category_addrCategory2_Spinner);
        num_tv=(TextView)findViewById(R.id.category_num_textView);
        listTitle_tv=(TextView)findViewById(R.id.category_listTitle_textView);
        address_tv=(TextView)findViewById(R.id.category_addr_textView);
        listView=(ListView)findViewById(R.id.category_list_listView);
        map_bt=(ImageButton)findViewById(R.id.category_map_ImageButton);
        list_bt=(ImageButton)findViewById(R.id.category_list_Imagebutton);
        chat_bt=(ImageButton)findViewById(R.id.category_chat_Imagebutton);
        set_bt=(ImageButton)findViewById(R.id.category_setting_Imagebutton);
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
        set_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToSetting=new Intent(getApplicationContext(),Setting.class);
                moveToSetting.putExtra("user_id",user_id);
                startActivity(moveToSetting);
                finish();
            }
        });

        //글꼴 설정
        title_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        subTitle_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        num_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        listTitle_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        address_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));

        //인텐트 받기
        final Intent getIntent=getIntent();
        user_id=getIntent.getStringExtra("user_id");

        //리스트뷰 설정
        adapter=new Category_view_Adapter(this.getApplication());
        //어뎁터에 db넣기
        dbManager=new DBManager(getApplicationContext(),"MEMO.db",null,1);
        getMarker=dbManager.listViewDB();
        getDb=addrIntToString(getMarker);
        adapter.setData(getDb);
        listView.setAdapter(adapter);
        ///스피너 값 고정을 위한 값들 초기화
        addrFlag1=-1;
        addrFlag2=-1;

        //스피너 카테고리 1 초기화 설정
        ArrayAdapter addrCategory1_adapter=ArrayAdapter.createFromResource(this,R.array.citiy,android.R.layout.simple_spinner_item);
        addrCategory1_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addrCategory1_sp.setAdapter(addrCategory1_adapter);
        addrCategory1_sp.setSelection(0);
        addrCategory2_sp.setSelection(0);
//        category1_array=getResources().getStringArray(R.array.citiy);

        //스피터 카테고리 1 셀렉트 이벤트
        addrCategory1_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(addrFlag1==-1){
                    addrFlag1=1;
                    addrFlag2=-1;
                }
                else{
                    setAddrCategory(position);
                    getMarker=dbManager.listViewDB(position);
                    getDb=addrIntToString(getMarker);
                    adapter.setData(getDb);
                    adapter.notifyDataSetChanged();
                    addrFlag2=-1;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //스피너 카테고리 2 셀렉트 이벤트
        addrCategory2_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(addrFlag2==-1){
                    addrFlag2=1;
                }
                else {
                    getMarker = dbManager.listViewDB(addrCategory1_sp.getSelectedItemPosition(), position);
                    getDb = addrIntToString(getMarker);
                    adapter.setData(getDb);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //리스트뷰 셀렉트 이벤트
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent moveToInput=new Intent(getApplicationContext(),Direct_input.class);
                moveToInput.putExtra("user_id",user_id);
                moveToInput.putExtra("fromMap",0);
                moveToInput.putExtra("DBId",String.valueOf(adapter.getDBId(position)));
                startActivity(moveToInput);
            }
        });
        //리스트뷰 롱 클릭 이벤트
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos=position;
                AlertDialog.Builder builder=new AlertDialog.Builder(new ContextThemeWrapper(Category_view.this, R.style.myDialog));
                builder.setTitle("메모 삭제");
                builder.setMessage("정말로 메모를 삭제하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String DbId=String.valueOf(adapter.getDBId(pos));
                        String query="delete from MEMO where memo_id="+DbId+";";
                        dbManager.delete(query);
                        adapter.deleteDb(pos);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
                return false;
            }
        });

    }


    //DB로부터 받아온 markerItem형탤ㄹ category_view_data형태로 바꿔줌
    public ArrayList<Category_view_Data> addrIntToString (ArrayList<MarkerItem> markerItems){
        ArrayList<Category_view_Data> categoryViewDatas=new ArrayList<>();

        for(int i=0;i<markerItems.size();i++){
            MarkerItem tempMarkerItem=markerItems.get(i);
            Category_view_Data tempCategory_db=new Category_view_Data();
            tempCategory_db.setId(tempMarkerItem.getMemo_id());
            tempCategory_db.setTitle(tempMarkerItem.getTitle());
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
    //1차 지역 카테고리를 입력하면 2차 지역 카테고리도 설정되는 함수
    public void setAddrCategory(int addrCategory1){
//        addrCategory1_sp.setSelection(addrCategory1);
        GetAreaData getAreaData=new GetAreaData(getApplicationContext(),addrCategory1);
        String[] addrCategory2Array=getAreaData.getCategory2Array();
        ArrayAdapter addrCategory2_adapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,addrCategory2Array);
        addrCategory2_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addrCategory2_sp.setAdapter(addrCategory2_adapter);
//        addrCategory2_sp.setSelection(addrCategory2);
    }

}
