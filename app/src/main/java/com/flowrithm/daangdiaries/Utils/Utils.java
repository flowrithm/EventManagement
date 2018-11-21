package com.flowrithm.daangdiaries.Utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.flowrithm.daangdiaries.Activity.MainActivity;
import com.flowrithm.daangdiaries.R;
import com.google.firebase.messaging.RemoteMessage;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Utils {

    @TargetApi(Build.VERSION_CODES.M)
    public static KProgressHUD ShowDialog(Context context) {
        final KProgressHUD progressDialog = new KProgressHUD(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setWindowColor(Color.parseColor("#1D89E4"))
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        /*GifImageView imageView = new GifImageView(context);
        GifDrawable gifFromAssets = null;
        GifDrawable gifFromResource = null;
        try {
            gifFromAssets = new GifDrawable(context.getResources(), R.drawable.loader);
            imageView.setImageDrawable(gifFromAssets);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final KProgressHUD progressDialog = new KProgressHUD(context)
                .setCancellable(false)
                .setCustomView(imageView)
                .show();*/
        return progressDialog;
    }

    public static void OpenWhatsapp(Context context, String Contact) {

    }

    public static void OpenGoogleMap(Context context, String address) {
        String map = "http://maps.google.co.in/maps?q=" + address;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void OpenGoogleMap(Context context, double lat, double lon) {
//        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat, lon);
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//        context.startActivity(intent);
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=loc:" + lat + "," + lon));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Only if initiating from a Broadcast Receiver
        context.startActivity(i);
    }

    public static void OpenDialerScreen(Context context, String Number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + Number));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void OpenMessage(Context context, String Number) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + Number));
        intent.putExtra("sms_body", "Test text...");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void OpenMail(Context context, String Email) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Email});
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "Send mail..."));
    }

    public static void OpenBrowser(Context context, String link) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(link));
            context.startActivity(i);
        } catch (Exception ex) {
            Log.e("Exception", ex.getMessage());
        }
    }

    public static int CheckPermission(Context context) {
        int GpsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int TelephonyPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        int Storage = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        int Camera=ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA);
        if (GpsPermission == PackageManager.PERMISSION_GRANTED && TelephonyPermission == PackageManager.PERMISSION_GRANTED
                && Storage == PackageManager.PERMISSION_GRANTED) {
            return PackageManager.PERMISSION_GRANTED;
        } else {
            return PackageManager.PERMISSION_DENIED;
        }
    }

