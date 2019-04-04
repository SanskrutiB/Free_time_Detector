package com.example.sanskruti_ads;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Sanskruti_Show_Result extends AppCompatActivity {

    ListView listView;

    int threshold_value=5;
    ArrayList<String >result;

    String sorryMsg=" Sorry, Threshold is too high.";
    boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sanskruti__show__result);
        listView=findViewById(R.id.call_list);
        result=new ArrayList<>();
        Intent sans_i=getIntent();
        if(sans_i!=null){
            int arr[]=sans_i.getExtras().getIntArray("Result");
            threshold_value=sans_i.getIntExtra("Value",5);
            Log.e("Result value ",String.valueOf(threshold_value));
            if(arr.length>0){
                for(int i=0;i<arr.length-1;i++){
                    Log.e("Value ",i+" ,"+arr[i]);
                    if(arr[i]>threshold_value){
                        flag=true;
                        result.add("Sanskruti is busy between "+String.valueOf(i)+" to "+String.valueOf(i+1));
                    }
                }
                if(flag==false){
                    result.add(sorryMsg);
                }

                ArrayAdapter<String >data=new ArrayAdapter<>(Sanskruti_Show_Result.this,R.layout.activity_listview,result);
                listView.setAdapter(data);
            }

        }
    }
}
