package jaewanlee.android_project.com.map_memo;


import android.content.Intent;

import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;

import android.view.View;

import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.IOException;

import java.util.List;

/**
 * Created by jaewan on 2017-01-04.
 */

public class Chat_map extends AppCompatActivity implements OnMapReadyCallback {
    //layout의 뷰들
    private TextView title_tv;//타이틀
    private AutoCompleteTextView search_auto;//서치창
    private ImageButton search_bt;//서치 버튼

    //마커 선택시 나오는 레이아웃의 뷰
    LinearLayout markerInform_linearlayout;//전체 리니어 레이아웃
    TextView layoutTitle_tv;//타이틀
    TextView layoutAddr_tv;//주소
    ImageView layoutImage_iv;//이미지

    //구글 맵
    MapFragment mapFragment;//맵이 뜬ㄴ 화면
    GoogleMap googleMap;//구글맵 object


    //인텐트로 id값 가져오기
    Intent getIntent;
    String user_id;
    String friend;
    String roomNumber;


    //서치버튼을 통해 새롭게 저장되는 값
    List<Address> geoResult;
    LatLng newLantLng;

    Marker selectedMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //인텐트로 유저 id가져오기
        getIntent = getIntent();
        user_id = getIntent.getStringExtra("user_id");
        friend = getIntent.getStringExtra("friend");
        roomNumber = getIntent.getStringExtra("roomNumber");

        //뷰랑 연결시키기
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_map_fragment);
        title_tv = (TextView) findViewById(R.id.map_title_textView);
        search_auto = (AutoCompleteTextView) findViewById(R.id.map_search_autoComplete);
        search_bt = (ImageButton) findViewById(R.id.map_search_button);
        markerInform_linearlayout = (LinearLayout) findViewById(R.id.map_Linear_LinearLayout);
        layoutTitle_tv = (TextView) findViewById(R.id.map_markertitle_TextView);
        layoutAddr_tv = (TextView) findViewById(R.id.map_markeraddr_textView);
        layoutImage_iv = (ImageView) findViewById(R.id.map_image_imageView);

        //글꼴설정
        title_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        search_auto.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        layoutTitle_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        layoutAddr_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));

        //구글 지도 그리기
        mapFragment.getMapAsync(this);

        //자동완성 설정 어뎁터 설정
        search_auto.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.acivity_test2));
        //자동완성 내용 클릭시
        search_auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String description = (String) parent.getItemAtPosition(position);

                Toast.makeText(Chat_map.this, description, Toast.LENGTH_SHORT).show();
                search_auto.destroyDrawingCache();
            }
        });
        //검색 버튼 클릭이벤트
        search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = search_auto.getText().toString();
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    geoResult = geocoder.getFromLocationName(str, 1);
                    if (geoResult.size() > 0) {
                        Address address = geoResult.get(0);
                        Log.d("address", address.toString());

                        newLantLng = new LatLng(address.getLatitude(), address.getLongitude());

                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(newLantLng));
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        googleMap.addMarker(new MarkerOptions().position(newLantLng).draggable(true).title("-1"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Chat_map.this, "잘못된 주소입니다. 다시 확인해 주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //리니어 레이아웃을 클릭했을시의 이벤트->상세화면으로 이동
        markerInform_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToDirect_input = new Intent(getApplicationContext(), Chat_input.class);
                moveToDirect_input.putExtra("user_id", user_id);
                moveToDirect_input.putExtra("fromMap", 1);
                LatLng tempLatLng = selectedMarker.getPosition();
                moveToDirect_input.putExtra("lat", String.valueOf(tempLatLng.latitude));
                moveToDirect_input.putExtra("lon", String.valueOf(tempLatLng.longitude));
                moveToDirect_input.putExtra("friend",friend);
                moveToDirect_input.putExtra("roomNumber",roomNumber);
                startActivityForResult(moveToDirect_input, 1);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;//구글 맵 object 연결시킴
        //초기 카메라 위치 설정
        LatLng start = new LatLng(37.534189, 126.900826);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(start));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        //마커 클릭시 이벤트
        googleMap.setOnMarkerClickListener(markerClickListener);

    }

    //마커 클릭 이벤트
    public GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            selectedMarker = marker;
            //마커 정보창을 보이도록 설정
            markerInform_linearlayout.setVisibility(View.VISIBLE);
            markerInform_linearlayout.setClickable(true);
            markerInform_linearlayout.setFocusableInTouchMode(true);
            return true;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
            }
        } else {
            Log.d("lg", "log");
        }
    }
}
