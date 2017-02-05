package jaewanlee.android_project.com.map_memo;

/**
 * Created by jaewan on 2017-01-13.
 */

public class Friend {
    int user_no;
    String user_id;
    String phone;
    String photo;
    String name;
    String type;
    String date;
    String update;
    String friend;

    public Friend(){

    }
    public Friend(int user_no,String user_id,String phone,String photo,String name,String type,String date,String update,String friend){
        this.user_no=user_no;
        this.user_id=user_id;
        this.phone=phone;
        this.photo=photo;
        this.name=name;
        this.type=type;
        this.date=date;
        this.update=update;
        this.friend=friend;
    }

    public int getUser_no() {
        return user_no;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUpdate() {
        return update;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getFriend() {
        return friend;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setUser_no(int user_no) {
        this.user_no = user_no;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
