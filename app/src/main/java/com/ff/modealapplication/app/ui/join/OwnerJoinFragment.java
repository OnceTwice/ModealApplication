package com.ff.modealapplication.app.ui.join;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ff.modealapplication.R;
import com.ff.modealapplication.app.ui.map.SearchShopToJoinActivity;

import static android.app.Activity.RESULT_OK;

public class OwnerJoinFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OwnerJoinFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OwnerJoinFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OwnerJoinFragment newInstance(String param1, String param2) {
        OwnerJoinFragment fragment = new OwnerJoinFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_owner_join, container, false);
        Button map = (Button) view.findViewById(R.id.btnCallByMarket);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentToMap = new Intent(OwnerJoinFragment.this.getActivity(), SearchShopToJoinActivity.class);
                startActivityForResult(intentToMap, 1000);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("RESULT========>", "" + requestCode);
        Log.d("RESULT========>", "" + resultCode);
        //Log.d("RESULT========>", data.getStringExtra("joinMapInfoVo".toString()));

        if (requestCode == 1000 && resultCode == RESULT_OK) {

        }
    }
}
