package jaewanlee.android_project.com.map_memo;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by jaewan on 2016-12-13.
 */

public class MarkerCluster implements ClusterItem {

    private LatLng location;//마커의 위도,경도
    private int markerId;//마커의 u_id , DB의 primary key
    private String title;//마커의 타이틀
    private Bitmap icon;//마커 뷰
    private String type;

    MarkerCluster (LatLng location,int markerId,Bitmap icon){
        this.location=location;
        this.markerId=markerId;
        this.icon=icon;
    }
    MarkerCluster (LatLng location,String title,int markerId,String type){
        this.location=location;
        this.title=title;
        this.markerId=markerId;
        this.type=type;
    }
    LatLng getLocation(){
        return this.location;
    }
    int getMarkerId(){
        return this.markerId;
    }
    String getTitle(){return this.title;}
    Bitmap getIcon(){return this.icon;}

    public String getType() {
        return type;
    }

    void setLoaction(LatLng location){
        this.location=location;
    }
    void setMarkerId(int markerId){
        this.markerId=markerId;
    }
    void setTitle(String title){this.title=title;}
    void setIcon(Bitmap icon){this.icon=icon;}

    public void setLocation(LatLng location) {
        this.location = location;
    }

    @Override
    public LatLng getPosition() {
        return location;
    }

}
class Rendering extends DefaultClusterRenderer<MarkerCluster> {

    public Rendering(Context context, GoogleMap map, ClusterManager<MarkerCluster> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MarkerCluster item, MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(item.getIcon()));
        markerOptions.title(String.valueOf(item.getMarkerId()));
        super.onBeforeClusterItemRendered(item, markerOptions);

    }
}
