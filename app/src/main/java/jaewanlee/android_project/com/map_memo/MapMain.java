package jaewanlee.android_project.com.map_memo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jaewan on 2016-12-13.
 */

public class MapMain extends AppCompatActivity implements OnMapReadyCallback{

    //layout의 뷰들
    private TextView title_tv;//타이틀
    private AutoCompleteTextView search_auto;//서치창
    private ImageButton search_bt;//서치 버튼
    ImageButton map_bt;
    ImageButton list_bt;
    ImageButton chat_bt;
    ImageButton setting_bt;

    //마커 선택시 나오는 레이아웃의 뷰
    LinearLayout markerInform_linearlayout;//전체 리니어 레이아웃
    TextView layoutTitle_tv;//타이틀
    TextView layoutAddr_tv;//주소
    ImageView layoutImage_iv;//이미지

    //구글 맵
    MapFragment mapFragment;//맵이 뜬ㄴ 화면
    GoogleMap googleMap;//구글맵 object

    //커스텀 마커에 사용되는 요소
    View maker_root_view;
    TextView tv_makrer;

    //마커를 구성하는 데이터 값
    HashMap<Integer,MarkerCluster> markerClusterHashMap;

    //마커 클러스터링을 관리하는 클러스터 매니저
    ClusterManager<MarkerCluster> clusterManager;

    //카메라의 변화를 감지하는 변수
    CameraPosition previousCameraPosition;

    //현재 선택된 마커를 저장하는 변수
    Marker selectedMarker;

    //geo코더로 위도/경도 값 주고받기

    //인텐트로 id값 가져오기
    Intent getIntent;
    String user_id;

    //서치버튼을 통해 새롭게 저장되는 값
    List<Address> geoResult;
    LatLng newLantLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //인텐트로 유저 id가져오기
        getIntent=getIntent();
        user_id=getIntent.getStringExtra("user_id");

