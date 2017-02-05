package jaewanlee.android_project.com.map_memo;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static jaewanlee.android_project.com.map_memo.MLRoundedImageView.getCroppedBitmap;


/**
 * Created by jaewan on 2017-01-16.
 */

public class Chat_room_adapter extends BaseAdapter {
    ArrayList<Chat_room_data> chat_room_datas=new ArrayList<>();
    Chat_room_data chat_room_data;
    String user_id;

    public Chat_room_adapter(String user_id){
        this.user_id=user_id;
    }
    @Override
    public int getCount() {
        return this.chat_room_datas.size();
    }

    @Override
    public Object getItem(int position) {
        return this.chat_room_datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int pos=position;
        Context parentContext=parent.getContext();
        chat_room_data=chat_room_datas.get(pos);
        ImageView imageView;
        TextView textView;
        TextView dateText;
        LinearLayout me_layout;
        LinearLayout other_layout;
        LinearLayout notice_layout;

        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)parentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.activity_chat_view_base,parent,false);
        }
        me_layout=(LinearLayout)convertView.findViewById(R.id.chat_me_layout_lv);
        other_layout=(LinearLayout)convertView.findViewById(R.id.chat_other_layout_lv);
        notice_layout=(LinearLayout)convertView.findViewById(R.id.chat_notice_lv);

        if(chat_room_data.getChat_user().equals("")){
            textView=(TextView)convertView.findViewById(R.id.chat_notice_tv);
            textView.setTypeface(Typeface.createFromAsset(parentContext.getAssets(), "bmhanna_11yrs_ttf.ttf"));
            textView.setText(chat_room_data.getChat_text());
            me_layout.setVisibility(View.INVISIBLE);
            other_layout.setVisibility(View.INVISIBLE);
            notice_layout.setVisibility(View.VISIBLE);
        }
        else{
            if(chat_room_data.getChat_user().equals(user_id)){
                imageView=(ImageView)convertView.findViewById(R.id.chat_me_profile_iv);
                textView=(TextView)convertView.findViewById(R.id.chat_me_text_tv);
                dateText=(TextView)convertView.findViewById(R.id.chat_me_date_tv);
                me_layout.setVisibility(View.VISIBLE);
                other_layout.setVisibility(View.INVISIBLE);
                notice_layout.setVisibility(View.INVISIBLE);
            }
            else{
                imageView=(ImageView)convertView.findViewById(R.id.chat_other_profile_iv);
                textView=(TextView)convertView.findViewById(R.id.chat_other_text_tv);
                dateText=(TextView)convertView.findViewById(R.id.chat_otherc_date_tv);
                me_layout.setVisibility(View.INVISIBLE);
                other_layout.setVisibility(View.VISIBLE);
                notice_layout.setVisibility(View.INVISIBLE);
            }
            //글꼴 설정하기
            textView.setTypeface(Typeface.createFromAsset(parentContext.getAssets(), "bmhanna_11yrs_ttf.ttf"));
            dateText.setTypeface(Typeface.createFromAsset(parentContext.getAssets(), "bmhanna_11yrs_ttf.ttf"));

            if(chat_room_data.getImg()==null){
                Bitmap bitmap= BitmapFactory.decodeResource(convertView.getResources(),R.drawable.default_user3);
                bitmap=getCroppedBitmap(bitmap,300);
                imageView.setImageBitmap(bitmap);
            }
            else if(!chat_room_data.getImg().equals("null")){
                String filePath="/data/data/jaewanlee.android_project.com.map_memo/files/";
                filePath+=chat_room_data.getImg();
                Bitmap bitmap= BitmapFactory.decodeFile(filePath);
                if(bitmap!=null){
                    bitmap=getCroppedBitmap(bitmap,300);
                    imageView.setImageBitmap(bitmap);
                }
            }
            else{
                Bitmap bitmap= BitmapFactory.decodeResource(convertView.getResources(),R.drawable.default_user3);
                bitmap=getCroppedBitmap(bitmap,300);
                imageView.setImageBitmap(bitmap);
            }
            textView.setText(chat_room_data.getChat_text());
            Date today=new Date(System.currentTimeMillis());
            String date=(new SimpleDateFormat("yyyy - MM - dd")).format(today);
            dateText.setText(date);
        }
        return convertView;

    }
    public void setData(ArrayList<Chat_room_data> chat_room_datas){
        this.chat_room_datas=chat_room_datas;
    }
    public void addData(Chat_room_data chat_room_data){
        this.chat_room_datas.add(chat_room_data);
    }
}
