package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import barqsoft.footballscores.widget.CollectionWidgetProvider;

public class MainActivity extends ActionBarActivity
{
    // This variable stores the match id of the selected item in the fragment
    public static int selected_match_id;
    // This variable stores the position of the selected item
    public static int selected_position;
    public static int current_fragment = 2;
    public static String LOG_TAG = "MainActivity";
    private final String save_tag = "Save Test";
    private PagerFragment my_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "Reached MainActivity onCreate");
        if (savedInstanceState == null) {
            // if position is set in intent then set the selected match id for the mainActivity. This is used to expand the corresponding
            //position when entering through widget. When the adapter is set to the scores list the selected match id and position are also set
            if (getIntent().getDoubleExtra(CollectionWidgetProvider.EXTRA_MATCH_ID,-1)!= -1) {
                double match_id = getIntent().getDoubleExtra(CollectionWidgetProvider.EXTRA_MATCH_ID, -1);
                selected_match_id = (int) match_id;
                selected_position = getIntent().getIntExtra(CollectionWidgetProvider.EXTRA_POSITION,0);
            }
            my_main = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, my_main)
                    .commit();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about)
        {
            Intent start_about = new Intent(this,AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.v(save_tag,"will save");
        Log.v(save_tag,"fragment: "+String.valueOf(my_main.mPagerHandler.getCurrentItem()));
        Log.v(save_tag,"selected id: "+selected_match_id);
        outState.putInt(getString(R.string.key_pager_current), my_main.mPagerHandler.getCurrentItem());
        outState.putInt(getString(R.string.key_selected_match),selected_match_id);
        outState.putInt(getString(R.string.key_selected_position),selected_position);
        // This makes sure that our my_main variable holds a reference to the pager fragment on configuration changes
        getSupportFragmentManager().putFragment(outState,"my_main",my_main);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.v(save_tag,"will retrive");
        Log.v(save_tag,"fragment: "+String.valueOf(savedInstanceState.getInt("Pager_Current")));
        Log.v(save_tag,"selected id: "+savedInstanceState.getInt("Selected_match"));
        current_fragment = savedInstanceState.getInt(getString(R.string.key_pager_current));
        selected_match_id = savedInstanceState.getInt(getString(R.string.key_selected_match));
        selected_position = savedInstanceState.getInt(getString(R.string.key_selected_position));
        my_main = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState,"my_main");
        super.onRestoreInstanceState(savedInstanceState);
    }
}