        //뷰랑 연결시키기
        mapFragment=(MapFragment)getFragmentManager().findFragmentById(R.id.map_map_fragment);
        title_tv=(TextView)findViewById(R.id.map_title_textView);
        search_auto=(AutoCompleteTextView) findViewById(R.id.map_search_autoComplete);
        search_bt=(ImageButton)findViewById(R.id.map_search_button);
        markerInform_linearlayout=(LinearLayout)findViewById(R.id.map_Linear_LinearLayout);
        layoutTitle_tv=(TextView)findViewById(R.id.map_markertitle_TextView);
        layoutAddr_tv=(TextView)findViewById(R.id.map_markeraddr_textView);
        layoutImage_iv=(ImageView)findViewById(R.id.map_image_imageView);
        map_bt=(ImageButton)findViewById(R.id.map_map_ImageButton);
        list_bt=(ImageButton)findViewById(R.id.map_list_Imagebutton);
        chat_bt=(ImageButton)findViewById(R.id.map_chat_Imagebutton);
        setting_bt=(ImageButton)findViewById(R.id.map_setting_Imagebutton);
        map_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveTomap=new Intent(getApplicationContext(),MapMain.class);
                moveTomap.putExtra("user_id",user_id);
                startActivity(moveTomap);
                finish();
            }
        });
        list_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToList=new Intent(getApplicationContext(),Category_view.class);
                moveToList.putExtra("user_id",user_id);
                startActivity(moveToList);
                finish();
            }
        });
        chat_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToChat=new Intent(getApplicationContext(),Chat_main.class);
                moveToChat.putExtra("user_id",user_id);
                startActivity(moveToChat);
                finish();

            }
        });
        setting_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToSetting=new Intent(getApplicationContext(),Setting.class);
                moveToSetting.putExtra("user_id",user_id);
                startActivity(moveToSetting);
                finish();
            }
        });


        //커스텀 마커를 만들기 위해 해당 뷰들 설정 해주기
        maker_root_view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.coutom_marker,null);
        tv_makrer=(TextView)maker_root_view.findViewById(R.id.marker_text_textView);

        //글꼴설정
        title_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        search_auto.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        layoutTitle_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        layoutAddr_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        tv_makrer.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));



        //마커 클러스터 구성 데이터 모음 해쉬태그
        markerClusterHashMap=new HashMap<Integer, MarkerCluster>();

        //구글 지도 그리기
        mapFragment.getMapAsync(this);

        //자동완성 설정 어뎁터 설정
        search_auto.setAdapter(new PlacesAutoCompleteAdapter(this,R.layout.acivity_test2));
        //자동완성 내용 클릭시
        search_auto.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String description=(String)parent.getItemAtPosition(position);

                Toast.makeText(MapMain.this, description, Toast.LENGTH_SHORT).show();
                search_auto.destroyDrawingCache();
            }
        });
        //검색 버튼 클릭이벤트
                search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str=search_auto.getText().toString();
                Geocoder geocoder=new Geocoder(getApplicationContext());
                try {
                    geoResult=geocoder.getFromLocationName(str,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(geoResult!=null){
                    if(geoResult.size()>0){
                        Address address=geoResult.get(0);
                        Log.d("address",address.toString());

                        newLantLng=new LatLng(address.getLatitude(),address.getLongitude());

                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(newLantLng));
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        googleMap.addMarker(new MarkerOptions().position(newLantLng).draggable(true).title("-1"));
                    }
                    else{
                        Toast.makeText(MapMain.this, "잘못된 주소입니다. 다시 확인해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        //리니어 레이아웃을 클릭했을시의 이벤트->상세화면으로 이동
        markerInform_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Linear","in");
                //새로 추가한 경우
                if(selectedMarker.getTitle().equals("-1")){
                    Intent moveToDirect_input=new Intent(getApplicationContext(),Direct_input.class);
                    moveToDirect_input.putExtra("user_id",user_id);
                    moveToDirect_input.putExtra("fromMap",1);
                    LatLng tempLatLng=selectedMarker.getPosition();
                    moveToDirect_input.putExtra("lat",String.valueOf(tempLatLng.latitude));
                    moveToDirect_input.putExtra("lon",String.valueOf(tempLatLng.longitude));
                    startActivity(moveToDirect_input);
                    finish();
                    //위치정보들 서울시,구 이런것들은 위도 경도를 넘겨서 geo코더로 다시 가져옴->검색 결과의 불안정성때문
                }
                else{
                    Intent moveToDirect_input=new Intent(getApplicationContext(),Direct_input.class);
                    moveToDirect_input.putExtra("user_id",user_id);
                    moveToDirect_input.putExtra("fromMap",0);
                    moveToDirect_input.putExtra("DBId",selectedMarker.getTitle().toString());
                    startActivity(moveToDirect_input);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //데이터 베이스 받아오기
        DBManager dbManager = new DBManager(getApplicationContext(), "MEMO.db", null, 1);
        markerClusterHashMap = dbManager.markerDB();

    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap=map;//구글 맵 object 연결시킴
        //초기 카메라 위치 설정
        LatLng start=new LatLng(37.534189,126.900826);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(start));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));

        //마커 클릭시 이벤트
        googleMap.setOnMarkerClickListener(markerClickListener);
        //클러스터 메니져 생성
        clusterManager=new ClusterManager<MarkerCluster>(getApplicationContext(),googleMap);
        //클러스터 메니져로 생성되는 마커들 마커 옵션 설정
        clusterManager.setRenderer(new Rendering(getApplicationContext(),googleMap,clusterManager));
        //초기 카메라 포지션값 설정
        previousCameraPosition=null;
        //카메라 변경 인식
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener(){
            @Override
            public void onCameraIdle() {
                //카메라 포지션이 이전과 달라질 경우
                CameraPosition position=googleMap.getCameraPosition();
                if(previousCameraPosition==null||previousCameraPosition.zoom!=position.zoom){
                    previousCameraPosition=googleMap.getCameraPosition();
                    //클러스터를 재정립함
                    clusterManager.cluster();
                }
            }
        });
        //초기 데이터 가져오기
//        getMarkInfo();
        //초기 데이터 설정
        setMark();

    }

    //마커 정보 생성
    public void getMarkInfo(){
//        markerClusterHashMap=new HashMap<Integer, MarkerCluster>();
//        markerClusterHashMap.put(0,new MarkerCluster(new LatLng(37.534389, 126.900826),"삼대천왕 물짜장",0));
//        markerClusterHashMap.put(1,new MarkerCluster(new LatLng(37.534489, 126.900826),"삼대천왕 물짜장",1));
//        markerClusterHashMap.put(3,new MarkerCluster(new LatLng(37.534589, 126.900826),"삼대천왕 물짜장",3));
//        markerClusterHashMap.put(4,new MarkerCluster(new LatLng(37.534689, 126.900826),"삼대천왕 물짜장",4));
//        markerClusterHashMap.put(5,new MarkerCluster(new LatLng(37.534789, 126.900826),"삼대천왕 물짜장",5));
//        markerClusterHashMap.put(6,new MarkerCluster(new LatLng(37.534889, 126.900826),"삼대천왕 물짜장",6));
//        markerClusterHashMap.put(7,new MarkerCluster(new LatLng(37.534989, 126.900826),"삼대천왕 물짜장",7));
//        markerClusterHashMap.put(8,new MarkerCluster(new LatLng(37.535089, 126.900826),"삼대천왕 물짜장",8));
//        markerClusterHashMap.put(9,new MarkerCluster(new LatLng(37.535189, 126.900826),"삼대천왕 물짜장",9));
//        markerClusterHashMap.put(10,new MarkerCluster(new LatLng(37.535289, 126.900826),"삼대천왕 물짜장",10));
//        markerClusterHashMap.put(11,new MarkerCluster(new LatLng(37.535389, 126.900826),"삼대천왕 물짜장",11));

    }
    //해쉬 맵의 마커 정보들 매벵 설정하기
    public void setMark(){
        Iterator<Integer> iterator = markerClusterHashMap.keySet().iterator();
        while(iterator.hasNext()){
            int key=(Integer)iterator.next();
            addMarker(markerClusterHashMap.get(key),false);
        }
    }
    //마커 클릭 이벤트
    public GoogleMap.OnMarkerClickListener markerClickListener=new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            //카메라 위치를 클릭된 마커가 중앙이 되도록 이동
            CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
            googleMap.animateCamera(center);
            int position=-1;
            //만일 마커가 사전에 저장되니 마커일경우 position에 마커의 id값을 넣어줌
            if(marker.getTitle()!=null){
                if((position=Integer.valueOf(marker.getTitle()))!=-1){
                    //마커의 id값을 사용하여 정보를 가져옴
                    MarkerCluster tempClustering=markerClusterHashMap.get(position);
                    //디비에 접속해서 해당 아이디에 관련된 정보들 가져오기
                    DBManager dbManager=new DBManager(getApplicationContext(), "MEMO.db", null, 1);
                    MarkerItem tempMarkerItem=dbManager.layoutDB(tempClustering.getMarkerId());
                    //가져온 정보로 세팅
                    layoutTitle_tv.setText(tempMarkerItem.getTitle());
                    //레이아웃에 표시될 주소
                    String address="";
                    //1차 카테고리
                    String[] addrCategory1=getResources().getStringArray(R.array.citiy);
                    address+=addrCategory1[tempMarkerItem.getAddrCategory1()]+" ";
                    //두번쨰 지역 string은 2차배열로 만들자
                    //*********************************************************************************************************************************************
                    GetAreaData getAreaData=new GetAreaData(getApplicationContext(),tempMarkerItem.getAddrCategory1());
                    String[] addrCategory2=getAreaData.getCategory2Array();
                    address+=addrCategory2[tempMarkerItem.getAddrCategory2()]+" ";
                    address+=tempMarkerItem.getAddrDetail();
                    layoutAddr_tv.setText(address);
                    if( !tempMarkerItem.getImg().equals("null")){
                        String imgPath="data/data/jaewanlee.android_project.com.map_memo/files/"+tempMarkerItem.getImg();
                        layoutImage_iv.setImageBitmap(BitmapFactory.decodeFile(imgPath));
                    }
                    //선택한 마커의 배경화면을 변경해줌
                    changeSelectedMarker(marker);
                }
                else{//값은 있지만 새로운 지역인 경우
                    layoutTitle_tv.setText("새로운 지역입니다");
                    layoutAddr_tv.setText("새로운 지역을 저장 하시겠습니까?");
                    layoutImage_iv.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.test2));
                    changeSelectedMarker(null);
                    selectedMarker=marker;
                }
            }
            else{//마커가 잘못 설정된 경우
                layoutTitle_tv.setText("새로운 지역입니다");
                layoutAddr_tv.setText("새로운 지역을 저장 하시겠습니까?");
                changeSelectedMarker(null);
                selectedMarker=marker;
            }
            //마커 정보창을 보이도록 설정

            markerInform_linearlayout.setVisibility(View.VISIBLE);
            markerInform_linearlayout.setClickable(true);
            markerInform_linearlayout.setFocusableInTouchMode(true);
            return true;
        }
    };
    private void changeSelectedMarker(Marker marker) {
        // 선택했던 마커 되돌리기
        if (selectedMarker != null &&!selectedMarker.getTitle().equals("-1")) {
            //chat이 1일경우 일반 -1일경우 공유된거
            if(markerClusterHashMap.get(Integer.valueOf(selectedMarker.getTitle())).getType().equals("1")){
                tv_makrer.setText(markerClusterHashMap.get(Integer.valueOf(selectedMarker.getTitle())).getTitle());
                tv_makrer.setBackgroundResource(R.drawable.markery);
            }
            else {
                tv_makrer.setText(markerClusterHashMap.get(Integer.valueOf(selectedMarker.getTitle())).getTitle());
                tv_makrer.setBackgroundResource(R.drawable.markershare);
            }
            Bitmap tempBitmap=createDrawableFromView(this,maker_root_view);
            selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(tempBitmap));
            selectedMarker=null;
        }

        // 선택한 마커 표시
        if (marker != null) {
            tv_makrer.setText(markerClusterHashMap.get(Integer.valueOf(marker.getTitle())).getTitle());
            tv_makrer.setBackgroundResource(R.drawable.marker_selectx);
            Bitmap viewBitamp=createDrawableFromView(this,maker_root_view);
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(viewBitamp));
            selectedMarker=marker;
        }
