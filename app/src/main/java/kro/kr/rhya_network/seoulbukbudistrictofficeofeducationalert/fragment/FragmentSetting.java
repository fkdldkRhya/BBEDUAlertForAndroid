package kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.R;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.activity.ActivityHome;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.activity.ActivitySplash;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.Application;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.DialogManager;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.RhyaSharedPreferences;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSetting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSetting extends Fragment {
    private RhyaSharedPreferences rhyaSharedPreferences;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam5;




    public FragmentSetting() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @param param3 Parameter 3.
     * @param param4 Parameter 4.
     * @param param5 Parameter 5.
     * @param param6 Parameter 6.
     * @param param7 Parameter 7.
     * @return A new instance of fragment FragmentMessage.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSetting newInstance(String param1, String param2, String param3,
                                              String param4, String param5, String param6, String param7) {
        FragmentSetting fragment = new FragmentSetting();
        Bundle args = new Bundle();
        args.putString(Application.FRAGMENT_ARG_PARAM1, param1);
        args.putString(Application.FRAGMENT_ARG_PARAM2, param2);
        args.putString(Application.FRAGMENT_ARG_PARAM3, param3);
        args.putString(Application.FRAGMENT_ARG_PARAM4, param4);
        args.putString(Application.FRAGMENT_ARG_PARAM5, param5);
        args.putString(Application.FRAGMENT_ARG_PARAM6, param6);
        args.putString(Application.FRAGMENT_ARG_PARAM7, param7);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(Application.FRAGMENT_ARG_PARAM1);
            mParam2 = getArguments().getString(Application.FRAGMENT_ARG_PARAM2);
            mParam5 = getArguments().getString(Application.FRAGMENT_ARG_PARAM5);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rhyaSharedPreferences = new RhyaSharedPreferences(requireContext());

        TextView nameForUserTextView = view.findViewById(R.id.nameForUserTextView);
        TextView founderForUserTextView = view.findViewById(R.id.founderForUserTextView);
        CheckBox noticeNoOffCheckbox = view.findViewById(R.id.noticeNoOffCheckbox);
        Button dataReset = view.findViewById(R.id.dataReset);
        Button accountLogOut = view.findViewById(R.id.accountLogOut);
        Button accountRemoveAccount = view.findViewById(R.id.accountRemoveAccount);

        final String NOTICE_ON_OFF_KEY = "_IS_NOTICE_SETTING_";
        final String NOTICE_ON_OFF_VALUE = rhyaSharedPreferences.getStringNoAES(NOTICE_ON_OFF_KEY);
        if (NOTICE_ON_OFF_VALUE.equals("true")) {
            noticeNoOffCheckbox.setChecked(true);
        }

        noticeNoOffCheckbox.setOnClickListener(v -> {
            if (noticeNoOffCheckbox.isChecked()) {
                rhyaSharedPreferences.setStringNoAES(NOTICE_ON_OFF_KEY, "true");
            }else {
                rhyaSharedPreferences.setStringNoAES(NOTICE_ON_OFF_KEY, "false");
            }
        });

        dataReset.setOnClickListener(v -> {
            StringBuilder stringBuilder;
            stringBuilder = new StringBuilder();
            // 파일 경로 생성
            stringBuilder.append(requireActivity().getFilesDir().getAbsolutePath());
            stringBuilder.append(File.separator);
            stringBuilder.append(getString(R.string.path_request_notification));
            final String REQUEST_NOTIFICATION_PATH = stringBuilder.toString();
            stringBuilder.setLength(0);
            stringBuilder.append(requireActivity().getFilesDir().getAbsolutePath());
            stringBuilder.append(File.separator);
            stringBuilder.append(getString(R.string.path_receive_notification));
            final String RESPONSE_NOTIFICATION_PATH = stringBuilder.toString();
            // 파일 제거
            final File REQUEST_NOTIFICATION = new File(REQUEST_NOTIFICATION_PATH);
            final File RESPONSE_NOTIFICATION = new File(RESPONSE_NOTIFICATION_PATH);
            if (REQUEST_NOTIFICATION.exists()) {
                REQUEST_NOTIFICATION.delete();

                ((FragmentViewSendMessage) ((FragmentMessage) ((ActivityHome) requireActivity()).messageFragment).fragmentViewSendMessage).isReload = true;
            }
            if (RESPONSE_NOTIFICATION.exists()) {
                RESPONSE_NOTIFICATION.delete();

                rhyaSharedPreferences.setStringNoAES("_RELOAD_REQUEST_MESSAGE_", "true");
            }
        });

        accountLogOut.setOnClickListener(v -> {
            DialogManager dialogManager = new DialogManager(requireActivity());
            dialogManager.setContentView(R.layout.dialog_select_2_for_logout);
            dialogManager.setCancelable(true);
            dialogManager.setBackground(null);
            dialogManager.setGravity(Gravity.BOTTOM);
            dialogManager.setDialogSizeWithCustomSize(1);
            dialogManager.dialog.findViewById(R.id.button1).setOnClickListener(v1 -> {
                FirebaseAuth.getInstance().signOut();
                GoogleSignInClient mGoogleSignInClient;
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.firebase_auth_web_client_id))
                        .requestEmail()
                        .requestProfile()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
                mGoogleSignInClient.signOut()
                        .addOnSuccessListener(unused -> {
                            dialogManager.dismissDialog();

                            Intent intent = new Intent(requireActivity(), ActivitySplash.class);
                            startActivity(intent);
                            requireActivity().overridePendingTransition(R.anim.slide_right_enter, R.anim.scale_small);
                            requireActivity().finish();
                        })
                        .addOnFailureListener(e -> {
                            dialogManager.dismissDialog();

                            Application.showToast(requireContext(), e.getMessage());
                        })
                        .addOnCanceledListener(() -> {
                            dialogManager.dismissDialog();

                            Application.showToast(requireContext(), "작업이 취소되었습니다.");
                        });
            });
            dialogManager.dialog.findViewById(R.id.button2).setOnClickListener(v1 -> dialogManager.dismissDialog());

            dialogManager.showDialog();
        });

        accountRemoveAccount.setOnClickListener(v -> {
            DialogManager dialogManager = new DialogManager(requireActivity());
            dialogManager.setContentView(R.layout.dialog_select_2_for_account_remove);
            dialogManager.setCancelable(true);
            dialogManager.setBackground(null);
            dialogManager.setGravity(Gravity.BOTTOM);
            dialogManager.setDialogSizeWithCustomSize(1);
            dialogManager.dialog.findViewById(R.id.button1).setOnClickListener(v1 -> {
                // 데이터 제거
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference deleteUserDataRef = database.getReference("/users/".concat(mParam5));

                deleteUserDataRef.removeValue()
                        .addOnSuccessListener(unused -> {
                            GoogleSignInClient mGoogleSignInClient;
                            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(getString(R.string.firebase_auth_web_client_id))
                                    .requestEmail()
                                    .requestProfile()
                                    .build();
                            mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
                            mGoogleSignInClient.signOut()
                                    .addOnSuccessListener(unused1 -> {
                                        dialogManager.dismissDialog();

                                        Intent intent = new Intent(requireActivity(), ActivitySplash.class);
                                        startActivity(intent);
                                        requireActivity().overridePendingTransition(R.anim.slide_right_enter, R.anim.scale_small);
                                        requireActivity().finish();
                                    })
                                    .addOnFailureListener(e1 -> {
                                        dialogManager.dismissDialog();

                                        Application.showToast(requireContext(), e1.getMessage());
                                    })
                                    .addOnCanceledListener(() -> {
                                        dialogManager.dismissDialog();

                                        Application.showToast(requireContext(), "작업이 취소되었습니다. CODE:1");
                                    });
                        })
                        .addOnFailureListener(e -> {
                            dialogManager.dismissDialog();

                            Application.showToast(requireContext(), e.getMessage());
                        })
                        .addOnCanceledListener(() -> {
                            dialogManager.dismissDialog();

                            Application.showToast(requireContext(), "작업이 취소되었습니다. CODE:2");
                        });
            });
            dialogManager.dialog.findViewById(R.id.button2).setOnClickListener(v1 -> dialogManager.dismissDialog());

            dialogManager.showDialog();
        });

        nameForUserTextView.setText(mParam2);
        founderForUserTextView.setText(mParam1);
    }
}