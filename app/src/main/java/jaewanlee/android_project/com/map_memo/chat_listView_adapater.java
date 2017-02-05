package jaewanlee.android_project.com.map_memo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static jaewanlee.android_project.com.map_memo.MLRoundedImageView.getCroppedBitmap;

/**
 * Created by jaewan on 2017-01-04.
 */

public class chat_listView_adapater extends BaseAdapter {
    ArrayList<chat_listView_data> chat_listView_dataArrayList=new ArrayList<>();
    chat_listView_data tempData;
    Context ctx;

    public chat_listView_adapater(Context ctx){
        super();
        this.ctx=ctx;

    }
    @Override
    public int getCount() {
        return chat_listView_dataArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return chat_listView_dataArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void deleteData(int position){
        chat_listView_dataArrayList.remove(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int pos=position;
        Context parentContext=parent.getContext();
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)parentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.activity_chat_listview,parent,false);
        }
        ImageView imageView=(ImageView)convertView.findViewById(R.id.chatList_image_imageView);
        TextView title=(TextView)convertView.findViewById(R.id.chatList_title_textView);
        TextView text=(TextView)convertView.findViewById(R.id.chatList_text_textView);
//        TextView newNum=(TextView)convertView.findViewById(R.id.chatList_newNum_textView);

        //글꼴 설정하기
        title.setTypeface(Typeface.createFromAsset(parentContext.getAssets(), "bmhanna_11yrs_ttf.ttf"));
        text.setTypeface(Typeface.createFromAsset(parentContext.getAssets(), "bmhanna_11yrs_ttf.ttf"));

        tempData=chat_listView_dataArrayList.get(position);

        //이미지 세팅 다되면 하기
        if(!tempData.getImageViewAddr().equals("null")){
//            imageView.setImageBitmap(decodeSampledBitmapFromResource("data/data/jaewanlee.android_project.com.map_memo/files/"+tempData.getImageViewAddr(), 100, 100));
            String filePath="/data/data/jaewanlee.android_project.com.map_memo/files/";
            filePath+=tempData.getImageViewAddr();
            Bitmap bitmap= BitmapFactory.decodeFile(filePath);
            if(bitmap!=null){
                bitmap=getCroppedBitmap(bitmap,300);
                imageView.setImageBitmap(bitmap);
            }
        }
        else{
            Bitmap bitmap= BitmapFactory.decodeResource(convertView.getResources(),R.drawable.ic_default_place);
            bitmap=getCroppedBitmap(bitmap,300);
            imageView.setImageBitmap(bitmap);
        }
        title.setText(tempData.getMemoTitle());
        text.setText(tempData.getFriend());
//        newNum.setText(tempData.getNewChat());

        return convertView;
    }
    public void setData(ArrayList<chat_listView_data> chat_listView_dataArrayList){
        this.chat_listView_dataArrayList=chat_listView_dataArrayList;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static Bitmap decodeSampledBitmapFromResource(String res,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(res, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(res, options);
    }
}
