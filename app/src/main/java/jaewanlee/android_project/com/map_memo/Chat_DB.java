package jaewanlee.android_project.com.map_memo;

/**
 * Created by jaewan on 2017-01-04.
 */

public class Chat_DB {
    String user_id;
    String friend;
    String roomNumber;
    String map_id;
    String text;
    String title;
    String img_addr;

    public Chat_DB(String roomNumber,String friend,String user_id,String text,String title,String img_addr,String map_id){
        this.user_id=user_id;
        this.friend=friend;
        this.roomNumber=roomNumber;
        this.map_id=map_id;
        this.text=text;
        this.title=title;
        this.img_addr=img_addr;
    }
    public Chat_DB(){
        this.user_id="";
        this.friend="";
        this.roomNumber="";
        this.map_id="";
        this.text="";
        this.img_addr="";
    }

    public String getUser_id() {
        return user_id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getFriend() {
        return friend;
    }

    public String getMap_id() {
        return map_id;
    }

    public String getText() {
        return text;
    }

    public String getImg_addr() {
        return img_addr;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public void setMap_id(String map_id) {
        this.map_id = map_id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImg_addr(String img_addr) {
        this.img_addr = img_addr;
    }
}