//    public static void OpenContactCreate(Context context, MPersonDirectory person) {
//        Intent intent = new Intent(Intent.ACTION_INSERT);
//        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
//        intent.putExtra(ContactsContract.Intents.Insert.NAME, person.getName());
//        if (!person.getMobile1().toString().equals("")) {
//            intent.putExtra(ContactsContract.Intents.Insert.PHONE, person.getMobile1());
//        }
//        if (!person.getMobile2().toString().equals("")) {
//            intent.putExtra(ContactsContract.Intents.Insert.PHONE, person.getMobile2());
//        }
//        if (!person.getEmail1().toString().equals("")) {
//            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, person.getEmail1());
//        }
//        context.startActivity(intent);
//    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public static void sendNotification(Context context, RemoteMessage message) {
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
//        String messageBody = message.getNotification().getBody();
//        Map<String, String> messageHash = message.getData();
//        Intent intent = null;
//
//        if (messageHash.get("NotificationType").equals("CallAssigned")) {
//            intent = new Intent(context, CallAssignList.class);
//        }else if(messageHash.get("NotificationType").equals("CallReceived")) {
//            intent = new Intent(context, Home.class);
//        }else if(messageHash.get("NotificationType").equals("Feedback")) {
//            intent = new Intent(context, FeedbackList.class);
//        }
//
////        if(messageHash.get("NotificationType").equals("SalesSupport")) {
////            if(pref.getInt("Role",0)== Role.SALES_EXECUTIVE || pref.getInt("Role",0)==Role.RM){
////                intent = new Intent(context, ExecutiveSalesSupportList.class);
////            }else if(pref.getInt("Role",0)== Role.MANAGMENT ||pref.getInt("Role",0)== Role.SALES_SUPPORT){
////                intent = new Intent(context, SalesSupportList.class);
////            }
////        }else if(messageHash.get("NotificationType").equals("LimitApproval")){
////            if(pref.getInt("Role",0)== Role.SALES_EXECUTIVE || pref.getInt("Role",0)==Role.RM){
////                intent = new Intent(context, RequestApprovalList.class);
////            }else if(pref.getInt("Role",0)== Role.MANAGMENT ||pref.getInt("Role",0)== Role.SALES_SUPPORT){
////                intent = new Intent(context, LimitApprovalList.class);
////            }
////        }else if(messageHash.get("NotificationType").equals("WeeklyPlan")){
////            if(pref.getInt("Role",0)== Role.SALES_EXECUTIVE ){
////                intent = new Intent(context, SalesPlanList.class);
////            }else if(pref.getInt("Role",0)== Role.MANAGMENT ||pref.getInt("Role",0)== Role.SALES_SUPPORT ){
////                intent = new Intent(context, ManagementSalesPlanList.class);
////            }else if(pref.getInt("Role",0)== Role.RM ){
////                if(messageHash.get("Open").equals("Create")) {
////                    intent = new Intent(context, SalesPlanList.class);
////                }else{
////                    intent = new Intent(context, ManagementSalesPlanList.class);
////                }
////            }
////        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        int color = ContextCompat.getColor(context, R.color.colorPrimary);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))/*Notification icon image*/
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(message.getNotification().getTitle())
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setColor(color)
//                .setPriority(Notification.PRIORITY_HIGH)
//                .setContentIntent(pendingIntent);
//
//
//        NotificationManager notificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }

    public static Point GetScreenSize(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();

        display.getSize(size);
        return size;
    }

