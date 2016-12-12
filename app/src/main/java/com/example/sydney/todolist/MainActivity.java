package com.example.sydney.todolist;

/* http://stackoverflow.com/questions/34579614/how-to-implement-recyclerview-in-a-fragment-with-tablayout */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sydney.todolist.Fragments.AbstractFragment;
import com.example.sydney.todolist.Fragments.DoneFragment;
import com.example.sydney.todolist.Fragments.TodayFragment;
import com.example.sydney.todolist.Fragments.TomorrowFragment;

import java.util.HashMap;

import static com.example.sydney.todolist.R.id.viewpager;

public class MainActivity extends AppCompatActivity
                implements ViewPager.OnPageChangeListener {
    ViewPager viewPager;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Settings Button
        ImageButton btnOne = (ImageButton) findViewById(R.id.settings_button);
        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(intent);
            }
        });

        //left arrow button press
        Button btnLeft = (Button) findViewById(R.id.button_left);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
                mViewPager.arrowScroll(View.FOCUS_LEFT);
            }
        });

        //right arrow button press
        Button btnRight = (Button) findViewById(R.id.button_right);
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
                mViewPager.arrowScroll(View.FOCUS_RIGHT);
            }
        });

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(viewpager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), MainActivity.this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);

        // Set default start tab to a certain tab
        viewPager.setCurrentItem(1);
        TextView textView = (TextView) findViewById(R.id.pageTitle);
        textView.setText(viewPager.getAdapter().getPageTitle(viewPager.getCurrentItem()));

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Fragment fragment = pagerAdapter.getFragment(position);
        if (fragment != null) {
            ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
            TextView textView = (TextView) findViewById(R.id.pageTitle);
            textView.setText(mViewPager.getAdapter().getPageTitle(mViewPager.getCurrentItem()));
            fragment.onResume();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void addTask(View view) {
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewpager + ":" + viewPager.getCurrentItem());
        AbstractFragment page2 = (AbstractFragment) page;
        page2.addTask(view);
    }

    public void statisticsToggle(View view) {

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
            DoneFragment page2 = (DoneFragment) page;
            page2.showStatistics(view);
        } else {
            //If stack no zero, press back button to remove statistics
            onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions_buttons, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_button:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    class PagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = new String[] { "Done", "Today", "Tomorrow" };
        Context context;
        private HashMap<Integer, String> mFragmentTags;
        private FragmentManager mFragmentManager;

        public PagerAdapter(FragmentManager fm, MainActivity context) {
            super(fm);
            this.context = context;
            mFragmentManager = fm;
            mFragmentTags = new HashMap<Integer, String>();
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object object = super.instantiateItem(container, position);
            if(object instanceof Fragment) {
                Fragment fragment = (Fragment) object;
                String tag = fragment.getTag();
                mFragmentTags.put(position, tag);
            }
            return object;
        }

        public Fragment getFragment(int position) {
            Fragment fragment = null;
            String tag = mFragmentTags.get(position);
            if(tag !=null) {
                fragment = mFragmentManager.findFragmentByTag(tag);
            }
            return fragment;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();

            switch (position) {
                case 0:
                    DoneFragment doneFragment = new DoneFragment();
                    bundle.putInt("position", position);
                    doneFragment.setArguments(bundle);
                    return doneFragment;
                    // DONE
                case 1:
                    TodayFragment todayFragment = new TodayFragment();
                    bundle.putInt("position", position);
                    todayFragment.setArguments(bundle);
                    return todayFragment;
                    // TODAY
                case 2:
                    TomorrowFragment tomorrowFragment = new TomorrowFragment();
                    bundle.putInt("position", position);
                    tomorrowFragment.setArguments(bundle);
                    return tomorrowFragment;
                    // TOMORROW
            }

            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }

    public ViewPager getViewPager() {
        return viewPager;
    }
}