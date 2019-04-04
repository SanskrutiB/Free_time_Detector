package com.example.sanskruti_ads;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.CallLog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button b1, b2, b3;
    private int mYear, mMonth, mDay,req,inc,out,mis,total,flag=0;
    TextView t1, t2,t3,t4;int[] arr= new int[24];
    Cursor c;static final int thresh=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        for(int i=0;i<24;i++){
            arr[i]=0;
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG},req);
        }
        else{
            b1 = findViewById(R.id.button1);
            b2 = findViewById(R.id.button2);
            b3 = findViewById(R.id.call);
            t1 = findViewById(R.id.date1);
            t2 = findViewById(R.id.date2);
            t4=findViewById(R.id.summary);
            b1.setOnClickListener(this);
            b2.setOnClickListener(this);
            t3=findViewById(R.id.details);
            b3.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Date date1=new Date() ,date2=new Date() ;
                    String d1 = t1.getText().toString();
                    String d2 = t2.getText().toString();
                    if(d1!=null && d2 !=null){

                        SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            date1=formatter2.parse(d1);
                            date2=formatter2.parse(d2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }}

                    if (d1 != "" && d2 != "" && date2.compareTo(date1)>=0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, req);
                        } else {
                            String[] projection = new String[]{
                                    CallLog.Calls._ID,
                                    CallLog.Calls.NUMBER,
                                    CallLog.Calls.DATE,
                                    CallLog.Calls.DURATION,
                                    CallLog.Calls.TYPE

                            };
                            c = getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null,
                                    null, CallLog.Calls.DATE + " DESC");
                            if (c.getCount() > 0) {
                                c.moveToFirst();
                                do {
                                    String callerID = c.getString(c.getColumnIndex(CallLog.Calls._ID));

                                    String callerNumber = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
                                    long callDateandTime = c.getLong(c.getColumnIndex(CallLog.Calls.DATE));
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                    SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm");
                                    String Dates = formatter.format(Long.parseLong(String.valueOf(callDateandTime)));
                                    Date date3=new Date();
                                    try {
                                        date3=formatter.parse(Dates);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    String time = formatter1.format(Long.parseLong(String.valueOf(callDateandTime)));
                                    assert time!=null;
                                    int hours=Integer.parseInt(time.split(":")[0]);

                                    long callDuration = c.getLong(c.getColumnIndex(CallLog.Calls.DURATION));
                                    int callType = c.getInt(c.getColumnIndex(CallLog.Calls.TYPE));
                                    if(date1.compareTo(date3)<=0 && date2.compareTo(date3)>=0) {
                                        if (callType == CallLog.Calls.INCOMING_TYPE) {
                                            inc++;
                                            // t3.append("\n\nINCOMING CALL:" + "\nCALL FROM:" + callerNumber + "\nCALL DATE:" + Dates + "\nCALL TIME:" + time + "\nCALL DURATION:" + callDuration+" sec");
                                        } else if (callType == CallLog.Calls.OUTGOING_TYPE) {
                                            out++;
                                            //  t3.append("\n\nOUTGOING CALL:" + "\nCALL FROM:" + callerNumber + "\nCALL DATE:" + Dates + "\nCALL TIME:" + time + "\nCALL DURATION:" + callDuration+" sec");

                                        } else if (callType == CallLog.Calls.MISSED_TYPE) {
                                            mis++;


                                            arr[hours]=arr[hours]+1;


                                            //   t3.append("\n\nMISSED CALL:" + "\nCALL FROM:" + callerNumber + "\nCALL DATE:" + Dates + "\nCALL TIME:" + Time + "\nCALL DURATION:" + callDuration+" sec");
                                        }
                                    }
                                } while (c.moveToNext());
                                total = inc + out + mis;
                                t4.setText("\nDISPLAYING LOGS FROM "+d1+" TO "+d2+"\nTOTAL NUMBER OF LOGS:  " + total + "\nNUMBER OF INCOMING CALLS:  " + inc + "\nNUMBER OF OUTGOING CALLS:  " + out + "\nNUMBER OF MISSED CALLS:  " + mis);
                            }
                        }
                    }

                    else{
                        Toast.makeText(getApplication(),"PLEASE CHECK FOR THE INPUT DATES",Toast.LENGTH_SHORT).show();

                    }
                }
            });
            // thresh=5

            int updatedarr[]=new int[24];
            int k=0;
            for(int j=0;j<24;j++){
                if(arr[j]>thresh) {
                    updatedarr[k++] = arr[j];
                }
            }

            Log.e("Sans", Arrays.toString(updatedarr));
        }

    }
    @Override
    public void onClick(View v) {
        if (v == b1) {

            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            t1.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == b2) {


            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            t2.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    }

}