//    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {
//
//        BadgeDrawable badge;
//
//        // Reuse drawable if possible
//        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
//        if (reuse != null && reuse instanceof BadgeDrawable) {
//            badge = (BadgeDrawable) reuse;
//        } else {
//            badge = new BadgeDrawable(context);
//        }
//
//        badge.setCount(count);
//        icon.mutate();
//        icon.setDrawableByLayerId(R.id.ic_badge, badge);
//    }

    public static void PickTimeInTextView(final Context context, final TextView txtDate) {

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {


                String AM_PM = " AM";
                String mm_precede = "";
                if (selectedHour >= 12) {
                    AM_PM = " PM";
                    if (selectedHour >= 13 && selectedHour < 24) {
                        selectedHour -= 12;
                    } else {
                        selectedHour = 12;
                    }
                } else if (selectedHour == 0) {
                    selectedHour = 12;
                }
                if (selectedMinute < 10) {
                    mm_precede = "0";
                }
                txtDate.setText("" + selectedHour + ":" + mm_precede + selectedMinute + AM_PM);
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public static void PickDateIntoTextView(final Context context, DatePickerDialog.OnDateSetListener listener, String MinDate, String MaxDate) {

        Calendar mincal = Calendar.getInstance();
        mincal.setTimeInMillis(0);
        if (!MinDate.equals("")) {
            java.util.Date date = getDateFromString(MinDate);
            mincal.setTime(date);
        } else {
            mincal.setTime(new Date());
        }
        int minday = mincal.get(Calendar.DAY_OF_MONTH);
        int minmonth = mincal.get(Calendar.MONTH);
        int minyear = mincal.get(Calendar.YEAR);

        Calendar maxcal = Calendar.getInstance();
        maxcal.setTimeInMillis(0);
        if (!maxcal.equals("")) {
            java.util.Date date = getDateFromString(MaxDate);
            maxcal.setTime(date);
        } else {
            maxcal.setTime(new Date());
        }
        int maxday = maxcal.get(Calendar.DAY_OF_MONTH);
        int maxmonth = maxcal.get(Calendar.MONTH);
        int maxyear = maxcal.get(Calendar.YEAR);


        DatePickerDialog datePicker = new DatePickerDialog(context,
                0,
                listener,
                minyear,
                minmonth,
                minday);
        datePicker.getDatePicker().setMinDate(mincal.getTimeInMillis());
        datePicker.getDatePicker().setMaxDate(maxcal.getTimeInMillis());
        datePicker.show();
    }

    public static void PickDateIntoTextView(final Context context, final TextView txtDate, String Date, boolean Previous) {

        DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                try {
                    String date = formatDate(selectedYear, selectedMonth, selectedDay);
                    txtDate.setText(date);
                } catch (Exception ex) {
                    ex.getMessage();
                }
            }
        };
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        if (!Date.equals("")) {
            java.util.Date date = getDateFromString(Date);
            cal.setTime(date);
        } else {
            cal.setTime(new Date());
        }
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        DatePickerDialog datePicker = new DatePickerDialog(context,
                0,
                datePickerListener,
                year,
                month,
                day);
//        DatePickerDialog datePicker = new DatePickerDialog(context,
//                0,
//                datePickerListener,
//                cal.get(Calendar.YEAR),
//                cal.get(Calendar.MONTH),
//                cal.get(Calendar.DAY_OF_MONTH));
        if (!Previous) {
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        } else {
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        }
        datePicker.show();
    }

    public static void PickDateIntoTextView(final Context context, final TextView txtDate, String Date) {

        DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                try {
                    String date = formatDate(selectedYear, selectedMonth, selectedDay);
                    txtDate.setText(date);
                } catch (Exception ex) {
                    ex.getMessage();
                }
            }
        };
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        if (!Date.equals("")) {
            java.util.Date date = getDateFromString(Date);
            cal.setTime(date);
        } else {
            cal.setTime(new Date());
        }
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        DatePickerDialog datePicker = new DatePickerDialog(context,
                0,
                datePickerListener,
                year,
                month,
                day);
        datePicker.show();
    }

    public static void PickDateIntoTextView(final Context context, final TextView txtDate, String Date, boolean Previous, int Days) {

        DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                try {
                    String date = formatDate(selectedYear, selectedMonth, selectedDay);
                    txtDate.setText(date);
                } catch (Exception ex) {
                    ex.getMessage();
                }
            }
        };
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        if (!Date.equals("")) {
            java.util.Date date = getDateFromString(Date);
            cal.setTime(date);
        } else {
            cal.setTime(new Date());
        }
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        DatePickerDialog datePicker = new DatePickerDialog(context,
                0,
                datePickerListener,
                year,
                month,
                day);
        if (!Previous) {
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        } else {
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        }
        datePicker.show();
    }

    public static void PickDateIntoTextView(final Context context, DatePickerDialog.OnDateSetListener listener, final TextView txtDate, String Date, boolean Previous) {
//
//        DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
//            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
//                try {
//                    String date = formatDate(selectedYear, selectedMonth, selectedDay);
//                    txtDate.setText(date);
//                } catch (Exception ex) {
//                    ex.getMessage();
//                }
//            }
//        };
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        if (!Date.equals("")) {
            java.util.Date date = getDateFromString(Date);
            cal.setTime(date);
        } else {
            cal.setTime(new Date());
        }
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        DatePickerDialog datePicker = new DatePickerDialog(context,
                0,
                listener,
                year,
                month,
                day);
//        DatePickerDialog datePicker = new DatePickerDialog(context,
//                0,
//                datePickerListener,
//                cal.get(Calendar.YEAR),
//                cal.get(Calendar.MONTH),
//                cal.get(Calendar.DAY_OF_MONTH));
        if (!Previous) {
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        } else {
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        }
        datePicker.show();
    }

    public static Date getDateFromString(String Date) {
        java.util.Date date;
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            date = format.parse(Date);
        } catch (Exception ex) {
            date = new Date();
            ex.getMessage();
        }
        return date;
    }

    public static Date getDateFromStringFull(String Date) {
        java.util.Date date;
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            date = format.parse(Date);
        } catch (Exception ex) {
            date = new Date();
            ex.getMessage();
        }
        return date;
    }

    public static String ConvertDateFormate(Date date) {
        String Sdate = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            Sdate = format.format(date);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return Sdate;
    }

    public static String ConvertDateFormateReverse(Date date) {
        String Sdate = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Sdate = format.format(date);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return Sdate;
    }

    public static String ConvertTimeIn24(String Time) {

        DateFormat outputformat = new SimpleDateFormat("hh:mm aa");
        //Date/time pattern of desired output date
        DateFormat df = new SimpleDateFormat("hh:mm:ss");
        Date date = null;
        String output = null;
        try {
            //Conversion of input String to date
            date = df.parse(Time);
            //old date format to new date format
            output = outputformat.format(date);
            System.out.println(output);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return output;
    }

    public static String formatDate(int year, int month, int day) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, day);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

        return sdf.format(date);
    }

