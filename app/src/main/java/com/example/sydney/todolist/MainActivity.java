package com.example.sydney.todolist;

/* http://stackoverflow.com/questions/34579614/how-to-implement-recyclerview-in-a-fragment-with-tablayout */

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sydney.todolist.Fragments.DoneFragment;
import com.example.sydney.todolist.Fragments.TodayFragment;
import com.example.sydney.todolist.Fragments.TomorrowFragment;

import java.util.HashMap;
import java.util.Map;

import static com.example.sydney.todolist.R.id.viewpager;

public class MainActivity extends AppCompatActivity
                implements ViewPager.OnPageChangeListener {
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(viewpager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), MainActivity.this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }


        // Set default start tab to a certain tab
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Fragment fragment = pagerAdapter.getFragment(position);
        if (fragment != null) {
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
        TodayFragment page2 = (TodayFragment) page;
        page2.addTask(view);
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

        public View getTabView(int position) {
            View tab = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) tab.findViewById(R.id.custom_text);
            tv.setText(tabTitles[position]);
            return tab;
        }

        //@Override
        //public int getItemPosition(Object object) {
        //    return POSITION_NONE;
       // }
    }


    public ViewPager getViewPager() {
        return viewPager;
    }

}