package jaewanlee.android_project.com.map_memo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
 * Created by jaewan on 2016-11-29.
 */

public class Find extends AppCompatActivity {

    //layout에서 사용되는 view
    TextView title_tv;//메인 타이틀
    TextView subTitle_tv;//부제목
    TextView name_tv;//이름 표시
    EditText name_et;//이름 입력
    TextView phone_tv;//핸드폰 번호 표시
    EditText phone_et1;//핸드폰 번호 입력
    EditText phone_et2;//핸드폰 번호 입력
    EditText phone_et3;//핸드폰 번호 입력
    Button find_bt;//아이디,비밀번호 찾기 버튼
    Button cancel_bt;//취소버튼

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        //view들 layout과 연결하기
        title_tv=(TextView)findViewById(R.id.find_title_textView);
        subTitle_tv=(TextView)findViewById(R.id.find_subtitle_textView);
        name_tv=(TextView)findViewById(R.id.find_name_textView);
        name_et=(EditText)findViewById(R.id.find_name_EditText);
        phone_tv=(TextView)findViewById(R.id.find_phone_textView);
        phone_et1=(EditText)findViewById(R.id.find_phone1_EditText);
        phone_et2=(EditText)findViewById(R.id.find_phone2_EditText);
        phone_et3=(EditText)findViewById(R.id.find_phone3_EditText);
        find_bt=(Button)findViewById(R.id.find_find_button);
        cancel_bt=(Button)findViewById(R.id.find_cancel_button);

        //각 뷰들 폰트 적용하기
        title_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        subTitle_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        name_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        name_et.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        phone_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        phone_et1.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        phone_et2.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        phone_et3.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));

        find_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name=name_et.getText().toString();
                String phone=phone_et1.getText().toString();
                phone+=phone_et2.getText().toString();
                phone+=phone_et3.getText().toString();
                FindUserInfo findUserInfo=new FindUserInfo(user_name,phone);
                findUserInfo.execute();
            }
        });

        cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public class FindUserInfo extends AsyncTask<Void,Void,String>{
        String user_name;
        String phone;
        ProgressDialog progressDialog;
        public FindUserInfo (String user_name,String phone){
            this.user_name=user_name;
            this.phone=phone;
        }

        @Override
        protected String doInBackground(Void... params) {
            //디비 주소
            String link = "http://allucanbuy.vps.phps.kr/map_memo/fotgot.php";
            //데이터 보내기 위해 urlEncoder로 변경하기
            String data = null;
            try {
                data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8");
                data += "&" + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8");
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
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(Find.this);
            progressDialog.setMessage("회원정보 체크중");
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.contains("false!")){
                Toast.makeText(Find.this, "이름과 핸드폰 번호를 다시 확인해 주십시오", Toast.LENGTH_SHORT).show();
            }
            else if(result.contains("Error")){
                Toast.makeText(Find.this, "네트워크 오류가 발생하였습니다. 잠시후 다시 시도해 주십시오", Toast.LENGTH_SHORT).show();
            }
            else if(result.contains("sent!")){
                int idPos=result.indexOf("#k");
                int finishPos=result.indexOf("</body>");
                if(idPos!=-1){
                    idPos+=2;
                    String Changed_user_name=result.substring(idPos,finishPos);
                    String result_name="";
                    for(int i=0;i<Changed_user_name.length();i++){
                        if(i>4){
                            result_name+='*';
                        }
                        else{
                            result_name+=Changed_user_name.charAt(i);
                        }
                    }
                    Toast.makeText(Find.this, result_name+"로 임시 비밀번호가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            else{

            }
            progressDialog.dismiss();
        }

    }
}