//    public static void PickDialogWithData(final Context context,final String[] items,final TextView txt) {
//        AlertDialog.Builder builder=new AlertDialog.Builder(context);
//        ListAdapter adapterlst=new ArrayAdapter<String>(context,R.layout.listrow,items){
//
//            class ViewHolder
//            {
//                TextView txtItem;
//            }
//
//            @NonNull
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                ViewHolder holder;
//                LayoutInflater inflater=(LayoutInflater)context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                if(convertView==null)
//                {
//                    convertView=inflater.inflate(R.layout.listrow,null);
//                    holder=new ViewHolder();
//                    holder.txtItem=(TextView)convertView.findViewById(R.id.lst_item);
//                    holder.txtItem.setText(items[position]);
//                    convertView.setTag(holder);
//                }
//                else
//                {
//                    holder=(ViewHolder) convertView.getTag();
//                }
//                holder.txtItem.setText(items[position]);
//                return convertView;
//            }
//        };
//        builder.setAdapter(adapterlst, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                txt.setText(items[which]);
//                dialog.dismiss();
//            }
//        });
////        builder.setItems(items, new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////                dialog.dismiss();
////            }
////        });
//        AlertDialog dialog=builder.create();
//        ListView lstview=dialog.getListView();
//        lstview.setDivider(new ColorDrawable(Color.BLACK));
//        lstview.setDividerHeight(3);
//        dialog.show();
//    }

