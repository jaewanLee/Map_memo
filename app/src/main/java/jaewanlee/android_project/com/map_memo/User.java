package jaewanlee.android_project.com.map_memo;

/**
 * Created by jaewan on 2017-01-11.
 */

public class User {
    int user_no;
    String user_id;
    String phone;
    String photo;
    String name;
    String type;
    String date;
    String update;


    public  User(){

    }
    public User(int user_no,String user_id,String phone,String photo,String name,String type,String date,String update){
        this.user_no=user_no;
        this.user_id=user_id;
        this.phone=phone;
        this.photo=photo;
        this.name=name;
        this.type=type;
        this.date=date;
        this.update=update;
    }

    public String getDate() {
        return date;
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

    public String getType() {
        return type;
    }

    public String getUpdate() {
        return update;
    }

    public String getUser_id() {
        return user_id;
    }

    public int getUser_no() {
        return user_no;
    }

    public void setDate(String date) {
        this.date = date;
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

    public void setType(String type) {
        this.type = type;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUser_no(int user_no) {
        this.user_no = user_no;
    }
}
