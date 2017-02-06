package com.ff.modealapplication.app.ui.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.ff.modealapplication.R;
import com.ff.modealapplication.app.ui.map.MainMapFragment;

public class MainFrameFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public MainFrameFragment() {
    }

    public static MainFrameFragment newInstance(String param1, String param2) {
        MainFrameFragment fragment = new MainFrameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private ViewPager viewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_frame, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.pager_main);
        viewPager.setAdapter(new PagerAdapter(getActivity().getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);

        // 처음으로 0번째 Fragment를 보여줌
        viewPager.setCurrentItem(0);

        pagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs_main);
        pagerSlidingTabStrip.setViewPager(viewPager);

        // Title 설정
        getActivity().setTitle("회원가입");

        // Inflate the layout for this fragment
        return view;
    }

    private String[] pageTitle = {"리스트", "지도"};

    private class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitle[position];
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new MainListFragment();
            } else if (position == 1) {
                return new MainMapFragment();
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
