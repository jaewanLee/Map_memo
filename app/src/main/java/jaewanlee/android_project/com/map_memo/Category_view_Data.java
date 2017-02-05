package jaewanlee.android_project.com.map_memo;

/**
 * Created by jaewan on 2016-12-22.
 */

public class Category_view_Data {
    int id;
    String title;
    String address;
    String chat;
    public Category_view_Data(){
        this.id=-1;
        this.title="";
        this.address="";
        this.chat="";
    }
    public Category_view_Data(int id,String title,String address){
        this.id=id;
        this.title=title;
        this.address=address;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getChat() {
        return chat;
    }

    public String getAddress() {
        return address;
    }
}
