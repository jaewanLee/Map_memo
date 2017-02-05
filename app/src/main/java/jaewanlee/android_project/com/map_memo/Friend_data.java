package jaewanlee.android_project.com.map_memo;

/**
 * Created by jaewan on 2017-01-13.
 */

public class Friend_data {
    String img;
    String name;
    String user_id;
    Friend_data(){

    }
    Friend_data(String img,String name,String user_id){
        this.img=img;
        this.name=name;
        this.user_id=user_id;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
