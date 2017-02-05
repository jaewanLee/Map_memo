package jaewanlee.android_project.com.map_memo;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

/**
 * Created by jaewan on 2016-12-16.
 */

public class GetAreaData {
    int category1Position;
    String[] array;
    Context ctx;

    GetAreaData(Context ctx,int category1Position){
        this.ctx=ctx;
        this.category1Position=category1Position;
    }
    String[] getCategory2Array(){
        Resources res=ctx.getResources();
        TypedArray ta=res.obtainTypedArray(R.array.spinner);
        int arrayLength=ta.length();
        String [][] array=new String[arrayLength][];

        int id=ta.getResourceId(category1Position,0);
        if(id>0){
            array[category1Position]=res.getStringArray(id);
        }
        return res.getStringArray(id);
    }
}
