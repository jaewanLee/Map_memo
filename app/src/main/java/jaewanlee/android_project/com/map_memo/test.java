package jaewanlee.android_project.com.map_memo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by jaewan on 2016-12-07.
 */

public class test  extends AppCompatActivity{

    EditText search;
    Button bt;
    AutoCompleteTextView autoCompleteTextView;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        search=(EditText)findViewById(R.id.test_search_EditTExt);
        bt=(Button)findViewById(R.id.test_button);
        autoCompleteTextView=(AutoCompleteTextView)findViewById(R.id.test_autocompleteTExt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        autoCompleteTextView.setAdapter(new PlacesAutoCompleteAdapter(this,R.layout.acivity_test2));
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String description=(String)parent.getItemAtPosition(position);
                Toast.makeText(test.this, description, Toast.LENGTH_SHORT).show();
            }
        });

    }
}