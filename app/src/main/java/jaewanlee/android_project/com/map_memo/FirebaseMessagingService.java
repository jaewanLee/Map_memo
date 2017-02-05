package jaewanlee.android_project.com.map_memo;

/**
 * Created by jaewan on 2017-01-02.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //추가한것
        sendNotification(remoteMessage.getData().get("message"));
    }

    private void sendNotification(String messageBody) {

        try {
            JSONObject jsonObject=new JSONObject(messageBody);
            String type=jsonObject.getString("type");
            if(type.equals("map_memo")){
                String query=jsonObject.getString("query");
                DBManager dbManager = new DBManager(getApplicationContext(), "MEMO.db", null, 1);
                dbManager.update(query);
                showPush("단체 메모가 업데이트 되었습니다");
            }
            else if(type.equals("chat")){
                SharedPreferences autoLogin = getSharedPreferences("autologin", 0);
                String user_id=autoLogin.getString("id", "");
                if(!user_id.equals("")){
                    //전체 채팅방의 텍스트 정보를 가지고 있는 DB를 가져옴,룸넘버가 key값임
                    Chat_DB_Manager chatDbManager=new Chat_DB_Manager(getApplicationContext(),"CHATDB.db",null,1);
                    Chat_DB tempChatDb=chatDbManager.getChatDB(user_id,jsonObject.getString("roomNumber"));
                    if(tempChatDb==null){
                        //아예 없는 새로운 방일 경우 새로운 DB를 작성해줌
                        String query="insert into CHATDB values(";
                        query+="null, ";
                        query+="'"+jsonObject.getString("roomNumber")+"', ";
                        query+="'"+jsonObject.getString("friend")+"', ";
                        query+="'"+user_id+"', ";
                        String text=jsonObject.getString("friend")+" 님이 입장하셨습니다.";
                        text+="\n"+jsonObject.getString("text");
                            query+="'"+text+"', ";
                        query+="'"+jsonObject.getString("title")+"', ";//여긴 타이틀
                        query+="'"+jsonObject.getString("img_addr")+"',";//여기 이미지 정리 해주기, 받아온다면...ㅠㅠ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        query+="'"+jsonObject.getString("map_id")+"'," ;//여긴 맵 아이디
                        query+="'"+System.currentTimeMillis()+"');";
                        chatDbManager.insert(query);
                        showPush(jsonObject.getString("text"));

                    }
                    else{
                        //기존에 존재하는 방일 경우 텍스트의 내용을 업데이트 해줌
                        String text=tempChatDb.getText();
                        text+="\n"+jsonObject.getString("text");
                        String query="update CHATDB set text=";
                        query+="'"+text+"' where roomNumber=";
                        query+="'"+tempChatDb.getRoomNumber()+"';";
                        chatDbManager.update(query);
                        showPush(jsonObject.getString("text"));
                    }
                }

            }
            else if(type.equals("img_addr")){
                //데이터를 처음 가져옴
                String img_addr=jsonObject.getString("img_addr");
                downLoadFile(img_addr);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    private void showPush(String message){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("#여기요")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
    public void downLoadFile(final String fileName){
        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference storageReference=storage.getReferenceFromUrl("gs://sunny-truth-151708.appspot.com/map_memo_img/").child(fileName);
        final long ONE_MEGABYTE=1024*1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                FileOutputStream fos = null;
                try {
                    fos = openFileOutput(fileName, 0);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("result",e.toString());
                Toast.makeText(FirebaseMessagingService.this, "이미지 다운로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
