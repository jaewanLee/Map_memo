package jaewanlee.android_project.com.map_memo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by jaewan on 2017-01-04.
 */

public class Chat_room extends AppCompatActivity {
   TextView title_tv;
    ListView chat_lv;
    EditText input_et;
    Button send_bt;

    Chat_DB_Manager chatDbManager;
    Chat_DB chat_db;

    Socket socket;//이방에서 사용되는 소켓


    ArrayList<Chat_room_data> chat_room_datas;
    Chat_room_data chat_room_data;
    Chat_room_adapter chat_room_adapter;

    String text;
    Friend_DB_Manager friend_db_manager;
    User_DB_Manager user_db_manager;
    String myImg;

    int semapore=0;
    final static int BTN_MESSAGE_CODE=1;
    final static int RECEIVE_MESSAGE_CODE=2;
    final static int SEND_MESSAGE_CODE=3;
    final static int UI_MESSAGE_CODE=4;
    final static int SOCKE_ERR_CODE=5;
    final static int MESSAGE_CLEAR_CODE=6;

    MyHandler myHandler=new MyHandler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        //뷰연결하기
        title_tv=(TextView)findViewById(R.id.chatRoom_title_textView);
        chat_lv=(ListView)findViewById(R.id.chatRoom_chat_ListView);
        input_et=(EditText)findViewById(R.id.chatRoom_input_EditText);
        send_bt=(Button)findViewById(R.id.chatRoom_send_Button);

