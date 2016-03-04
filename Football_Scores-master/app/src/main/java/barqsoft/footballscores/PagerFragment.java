package barqsoft.footballscores;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.widget.CollectionWidgetProvider;

/**
 * Created by yehya khaled on 2/27/2015.
 * This is the pager fragment that displays various fragments. It loads 5 different MainScreenFragment with different dates thus showing
 * matches for 5 different days
 */
public class PagerFragment extends Fragment
{
    public static final int NUM_PAGES = 5;
    public ViewPager mPagerHandler;
    private myPageAdapter mPagerAdapter;
    private MainScreenFragment[] viewFragments = new MainScreenFragment[5];
    private boolean isRTL = false;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        mPagerAdapter = new myPageAdapter(getChildFragmentManager());
        // Check if the current layout is right to left
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { //getLayoutDirection works for API level above 16
            isRTL = getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        }
        for (int i = 0;i < NUM_PAGES;i++)
        {
            // The date is set by adding/subtracting day time in milliseconds to get the next or previous date to today's date
            // For RTL layout the fragment dates are set in opposite direction (Tomorrow, Today, Yesterday) in place of (Yes, Tod, Tom)
            Date fragmentdate;
            if (isRTL)
                fragmentdate = new Date(System.currentTimeMillis()+((2-i)*86400000));
            else
                fragmentdate = new Date(System.currentTimeMillis()+((i-2)*86400000));
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
            viewFragments[i] = new MainScreenFragment();
            // If the selected match id has been set then set the same for the fragment
            //if (getArguments().getDouble(CollectionWidgetProvider.EXTRA_POSITION,-1)!= -1)
            //    viewFragments[i].setmSelectedMatchId(getArguments().getDouble(CollectionWidgetProvider.EXTRA_POSITION));
            viewFragments[i].setFragmentDate(mformat.format(fragmentdate));
        }
        mPagerHandler.setAdapter(mPagerAdapter);
        mPagerHandler.setCurrentItem(MainActivity.current_fragment);
        return rootView;
    }

    /** Adapter for the pagerView */
    private class myPageAdapter extends FragmentStatePagerAdapter
    {
        @Override
        public Fragment getItem(int i)
        {
            return viewFragments[i];
        }

        @Override
        public int getCount()
        {
            return NUM_PAGES;
        }

        public myPageAdapter(FragmentManager fm)
        {
            super(fm);
        }
        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position)
        {
            // For RTL layout day names are set in opposite direction
            if (isRTL)
                return getDayName(getActivity(),System.currentTimeMillis()+((2-position)*86400000));
            else
                return getDayName(getActivity(),System.currentTimeMillis()+((position-2)*86400000));
        }

        /** Returns the day name (today, tomorrow, yesterday or week day name) based on time set for position of fragment*/
        public String getDayName(Context context, long dateInMillis) {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.

            Time t = new Time();
            t.setToNow();
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            if (julianDay == currentJulianDay) {
                return context.getString(R.string.today);
            } else if ( julianDay == currentJulianDay +1 ) {
                return context.getString(R.string.tomorrow);
            }
             else if ( julianDay == currentJulianDay -1)
            {
                return context.getString(R.string.yesterday);
            }
            else
            {
                Time time = new Time();
                time.setToNow();
                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                return dayFormat.format(dateInMillis);
            }
        }
    }
}
