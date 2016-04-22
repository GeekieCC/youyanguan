package com.gusteauscuter.youyanguan.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Z on 2016/4/16 0016.
 */
public class CalendarUtil {

    private Context mContext;
    private Uri mCalendarUri;

    private static final String calanderURL = "content://com.android.calendar/calendars";
    private static final String calanderEventURL = "content://com.android.calendar/events";
    private static final String calanderRemiderURL = "content://com.android.calendar/reminders";

    private static final String Calendars_NAME = "BooksCalendar";
    private static final String Calendars_ACCOUNT_NAME ="cc.geekie.wjw";
    private static final String Calendars_CALENDAR_DISPLAY_NAME = "还书提醒";
    private static final String Events_TITLE = "图书到期:";

    private static final int START_HOUR =7;
    private static final int END_HOUR =12;

    public CalendarUtil(Context context){
        mContext=context;
        mCalendarUri = Calendars.CONTENT_URI;
        mCalendarUri = mCalendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, Calendars_ACCOUNT_NAME)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, "LOCAL")
                .build();
    }

    /**
     * 添加提醒事件，按时间、内容添加
     */
    public void addEvent(int year, int month, int date, String title, String description){

        insertCalendar();

        Cursor userCursor =  mContext.getContentResolver().query(mCalendarUri, null, null, null, null);
        if(userCursor.getCount()==0)
            return;

        userCursor.moveToFirst();
        String calId = userCursor.getString(userCursor.getColumnIndex(Calendars._ID));

        Calendar tmpCalendar = Calendar.getInstance();
        tmpCalendar.set(Calendar.YEAR, year);
        tmpCalendar.set(Calendar.MONTH, month-1); // The month value is 0-based, so it may be clearer to use a constant like {@code JANUARY}.
        tmpCalendar.set(Calendar.DATE, date);
        tmpCalendar.set(Calendar.HOUR_OF_DAY,START_HOUR);
        tmpCalendar.set(Calendar.MINUTE,0);
        long start = tmpCalendar.getTimeInMillis();
        tmpCalendar.set(Calendar.HOUR_OF_DAY,END_HOUR);
        long end = tmpCalendar.getTimeInMillis();

        String timeZone=TimeZone.getDefault().toString();

        ContentValues eventValues = new ContentValues();
        eventValues.put(Events.CALENDAR_ID, calId);
        eventValues.put(Events.TITLE, Events_TITLE+title);
        eventValues.put(Events.DESCRIPTION, description);
        eventValues.put(Events.DTSTART, start);
        eventValues.put(Events.DTEND, end);
        eventValues.put(Events.HAS_ALARM, 1);
        eventValues.put(Events.EVENT_TIMEZONE, timeZone);

        Uri uri = mContext.getContentResolver().insert(Uri.parse(calanderEventURL), eventValues);

        long eventID = Long.parseLong(uri.getLastPathSegment());
        ContentValues reminderValues = new ContentValues();
        reminderValues.put(Reminders.MINUTES, 0);
        reminderValues.put(Reminders.EVENT_ID, eventID);
        reminderValues.put(Reminders.METHOD, Reminders.METHOD_ALERT);

        mContext.getContentResolver().insert(Uri.parse(calanderRemiderURL), reminderValues);
    }

    /**
     * 单账户模式，如果已存在此账户，则直接退出
     */
    public void insertCalendar(){

        Cursor userCursor =  mContext.getContentResolver().query(mCalendarUri, null, null, null, null);
        if(userCursor.getCount()>0)
            return;

        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(Calendars.NAME, Calendars_NAME);
        value.put(Calendars.ACCOUNT_NAME, Calendars_ACCOUNT_NAME);
        value.put(Calendars.OWNER_ACCOUNT, Calendars_ACCOUNT_NAME);
        value.put(Calendars.ACCOUNT_TYPE, "LOCAL");
        value.put(Calendars.CALENDAR_DISPLAY_NAME, Calendars_CALENDAR_DISPLAY_NAME);
        value.put(Calendars.VISIBLE, 1);
        value.put(Calendars.CALENDAR_COLOR, -9206951);
        value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        value.put(Calendars.SYNC_EVENTS, 1);
        value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(Calendars.CAN_ORGANIZER_RESPOND, 0);

        mContext.getContentResolver().insert(mCalendarUri, value);
    }

    public void deleteCalendar(){
        Cursor userCursor =  mContext.getContentResolver().query(mCalendarUri, null, null, null, null);
        if(userCursor.getCount()>0)
            mContext.getContentResolver().delete(mCalendarUri, null, null);
    }
}
