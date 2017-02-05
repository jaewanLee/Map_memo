//package jaewanlee.android_project.com.map_memo;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.location.LocationListener;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Filter;
//import android.widget.Filterable;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.maps.CameraUpdate;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapFragment;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.maps.android.clustering.ClusterItem;
//import com.google.maps.android.clustering.ClusterManager;
//import com.google.maps.android.clustering.view.DefaultClusterRenderer;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
///**
// * Created by jaewan on 2016-12-06.
// */
//
//public class Map extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener,GoogleMap.OnMarkerClickListener{
//
//    //layout의 뷰들
//    TextView title_tv;//타이틀
//    AutoCompleteTextView search_auto;//서치창
//    ImageButton search_bt;//서치 버튼
//
//    MapFragment mapFragment;
//    GoogleMap googleMap;
//
//    GoogleApiClient mGoogleApiClient;
//    static final LatLng SEOUL = new LatLng(37.56, 126.97);
//
//    int beforeNum=0;
//
//    //위도,경도
//    List<Address> latLng;
//
//    //마커 테스트 뷰
//    View maker_root_view;
//    TextView tv_makrer;
//    Clustering selectedMarker;
//
//    LinearLayout markerInform;
//    ArrayList <MarkerItem> sampleList;
//    TextView markerTitle;
//    TextView markerAddr;
//
//    //클러스터링
//    CameraPosition previousCameraPosition;
//    ArrayList<Clustering> clusteringArrayList;
//    HashMap<Integer,Clustering> clusteringHashMap;
//    ClusterManager<Clustering> clusterManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_map);
//
//        //뷰랑 연결시키기
//        mapFragment=(MapFragment)getFragmentManager().findFragmentById(R.id.map_map_fragment);
//
//        title_tv=(TextView)findViewById(R.id.map_title_textView);
//        search_auto=(AutoCompleteTextView) findViewById(R.id.map_search_autoComplete);
//        search_bt=(ImageButton)findViewById(R.id.map_search_button);
//
//        //테스트
//        markerInform=(LinearLayout)findViewById(R.id.map_Linear_LinearLayout);
//        markerTitle=(TextView)findViewById(R.id.map_markertitle_TextView);
//        markerAddr=(TextView)findViewById(R.id.map_markeraddr_textView);
//
//        mapFragment.getMapAsync(this);
//
//        //글꼴설정
//        title_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
//        search_auto.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
//
//        //자동완성 설정
//        search_auto.setAdapter(new PlacesAutoCompleteAdapter(this,R.layout.acivity_test2));
//
//        search_auto.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String description=(String)parent.getItemAtPosition(position);
//                Toast.makeText(Map.this, description, Toast.LENGTH_SHORT).show();
//                search_auto.destroyDrawingCache();
//            }
//        });
//
//        search_bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                APIExamMapGeocode naver=new APIExamMapGeocode();
////                naver.execute(search_auto.getText().toString());
//                Log.d("onclick","in");
//                String str=search_auto.getText().toString();
//                Geocoder geocoder=new Geocoder(getApplicationContext());
//                try {
//                    latLng=geocoder.getFromLocationName(str,1);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if(latLng.size()>0){
//                    Address address=latLng.get(0);
//
//                    LatLng latLng1=new LatLng(address.getLatitude(),address.getLongitude());
//                    Log.d("latLng result",latLng1.toString());
//
//                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
//                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//                    googleMap.addMarker(new MarkerOptions().position(latLng1).draggable(true).title("새로운 지역")).setSnippet("새로운 지역을 추가 하시겠습니까?");
//
//                }
//                else{
//                    Toast.makeText(Map.this, "잘못된 주소입니다. 다시 확인해 주세요.", Toast.LENGTH_SHORT).show();
//                }
//
//
//            }
//        });
//
//
//    }
//
//    //주소로 위도/경도 획득하기
//    @Override
//    public void onMapReady(GoogleMap map) {
//        googleMap=map;
//        LatLng sydney=new LatLng(37.534189,126.900826);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
//
//        maker_root_view= LayoutInflater.from(this).inflate(R.layout.coutom_marker,null);
//        tv_makrer=(TextView)maker_root_view.findViewById(R.id.marker_text_textView);
//        tv_makrer.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
//
//        googleMap.setOnMarkerClickListener(this);
//
////        googleMap.addMarker(new MarkerOptions().position(new LatLng(37.534189,126.900826)).title("물짜장"));
////        googleMap.addMarker(new MarkerOptions().position(new LatLng(37.534389, 126.901207)).title("맥도날드 세일중"));
////        googleMap.addMarker(new MarkerOptions().position(new LatLng(37.533425, 126.903353)).title("에쁜 만화책 방"));
//        getSampleMarkerItme();
//
//        clusterManager=new ClusterManager<Clustering>(this,googleMap);
//        clusterManager.setRenderer(new ownRendering(getApplicationContext(),googleMap,clusterManager));
//        previousCameraPosition=null;
//        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener(){
//
//            @Override
//            public void onCameraIdle() {
//                CameraPosition position=googleMap.getCameraPosition();
//                if(previousCameraPosition==null||previousCameraPosition.zoom!=position.zoom){
//                    previousCameraPosition=googleMap.getCameraPosition();
//                    clusterManager.cluster();
//                }
//            }
//        });
//        clusteringArrayList=new ArrayList<Clustering>();
//        clusteringArrayList.add(new Clustering(new LatLng(37.534389, 126.900826),"삼대천왕 물짜장",1));
//        clusteringArrayList.add(new Clustering(new LatLng(37.534489, 126.900826),"삼대천왕 물짜장",2));
//        clusteringArrayList.add(new Clustering(new LatLng(37.534589, 126.900826),"삼대천왕 물짜장",3));
//        clusteringArrayList.add(new Clustering(new LatLng(37.534689, 126.900826),"삼대천왕 물짜장",4));
//        clusteringArrayList.add(new Clustering(new LatLng(37.534789, 126.900826),"삼대천왕 물짜장",5));
//        clusteringArrayList.add(new Clustering(new LatLng(37.534889, 126.900826),"삼대천왕 물짜장",6));
//        clusteringArrayList.add(new Clustering(new LatLng(37.534989, 126.900826),"삼대천왕 물짜장",7));
//        clusteringArrayList.add(new Clustering(new LatLng(37.535089, 126.900826),"삼대천왕 물짜장",8));
//        clusteringArrayList.add(new Clustering(new LatLng(37.535189, 126.900826),"삼대천왕 물짜장",9));
//        clusteringArrayList.add(new Clustering(new LatLng(37.535289, 126.900826),"삼대천왕 물짜장",10));
//        clusteringArrayList.add(new Clustering(new LatLng(37.535389, 126.900826),"삼대천왕 물짜장",11));
//
//        clusteringHashMap=new HashMap<Integer, Clustering>();
//        clusteringHashMap.put(1,new Clustering(new LatLng(37.534389, 126.900826),"삼대천왕 물짜장",1));
//        clusteringHashMap.put(2,new Clustering(new LatLng(37.534489, 126.900826),"삼대천왕 물짜장",2));
//        clusteringHashMap.put(3,new Clustering(new LatLng(37.534589, 126.900826),"삼대천왕 물짜장",3));
//        clusteringHashMap.put(4,new Clustering(new LatLng(37.534689, 126.900826),"삼대천왕 물짜장",4));
//        clusteringHashMap.put(5,new Clustering(new LatLng(37.534789, 126.900826),"삼대천왕 물짜장",5));
//        clusteringHashMap.put(6,new Clustering(new LatLng(37.534889, 126.900826),"삼대천왕 물짜장",6));
//        clusteringHashMap.put(7,new Clustering(new LatLng(37.534989, 126.900826),"삼대천왕 물짜장",7));
//        clusteringHashMap.put(8,new Clustering(new LatLng(37.535089, 126.900826),"삼대천왕 물짜장",8));
//        clusteringHashMap.put(9,new Clustering(new LatLng(37.535189, 126.900826),"삼대천왕 물짜장",9));
//        clusteringHashMap.put(10,new Clustering(new LatLng(37.535289, 126.900826),"삼대천왕 물짜장",10));
//        clusteringHashMap.put(11,new Clustering(new LatLng(37.535389, 126.900826),"삼대천왕 물짜장",11));
//
//
////        clusterManager.addItem(new Clustering(new LatLng(37.534389, 126.900826),"삼대천왕 물짜장",1));
////        clusterManager.addItem(new Clustering(new LatLng(37.534489, 126.900926),"삼대천왕 물짜장2",2));
////        clusterManager.addItem(new Clustering(new LatLng(37.534589, 126.901026),"삼대천왕 물짜장3",3));
////        clusterManager.addItem(new Clustering(new LatLng(37.534689, 126.901126),"삼대천왕 물짜장4",4));
////        clusterManager.addItem(new Clustering(new LatLng(37.534789, 126.901226),"삼대천왕 물짜장5",5));
////        clusterManager.addItem(new Clustering(new LatLng(37.534889, 126.901326),"삼대천왕 물짜장6",6));
////        clusterManager.addItem(new Clustering(new LatLng(37.534989, 126.901426),"삼대천왕 물짜장7",7));
////        clusterManager.addItem(new Clustering(new LatLng(37.535089, 126.901526),"삼대천왕 물짜장8",8));
////        clusterManager.addItem(new Clustering(new LatLng(37.535189, 126.901626),"삼대천왕 물짜장9",9));
////        clusterManager.addItem(new Clustering(new LatLng(37.535289, 126.901726),"삼대천왕 물짜장10",10));
////        for(int i=0;i<clusteringArrayList.size();i++){
////            addMarker(clusteringArrayList.get(i),false);
////        }
//        for(int i=1;i<clusteringHashMap.size();i++){
//            Clustering tempClustering=clusteringHashMap.get(i);
//            addMarker(clusteringHashMap.get(i),false);
//        }
//
////여기다가 클러스터링 메니저 등록하고 하기. 이거 예전꺼로나와서 아직 못함
////        googleMap.setCamera
//    }
//
//    //마커 이벤트 테스트, 마커 예시
//    private void getSampleMarkerItme(){
//        sampleList=new ArrayList<MarkerItem>();
////        sampleList.add(new MarkerItem(37.534989, 126.900826, "물짜장","서울시 영등포구 당산동 6가"));
////        sampleList.add(new MarkerItem(37.534389, 126.901207,"맥도날드 세일중","서울특별시 영등포구 당산동6가 331-1"));
////        sampleList.add(new MarkerItem(37.533425, 126.903353, "에쁜 만화책 방 삼대천왕 진짜 맛집입니다 정말 맛나요","서울특별시 영등포구 양평로 26"));
//
//        for (int i=0;i<sampleList.size();i++) {
////            MarkerOptions tempMarkerOption=new MarkerOptions();
////            tempMarkerOption.position(new LatLng(sampleList.get(i).getLat(),sampleList.get(i).getLng()));
////            tempMarkerOption.title(sampleList.get(i).getName());
////            tempMarkerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.marekerbackground2));
////            googleMap.addMarker(tempMarkerOption);
//            addMarker(sampleList.get(i),false);
//        }
//
//    }
//    private Clustering addMarker(Clustering markerItem,boolean isSelectedMarker){
//        LatLng position = markerItem.getLocation();
//        String name = markerItem.getTitle();
//
//        tv_makrer.setText(name);
//
//        if (isSelectedMarker) {
//            tv_makrer.setBackgroundResource(R.drawable.marker_selectx);
//            tv_makrer.setTextColor(Color.BLACK);
//        } else {
//            tv_makrer.setBackgroundResource(R.drawable.markery);
//            tv_makrer.setTextColor(Color.BLACK);
//        }
//
////        MarkerOptions markerOptions = new MarkerOptions();
////        markerOptions.title(name);
////        markerOptions.position(position);
//        Bitmap viewBitamp=createDrawableFromView(this,maker_root_view);
////        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(viewBitamp));
//        markerItem.setIcon(viewBitamp);
//        clusterManager.addItem(markerItem);
//        return markerItem;
//    }
//    private Clustering addMarker(Marker marker, boolean isSelectedMarker) {
////        double lat = marker.getPosition().latitude;
////        double lon = marker.getPosition().longitude;
////        String name = marker.getTitle();
////        MarkerItem temp = new MarkerItem(lat, lon, name);
//        Clustering tempMarker=clusteringHashMap.get(marker.getTitle());
//        return addMarker(tempMarker, isSelectedMarker);
//    }
//    private Marker addMarker(MarkerItem markerItem,boolean isSelectedMarker){
//        LatLng position = new LatLng(markerItem.getLat(), markerItem.getLng());
//        String name = markerItem.getName();
//
//        tv_makrer.setText(name);
//
//        if (isSelectedMarker) {
//            tv_makrer.setBackgroundResource(R.drawable.marker_selectx);
//            tv_makrer.setTextColor(Color.BLACK);
//        } else {
//            tv_makrer.setBackgroundResource(R.drawable.markery);
//            tv_makrer.setTextColor(Color.BLACK);
//        }
//
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.title(name);
//        markerOptions.position(position);
//        Bitmap viewBitamp=createDrawableFromView(this,maker_root_view);
//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(viewBitamp));
//
//
//        return googleMap.addMarker(markerOptions);
//    }
//
//    private Bitmap createDrawableFromView(Context context, View view) {
//
//        //해상도와 그래픽을 확인하는 부분
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        //레이아웃 params는 레이아웃의 속성을 정해줘서 만드는거임
//        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
//        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
//        view.buildDrawingCache();
//
//        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(bitmap);
//        view.draw(canvas);
//
//        return bitmap;
//    }
//
//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
//        googleMap.animateCamera(center);
//        int position=-1;
//        if(marker.getTitle()!=null){
//            position=Integer.valueOf(marker.getTitle());
//        }
////        for(int i=0;i<sampleList.size();i++){
////            if(marker.getTitle().equals(sampleList.get(i).getName())) {
////                position=i;
////            }
////        }
//        if(position!=-1){
//            Clustering tempClustering=clusteringHashMap.get(position);
//            markerTitle.setText(tempClustering.getTitle());
//            markerAddr.setText(tempClustering.getLocation().toString());
//            changeSelectedMarker(marker);
//        }
//        else{
//            markerTitle.setText("새로운 지역입니다");
//            markerAddr.setText("새로운 지역을 저장 하시겠습니까?");
//        }
//        markerInform.setVisibility(View.VISIBLE);
//        return true;
//    }
//    private void changeSelectedMarker(Marker marker) {
//        // 선택했던 마커 되돌리기
//        if (selectedMarker != null) {
//            addMarker(selectedMarker, false);
//            //이부분 확인하기
//        }
//
//        // 선택한 마커 표시
//        if (marker != null) {
//            selectedMarker = addMarker(marker, true);
//            marker.remove();
//        }
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//
//    }
//
//    @Override
//    public void onStatusChanged(String s, int i, Bundle bundle) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String s) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String s) {
//
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//}
//class Clustering implements ClusterItem {
//    private LatLng location;
//    private int markerId;
//    private String title;
//    private Bitmap icon;
//    Clustering (LatLng location,int markerId,Bitmap icon){
//        this.location=location;
//        this.markerId=markerId;
//        this.icon=icon;
//    }
//    Clustering (LatLng location,String title,int markerId){
//        this.location=location;
//        this.title=title;
//        this.markerId=markerId;
//    }
//    LatLng getLocation(){
//        return this.location;
//    }
//    int getMarkerId(){
//        return this.markerId;
//    }
//    String getTitle(){return this.title;}
//    Bitmap getIcon(){return this.icon;}
//
//
//    void setLoaction(LatLng location){
//        this.location=location;
//    }
//    void setMarkerId(int markerId){
//        this.markerId=markerId;
//    }
//    void setTitle(String title){this.title=title;}
//    void setIcon(Bitmap icon){this.icon=icon;}
//    @Override
//    public LatLng getPosition() {
//        return location;
//    }
//}
//class ownRendering extends DefaultClusterRenderer<Clustering>{
//
//    public ownRendering(Context context, GoogleMap map, ClusterManager<Clustering> clusterManager) {
//        super(context, map, clusterManager);
//    }
//
//    @Override
//    protected void onBeforeClusterItemRendered(Clustering item, MarkerOptions markerOptions) {
//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(item.getIcon()));
//        markerOptions.title(String.valueOf(item.getMarkerId()));
//        super.onBeforeClusterItemRendered(item, markerOptions);
//
//    }
//}
//
//import android.content.Context;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.Filter;
//import android.widget.Filterable;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//
//class PlaceAPI {
//
//    private static final String TAG = PlaceAPI.class.getSimpleName();
//
//    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
//    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
//    private static final String OUT_JSON = "/json";
//
//    private static final String API_KEY = "AIzaSyA129i52EuRRpOnoypHnsEexWbE0IFhKgc";
//
//    //함수 파라미터로 들어오는 input값이 검색어
//    public ArrayList<String> autocomplete (String input) {
//        ArrayList<String> resultList = null;
//
//        HttpURLConnection conn = null;
//        StringBuilder jsonResults = new StringBuilder();
//
//        try {
//            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
//            sb.append("?input=" + URLEncoder.encode(input, "utf8"));
//            sb.append("&types=geocode");
//            sb.append("&language=ko");
//            sb.append("&key=" + API_KEY);
//
//            URL url = new URL(sb.toString());
//            conn = (HttpURLConnection) url.openConnection();
//            InputStreamReader in = new InputStreamReader(conn.getInputStream());
//
//            // Load the results into a StringBuilder
//            int read;
//            char[] buff = new char[1024];
//            while ((read = in.read(buff)) != -1) {
//                jsonResults.append(buff, 0, read);
//            }
////            Log.d("result",jsonResults.toString());
//        } catch (MalformedURLException e) {
//            Log.e(TAG, "Error processing Places API URL", e);
//            return resultList;
//        } catch (IOException e) {
//            Log.e(TAG, "Error connecting to Places API", e);
//            return resultList;
//        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
//
//        try {
//            Log.d(TAG, jsonResults.toString());
//
//            // Create a JSON object hierarchy from the results
//            JSONObject jsonObj = new JSONObject(jsonResults.toString());
//            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
//
////            Log.d("result",String.valueOf(predsJsonArray.length()));
//            // Extract the Place descriptions from the results
//            resultList = new ArrayList<String>(predsJsonArray.length());
//            for (int i = 0; i < predsJsonArray.length(); i++) {
//                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
////                Log.d("result",predsJsonArray.getJSONObject(i).getString("description"));
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Cannot process JSON results", e);
//        }
//
//        return resultList;
//    }
//}
//class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
//
//    ArrayList<String> resultList;
//
//    Context mContext;
//    int mResource;
//
//    PlaceAPI mPlaceAPI = new PlaceAPI();
//
//    public PlacesAutoCompleteAdapter(Context context, int resource) {
//        super(context, resource);
//        mContext = context;
//        mResource = resource;
//    }
//
//    @Override
//    public int getCount() {
//        // Last item will be the footer
//        return resultList.size();
//    }
//
//    @Override
//    public String getItem(int position) {
//        return resultList.get(position);
//    }
//
//    @Override
//    public Filter getFilter() {
//        Filter filter = new Filter() {
//
//            @Override
//            //constraint에 char형태로 input이 들어가있음.
//            //초기화되고 계속해서 불림
//            protected FilterResults performFiltering(CharSequence constraint) {
//                FilterResults filterResults = new Filter.FilterResults();
//                if (constraint != null) {
//                    resultList = mPlaceAPI.autocomplete(constraint.toString());
//
//                    // Footer
//                    resultList.add("footer");
//
//                    filterResults.values = resultList;
//                    filterResults.count = resultList.size();
//                }
//
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                if (results != null && results.count > 0) {
//                    notifyDataSetChanged();
//                } else {
//                    notifyDataSetInvalidated();
//                }
//            }
//        };
//
//        return filter;
//    }
//}
