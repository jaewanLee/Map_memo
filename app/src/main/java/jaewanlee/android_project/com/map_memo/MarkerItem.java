package jaewanlee.android_project.com.map_memo;

/**
 * Created by jaewan on 2016-12-11.
 */

public class MarkerItem {

    int memo_id;
    String lat;
    String lng;
    String title;
    int addrCategory1;
    int addrCategory2;
    String addrDetail;
    String img;
    String chat;

    public MarkerItem(){

    }
    public MarkerItem(int memo_id, String title, int addrCategory1, int addrCategory2, String addrDetail) {
        this.memo_id = memo_id;
        this.title = title;
        this.addrCategory1 = addrCategory1;
        this.addrCategory2 = addrCategory2;
        this.addrDetail = addrDetail;
    }

    public MarkerItem(int memo_id, String title, int addrCategory1, int addrCategory2, String addrDetail, String chat) {
        this.memo_id = memo_id;
        this.title = title;
        this.addrCategory1 = addrCategory1;
        this.addrCategory2 = addrCategory2;
        this.addrDetail = addrDetail;
        this.chat=chat;
    }
    public MarkerItem(int memo_id, String title, int addrCategory1, int addrCategory2, String addrDetail, String img,String chat) {
        this.memo_id = memo_id;
        this.title = title;
        this.addrCategory1 = addrCategory1;
        this.addrCategory2 = addrCategory2;
        this.addrDetail = addrDetail;
        this.img = img;
        this.chat=chat;
    }

    public MarkerItem(int memo_id, String title, String lat, String lng) {
        this.memo_id = memo_id;
        this.title = title;
        this.lat = lat;
        this.lng = lng;
    }

    public int getMemo_id() {
        return memo_id;
    }

    public int getAddrCategory1() {
        return addrCategory1;
    }

    public int getAddrCategory2() {
        return addrCategory2;
    }

    public String getAddrDetail() {
        return addrDetail;
    }

    public String getImg() {
        return img;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getTitle() {
        return title;
    }

    public String getChat() {
        return chat;
    }

    public void setAddrCategory2(int addrCategory2) {
        this.addrCategory2 = addrCategory2;
    }

    public void setAddrCategory1(int addrCategory1) {
        this.addrCategory1 = addrCategory1;
    }

    public void setAddrDetail(String addrDetail) {
        this.addrDetail = addrDetail;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setMemo_id(int memo_id) {
        this.memo_id = memo_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }
}
