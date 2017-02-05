package jaewanlee.android_project.com.map_memo;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jaewan on 2016-12-22.
 */

public class Category_view_Adapter extends BaseAdapter {

    ArrayList<Category_view_Data> category_view_datas=new ArrayList<>();
    Category_view_Data tempData;
    Context ctx;
    int lastPosition;
    public Category_view_Adapter(Context ctx){
        super();
        this.ctx=ctx;
        this.lastPosition=-1;
    }
    @Override
    public int getCount() {
        return category_view_datas.size();
    }

    @Override
    public Object getItem(int position) {
        return category_view_datas.get(position);
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
            convertView=inflater.inflate(R.layout.activity_category_listview,parent,false);
        }
        //뷰 내부에 쓰여질 내용들 가져오기
        TextView num=(TextView)convertView.findViewById(R.id.categoryView_num_textView);
        TextView title=(TextView)convertView.findViewById(R.id.categoryView_listTitle_textView);
        TextView addr=(TextView)convertView.findViewById(R.id.categoryView_addr_textView);

        num.setTypeface(Typeface.createFromAsset(ctx.getAssets(),"bmhanna_11yrs_ttf.ttf"));
        title.setTypeface(Typeface.createFromAsset(ctx.getAssets(),"bmhanna_11yrs_ttf.ttf"));
        addr.setTypeface(Typeface.createFromAsset(ctx.getAssets(),"bmhanna_11yrs_ttf.ttf"));
        tempData=category_view_datas.get(position);

        num.setText(String.valueOf(position+1));
        title.setText(tempData.getTitle());
        addr.setText(tempData.getAddress());

        return convertView;
    }
    public void setData(ArrayList<Category_view_Data> db){
        this.category_view_datas=db;
    }
    public int getDBId(int position){
        return this.category_view_datas.get(position).getId();
    }
    public void deleteDb(int position){
        this.category_view_datas.remove(position);
    }
}
