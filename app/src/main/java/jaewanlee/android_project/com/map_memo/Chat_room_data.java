package jaewanlee.android_project.com.map_memo;

/**
 * Created by jaewan on 2017-01-16.
 */

public class Chat_room_data {
    String img;
    String chat_text;
    String chat_date;
    String chat_user;

    Chat_room_data(){
        this.img="";
        this.chat_text="";
        this.chat_date="";
        this.chat_user="";

    }
    Chat_room_data(String chat_user,String chat_text,String img){
        this.img=img;
        this.chat_text=chat_text;
        this.chat_user=chat_user;
    }

    public String getImg() {
        return img;
    }

    public String getChat_user() {
        return chat_user;
    }

    public String getChat_date() {
        return chat_date;
    }

    public String getChat_text() {
        return chat_text;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setChat_date(String chat_date) {
        this.chat_date = chat_date;
    }

    public void setChat_text(String chat_text) {
        this.chat_text = chat_text;
    }

    public void setChat_user(String chat_user) {
        this.chat_user = chat_user;
    }
}
