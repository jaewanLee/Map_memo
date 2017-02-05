package jaewanlee.android_project.com.map_memo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jaewan on 2017-01-04.
 */

public class chat_listView_data {
    String roomNumber;
    String friend;
    String imageViewAddr;
    String memoTitle;

//    String chat_text;
//    String newChat;

    public chat_listView_data(){
        this.roomNumber="-1";
        this.imageViewAddr="";
        this.memoTitle="";
        this.friend="";
//        this.chat_text="";
//        this.newChat="";
    }
    public chat_listView_data(String roomNumber,String imageViewAddr,String memoTitle,String friend){
        this.roomNumber=roomNumber;
        this.imageViewAddr=imageViewAddr;
        this.memoTitle=memoTitle;
        this.friend=friend;
//        this.chat_text=chat_text;
//        this.newChat=newChat;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
//    public void setChat_text(String chat_text) {
//        this.chat_text = chat_text;
//    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public void setImageViewAddr(String imageViewAddr) {
        this.imageViewAddr = imageViewAddr;
    }

    public void setMemoTitle(String memoTitle) {
        this.memoTitle = memoTitle;
    }

//    public void setNewChat(String newChat) {
//        this.newChat = newChat;
//    }

    public String getRoomNumber() {
        return roomNumber;
    }

//    public String getChat_text() {
//        return chat_text;
//    }

    public String getImageViewAddr() {
        return imageViewAddr;
    }

    public String getFriend() {
        return friend;
    }

    public String getMemoTitle() {
        return memoTitle;
    }

//    public String getNewChat() {
//        return newChat;
//    }
}
