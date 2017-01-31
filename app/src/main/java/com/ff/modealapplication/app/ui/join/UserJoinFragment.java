package com.ff.modealapplication.app.ui.join;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.UserJoinService;
import com.ff.modealapplication.app.core.vo.UserVo;

import java.util.List;

public class UserJoinFragment extends Fragment {
    private UserJoinService userJoinService = new UserJoinService();

    private EditText etID;
    private EditText etPassword;
    private EditText etPasswordConfirm;

    private RadioGroup group;
    private RadioButton man;
    private RadioButton woman;

    private Spinner spinnerCity;
    private Spinner spinnerGu;
    private Spinner spinnerDong;

    private EditText etYear;
    private Spinner spinnerMonth;
    private EditText etDay;

    private Button btnSubmit;
    private Button btnCancel;

    String id = "";
    String password = "";
    String gender = "";

    String city = "";
    String gu = "";
    String dong = "";

    String year = "";
    String month = "";
    String day = "";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserJoinFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserJoinFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserJoinFragment newInstance(String param1, String param2) {
        UserJoinFragment fragment = new UserJoinFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {       // Fragment가 생성될 때 호출되는 부분
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {    // onCreate 후에 화면을 구성할 때 호출되는 부분
        View view = inflater.inflate(R.layout.fragment_user_join, container, false);

        etID = (EditText) view.findViewById(R.id.id);
        etPassword = (EditText) view.findViewById(R.id.etPassword);
        etPasswordConfirm = (EditText) view.findViewById(R.id.etPasswordConfirm);

        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);

        Log.d("아이디아이디", id);
        Log.d("비번비번", password);
        Log.d("녀녀녀녀년", year);
        Log.d("데이데이데이", day);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("111아이디아이디", id);
                Log.d("222비번비번", password);
                Log.d("333녀녀녀녀년", year);
                Log.d("444데이데이데이", day);

                new FetchUserListAsyncTask().execute();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private class FetchUserListAsyncTask extends SafeAsyncTask<List<UserVo>> {
        @Override
        public List<UserVo> call() throws Exception {
            id = etID.getText().toString();
            password = etPassword.getText().toString();

            year = etYear.getText().toString();
            day = etDay.getText().toString();

            Log.d("1111111111", id);
            Log.d("2222222222", password);
            Log.d("3333333333", year);
            Log.d("4444444444", day);


            List<UserVo> list = userJoinService.fetchUserList(id, password, gender, city+gu+dong, year+month+day);

            return list;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
            throw new RuntimeException(e);
        }

        @Override
        protected void onSuccess(List<UserVo> userVo) throws Exception {
//            super.onSuccess(userVo);
            Log.d("성공", "성공해쓰요");
        }
    }
}
