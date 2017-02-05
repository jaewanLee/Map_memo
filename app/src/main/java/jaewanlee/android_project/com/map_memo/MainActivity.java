package jaewanlee.android_project.com.map_memo;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    //layout에서 사용되는 view
    TextView title_tv;//메인 타이틀
    EditText id_et;//아이디 입력
    EditText password_et;//패스워드 입력
    Button login_bt;//로그인버튼
    TextView signup_tv;//회원가입
    TextView findid_tv;//아이디/비밀번호 찾기

    //facebook콜벨 메세지
    CallbackManager callbackManager;

    //인텐트 request값값
    int FACEBOOKLOGIN = 1;

    //자동로그인 체크
    SharedPreferences autoLogin;
    String token;//fcm에서 사용되는 토큰

    int checkResult;
    String filePath;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showContacts();

        autoLogin = getSharedPreferences("autologin", 0);
        Log.d("autoLogin", ((Boolean) autoLogin.getBoolean("autoLogin", false)).toString());
        if (autoLogin.getBoolean("autoLogin", false)) {
            Intent moveToHome = new Intent(getApplicationContext(), Home.class);
            moveToHome.putExtra("user_id", autoLogin.getString("id", ""));
            //fcm 초기화 및 토큰 날려주기
            FirebaseMessaging.getInstance().subscribeToTopic("notice");
            token = FirebaseInstanceId.getInstance().getToken();
            SendToken sendToken = new SendToken(autoLogin.getString("id", ""));
            sendToken.execute();
            startActivity(moveToHome);
        }


        //facebook sdk초기화
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        //KeyHash 얻기
        try {
            PackageInfo info = getPackageManager().getPackageInfo("jaewanlee.android_project.com.map_memo", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //view들 layout과 연결하기
        title_tv = (TextView) findViewById(R.id.main_title_textView);
        id_et = (EditText) findViewById(R.id.main_id_editText);
        password_et = (EditText) findViewById(R.id.main_pass_editText);
        login_bt = (Button) findViewById(R.id.main_login_button);
        signup_tv = (TextView) findViewById(R.id.main_signup_textView);
        findid_tv = (TextView) findViewById(R.id.main_findId_textView);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);


        //페이스북 로그인 버튼 설정
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        //페이스북 콜백 메세지
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            //성공시
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        //로그인 성공시 수행
                        Log.v("result", object.toString());
                        int checkResult = -1;//이미 회원 가입되어 있는지 체크
                        String user_no="";
                        String user_id = "";
                        try {
                            user_no=object.getString("id");
                            user_id = object.getString("email");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //여기서 이미지 다운 받자!
                        filePath=user_id+System.currentTimeMillis()+".png";
                        new DownLoadProfileImage(user_id).execute("https://graph.facebook.com/"+user_no+"/picture");
                        CheckAccount checkAccount = new CheckAccount(checkResult, object);
                        checkAccount.execute(user_id);
                    }
                });

                //facebook 로그인 성공시 해당하는 내용들을 보여줌
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }
            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException error) {
                Log.e("LoginErr", error.toString());
            }
        });

        //각 뷰들 폰트 적용하기
        title_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        id_et.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        login_bt.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        signup_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));
        findid_tv.setTypeface(Typeface.createFromAsset(getAssets(), "bmhanna_11yrs_ttf.ttf"));

        //패스워드 입력창 패스워드 형태로 변경
        password_et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password_et.setTransformationMethod(PasswordTransformationMethod.getInstance());

        //회원가입 페이지 이동
        signup_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveTosigup = new Intent(getApplicationContext(), SignUp.class);//회원가입 페이지로 연결
                startActivity(moveTosigup);
            }
        });

        //아이디,비밀번호 찾기 이동
        findid_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToFind = new Intent(getApplicationContext(), Find.class);//찾기 페이지로 연결
                startActivity(moveToFind);
            }
        });
        login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //아이디가 맞는지 확인하기
                //*********** 비밀번호는 아직 안했음요 ㅜㅜ
                CheckAccountWithLogin checkAccountWitheLogin=new CheckAccountWithLogin(password_et.getText().toString());
                checkAccountWitheLogin.execute(id_et.getText().toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    class SendToken extends AsyncTask<String, Void, String> {
        String user_id;
        public SendToken(String user_id) {
            this.user_id = user_id;
        }

        @Override
        protected String doInBackground(String... strings) {
            while (token == null) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("Token", token)
                    .add("user_id", user_id)
                    .build();
            //request
            Request request = new Request.Builder()
                    .url("http://allucanbuy.vps.phps.kr/map_memo/register.php")
                    .post(body)
                    .build();
            try {
                Response response=client.newCall(request).execute();
                Log.d("token register", token);
                Log.d("token result",response.toString());
                response.body().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    class CheckAccountWithLogin extends AsyncTask<String, Void, String>{
        int checkResult;//이미 가입되어 있는지 확인하는 변수
        String id = "";
        private String password="";

        public CheckAccountWithLogin(String password) {
            this.password=password;
        }

        public int getResult() {
            return this.checkResult;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                id = strings[0];
                //디비 주소
                String link = "http://allucanbuy.vps.phps.kr/map_memo/loginCheck.php";
                //데이터 보내기 위해 urlEncoder로 변경하기
                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                data +=  "&" +URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
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
                    break;
                }
                //결과 post로 보내기
                return sb.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("1")) {
                Toast.makeText(MainActivity.this, "아이디 또는 비밀번호를 다시 확인해 주십시오", Toast.LENGTH_SHORT).show();
            }
            else{
                //자동 로그인 설정
                SharedPreferences.Editor editor = autoLogin.edit();
                editor.putBoolean("autoLogin", true);
                editor.putString("id", this.id);
                editor.commit();
                Toast.makeText(MainActivity.this, "환영합니다.", Toast.LENGTH_SHORT).show();

                //인텐트로 이동 및 로그인 아이디 넘기기
                Intent moveToHome = new Intent(getApplicationContext(), Home.class);
                //페이스북 데이터를 확용할 수 있는 아이디임
                //                        moveToHome.putExtra("user_id",loginResult.getAccessToken().getUserId());
                moveToHome.putExtra("user_id", id);
                //fcm 초기화 및 토큰 날려주기
                FirebaseMessaging.getInstance().subscribeToTopic("notice");
                token = FirebaseInstanceId.getInstance().getToken();
                //        Log.d("token",token);
                SendToken sendToken = new SendToken(id);
                sendToken.execute();
                startActivity(moveToHome);
            }
        }
    }
    class CheckAccount extends AsyncTask<String, Void, String> {

        int checkResult;//이미 가입되어 있는지 확인하는 변수
        String id = "";
        JSONObject object;

        public CheckAccount(int checkResult, JSONObject object) {
            this.checkResult = checkResult;
            this.object = object;
        }

        public int getResult() {
            return this.checkResult;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                id = strings[0];
                //디비 주소
                String link = "http://allucanbuy.vps.phps.kr/map_memo/id_duplicate_check.php";
                //데이터 보내기 위해 urlEncoder로 변경하기
                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
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
                    break;
                }
                //결과 post로 보내기
                return sb.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("1")) {
                this.checkResult = 1;
                String name = "";
                try {
                    name = object.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String password = "tempPassword!@#";
//                            TelephonyManager telephonyManager=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//                            String phone=telephonyManager.getLine1Number();
                MakeAccount makeAccount = new MakeAccount();
                makeAccount.execute(id, name, password);
            }
            else{
                //자동 로그인 설정
                SharedPreferences.Editor editor = autoLogin.edit();
                editor.putBoolean("autoLogin", true);
                editor.putString("id", this.id);
                editor.commit();
                Toast.makeText(MainActivity.this, "환영합니다.", Toast.LENGTH_SHORT).show();

                //인텐트로 이동 및 로그인 아이디 넘기기
                Intent moveToHome = new Intent(getApplicationContext(), Home.class);
                //페이스북 데이터를 확용할 수 있는 아이디임
                //                        moveToHome.putExtra("user_id",loginResult.getAccessToken().getUserId());
                moveToHome.putExtra("user_id", id);
                //fcm 초기화 및 토큰 날려주기
                FirebaseMessaging.getInstance().subscribeToTopic("notice");
                token = FirebaseInstanceId.getInstance().getToken();
                //        Log.d("token",token);

                SendToken sendToken = new SendToken(id);
                sendToken.execute();
                startActivity(moveToHome);
            }
        }
    }

    class MakeAccount extends AsyncTask<String, Void, String> {
        String id = "";

        @Override
        protected String doInBackground(String... strings) {
            try {
                id = strings[0];
                String password = strings[2];
                String phone="";
                String photo=filePath;
                String name = strings[1];
                String type="1";//페이스북 회원가입의 타입은 1
                Date today=new Date(System.currentTimeMillis());
                String date=(new SimpleDateFormat("yyyy - MM - dd")).format(today);
                String update_date=String.valueOf(System.currentTimeMillis());

                Log.d("input", "id :" + id + "name :" + name + "password :" + password);
//                                        String phone=strings[3];
                //디비 주소
                String link = "http://allucanbuy.vps.phps.kr/map_memo/insert_DB.php";
                //데이터 보내기 위해 urlEncoder로 변경하기
                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                data += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                data += "&" + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8");
                data += "&" + URLEncoder.encode("photo", "UTF-8") + "=" + URLEncoder.encode(photo, "UTF-8");
                data += "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8");
                data += "&" + URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8");
                data += "&" + URLEncoder.encode("update", "UTF-8") + "=" + URLEncoder.encode(update_date, "UTF-8");

//              data+=URLEncoder.encode("phone","UTF-8")+"="+URLEncoder.encode(phone,"UTF-8");
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


                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//                                            if(s.equals("1")){
            //자동 로그인 설정
            SharedPreferences.Editor editor = autoLogin.edit();
            editor.putBoolean("autoLogin", true);
            editor.putString("id", this.id);
            editor.commit();
            Toast.makeText(MainActivity.this, "환영합니다.", Toast.LENGTH_SHORT).show();

            //인텐트로 이동 및 로그인 아이디 넘기기
            Intent moveToHome = new Intent(getApplicationContext(), Home.class);
            //페이스북 데이터를 확용할 수 있는 아이디임
            //                        moveToHome.putExtra("user_id",loginResult.getAccessToken().getUserId());
            moveToHome.putExtra("user_id", id);
            //fcm 초기화 및 토큰 날려주기
            FirebaseMessaging.getInstance().subscribeToTopic("notice");
            token = FirebaseInstanceId.getInstance().getToken();
            //        Log.d("token",token);

            SendToken sendToken = new SendToken(id);
            sendToken.execute();
            startActivity(moveToHome);
        }
    }

    //페이스북 아이디로 프로필 사진 받아오는 클래스, 인터넷에 접속해서 사진을 받아오기 때문에 asynctast로 진행함
    private class DownLoadProfileImage extends AsyncTask<String,String,Bitmap>{
        String user_id;
        public DownLoadProfileImage(String user_id){
            this.user_id=user_id;
        }
        @Override
        protected Bitmap doInBackground(String... args) {
            try {
                URL url=new URL(args[0]);
                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream inputStream=conn.getInputStream();
                Bitmap bm=BitmapFactory.decodeStream(inputStream);


                return bm;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            //비트맵을 해당위치에 저장하기

            FileOutputStream fos = null;
            try {
                fos = openFileOutput(filePath, 0);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