//    public static boolean GetConnectivityStatus(Context context) {
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        if (null != activeNetwork) {
//            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
//                return true;
//
//            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
//                return true;
//        }
//        Toast.makeText(context,R.string.Error_Network_Not_Reachable,Toast.LENGTH_SHORT).show();
//        return false;
//    }

    public static int CheckPhonePermission(Context context) {
        int GpsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        if (GpsPermission == PackageManager.PERMISSION_GRANTED) {
            return PackageManager.PERMISSION_GRANTED;
        } else {
            return PackageManager.PERMISSION_DENIED;
        }
    }

    public static int CheckGPSPermission(Context context) {
        int GpsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (GpsPermission == PackageManager.PERMISSION_GRANTED) {
            return PackageManager.PERMISSION_GRANTED;
        } else {
            return PackageManager.PERMISSION_DENIED;
        }
    }

    public static int CheckCameraPermission(Context context) {
        int GpsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (GpsPermission == PackageManager.PERMISSION_GRANTED) {
            return PackageManager.PERMISSION_GRANTED;
        } else {
            return PackageManager.PERMISSION_DENIED;
        }
    }

    public static void ShowServiceDialog(Context context, JSONObject json) {
        try {
            if (!json.getString("message").equals("")) {
                Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void sendNotification(Context context, RemoteMessage message) {
        Map<String, String> messageHash = message.getData();
        String ImagePath = messageHash.get("Image");
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Bitmap image=((BitmapDrawable)context.getDrawable(R.drawable.icon_app)).getBitmap();
        Bitmap image = null;
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_app))
                .setSmallIcon(R.mipmap.icon_app)
                .setContentTitle(message.getNotification().getTitle())
                .setContentText(messageHash.get("Message"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (!ImagePath.equals("")) {
            try {
                image = Picasso.with(context).load(ImagePath).get();
                notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(image));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void sendNewNotification(Context context, RemoteMessage message) {
//        String messageBody=message.getNotification().getBody();
        Map<String, String> messageHash = message.getData();
        String ImagePath = messageHash.get("Image");
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap image = null;

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            notificationBuilder = new NotificationCompat.Builder(context, context.getString(R.string.ChannelId));
            NotificationChannel mChannel = new NotificationChannel(context.getString(R.string.ChannelId), context.getString(R.string.ChannelName), NotificationManager.IMPORTANCE_HIGH);
            notificationBuilder.setChannelId(mChannel.getId());
            Notification notification = notificationBuilder.build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notificationManager.createNotificationChannel(mChannel);
        } else {
            notificationBuilder = new NotificationCompat.Builder(context);
        }
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_ticket));
        notificationBuilder.setSmallIcon(R.mipmap.icon_ticket);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
        notificationBuilder.setContentTitle(messageHash.get("Title"));
        notificationBuilder.setContentText(messageHash.get("Message"));
        notificationBuilder.setPriority(Notification.PRIORITY_MAX);
        notificationBuilder.setSound(defaultSoundUri);

        if(ImagePath!=null) {
            if (!ImagePath.equals("")) {
                try {
                    image = Picasso.with(context).load(ImagePath).get();
                    notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(image));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public static boolean CheckEmptyValidation(Context context, EditText text) {
        boolean ans = text.getText().toString().equals("");
        if (text.getText().toString().trim().equals("")) {
            text.setError("Mandatory");
            return false;
        }
        return true;
    }

    public static boolean CheckEmailValidation(Context context, EditText text) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!text.getText().toString().matches(emailPattern)) {
            text.setError("Invalid Email");
            return false;
        }
        return true;
    }

    public static void askForPermission(final Activity context, String[] permissions, Integer requestCode) {
//        for(String permission:permissions) {
//            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
//
//                // Should we show an explanation?
////            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
////                ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
////
////            } else {
//
//                ActivityCompat.requestPermissions(context,permissions, requestCode);
//                //         }
//            }
//        }
        ActivityCompat.requestPermissions(context, permissions, requestCode);
    }

    public static boolean IsMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            String servicename = service.service.getClassName();
            String classname = serviceClass.getName();
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void PickTimeIntoTextView(final Context context, final TextView txtTime, String Time) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String time = TimeFormatAMPM(hourOfDay, minute);
                        txtTime.setText(time);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public static String TimeFormatAMPM(int hours, int mins) {
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        return aTime;
    }

    public static String GetCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static String GetPreviousDate(int days) {
        days = -days;
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, days);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static Bitmap mark(Bitmap src, String watermark, Point location, int size) {

        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        Paint mTxtPaint = new Paint();
        Paint.FontMetrics fm = new Paint.FontMetrics();
        mTxtPaint.setColor(Color.BLACK);
        mTxtPaint.setTextSize(70.0f);

        mTxtPaint.getFontMetrics(fm);

        int margin = 5;

        canvas.drawRect(location.x - margin, location.y + fm.top, mTxtPaint.measureText(watermark) + 60, location.y + fm.bottom, mTxtPaint);
        mTxtPaint.setColor(Color.WHITE);

        canvas.drawText(watermark, location.x, location.y, mTxtPaint);
        return result;
    }
}
