package jaewanlee.android_project.com.map_memo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jaewan on 2016-11-29.
 */

public class SignUp extends AppCompatActivity {
    //layout에서 사용되는 view
    TextView title_tv;//최상단 타이틀
    TextView subTitle_tv;//부제목
    TextView id_tv;//아이디 표시
    EditText id_et;//아이디 입력
    Button duplicate_bt;//중복체크
    TextView password_tv;//패스워드 표시
    EditText password_et;//패스워드 입력
    TextView passwordCheck_tv;//패스워드 확인 표시
    EditText passwordCheck_et;//패스워드 확인 입력
    TextView name_tv;//이름 표시
    EditText name_et;//이름 입력
    TextView phone_tv;//전화번호 표시
    EditText phone_et1;//전화번호 입력
    EditText phone_et2;//전화번호 입력
    EditText phone_et3;//전화번호 입력
    Button save_bt;//저장 버튼
    Button cancel_bt;//취소 버튼

    Boolean duplicateCheck;//중복 체크 확인 여부
    String phone;//나누어 져있는 핸드폰번호 합치기

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //view와 레이아웃 연결
        title_tv=(TextView)findViewById(R.id.signUp_title_textView);
        subTitle_tv=(TextView)findViewById(R.id.signUp_subtitle_textView);
        id_tv=(TextView)findViewById(R.id.signUp_userId_textView);
        id_et=(EditText)findViewById(R.id.signUp_userId_EditText);
        duplicate_bt=(Button)findViewById(R.id.signUp_dulpicate_button);
        password_tv=(TextView)findViewById(R.id.signUp_password_textView);
        password_et=(EditText)findViewById(R.id.signUp_password_EditText);
        passwordCheck_tv=(TextView)findViewById(R.id.signUp_passwordCheck_textView);
        passwordCheck_et=(EditText)findViewById(R.id.signUp_passwordCheck_EditText);
        name_tv=(TextView)findViewById(R.id.signUp_name_textView);
        name_et=(EditText)findViewById(R.id.signUp_name_EditText);
        phone_tv=(TextView)findViewById(R.id.signUp_phone_textView);
        phone_et1=(EditText)findViewById(R.id.signUp_phone1_EditText);
        phone_et2=(EditText)findViewById(R.id.signUp_phone2_EditText);
        phone_et3=(EditText)findViewById(R.id.signUp_phone3_EditText);
        save_bt=(Button)findViewById(R.id.signUp_save_button);
        cancel_bt=(Button)findViewById(R.id.signUp_cancel_button);

        duplicateCheck=false;//중복확인 확인 안했을 경우 값

