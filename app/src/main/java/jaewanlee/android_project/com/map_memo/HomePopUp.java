package jaewanlee.android_project.com.map_memo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by jaewan on 2016-12-13.
 */

public class HomePopUp extends Activity {
    TextView message;
    Button map;
    Button direct;
    //사용자 id받아오기
    Intent getIntent;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home_pop_up);
        message=(TextView)findViewById(R.id.homePop_message_TextView);
        map=(Button)findViewById(R.id.homePop_map_Button);
        direct=(Button)findViewById(R.id.homePop_direct_Button);

        message.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        map.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        direct.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));

        getIntent=getIntent();
        user_id=getIntent.getStringExtra("user_id");

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToMap=new Intent(getApplicationContext(),MapMain.class);
                moveToMap.putExtra("user_id",user_id);
                startActivity(moveToMap);
                finish();
            }
        });
        direct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToDirect=new Intent(getApplicationContext(),Direct_input.class);
                moveToDirect.putExtra("user_id",user_id);
                startActivity(moveToDirect);
                finish();
            }
        });
    }
}
