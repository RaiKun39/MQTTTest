package com.raikun.mqtttest;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class SwitchStatus extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.switch_status);

        String status = intent.getStringExtra("status");

        if (status.equals("On")) {
            Log.e("Status", status);
            views.setImageViewResource(R.id.status1, R.drawable.switch_on);
        } else {
            Log.e("Status", status);
            views.setImageViewResource(R.id.status1, R.drawable.switch_off);
        }

        appWidgetManager.updateAppWidget(new ComponentName(context,
                SwitchStatus.class), views);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.switch_status);



        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.switch_status);



    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}