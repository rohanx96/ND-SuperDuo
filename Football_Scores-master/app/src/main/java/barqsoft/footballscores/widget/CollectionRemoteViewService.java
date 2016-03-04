package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * This service is responsible for binding remote views to our list collection widget
 * Created by rose on 1/3/16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CollectionRemoteViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        // create a new adapter for the remote views
        return new ListRemoteViewsFactory(this);
    }

    /** This class binds remote views of the collection widget and the data */
    class ListRemoteViewsFactory implements RemoteViewsFactory{

        public static final int COL_MATCHTIME = 2;
        public static final int COL_HOME = 3;
        public static final int COL_AWAY = 4;
        public static final int COL_HOME_GOALS = 6;
        public static final int COL_AWAY_GOALS = 7;
        public static final int COL_ID = 8;
        Context mContext;
        Cursor mCursor;
        int mCount;
        public ListRemoteViewsFactory(Context context){
            super();
            this.mContext = context;
        }
        @Override
        public void onCreate() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String date = format.format(new Date(System.currentTimeMillis()));
            // Query the content provider with match data for today's date
            mCursor = mContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                    null,null,new String[]{date},null);
            Log.i("Widget","Loaded cursor");
            if (mCursor!=null)
                mCount = mCursor.getCount();
            else mCount = 0;
        }

        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory.
        @Override
        public void onDataSetChanged() {
            onCreate();
        }

        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        @Override
        public void onDestroy() {
            // Close the cursor to prevent memory leaks
            mCursor.close();
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            //move the cursor to the match item at the bind position
            mCursor.moveToPosition(position);
            // Create a remote view with the layout of widget item
            RemoteViews mHolder = new RemoteViews(mContext.getPackageName(), R.layout.collection_widget_item_layout);

            /* Bind the data corresponding to the match item to the remote view */
            mHolder.setTextViewText(R.id.home_name,mCursor.getString(COL_HOME));
            mHolder.setTextViewText(R.id.away_name,mCursor.getString(COL_AWAY));
            mHolder.setTextViewText(R.id.data_textview,mCursor.getString(COL_MATCHTIME));
            mHolder.setTextViewText(R.id.score_textview,
                    Utilies.getScores(mCursor.getInt(COL_HOME_GOALS), mCursor.getInt(COL_AWAY_GOALS),mContext));
            mHolder.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(mCursor.getString(COL_HOME)));
            mHolder.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(mCursor.getString(COL_AWAY)
            ));

            /* Set the click intent for the item at this position. On clicking the main activity is opened with the item expanded */
            Intent fillIntent = new Intent();
            fillIntent.putExtra(CollectionWidgetProvider.EXTRA_MATCH_ID,mCursor.getDouble(COL_ID));
            fillIntent.putExtra(CollectionWidgetProvider.EXTRA_POSITION,position);
            mHolder.setOnClickFillInIntent(R.id.collection_widget_list_item,fillIntent);

            return mHolder;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