//        clusterManager.cluster();
    }
    //마커 추가 함수
    private MarkerCluster addMarker(MarkerCluster markerItem,boolean isSelectedMarker){
        //정보 가져오기
        String name = markerItem.getTitle();//마커에 표시될 타이틀
        tv_makrer.setText(name);//마커의 타이틀 설정
        //해당 마커를 선택 할 경우
        if (isSelectedMarker) {
            //배경 변경
            tv_makrer.setBackgroundResource(R.drawable.marker_selectx);
            tv_makrer.setTextColor(Color.BLACK);
        } else {//선택하지 않은 마커
            if(markerItem.getType().equals("1")){
                tv_makrer.setBackgroundResource(R.drawable.markery);
                tv_makrer.setTextColor(Color.BLACK);
            }
            else{
                tv_makrer.setBackgroundResource(R.drawable.markershare);
                tv_makrer.setTextColor(Color.BLACK);
            }

        }
        //뷰를 사진으로 바꾸기
        Bitmap viewBitamp=createDrawableFromView(this,maker_root_view);
        //markerItem에 이미지 설정하기
        markerItem.setIcon(viewBitamp);
        //마커 매니저에 마커 추가
        clusterManager.addItem(markerItem);
        return markerItem;
    }
    //마커 재추가 함수
    private MarkerCluster addMarker(Marker marker, boolean isSelectedMarker) {
        MarkerCluster tempMarker=markerClusterHashMap.get(Integer.valueOf(marker.getTitle()));
        return addMarker(tempMarker, isSelectedMarker);
    }
    //뷰 이미지로 바꾸기
    private Bitmap createDrawableFromView(Context context, View view) {
        //해상도와 그래픽을 확인하는 부분
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //레이아웃 params는 레이아웃의 속성을 정해줘서 만드는거임
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        //이미지로 바꾸기
        view.buildDrawingCache();
        //비트맵으로 변경
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        //캔버스를 이용해서 뷰에 생성하기 직접 뷰에 그려주는 역할을 함. 즉 지금 bitmap이 뷰랑 따로놀고있는데 이걸 뷰에 덮어씌워주는거임
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


}
