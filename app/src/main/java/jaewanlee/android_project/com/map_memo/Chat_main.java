package jaewanlee.android_project.com.map_memo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by jaewan on 2017-01-04.
 */

public class Chat_main extends AppCompatActivity {

    TextView title_tv;
    ImageButton chatAdd_ib;
    ListView chatList_lv;
    ImageButton map_bt;//지도화면 이동 버튼
    ImageButton list_bt;//리스트 화면 이동버튼
    ImageButton chat_bt;//메세지 화면 이동 버튼
    ImageButton setting_bt;//세팅 화면 이동버튼

    String user_id;//사용자 아이디
    Chat_DB_Manager chatDbManager;

    ArrayList<chat_listView_data> chat_listView_dataArrayList;//채팅방 데이터
    chat_listView_adapater adapater;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //뷰 열결
        title_tv=(TextView)findViewById(R.id.chat_title_textView);
        chatAdd_ib=(ImageButton)findViewById(R.id.chat_add_Imagebutton);
        chatList_lv=(ListView)findViewById(R.id.chat_listview_listView);
        map_bt=(ImageButton)findViewById(R.id.chat_map_ImageButton);
        list_bt=(ImageButton)findViewById(R.id.chat_list_Imagebutton);
        chat_bt=(ImageButton)findViewById(R.id.chat_chat_Imagebutton);
        setting_bt=(ImageButton)findViewById(R.id.chat_setting_Imagebutton);
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

        //초기 인텐트 설정
        Intent getIntent=getIntent();
        user_id=getIntent.getStringExtra("user_id");

        chatAdd_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveTochatAdd=new Intent(getApplicationContext(),Chat_add.class);
                moveTochatAdd.putExtra("user_id",user_id);
                startActivity(moveTochatAdd);

            }
        });

        //리스트 뷰 설정
        chat_listView_dataArrayList=new ArrayList<>();
        //방 데이터 넣어주기
        //이미지 및 map id string으로 수정되면 최종 수정해서 이미지 뜨게 하기
        chatDbManager=new Chat_DB_Manager(getApplicationContext(),"CHATDB.db",null,1);
        //저장된 방 정보 가져오기
        ArrayList<Chat_DB> chat_dbArrayList=chatDbManager.getWholeDB(user_id);
        //리스트뷰에서 사용할 수 있는 데이터 형태로 바꿔주기
        for(int i=0;i<chat_dbArrayList.size();i++){
            chat_listView_dataArrayList.add(new chat_listView_data(chat_dbArrayList.get(i).getRoomNumber(),chat_dbArrayList.get(i).getImg_addr(),chat_dbArrayList.get(i).getTitle(),chat_dbArrayList.get(i).getFriend()));
        }
        //어뎁터 데이터 설정 및 연결해주기
        adapater=new chat_listView_adapater(Chat_main.this);
        adapater.setData(chat_listView_dataArrayList);
        chatList_lv.setAdapter(adapater);

        //리스트 클릭 리스터
        chatList_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chat_listView_data chat_listView_data= (chat_listView_data) adapater.getItem(position);
                Intent moveToChatRoom=new Intent(getApplicationContext(),Chat_room.class);
                moveToChatRoom.putExtra("user_id",user_id);
                moveToChatRoom.putExtra("roomNumber",chat_listView_data.getRoomNumber());
                startActivity(moveToChatRoom);

            }
        });
        chatList_lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(new ContextThemeWrapper(Chat_main.this,R.style.myDialog));
                builder.setTitle("대화방 나가기").setMessage("대화방을 나가시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //여기서 리시트에서 삭제 및 디비에서 삭제가 진행됨
                                String deleted_room_number=chat_listView_dataArrayList.get(position).getRoomNumber();
                                String query="delete from CHATDB where roomNumber='"+deleted_room_number+"';";
                                chatDbManager.delete(query);
                                chat_listView_dataArrayList.remove(position);
//                                adapater.deleteData(position);
                                adapater.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
