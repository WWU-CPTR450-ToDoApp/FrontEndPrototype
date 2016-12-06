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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import android.support.v4.app.Fragment;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        // Settings Button
        Button btnOne = (Button) findViewById(R.id.settings_button);
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

        /*Resources resources = getResources();
        TabHost tabHost = getTabHost();

        // Done tab
        Intent intentDone = new Intent(this, DoneFragment.class);
        tabHost.addTab(tabHost.newTabSpec("done_fragment").setIndicator("Done",
                resources.getDrawable(R.drawable.icon_android_config)).setContent(intentDone));

        // Today tab
        Intent intentToday = new Intent(this, TodayFragment.class);
        tabHost.addTab(tabHost.newTabSpec("Today").setIndicator("Today",
                resources.getDrawable(R.drawable.icon_android_config)).setContent(intentToday));

        // Tomorrow tab
        Intent intentTomorrow = new Intent(this, TomorrowFragment.class);
        tabHost.addTab(tabHost.newTabSpec("Tomorrow").setIndicator("Tomorrow",
                resources.getDrawable(R.drawable.icon_android_config)).setContent(intentTomorrow));

        tabHost.setCurrentTab(1);*/

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        PagerAdapter pagerAdapter =
                new PagerAdapter(getSupportFragmentManager(), MainActivity.this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new PageChanger());

        // Give the TabLayout the ViewPager
        /*TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }*/

        // Set default start tab to a certain tab
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void addTask(View view) {
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
        TodayFragment page2 = (TodayFragment) page;
        page2.addTask(view);
    }
//    public void finishedTask(View view) {
//        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
//        DoneFragment page2 = (DoneFragment) page;
//        page2.finishedTask(view);
//    }
    public void addTomorrowTask(View view) {
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
        TomorrowFragment page2 = (TomorrowFragment) page;
        page2.addTask(view);
    }


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    class PageChanger implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onPageSelected(int position) {
            ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
            TextView textView = (TextView) findViewById(R.id.pageTitle);
            textView.setText(mViewPager.getAdapter().getPageTitle(mViewPager.getCurrentItem()));
        }
    }

    class PagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = new String[] { "Done", "Today", "Tomorrow" };
        Context context;

        //ViewPager mViewPager;

        /*public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
            mViewPager = (ViewPager) findViewById(R.id.viewpager);

            TextView textView = (TextView) findViewById(R.id.pageTitle);
            textView.setText(getPageTitle(mViewPager.getCurrentItem()));
        }
*/
        public PagerAdapter(FragmentManager fm, MainActivity context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new DoneFragment();
                case 1:
                    return new TodayFragment();
                case 2:
                    return new TomorrowFragment();
            }

            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }

        public View getTabView(int position) {
            View tab = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) tab.findViewById(R.id.custom_text);
            tv.setText(tabTitles[position]);
            return tab;
        }
    }
}