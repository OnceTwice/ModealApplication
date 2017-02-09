package com.ff.modealapplication.app.ui.join;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.OwnerJoinService;
import com.ff.modealapplication.app.core.service.map.JoinMapInfoVo;
import com.ff.modealapplication.app.core.vo.ShopVo;
import com.ff.modealapplication.app.core.vo.UserVo;
import com.ff.modealapplication.app.ui.map.SearchShopToJoinActivity;
import com.ff.modealapplication.app.ui.map.SearchToShopInMapActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import static android.app.Activity.RESULT_OK;

public class OwnerJoinFragment extends Fragment {
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

    private EditText etMarketName;
    private Button btnMarketSearch;

    private EditText etMarketAddress;

    private EditText etMarketPhoneNumber;

    ImageView imageView;

    private EditText etMarketIntroduce;

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

    String marketName = "";
    String marketAddress = "";
    String marketNewAddress = "";
    String marketPhoneNumber = "";
    String imageURL = "";
    String marketIntroduce = "";
    String longitude = "";
    String latitude = "";

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
    public void onAttach(Context context) {
        super.onAttach(context);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {        // SearchShopToJoinActivity.java에서 보낸 데이터를 받아옴
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== RESULT_OK && requestCode == 1000) {
            Log.d("tttttttt", "bbbbbbbbb");
            JoinMapInfoVo joinMapInfoVo = (JoinMapInfoVo) data.getSerializableExtra("joinMapInfoVo");

            etMarketName.setText(joinMapInfoVo.getTitle());         // 매장명
            etMarketAddress.setText(joinMapInfoVo.getAddress());    // 매장주소
            marketNewAddress = joinMapInfoVo.getNewAddress();
            etMarketPhoneNumber.setText(joinMapInfoVo.getPhone());  // 매장전화번호
            imageURL = joinMapInfoVo.getImageUrl();
            longitude = joinMapInfoVo.getLongitude();
            latitude = joinMapInfoVo.getLatitude();

            Log.d("onActivityResult 이미지", imageURL);

            ImageLoader.getInstance().displayImage(imageURL, (ImageView)getView().findViewById(R.id.marketImage));
        } else {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {    // onCreate 후에 화면을 구성할 때 호출되는 부분
        View view = inflater.inflate(R.layout.fragment_owner_join, container, false);

        etID = (EditText) view.findViewById(R.id.etOwnerID);
        etPassword = (EditText) view.findViewById(R.id.etOwnerPassword);
        etPasswordConfirm = (EditText) view.findViewById(R.id.etOwnerPasswordConfirm);

        group = (RadioGroup) view.findViewById(R.id.radioGroupOwnerGender);
        man = (RadioButton) view.findViewById(R.id.OwnerRadio_man);
        woman = (RadioButton) view.findViewById(R.id.OwnerRadio_woman);

        etCity = (EditText) view.findViewById(R.id.etOwnerCity);
        etGu = (EditText) view.findViewById(R.id.etOwnerGu);
        etDong = (EditText) view.findViewById(R.id.etOwnerDong);

        etYear = (EditText) view.findViewById(R.id.etOwnerYear);
        spinnerMonth = (Spinner) view.findViewById(R.id.spOwnerMonth);
        etDay = (EditText) view.findViewById(R.id.etOwnerDay);

        etMarketName = (EditText) view.findViewById(R.id.etOwnerMarketName);
        btnMarketSearch = (Button) view.findViewById(R.id.btnOwnerAddressSearch);

        etMarketAddress = (EditText) view.findViewById(R.id.etOwnerMarketAddress);

        etMarketPhoneNumber = (EditText) view.findViewById(R.id.etOwnerMarketPhoneNumber);

        imageView = (ImageView) view.findViewById(R.id.marketImage);        // 이미지

        etMarketIntroduce = (EditText) view.findViewById(R.id.etOwnerMarketIntroduce);

        btnSubmit = (Button) view.findViewById(R.id.btnOwnerSubmit);
        btnCancel = (Button) view.findViewById(R.id.btnOwnerCancel);

        /********  성별입력(최초선택)    ********/
        if(man.isChecked()) {
            gender = "man";
            Log.d("사업자 젠더젠더12121212", gender);
        }
        else if(woman.isChecked()) {
            gender = "woman";
            Log.d("사업자 젠더젠더13131313", gender);
        }

        /********  성별입력(변경)    ********/
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.OwnerRadio_man : {
                        gender = "man";
                        Log.d("사업자 젠더젠더", gender);
                        break;
                    }
                    case R.id.OwnerRadio_woman : {
                        gender = "woman";
                        Log.d("사업자 젠더젠더", gender);
                        break;
                    }
                }
            }
        });

        /********   생년월일입력    ********/
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                month = (String)adapterView.getItemAtPosition(position);
//                Log.d("=========month", month);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("------>", "onNothingMonthSelected");
            }
        });

        /******************************************* 사용자 & 사업자 경계 **************************************************/

        btnMarketSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int[] flag = {0};
                String items[] = {"매장명으로 검색", "지도에서 직접 찾기"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Title");
                builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(getActivity(), i+"", Toast.LENGTH_SHORT).show();
                        flag[0] = i;
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(flag[0] == 0) {
                            Intent intentToMap = new Intent(OwnerJoinFragment.this.getActivity(), SearchShopToJoinActivity.class);
                            startActivityForResult(intentToMap, 1000);
                        } else if(flag[0] == 1) {
                            Toast.makeText(getActivity(), "다른 곳으로!!!", Toast.LENGTH_SHORT).show();
                            Intent intentToMap = new Intent(OwnerJoinFragment.this.getActivity(), SearchToShopInMapActivity.class);
                            startActivityForResult(intentToMap, 1000);
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Cancel 버튼 클릭 시
                        dialog.cancel();
                    }
                });
                builder.show();
            }

        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("사업자 회원가입", "제출!!!!!");

                // 아이디 입력 확인
                if( etID.getText().toString().length() == 0 ) {
                    Toast.makeText(OwnerJoinFragment.this.getActivity(), "사업자 ID를 입력하세요!", Toast.LENGTH_SHORT).show();
                    etID.requestFocus();
                    return;
                }

                // 비밀번호 입력 확인
                if( etPassword.getText().toString().length() == 0 ) {
                    Toast.makeText(OwnerJoinFragment.this.getActivity(), "사업자 비밀번호를 입력하세요!", Toast.LENGTH_SHORT).show();
                    etPassword.requestFocus();
                    return;
                }

                // 비밀번호 확인 입력 확인
                if( etPasswordConfirm.getText().toString().length() == 0 ) {
                    Toast.makeText(OwnerJoinFragment.this.getActivity(), "사업자 비밀번호 확인을 입력하세요!", Toast.LENGTH_SHORT).show();
                    etPasswordConfirm.requestFocus();
                    return;
                }

                // 비밀번호 일치 확인
                if( !etPassword.getText().toString().equals(etPasswordConfirm.getText().toString()) ) {
                    Toast.makeText(OwnerJoinFragment.this.getActivity(), "사업자 비밀번호가 일치하지 않습니다!", Toast.LENGTH_SHORT).show();
                    etPassword.setText("");
                    etPasswordConfirm.setText("");
                    etPassword.requestFocus();
                    return;
                }

                new FetchOwnerListAsyncTask().execute();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                Log.d("사업자 회원가입", "취소!!!!!");
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private class FetchOwnerListAsyncTask extends SafeAsyncTask<UserVo> {
        private UserVo userVo = new UserVo();
        private ShopVo shopVo = new ShopVo();

        @Override
        public UserVo call() throws Exception {
            Log.d("사업자 패치 메소드", "들어옴!!!!!");

            id = etID.getText().toString();
            password = etPassword.getText().toString();

            city = etCity.getText().toString()+"시 ";
            gu = etGu.getText().toString()+"구 ";
            dong = etDong.getText().toString()+"동 ";

            year = etYear.getText().toString();
            day = etDay.getText().toString();

            marketName = etMarketName.getText().toString();
            marketAddress = etMarketAddress.getText().toString();
            marketPhoneNumber = etMarketPhoneNumber.getText().toString();
            marketIntroduce = etMarketIntroduce.getText().toString();

            userVo.setId(id);
            userVo.setPassword(password);
            userVo.setGender(gender);
            userVo.setLocation(city+gu+dong);
            userVo.setBirth(year+month+day);
            userVo.setManagerIdentified(2L);     // 2 : 사업자 고유 번호

            Log.d("id======", id);
            Log.d("password======", password);

            Log.d("city======", city);
            Log.d("gu======", gu);
            Log.d("dong======", dong);

            Log.d("year======", year);
            Log.d("month======", month);
            Log.d("day======", day);

            shopVo.setName(marketName);
            shopVo.setAddress(marketAddress);
            shopVo.setNewAddress(marketNewAddress);
            shopVo.setPhone(marketPhoneNumber);
            shopVo.setPicture(imageURL);
            shopVo.setIntroduce(marketIntroduce);

            Log.d("marketName===", marketName);
            Log.d("marketAddress===", marketAddress);
            Log.d("marketNewAddress===", marketNewAddress);
            Log.d("marketPhoneNumber===", marketPhoneNumber);
            Log.d("imageURL===", imageURL);
            Log.d("marketIntroduce===", marketIntroduce);
            Log.d("longitude===", longitude);
            Log.d("latitude===", latitude);

            if(longitude == null) {
                shopVo.setLongitude(Double.parseDouble("0.0"));
            } else {
                shopVo.setLongitude(Double.parseDouble(longitude));
            }
            if(latitude == null) {
                shopVo.setLatitude(Double.parseDouble("0.0"));
            } else {
                shopVo.setLatitude(Double.parseDouble(latitude));
            }
            Log.d("longitude===", longitude);
            Log.d("latitude===", latitude);

            UserVo ownerVo = new OwnerJoinService().fetchOwnerList(userVo, shopVo);

            return ownerVo;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
            Log.d("사업자", "서비스 에러뜸!!!"+e);
            throw new RuntimeException(e);
        }

        @Override
        protected void onSuccess(UserVo userVo) throws Exception {
//            super.onSuccess(userVo);
            Log.d("사업자 성공", "성공해쓰요");
            getActivity().finish();
        }
    }
}
