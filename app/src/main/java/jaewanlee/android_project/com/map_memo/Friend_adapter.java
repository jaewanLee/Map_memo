package jaewanlee.android_project.com.map_memo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static jaewanlee.android_project.com.map_memo.MLRoundedImageView.getCroppedBitmap;

/**
 * Created by jaewan on 2017-01-13.
 */

public class Friend_adapter extends BaseAdapter {
    ArrayList<Friend> friend_dataArrayList=new ArrayList<>();
    Friend tempData;

    @Override
    public int getCount() {
        return friend_dataArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return friend_dataArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int pos=position;
        Context parentContext=parent.getContext();
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)parentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.activity_friend,parent,false);
        }
        ImageView friendImage=(ImageView)convertView.findViewById(R.id.friend_image_imageView);
        TextView name=(TextView)convertView.findViewById(R.id.friend_name_textView);

        tempData=friend_dataArrayList.get(pos);

        //이미지 세팅 다되면 하기
        if(!tempData.getPhoto().equals("null")){
            String filePath="/data/data/jaewanlee.android_project.com.map_memo/files/";
            filePath+=tempData.getPhoto();
            Bitmap bitmap= BitmapFactory.decodeFile(filePath);
            if(bitmap!=null){
                bitmap=getCroppedBitmap(bitmap,300);
                friendImage.setImageBitmap(bitmap);
            }
            else{
                bitmap=BitmapFactory.decodeResource(convertView.getResources(),R.drawable.default_user3);
                bitmap=getCroppedBitmap(bitmap,300);
                friendImage.setImageBitmap(bitmap);
            }
        }
        else{
            Bitmap bitmap=BitmapFactory.decodeResource(convertView.getResources(),R.drawable.default_user3);
            bitmap=getCroppedBitmap(bitmap,300);
            friendImage.setImageBitmap(bitmap);
        }
        name.setText(tempData.getName());
        return convertView;
    }
    public void setData(ArrayList<Friend> friend_datas){
        this.friend_dataArrayList=friend_datas;
    }
    public void setData(Friend friend_data){
        this.friend_dataArrayList.add(friend_data);
    }
}