        //글꼴 설정하기
        title_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        input_et.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        send_bt.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));;


        chatDbManager=new Chat_DB_Manager(getApplicationContext(),"CHATDB.db",null,1);
        friend_db_manager=new Friend_DB_Manager(getApplicationContext(),"FRIEND.db",null,1);
        user_db_manager=new User_DB_Manager(getApplicationContext(),"USER.db",null,1);


        //초기 인텐트 받기
        chat_db=new Chat_DB();
        Intent getintent=getIntent();
        chat_db.setUser_id(getintent.getStringExtra("user_id"));
        chat_db.setRoomNumber(getintent.getStringExtra("roomNumber"));

        myImg=user_db_manager.getUserPhoto(chat_db.getUser_id());


        chat_lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        text="";

        chat_room_datas=new ArrayList<>();
        chat_room_adapter=new Chat_room_adapter(chat_db.getUser_id());
        chat_lv.setAdapter(chat_room_adapter);

        //새로운 방인지 기존의 방인지 확인
        if(chat_db.getRoomNumber().equals("-1")){
            //새로운 방일 경우
            //인텐트에서 친구목록과 지도 아이디를 받아옴
            chat_db.setFriend(getintent.getStringExtra("friend"));
            chat_db.setMap_id(getintent.getStringExtra("map_id"));
            chat_db.setTitle(getintent.getStringExtra("title"));
            chat_db.setImg_addr(getintent.getStringExtra("img_addr"));
            //쓰래드 시작
            ConnectThread connectThread=new ConnectThread();
            connectThread.start();
        }
        else{
            //roomNumber 와 회원 아이디로 이전 데이터 불러오기
            chat_db=chatDbManager.getChatDB(chat_db.getUser_id(),chat_db.getRoomNumber());
            //이전 대화 불러오기
            text=chat_db.getText();
            String textSplit[]=text.split("\n");
            for(int i=0;i<textSplit.length;i++){
                if(textSplit[i].length()>3){
                    insertChatText(textSplit[i]);
                }
            }
            chat_room_adapter.notifyDataSetChanged();
            chat_lv.setSelection(chat_room_adapter.getCount()-1);
            //            Message message=Message.obtain();
//            message.what=RECEIVE_MESSAGE_CODE;
//            myHandler.sendMessage(message);


            //맵과 관련된 자료들입력해야함
            //소켓통신 시작
            ConnectThread connectThread=new ConnectThread();
            connectThread.start();

        }
        //보내기 버튼
        send_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendThread sendThread=new SendThread(socket);
                sendThread.start();
            }
        });


    }
    class ConnectThread extends Thread{
        String hostname;
        DataOutputStream dataOutputStream;
        DataInputStream dataInputStream;
        public ConnectThread(){
            hostname="115.71.236.6";
        }
        public void run(){
            try {
                socket=new Socket(hostname,9999);
                dataOutputStream=new DataOutputStream(socket.getOutputStream());
                dataInputStream=new DataInputStream(socket.getInputStream());
//                dataOutputStream.writeUTF("connecting");
                //첫 접속시 제이슨 보낼때, map id랑 그 정보들도 전부다 같이 보내줘야함....이건 나중에하자 ㅠㅠ
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("user_id",chat_db.getUser_id());
                jsonObject.put("roomNumber",chat_db.getRoomNumber());
                jsonObject.put("friend",chat_db.getFriend());
                jsonObject.put("title",chat_db.getTitle());
                jsonObject.put("map_id",chat_db.getMap_id());
                jsonObject.put("img_addr",chat_db.getImg_addr());

                Log.d("msg","처음 서버에 접속할떄 보내는 메세지 "+jsonObject.toString());
                dataOutputStream.writeUTF(jsonObject.toString());
                dataOutputStream.flush();
                //첫번째 리턴 값
                String result=dataInputStream.readUTF();
                Log.d("msg","처음 서버에 접속하고 처음 받는 메세지 "+result);
                jsonObject=new JSONObject(result);
                if(!chat_db.getRoomNumber().equals(jsonObject.getString("roomNumber"))){
                    //새로 만들어진 방이라 roomNumber를 새롭게 부여받은 경우
                    chat_db.setRoomNumber(jsonObject.getString("roomNumber"));
                    //첫 텍스트 내용을 입력해줌
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            text+=chat_db.getFriend()+"님이 입장하셨습니다.";
//                            chat_tv.setText(chat_db.getFriend()+"님이 입장하셨습니다.");
//                        }
//                    });
                    text+=chat_db.getFriend()+"님이 입장하셨습니다.";
                    insertChatText(text);
                    Message message=Message.obtain();
                    message.what=RECEIVE_MESSAGE_CODE;
                    myHandler.sendMessage(message);

//                    chat_room_adapter.notifyDataSetChanged();
                    chat_db.setText(text);
                    //새로 만들어진 방에 대한 DB 생성
                    String query="insert into CHATDB values(";
                    query+="null, ";
                    query+="'"+chat_db.getRoomNumber()+"', ";
                    query+="'"+chat_db.getFriend()+"', ";
                    query+="'"+chat_db.getUser_id()+"', ";
                    query+="'"+chat_db.getText()+"', ";
                    query+="'"+chat_db.getTitle()+"', ";
                    query+="'"+chat_db.getImg_addr()+"', ";
                    query+="'"+chat_db.getMap_id()+"', ";
                    query+="'"+System.currentTimeMillis()+"');";
                    chatDbManager.insert(query);
                }
                ReceiveThread receiveThread=new ReceiveThread(socket);
                receiveThread.start();
            } catch (IOException e) {
                Log.d("error on chat","1");
                e.printStackTrace();
            } catch (JSONException e) {
                Log.d("error on chat","2");
                e.printStackTrace();
            }
        }
    }
    class ReceiveThread extends Thread {
        private Socket socket;
        private DataInputStream dataInputStream;

        ReceiveThread(Socket socket) {
            this.socket = socket;
            try {
                dataInputStream = new DataInputStream(this.socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            //메세지를 받아오기 위한 대기
            try {
                while (dataInputStream != null) {
                    Log.d("receive", "on recieve");
                    //메세지를 받음
                    String msg = dataInputStream.readUTF();
                    Log.d("msg", "접속한뒤에 메세지를 받아오는 부분 " + msg);
                    //받은 메세지를 json형태로 바꿔줌
                    JSONObject jsonObject = new JSONObject(msg);
                    //받은 메세지의 방번호 확인
                    if (jsonObject.getString("roomNumber").equals(chat_db.getRoomNumber())) {
                        //방번호가 같을 경우 해당 메세지 내용을 써줌
                        String getText = jsonObject.getString("text");
                        Log.d("msg", "접속한 뒤에 방번호가 같을경우 써주는 부분 " + getText);
                        text+="\n"+getText;
                        insertChatText(getText);
                        Message message=Message.obtain();
                        message.what=RECEIVE_MESSAGE_CODE;
                        myHandler.sendMessage(message);
//                        chat_room_adapter.notifyDataSetChanged();

                    } else {
                        //방번호가 다를 경우
                        //전체 채팅방의 텍스트 정보를 가지고 있는 DB를 가져옴,룸넘버가 key값임
                        Chat_DB tempChatDb=chatDbManager.getChatDB(chat_db.getUser_id(),jsonObject.getString("roomNumber"));
                        if(tempChatDb==null){
                            //아예 없는 새로운 방일 경우 새로운 DB를 작성해줌
                            String query="insert into CHATDB values(";
                            query+="null, ";
                            query+="'"+jsonObject.getString("roomNumber")+"', ";
                            query+="'"+jsonObject.getString("friend")+"', ";
                            query+="'"+chat_db.getUser_id()+"', ";
                            String getText=jsonObject.getString("friend")+" 님이 입장하셨습니다.";
                            getText+="\n"+jsonObject.getString("text");
                            query+="'"+getText+"', ";
                            query+="'"+jsonObject.getString("title")+"', ";//여긴 타이틀
                            query+="'"+jsonObject.getString("img_addr")+"',";//여기 이미지 정리 해주기, 받아온다면...ㅠㅠ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            query+="'"+jsonObject.getString("map_id")+"'," ;//여긴 맵 아이디
                            query+="'"+System.currentTimeMillis()+"');";
                            chatDbManager.insert(query);
                        }
                        else{
                            //기존에 존재하는 방일 경우 텍스트의 내용을 업데이트 해줌
                            String getText=tempChatDb.getText();
                            getText+="\n"+jsonObject.getString("text");
                            String query="update CHATDB set text=";
                            query+="'"+getText+"' where roomNumber=";
                            query+="'"+tempChatDb.getRoomNumber()+"';";
                            chatDbManager.update(query);
                        }
                    }
                }
            } catch (IOException e) {
                Log.d("error on chat","3");
                e.printStackTrace();
            } catch (JSONException e) {
                Log.d("error on chat","4");
                e.printStackTrace();
            }

        }
    }
    class SendThread extends Thread{
        private Socket socket;
        private DataOutputStream dataOutputStream;
        SendThread(Socket socket){
            this.socket=socket;
            if(this.socket==null){
                Toast.makeText(Chat_room.this, "인터넷 연결을 확인해 주세요", Toast.LENGTH_SHORT).show();
            }
            else{
                try {
                    this.dataOutputStream=new DataOutputStream(this.socket.getOutputStream());
                } catch (IOException e) {
                    Log.d("error on chat","5");
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            super.run();

            //메세지를 보낼때 실행됨
            Message message=Message.obtain();
            message.what=SEND_MESSAGE_CODE;
            myHandler.sendMessage(message);

            if(this.socket==null){
                //소켓이 죽었을때,
                Message message1=Message.obtain();
                message1.what=SOCKE_ERR_CODE;
                myHandler.sendMessage(message1);
            }
            else{
                try {

                    //여기도 보낼때 map_id랑 정보 보내주기
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("type","chat");
                    jsonObject.put("user_id",chat_db.getUser_id());
                    jsonObject.put("friend",chat_db.getFriend());
                    jsonObject.put("roomNumber",chat_db.getRoomNumber());
                    jsonObject.put("text",input_et.getText().toString());
                    jsonObject.put("title",chat_db.getTitle());
                    jsonObject.put("img_addr",chat_db.getImg_addr());
                    jsonObject.put("map_id",chat_db.getMap_id());
                    dataOutputStream.writeUTF(jsonObject.toString());
                    Log.d("msg","접속한 뒤에 내용을 보내는 부분 "+jsonObject.toString());
                    dataOutputStream.flush();
                    //메시지를 모두 보내고 나서 실행함, 메세지의 내용을 지우기
                    Message message2=Message.obtain();
                    message2.what=MESSAGE_CLEAR_CODE;
                    myHandler.sendMessage(message2);
                } catch (IOException e) {
                    Log.d("error on chat","6");
                    e.printStackTrace();
                } catch (JSONException e) {
                    Log.d("error on chat","7");
                    e.printStackTrace();
                }
            }
            //메세지를 모두 보내고 나서 버튼 다시 활성화 시키기
            Message message1=Message.obtain();
            message1.what=BTN_MESSAGE_CODE;
            myHandler.sendMessage(message1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //나가기 전에 최종 저장함
        String query="update CHATDB set text=";
        query+="'"+text+"' where roomNumber=";
        query+="'"+chat_db.getRoomNumber()+"';";
        chatDbManager.update(query);
        if(socket!=null){
            try {
                socket.close();
            } catch (IOException e) {
                Log.d("error on chat","8");
                e.printStackTrace();
            }
        }
    }
    public void insertChatText(String text){
        if(text.contains(":")){
            String splitText[]=text.split(":");
            String fromUser=splitText[0];
            String textMessage=splitText[1];
            String img="null";
            if(fromUser.equals(chat_db.getUser_id())){
               img=myImg;
            }
            else{
                img=friend_db_manager.getImg(fromUser);
            }
            Chat_room_data chat_room_data=new Chat_room_data(fromUser,textMessage,img);
            chat_room_adapter.addData(chat_room_data);
            //데이터를 넣을때 실행됨
//            Message message=Message.obtain();
//            message.what=RECEIVE_MESSAGE_CODE;
//            myHandler.sendMessage(message);
        }
        else{
            Chat_room_data chat_room_data=new Chat_room_data();
            chat_room_data.setChat_text(text);
            chat_room_adapter.addData(chat_room_data);
//            Message message=Message.obtain();
//            message.what=RECEIVE_MESSAGE_CODE;
//            myHandler.sendMessage(message);
        }


    }
    private class MyHandler extends Handler {
        Queue<Message> queue=new LinkedList<Message>();
    public MyHandler() {
        }
        @Override
        public void handleMessage(Message msg) {
            queue.add(msg);
                if (semapore>=0) {
                /**
                 * 넘겨받은 what값을 이용해 실행할 작업을 분류합니다
                 */
                if(msg.what==BTN_MESSAGE_CODE){
                    //버튼 쓰도록
                    send_bt.setEnabled(true);
                }else if(msg.what==SEND_MESSAGE_CODE){
                    //버튼 못쓰도록
                    send_bt.setEnabled(false);
                }else if(msg.what==RECEIVE_MESSAGE_CODE){
                    //어뎁터 변화 감지
                    semapore++;
                    chat_room_adapter.notifyDataSetChanged();
                    chat_lv.setSelection(chat_room_adapter.getCount()-1);
//                    chat_room_adapter.notifyDataSetChanged();
                    semapore--;
                }
                else if(msg.what==UI_MESSAGE_CODE){
                    //리시브 하고나서 제일 아래화면으로 이동하기
                    chat_lv.setSelection(chat_room_adapter.getCount()-1);
                }
                else if(msg.what==SOCKE_ERR_CODE){
                    //소켓 에러일 경우
                    semapore++;
                    Toast.makeText(Chat_room.this, "인터넷 연결을 확인해 주세요", Toast.LENGTH_SHORT).show();
                    semapore--;
                }
                else if(msg.what==MESSAGE_CLEAR_CODE){
                    //샌드 보내고나서 메세지창 정리하기
                    semapore++;
                    input_et.setText("");
                    semapore--;
                }
            }
        }
    }
}
