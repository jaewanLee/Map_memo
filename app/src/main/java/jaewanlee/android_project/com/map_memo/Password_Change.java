package jaewanlee.android_project.com.map_memo;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by jaewan on 2017-01-21.
 */

public class Password_Change extends AppCompatActivity {

    ImageButton back_ib;
    TextView title_tv;
    EditText nowPass_et;
    EditText newPass_et;
    EditText newPassCheck_et;
    Button save_bt;

    String user_id;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        back_ib=(ImageButton)findViewById(R.id.passChange_back_ImageButton);
        title_tv=(TextView)findViewById(R.id.passChange_title_textView);
        nowPass_et=(EditText) findViewById(R.id.passChange_nowPass_textView);
        newPass_et=(EditText)findViewById(R.id.passChange_newPass_textView);
        newPassCheck_et=(EditText)findViewById(R.id.passChange_newPassCheck_textView);
        save_bt=(Button)findViewById(R.id.passChange_save_button);

        title_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        nowPass_et.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        newPass_et.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        newPassCheck_et.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        save_bt.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));

        Intent getintent=getIntent();
        user_id=getintent.getStringExtra("user_id");

        save_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newPassCheck_et.getText().toString().equals(newPass_et.getText().toString())){
                    ModifyPassword modifyPassword=new ModifyPassword(nowPass_et.getText().toString(),newPass_et.getText().toString());
                    modifyPassword.execute();
                }
                else{
                    Toast.makeText(Password_Change.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    class ModifyPassword extends AsyncTask<Void,Void,String>{
        String nowPassword;
        String newPassword;
        ModifyPassword(String nowPassword,String newPassword){
            this.nowPassword=nowPassword;
            this.newPassword=newPassword;
        }

        @Override
        protected String doInBackground(Void... params) {
            //디비 주소
            String link = "http://allucanbuy.vps.phps.kr/map_memo/chagePassword.php";
            //데이터 보내기 위해 urlEncoder로 변경하기
            String data = null;
            try {
                data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");
                data += "&" + URLEncoder.encode("nowPassword", "UTF-8") + "=" + URLEncoder.encode(nowPassword, "UTF-8");
                data += "&" + URLEncoder.encode("newPassword", "UTF-8") + "=" + URLEncoder.encode(newPassword, "UTF-8");

                //연결
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                //값을 쓰기위한 stream
                conn.setDoOutput(true);
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                //값을 보내고 끝내기
                osw.write(data);
                osw.close();
                //return 값 읽기
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                //결과 post로 보내기
                return sb.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("password fail")){
                Toast.makeText(Password_Change.this, "비밀번호가 일치하지 않습니다1", Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("modify success")){
                Toast.makeText(Password_Change.this, "비밀번호가 정상적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(Password_Change.this, "통신오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
}
