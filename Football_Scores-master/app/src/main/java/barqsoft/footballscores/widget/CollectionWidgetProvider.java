package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.service.myFetchService;

/**
 * Created by rose on 29/2/16.
 */
@TargetApi(14)
public class CollectionWidgetProvider extends AppWidgetProvider {
    public static final String EXTRA_POSITION = "position";
    public static final String EXTRA_MATCH_ID = "match_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        //If the actions set for broadcast intent is update we update the widget. Used by myFetchService to send a broadcast after insertion
        //of data is complete
        //Toast.makeText(context,"onReceive",Toast.LENGTH_SHORT).show();
        if(myFetchService.INTENT_ACTION_UPDATE_WIDGET.equals(intent.getAction())){
            ComponentName widget = new ComponentName(context.getPackageName(),CollectionWidgetProvider.class.getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.collection_widget_layout);
            appWidgetManager.updateAppWidget(widget,widgetView);
            //Toast.makeText(context,"onReceive Updating",Toast.LENGTH_SHORT).show();
        }
        else super.onReceive(context,intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Refresh data in our database
        Intent fetchData = new Intent(context,myFetchService.class);
        context.startService(fetchData);
        for (int appWidgetID : appWidgetIds) {
            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.collection_widget_layout);
            /* Set the click on the widget header to open the main activity */
            Intent mainActivity = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,appWidgetID,mainActivity,0);
            widget.setOnClickPendingIntent(R.id.widget_heading, pendingIntent);

            Intent remoteViewService = new Intent(context,CollectionRemoteViewService.class);
            widget.setRemoteAdapter(R.id.widget_collection_list, remoteViewService);

            /* Set the intent template to be used by list items in the widget */
            Intent details = new Intent(context,MainActivity.class);
            PendingIntent pIntent =PendingIntent.getActivity(context,0,details,PendingIntent.FLAG_UPDATE_CURRENT);
            widget.setPendingIntentTemplate(R.id.widget_collection_list,pIntent);

            appWidgetManager.updateAppWidget(appWidgetID,widget);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.collection_widget_layout);
        Intent remoteViewService = new Intent(context,CollectionRemoteViewService.class);
        widget.setRemoteAdapter(R.id.widget_collection_list, remoteViewService);
        appWidgetManager.updateAppWidget(appWidgetId,widget);
    }
}