        //폰트 적용
        title_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        subTitle_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        id_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        id_et.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        duplicate_bt.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        password_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        passwordCheck_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        name_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        name_et.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        phone_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        phone_et1.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        phone_et2.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        phone_et3.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        save_bt.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        cancel_bt.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));

        //비밀번호 적용
        password_et.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password_et.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordCheck_et.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordCheck_et.setTransformationMethod(PasswordTransformationMethod.getInstance());

        //Save버튼 클릭 이벤트
        save_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //중복확인 했는지 체크하기
                if(duplicateCheck!=true){
                    Toast.makeText(SignUp.this, "아이디 중복체크를 진행해 주십시오", Toast.LENGTH_SHORT).show();
                }
                //두개의 비밀번호가 일치하는지 체크하기
                else if(!(password_et.getText().toString().equals(passwordCheck_et.getText().toString()))){
                    Toast.makeText(SignUp.this,"비밀번호가 일치하지 않습니다. 다시 확인해 주십시오",Toast.LENGTH_SHORT).show();
                }
                else if(name_et.getText().toString().length()<2){
                    Toast.makeText(SignUp.this, "이름을 입력해 주세요", Toast.LENGTH_SHORT).show();
                }
                else if(phone_et1.getText().toString().length()<2){
                    Toast.makeText(SignUp.this, "핸드폰 번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
                }
                else if(phone_et2.getText().toString().length()<2){
                    Toast.makeText(SignUp.this, "핸드폰 번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
                }
                else if(phone_et3.getText().toString().length()<2){
                    Toast.makeText(SignUp.this, "핸드폰 번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
                }

                //비밀번호가 형식에 맞는지 체크하기
                else{
                    String regex="^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$";
                    Pattern p=Pattern.compile(regex);
                    Matcher m=p.matcher(password_et.getText().toString());
                    if(!m.matches()){
                        Toast.makeText(SignUp.this, "비밀번호는 4~16자리여야 합니다", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //비밀번호도 일치하고, 비밀번호 정규식도 충족하고 아이디 중복체크도 해서 디비에 저장하기

                        //핸드폰 번호 string으로 바꾸기
                        phone=phone_et1.getText().toString()+phone_et2.getText().toString()+phone_et3.getText().toString();
                        //핸드폰 번호 중복 여부 체크하기

                        //DB연결을 위한 asyncTask 클래스
                        class InsertDB extends AsyncTask<String,Void,String>{
                            ProgressDialog loading;
                            @Override
                            protected String doInBackground(String... strings) {
                                try {
                                    String id=strings[0];
                                    String pass=strings[1];
                                    String phone=strings[2];
                                    String name=strings[3];
                                    String photo="null";
                                    Date today=new Date(System.currentTimeMillis());
                                    String date=(new SimpleDateFormat("yyyy - MM - dd")).format(today);
                                    String update_date=String.valueOf(System.currentTimeMillis());
                                    String type="0";
//                                    Log.d("insertDB",strings[0]+strings[1]+strings[2]);
                                    //디비 주소
                                    String link="http://allucanbuy.vps.phps.kr/map_memo/insert_DB.php";
                                    //데이터 보내기 위해 urlEncoder로 변경하기
                                    String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                                    data += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                                    data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(pass, "UTF-8");
                                    data += "&" + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8");
                                    data += "&" + URLEncoder.encode("photo", "UTF-8") + "=" + URLEncoder.encode(photo, "UTF-8");
                                    data += "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8");
                                    data += "&" + URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8");
                                    data += "&" + URLEncoder.encode("update", "UTF-8") + "=" + URLEncoder.encode(update_date, "UTF-8");

                                    //연결
                                    URL url=new URL(link);
                                    URLConnection conn=url.openConnection();
                                    //값을 쓰기위한 stream
                                    conn.setDoOutput(true);
                                    OutputStreamWriter osw=new OutputStreamWriter(conn.getOutputStream());
                                    //값을 보내고 끝내기
                                    osw.write(data);
                                    osw.close();
                                    //return 값 읽기
                                    BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                    StringBuilder sb=new StringBuilder();
                                    String line=null;

                                    //로컬 디비에 저장하기
                                    String query="insert into USER values(";
                                    query+="null,";
                                    query+="'"+id+"',";
                                    query+="'"+phone+"',";
                                    query+="'"+photo+"',";
                                    query+="'"+name+"',";
                                    query+="'"+type+"',";
                                    query+="'"+date+"',";
                                    query+="'"+update_date+"');";
                                    User_DB_Manager user_db_manager=new User_DB_Manager(getApplicationContext(),"USER.db",null,1);
                                    user_db_manager.insert(query);

                                    while((line=reader.readLine())!=null){
                                        sb.append(line);
                                        break;
                                    }
                                    //결과 post로 보내기
                                    return sb.toString();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                    return e.getMessage();
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                    return  e.getMessage();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return  e.getMessage();
                                }

                            }

                            @Override
                            protected void onPostExecute(String s) {
                                super.onPostExecute(s);
                                loading.dismiss();
                                if(s.equals("1")){
                                    Toast.makeText(SignUp.this, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();

                                    //자동로그인 설정
                                    SharedPreferences autoLogin=getSharedPreferences("autologin",0);
                                    SharedPreferences.Editor editor=autoLogin.edit();
                                    editor.putBoolean("autoLogin",true);
                                    editor.putString("id",id_et.getText().toString());
                                    editor.commit();

                                    //인텐트로 이동 및 로그인 아이디 넘기기
                                    Intent moveBack= new Intent(getApplicationContext(),Home.class);
                                    moveBack.putExtra("user_id",id_et.getText().toString());
                                    startActivity(moveBack);
                                    finish();
                                }
                                else{
                                    Toast.makeText(SignUp.this, "회원가입에 실패하였습니다. 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                loading=ProgressDialog.show(SignUp.this,"저장 중 입니다",null,true,true);                            }
                        }
                        InsertDB insert=new InsertDB();
                        insert.execute(id_et.getText().toString(),password_et.getText().toString(),phone,name_et.getText().toString());

                    }

                }
            }
        });
        //취소 버튼 클릭 이벤트
        cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //아이디 중복여부 체크
        duplicate_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //아이디가 이메일 형식에 맞는지 체크하기
                String regex="^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
                Pattern p= Pattern.compile(regex);
                Matcher m=p.matcher(id_et.getText().toString());

                if(!m.matches()){
                    Toast.makeText(SignUp.this, "아이디가 이메일 형식에 맞지 않습니다", Toast.LENGTH_SHORT).show();
                }
                else{
                    //디비에서 중복되는게 있는지 확인하기
                    class DuplicateCheck extends AsyncTask<String,Void,String>{
                        ProgressDialog loading;
                        @Override
                        protected String doInBackground(String... strings) {
                            try {
                                String id=strings[0];
                                //디비 주소
                                String link="http://allucanbuy.vps.phps.kr/map_memo/id_duplicate_check.php";
                                //인코딩하기
                                String data=URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(id,"UTF-8");
                                //디비 연결
                                URL url=new URL(link);
                                URLConnection conn=url.openConnection();
                                //아웃풋 열기
                                conn.setDoOutput(true);
                                conn.setDoInput(true);

                                OutputStreamWriter osw=new OutputStreamWriter(conn.getOutputStream());
                                osw.write(data);
                                osw.close();

                                BufferedReader bf=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                StringBuilder sb=new StringBuilder();
                                String line=null;

                                while((line=bf.readLine())!=null){
                                    sb.append(line);
                                }
                                return sb.toString();

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                return e.getMessage().toString();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                                return e.getMessage().toString();
                            } catch (IOException e) {
                                e.printStackTrace();
                                return e.getMessage().toString();
                            }
                        }

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            loading=ProgressDialog.show(SignUp.this,"확인 진행 중 입니다",null,true,true);
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            loading.dismiss();
                            Log.d("duplicate_check_result",s);
                            if(s.equals("1")){
                                //중복이 없을경우
                                duplicateCheck=true;
                                Toast.makeText(SignUp.this, "사용가능한 아이디 입니다", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                //중복일 경우
                                Toast.makeText(SignUp.this, "사용불가능한 아이디 입니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    DuplicateCheck check=new DuplicateCheck();
                    check.execute(id_et.getText().toString());
                }
            }
        });

    }
}
