package com.ff.modealapplication.app.ui.join;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    private EditText etCity;
    private EditText etGu;
    private EditText etDong;

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

        etID = (EditText) view.findViewById(R.id.etUserId);
        etPassword = (EditText) view.findViewById(R.id.etUserPassword);
        etPasswordConfirm = (EditText) view.findViewById(R.id.etUserPasswordConfirm);

        group = (RadioGroup) view.findViewById(R.id.radioGroupUserGender);
        man = (RadioButton) view.findViewById(R.id.UserRadio_man);
        woman = (RadioButton) view.findViewById(R.id.UserRadio_woman);

        etCity = (EditText) view.findViewById(R.id.etUserCity);
        etGu = (EditText) view.findViewById(R.id.etUserGu);
        etDong = (EditText) view.findViewById(R.id.etUserDong);

        etYear = (EditText) view.findViewById(R.id.etYear);
        spinnerMonth = (Spinner) view.findViewById(R.id.month);
        etDay = (EditText) view.findViewById(R.id.etdDay);

        btnSubmit = (Button) view.findViewById(R.id.btnUserSubmit);
        btnCancel = (Button) view.findViewById(R.id.btnUserCancel);

        /********  성별입력(최초선택)    ********/
        if(man.isChecked()) {
            gender = "man";
            Log.d("젠더젠더12121212", gender);
        }
        else if(woman.isChecked()) {
            gender = "woman";
            Log.d("젠더젠더13131313", gender);
        }

        /********  성별입력(변경)    ********/
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio_man :
                        gender = "man";
                            Log.d("젠더젠더", gender);
                        break;
                    case R.id.radio_woman :
                        gender = "woman";
                            Log.d("젠더젠더", gender);
                        break;
                }
            }
        });

        /********   생년월일입력    ********/
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                month = (String)adapterView.getItemAtPosition(position);
//                Log.d("=========month", month);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("------>", "onNothingMonthSelected");
            }
        });

        Log.d("아이디아이디", id);
        Log.d("비번비번", password);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("보내기 버튼 클릭!!!", "클릭함!!!!!!");

                new FetchUserListAsyncTask().execute();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("click to back!!!!!", "취소!!!!!!!");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private class FetchUserListAsyncTask extends SafeAsyncTask<List<UserVo>> {
        @Override
        public List<UserVo> call() throws Exception {
            Log.d("11123123123123", "tqtqtqtqtqtqtqtq");

            id = etID.getText().toString();
            password = etPassword.getText().toString();

            city = etCity.getText().toString();
            gu = etGu.getText().toString();
            dong = etDong.getText().toString();

            year = etYear.getText().toString();
            day = etDay.getText().toString();

//            Log.d("타입확인", id.getClass().getName());

            Log.d("id======", id);
            Log.d("password======", password);

            Log.d("city======", city);
            Log.d("gu======", gu);
            Log.d("dong======", dong);

            Log.d("year======", year);
            Log.d("month======", month);
            Log.d("day======", day);

            List<UserVo> list = userJoinService.fetchUserList(id, password);

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
