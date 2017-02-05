package jaewanlee.android_project.com.map_memo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static jaewanlee.android_project.com.map_memo.MLRoundedImageView.getCroppedBitmap;

/**
 * Created by jaewan on 2017-01-11.
 */

public class Setting extends AppCompatActivity {

    //뷰초기화
    ImageButton back_ib;
    TextView main_tv;
    ImageButton profile_ib;
    TextView id_tv;
    TextView name_tv;
    TextView password_tv;
    TextView phone_tv;
    Button logOut_bt;
    Button memberLeaver_bt;

    String user_id;//사용자 아이디
    User user;//현재 사용자 정보
    String fileName;

    final static int GETIMAGE=1;//intent result에 사용되는 값, 갤러리로 부터 이미지 가져오기

    User_DB_Manager user_db_manager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //뷰연결
        back_ib=(ImageButton)findViewById(R.id.setting_back_ImageButton);
        main_tv=(TextView)findViewById(R.id.setting_title_textView);
        profile_ib=(ImageButton)findViewById(R.id.setting_profileImg_ImageButton);
        id_tv=(TextView)findViewById(R.id.setting_user_id_textView);
        name_tv=(TextView)findViewById(R.id.setting_name_textView);
        password_tv=(TextView)findViewById(R.id.setting_password_textView);
        phone_tv=(TextView)findViewById(R.id.setting_phone_textView);
        logOut_bt=(Button)findViewById(R.id.setting_log_out_Button);
        memberLeaver_bt=(Button)findViewById(R.id.setting_member_leave_button);
        //글꼴 설정
        main_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        id_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        name_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        password_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        phone_tv.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        logOut_bt.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));
        memberLeaver_bt.setTypeface(Typeface.createFromAsset(getAssets(),"bmhanna_11yrs_ttf.ttf"));

        //프로필 사진 설정

        //초기 인텐트 설정
        Intent getIntent=getIntent();
        user_id=getIntent.getStringExtra("user_id");

        //일반 디비에서 가져오기
        user_db_manager=new User_DB_Manager(getApplicationContext(),"USER.db",null,1);
        user=user_db_manager.getUserProfile(user_id);

        //디비로 초기화 설정
        //프로필 설정
        if(user.getPhoto().equals("null")){
            Bitmap bitmap= BitmapFactory.decodeResource(this.getResources(),R.drawable.default_user3);
            bitmap=getCroppedBitmap(bitmap,300);
            profile_ib.setImageBitmap(bitmap);
        }
        else{
            String filePath="/data/data/jaewanlee.android_project.com.map_memo/files/";
            filePath+=user.getPhoto();
            Bitmap bitmap=BitmapFactory.decodeFile(filePath);
            bitmap=getCroppedBitmap(bitmap,300);
            profile_ib.setImageBitmap(bitmap);
        }
        //아이디,이름,핸드폰번호 설정
        id_tv.setText(user.getUser_id());
        name_tv.setText(user.getName());
        phone_tv.setText(user.getPhone());

        //변경하기
        profile_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //갤러리에서 사진 불러오기
                Intent getImage = new Intent(Intent.ACTION_PICK);
                getImage.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(getImage, GETIMAGE);
            }
        });
        password_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveTopassChange=new Intent(getApplicationContext(),Password_Change.class);
                moveTopassChange.putExtra("user_id",user_id);
                startActivity(moveTopassChange);
            }
        });
        //뒤로가기 버튼
        back_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //로그아웃 버튼
        logOut_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences=getSharedPreferences("autologin", 0);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("autoLogin", false);
                editor.commit();
                Toast.makeText(Setting.this, "자동로그인에 해제 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        memberLeaver_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //우리쪽 디비 지우기+서버디비 지우기
                //노가다인데...냅두지뭐뭐
            }
       });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //이미지를 가져온뒤 처리하는 과정
        if (requestCode == GETIMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                String update_date=String.valueOf(System.currentTimeMillis());
                Uri mImageUri = data.getData();
                    int[] maxTexturesize = new int[1];
                    GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTexturesize, 0);
                    try {
                        //갤러리에서 가져온 이미지 옵션으로 크기 줄여서 가져오기
                        AssetFileDescriptor afd = getContentResolver().openAssetFileDescriptor(mImageUri, "r");
                        BitmapFactory.Options opt = new BitmapFactory.Options();
                        opt.inSampleSize = 4;//이게 커지면 더 커짐
                        Bitmap btm = BitmapFactory.decodeFileDescriptor(afd.getFileDescriptor(), null, opt);
                        btm=getCroppedBitmap(btm,300);
                        profile_ib.setImageBitmap(btm);
                        //bitmap 이미지 저장하기
                        fileName=user_id;
                        fileName+=String.valueOf(System.currentTimeMillis());
                        fileName+= ".png";
                    FileOutputStream fos = openFileOutput(fileName, 0);
                    btm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    Log.d("", "i/oexception");
                } catch (IndexOutOfBoundsException e) {
                    Log.d("somethig wrong!", "wrong!");
                } catch (SecurityException e) {
                    Log.d("w", "security");
                } catch (RuntimeException e) {
                    Log.d("somethig wrong!", "runtime!");
                }
                //내디비에서 사진 정보 가져오기
                String oldFile="/data/data/jaewanlee.android_project.com.map_memo/files/";
                String oldPhoto=user_db_manager.getUserPhoto(user_id);
                File f= new File(oldFile+oldPhoto);
                if(f.delete()){
                    Log.i("banana", "file remove = " + f.getName() + ", 삭제 성공"); }
                else { Log.i("banana", "file remove = " + f.getName() + ", 삭제 실패"); }

                //내디비에 업로드하기
                String query="update USER set photo='";
                query+=fileName+"',";
                query+="update_date ='"+update_date+"' where user_no=";
                query+=user.getUser_no()+";";
                user_db_manager.update(query);
                //서버에 사진 올리기
                String filePath="/data/data/jaewanlee.android_project.com.map_memo/files/";
                UpLoadImg upLoadImg=new UpLoadImg(filePath+fileName,update_date,oldFile);
                upLoadImg.execute();
            } else {
                Log.d("no resultCode", "no resultCode");
            }
        } else {
            Log.d("no requesetCode", "no requeset");
        }
    }
    class UpLoadImg extends AsyncTask<String,Void,String> {
        final private String upLoadServerUri = "http://allucanbuy.vps.phps.kr/map_memo/profile_img_upload.php";//서버컴퓨터의 ip주소
        private String filePath;//파일 위치
        private HttpURLConnection conn = null;//http connect
        private DataOutputStream dos = null;//쓰기
        private String lineEnd = "\r\n";//끝표시
        private String twoHyphens = "--";
        private String boundary = "*****";
        private int bytesRead, bytesAvailable, bufferSize;
        private byte[] buffer;
        private int maxBufferSize = 1 * 1024 * 1024;
        private int serverResponseCode = 0;
        String update_date;
        String oldPhoto;
        public UpLoadImg(String filePath,String update_date,String oldFile){
            this.filePath=filePath;
            this.update_date=update_date;
            this.oldPhoto=oldFile;
        }
        @Override
        protected String doInBackground(String... params) {

            File sourceFile = new File(filePath);
            if(!sourceFile.isFile()){
                Toast.makeText(Setting.this, "파일이 없습니다", Toast.LENGTH_SHORT).show();
            }
            else{
                // 서버 연결 설정
                try {
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(upLoadServerUri);
                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("fileName",fileName);
                    dos = new DataOutputStream(conn.getOutputStream());
                    //포스트로 넘길 텍스트 추가하기,유저 아이디
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"user_id\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(user_id + lineEnd);
                    //두번째 텍스트,파일 이름
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"fileName\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(fileName  + lineEnd);
                    //세번재 텍스트,업데이트 시기
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"update\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(update_date  + lineEnd);
                    //네번째 텍스트, 삭제해야할 이미지 이름
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"oldPhoto\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(oldPhoto  + lineEnd);
                    //파일전송
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + fileName + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();
                    Log.i("uploadFile", "HTTP Response is : "
                            + serverResponseMessage + ": " + serverResponseCode);
                    if(serverResponseCode == 200){
//                        Toast.makeText(getApplicationContext(), "File Upload Complete.",
//                                Toast.LENGTH_SHORT).show();
                        Log.d("result",serverResponseMessage);
                    }
                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                    conn.disconnect();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return String.valueOf(serverResponseCode);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
