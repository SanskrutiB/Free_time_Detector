package com.example.sanskruti_ads;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.security.spec.ECField;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Read_Call_Logs extends AppCompatActivity {

    int missedCallsCount = 0, OutgoingCallCount = 0, IncomingCallsCount = 0;

    Button getdata;

    Button start,end;
    Calendar calendar;

    Button showdata;

    int option;

    int arr[]=new int[24];

    int threshold_value;
    int year,month,day;
    final int[] startdatearray = new int[3];
    final int[] enddatearray = new int[3];
    DatePickerDialog.OnDateSetListener startDateListener,endDateListener;

EditText thvalue;
    ArrayList<String> missedcallList=new ArrayList<>();
    ArrayList<String> outgoingcallList=new ArrayList<>();
    ArrayList<String> incomingcallList=new ArrayList<>();


    static int missedCallOption=1,outgoingCallOption=7,incomingCallOption=74;
    /*

    String strOrder = android.provider.CallLog.Calls.DATE + " DESC";

    Calendar calender = Calendar.getInstance();

    calender.set(2016, calender.NOVEMBER, 18);
    String fromDate = String.valueOf(calender.getTime());

    calender.set(2016, Calendar.NOVEMBER, 20);
    String toDate = String.valueOf(calender.getTimeInMillis());

    String[] whereValue = {fromDate,toDate};
     Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
      android.provider.CallLog.Calls.DATE + " BETWEEN ? AND ?", whereValue, strOrder);
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read__call__logs);


        getdata=findViewById(R.id.getData);
        showdata=findViewById(R.id.ShowData);

        thvalue=findViewById(R.id.thresold_Value);


        start=findViewById(R.id.startText);
        end=findViewById(R.id.endText);

        calendar=Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        day=calendar.get(Calendar.DAY_OF_MONTH);

        // initiliaze values
        for(int i=0;i<24;i++){
            arr[i]=0;
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(Read_Call_Logs.this,
                        R.style.Theme_AppCompat_DayNight_Dialog_Alert,startDateListener,year,month,day);
                datePickerDialog.show();
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(Read_Call_Logs.this,
                        R.style.Theme_AppCompat_DayNight_Dialog_Alert,endDateListener,year,month,day);
                datePickerDialog.show();

            }
        });


        startDateListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                startdatearray[0] =i;
                startdatearray[1]=i1+1;
                startdatearray[2]=i2;
            }
        };

        endDateListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                enddatearray[0]=i;
                enddatearray[1]=i1+1;
                enddatearray[2]=i2;
            }
        };


        getdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkForCallPermission()) {
                    //do the work
                    try{
                        Log.e("THRESHOLD VALUE",String.valueOf(Integer.parseInt(thvalue.getText().toString())));
//            if(TextUtils.isDigitsOnly(thvalue.toString()))
                        threshold_value=Integer.parseInt(thvalue.getText().toString());
                    }

                    catch (Exception e){
                        Toast.makeText(Read_Call_Logs.this, "Threshold value is not proper", Toast.LENGTH_SHORT).show();
                    }

                    performCount();

                } else {
                    int requestCode_Contacts = 555;
                    ActivityCompat.requestPermissions(Read_Call_Logs.this, new String[]{Manifest.permission.READ_CALL_LOG}, requestCode_Contacts);
                }
            }
        });

        showdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                option=1;
                Intent sans_i=new Intent(Read_Call_Logs.this,Sanskruti_Show_Result.class);
                sans_i.putExtra("Result",arr);
                sans_i.putExtra("Value",threshold_value);
                startActivity(sans_i);

            }
        });


    }



    private boolean checkForCallPermission() {
        int check = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);
        if (check == PackageManager.PERMISSION_GRANTED) {
            return true;
        }


        return false;

    }

    /* it will be called when the App tries to request the Permissions forcefully */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 555:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    performCount();
                }
            default:
                Toast.makeText(getApplicationContext(), "Wrong ID", Toast.LENGTH_SHORT).show();
        }
    }




    void performCount() {
        String sortingorder = CallLog.Calls.DATE + " DESC";
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            String selection=CallLog.Calls.DATE;

            Cursor cursor=getContentResolver().query(CallLog.Calls.CONTENT_URI,null,null,null,sortingorder);
            StringBuilder stringBuilder=new StringBuilder();

            int numberIndex=cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int typeNumberIndex=cursor.getColumnIndex(CallLog.Calls.TYPE);
            int dateIndex=cursor.getColumnIndex(CallLog.Calls.DATE);

            int count=cursor.getCount();
            //showing just first 10 values
            cursor.moveToFirst();

            Calendar calendar=Calendar.getInstance();


            calendar.set(Calendar.YEAR,startdatearray[0]);
            calendar.set(Calendar.MONTH,startdatearray[1]);
            calendar.set(Calendar.DAY_OF_MONTH,startdatearray[2]);

            Date specifiedstartDate=calendar.getTime();

            StringBuilder s=new StringBuilder();
            s.append("--Date:-- "+specifiedstartDate);
            Log.e("Specifed start Date",s.toString());

            Calendar calendar1=Calendar.getInstance();

            calendar1.set(Calendar.YEAR,enddatearray[0]);
            calendar1.set(Calendar.MONTH,enddatearray[1]);
            calendar1.set(Calendar.DAY_OF_MONTH,enddatearray[2]);

            Date specifiedendDate=calendar1.getTime();

            s=new StringBuilder();
            s.append("--Date:-- "+specifiedendDate);
            Log.e("Specifed end Date",s.toString());


            int c=count;
            missedCallsCount=0;
            OutgoingCallCount=0;
            IncomingCallsCount=0;

            while(c>0){
                Date date=new Date(Long.valueOf(cursor.getString(dateIndex)));
                if(date.after(specifiedstartDate) && date.before(specifiedendDate)){
                    stringBuilder=new StringBuilder();
                    stringBuilder.append("Number: "+cursor.getString(numberIndex)+"\n");
                    stringBuilder.append("Type: "+cursor.getString(typeNumberIndex)+"\n");

                    stringBuilder.append("Date: "+date);
                    SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm");
                    String dates1=formatter1.format(date);
                    int hours=0;
                    try{
                       hours =Integer.parseInt(dates1.split(":")[0]);
                    }
                    catch (NullPointerException e){
                        Log.e("SANSKRUTI","Not found ");
                    }

                    int callType=Integer.parseInt(cursor.getString(typeNumberIndex));
                    switch (callType){
                        case CallLog.Calls.MISSED_TYPE:
                            missedCallsCount+=1;
                            missedcallList.add(stringBuilder.toString());
                            arr[hours]=arr[hours]+1;
                            break;

                        case CallLog.Calls.OUTGOING_TYPE:
                            OutgoingCallCount+=1;
                            outgoingcallList.add(stringBuilder.toString());
                            break;

                        case CallLog.Calls.INCOMING_TYPE :
                            IncomingCallsCount+=1;
                            incomingcallList.add(stringBuilder.toString());
                            break;
                    }
                    //   Log.e("Result: ",stringBuilder.toString());
                }


                c--;
                cursor.moveToNext();
            }

            cursor.close();


            for(int i=0;i<23;i++){
                if(arr[i]>threshold_value){
                        String t="Sanskruti is Busy from "+String.valueOf(i)+" to "+String.valueOf(i+1);
                        Log.e("FREE TIME ",t);
                }
            }

            Log.e("Final Incoming: ",String.valueOf(IncomingCallsCount));
            Log.e("Final Missed: ",String.valueOf(missedCallsCount));
            Log.e("Final Outgoing: ",String.valueOf(OutgoingCallCount));

            AlertDialog.Builder dialog=new AlertDialog.Builder(this);
            dialog.setTitle("Call Logs");
//            dialog.setMessage("Missed Calls: "+String.valueOf(missedCallsCount)+"\n"+
//                    "Outgoing Calls:" +String.valueOf(OutgoingCallCount)+"\n"
//                    +"Incoming Calls: "+String.valueOf(IncomingCallsCount));

            dialog.setMessage("Total Missed Calls : "+String.valueOf(missedCallsCount));



            AlertDialog d=dialog.create();
            d.show();

            showdata.setVisibility(View.VISIBLE);







        }


    }
}
