package kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.BuildConfig;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.R;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.Application;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.DialogManager;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.NetworkChecker;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.RhyaSharedPreferences;

public class ActivitySplash extends AppCompatActivity {
    // Network checker
    private NetworkChecker networkChecker;
    // UI Object
    private ConstraintLayout appLogoConstraintLayout;
    private ConstraintLayout progressBarLayout;
    private TextView taskMessageTextView;
    // Google firebase login
    private GoogleSignInClient mGoogleSignInClient;
    // Activity Result Launcher 변수
    private ActivityResultLauncher<Intent> resultLauncher;
    // Dialog Manager
    private DialogManager dialogManagerForAddUser;
    // Firebase Aut 설정
    private FirebaseAuth mFirebaseAuth;
    // 뒤로가기 버튼 시간
    private long backBtnTime = 0;
    // Activity run check
    private boolean isHomeActivityRun = false;
    // Google login intent check
    private boolean isGoogleLoginIntentRun = false;
    // Delay check boolean
    private boolean isDelayedChecker = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        // UI Object setting
        appLogoConstraintLayout = findViewById(R.id.appLogoConstraintLayout);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        taskMessageTextView = findViewById(R.id.taskMessageTextView);


        // Activity result launcher 설정
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // 변수 변경
                        isGoogleLoginIntentRun = false;

                        Intent intent = result.getData();
                        // 로그인 확인
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                        handleSignInResult(task);
                    }else {
                        // 변수 변경
                        isGoogleLoginIntentRun = false;

                        // 네트워크 연결 확인
                        if (networkChecker.isNetworkConnection(getApplicationContext())) {
                            // Google firebase login request
                            googleSignTaskRequest();
                        }else {
                            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            finish();
                        } // if-else end
                    } // if-else end
                }); // listener end

        // Google firebase login 요청 정보 옵션
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_auth_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();

        // Initialize dialog manager
        dialogManagerForAddUser = new DialogManager(this);
        dialogManagerForAddUser.setContentView(R.layout.dialog_add_user);
        dialogManagerForAddUser.setCancelable(false);
        dialogManagerForAddUser.setBackground(null);
        dialogManagerForAddUser.setGravity(Gravity.BOTTOM);
        dialogManagerForAddUser.setDialogSizeWithCustomSize(1);

        // Initialize network checker
        networkChecker = new NetworkChecker();

        // UI Animation
        appLogoConstraintLayout.animate()
                .setStartDelay(500)
                .alpha(1f)
                .translationY(-50f)
                .setDuration(1000)
                .withEndAction(() -> {
                    // 변수 변경
                    isDelayedChecker = true;

                    // UI 보여 주기
                    appLogoConstraintLayout.setVisibility(View.VISIBLE);
                    progressBarLayout.setVisibility(View.VISIBLE);

                    // 다음 작업 실행
                    isAccessCheck();
                }) // listener end
                .start();


        RhyaSharedPreferences rhyaSharedPreferences = new RhyaSharedPreferences(this);
        final String NOTICE_ON_OFF_KEY = "_IS_NOTICE_SETTING_";
        final String NOTICE_ON_OFF_VALUE = rhyaSharedPreferences.getStringNoAES(NOTICE_ON_OFF_KEY);
        if (NOTICE_ON_OFF_VALUE.equals(rhyaSharedPreferences.DEFAULT_RETURN_STRING_VALUE)) {
            rhyaSharedPreferences.setStringNoAES(NOTICE_ON_OFF_KEY, "true");
        }
    }



    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if (0 <= gapTime && 2000 >= gapTime) {
            // 종료
            finish();
        }else {
            backBtnTime = curTime;
            Toast.makeText(getApplicationContext(), "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } // if-else end
    }


    @Override
    protected void onStart() {
        super.onStart();

        // 바로 실행 확인
        if (isDelayedChecker) {
            isAccessCheck();
        } // if end
    }



    /**
     * 권한 확인 성공 이후 작업
     */
    private void nextTask() {
        googleSignTaskRequest();
    }



    /**
     * Home activity 로 전환
     */
    private void startHomeActivity(String regKey, String token) {
        if (!isFinishing() && !isHomeActivityRun) {
            // 변수 변경
            isHomeActivityRun = true;

            // UI 변경
            taskMessageTextView.setText(getString(R.string.text_welcome));

            // 데이터 가져오기
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference getPrivateKeyRef = database.getReference("/private-key/key");
            getPrivateKeyRef.get()
                    .addOnCanceledListener(() -> {
                        // 작업 취소
                        dialogManagerForAddUser.showDialog();
                    }) // cancel-listener end
                    .addOnSuccessListener(unused -> {
                        // 작업 성공!

                        // 예외 처리
                        try {
                            // 데이터 추출
                            final String privateKey = (String) unused.getValue();
                            if (privateKey != null) {
                                // Reg key 변환
                                final String decryptRegKey = regKey.replace("-".concat(privateKey), "");
                                DatabaseReference getMainDataRef = database.getReference("/reg-key/".concat(decryptRegKey));
                                // 데이터 가져오기
                                getMainDataRef.get()
                                        .addOnCanceledListener(() -> {
                                            // 작업 취소
                                            dialogManagerForAddUser.showDialog();
                                        }) // cancel-listener end
                                        .addOnSuccessListener(unused2 -> {
                                            // 작업 성공!

                                            // 예외 처리
                                            try {
                                                // 데이터 추출
                                                @SuppressWarnings("unchecked")
                                                Map<String, Object> inputValue = (Map<String, Object>) unused2.getValue();
                                                if (inputValue != null) {
                                                    // 데이터
                                                    final String FOUNDER = (String) inputValue.get("founder");
                                                    final String NAME = (String) inputValue.get("name");
                                                    final String TYPE = (String) inputValue.get("type");

                                                    // Topics 데이터
                                                    final String TOPICS_NAME_1 = "Topics1";
                                                    final String TOPICS_NAME_2 = "Topics2";

                                                    // Topics 등록
                                                    if (TYPE != null) {
                                                        if (TYPE.equals(TOPICS_NAME_1)) {
                                                            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPICS_NAME_2);
                                                            FirebaseMessaging.getInstance().subscribeToTopic(TOPICS_NAME_1);
                                                        }else if (TYPE.equals(TOPICS_NAME_2)) {
                                                            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPICS_NAME_1);
                                                            FirebaseMessaging.getInstance().subscribeToTopic(TOPICS_NAME_2);
                                                        }
                                                    }

                                                    // 화면 전환
                                                    Intent intent = new Intent(this, ActivityHome.class);
                                                    intent.putExtra("founder", FOUNDER);
                                                    intent.putExtra("name", NAME);
                                                    intent.putExtra("type", TYPE);
                                                    intent.putExtra("regKey", regKey);
                                                    intent.putExtra("token", token);
                                                    intent.putExtra("private-key", privateKey);
                                                    intent.putExtra("decrypt-key", decryptRegKey);

                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.slide_right_enter, R.anim.scale_small);

                                                    finish();
                                                }else {
                                                    dialogManagerForAddUser.showDialog();
                                                }
                                            }catch (Exception ex) {
                                                ex.printStackTrace();

                                                dialogManagerForAddUser.showDialog();
                                            } // try-catch end
                                        }) // success-listener end
                                        .addOnFailureListener(e -> {
                                            // 오류 발생
                                            dialogManagerForAddUser.showDialog();
                                        }); // fail-listener end
                            }else {
                                dialogManagerForAddUser.showDialog();
                            } // if-else end
                        }catch (Exception ex) {
                            ex.printStackTrace();

                            dialogManagerForAddUser.showDialog();
                        } // try-catch emd
                    }) // success-listener end
                    .addOnFailureListener(e -> {
                        // 오류 발생
                        dialogManagerForAddUser.showDialog();
                    }); // fail-listener end/
        } // if end
    }



    /**
     * Google firebase 로그인 진행
     */
    private void googleSignTaskRequest() {
        // 변수 확인
        if(!isGoogleLoginIntentRun) {
            // UI 변경
            taskMessageTextView.setText(getString(R.string.text_check_login));

            // 변수 변경
            isGoogleLoginIntentRun = true;

            // 로그인 요청 Intent 시작
            Intent intent = mGoogleSignInClient.getSignInIntent();
            resultLauncher.launch(intent);
        } // if end
    }



    /**
     * Google firebase 로그인 여부 확인
     *
     * @param completedTask GoogleSignIn.getSignedInAccountFromIntent(<Intent>)
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            e.printStackTrace();

            // 오류 메시지 출력력
           Toast.makeText(getApplicationContext(), "ApiException : Google sign in Failed", Toast.LENGTH_SHORT).show();
           finish();
        } // try-catch end
    }



    /**
     * Firebase sign in
     *
     * @param acct GoogleSignInAccount
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // Firebase login
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 사용자 추가 확인
                        userAddChecker();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }); // listener end
    }



    /**
     * 안드로이드 권한 부여 확인
     */
    private void checkPermission() {
        // UI 변경
        taskMessageTextView.setText(getString(R.string.text_check_permission));

        // Android API 버전 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            TedPermission.create()
                    .setGotoSettingButton(false)
                    .setPermissionListener(permissionListener)
                    // Scoped Storage 대응
                    .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check();
        }else {
            TedPermission.create()
                    .setGotoSettingButton(false)
                    .setPermissionListener(permissionListener)
                    .setPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        } // if-else end
    }



    // Permission 요청 결과 리스너
    private final PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            // 권한 부여됨
            nextTask();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            // 권한 거부됨 - 오류 메시지 출력 및 종료
            Toast.makeText(getApplicationContext(), "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }; // listener end



    /**
     * 사용자 인증 확인
     */
    private void userAddChecker() {
        // UI object setting
        final ConstraintLayout progressBarLayout = dialogManagerForAddUser.dialog.findViewById(R.id.progressBarLayout);
        final TextInputLayout regNumLayout = dialogManagerForAddUser.dialog.findViewById(R.id.regNumLayout);
        final TextInputEditText regNumEditText = dialogManagerForAddUser.dialog.findViewById(R.id.regNumEditText);
        final Button button = dialogManagerForAddUser.dialog.findViewById(R.id.button);

        // UI 변경
        checkAddUserForDialog(regNumLayout, regNumEditText, button);
        taskMessageTextView.setText(getString(R.string.text_verifying_user_authentication));

        // EditText changed listener 설정
        regNumEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // UI 변경
                checkAddUserForDialog(regNumLayout, regNumEditText, button);
            }
        }); // listener end
        // Button listener
        button.setOnClickListener(v -> {
            // 네트워크 연결 확인
            if (!networkChecker.isNetworkConnection(getApplicationContext())) {
                // Toast 출력
                Application.showToast(getApplicationContext(), "인터넷 연결을 확인 후 다시 시도해 주세요.");

                // 종료
                return;
            } // if end

            // 데이터 입력 확인
            if (checkAddUserForDialog(regNumLayout, regNumEditText, button)) {
                // UI 설정 변경
                button.setEnabled(false);
                progressBarLayout.setVisibility(View.VISIBLE);
                regNumLayout.setVisibility(View.GONE);

                // 예외 처리
                try {
                    // 데이터 추출
                    String regNum = Objects.requireNonNull(regNumEditText.getText()).toString();
                    // Firebase token
                    String firebaseToken = FirebaseAuth.getInstance().getUid();
                    // Null 확인
                    if (firebaseToken != null) {
                        // Firebase connection
                        String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference("/users/".concat(firebaseToken));
                        // 토큰 생성
                        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                            Map<String, String> inputValue = new HashMap<>();
                            inputValue.put("token", token);
                            inputValue.put("key", regNum);
                            inputValue.put("email", email);
                            inputValue.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(new Date()));
                            // 사용자 데이터 생성
                            ref.setValue(inputValue)
                                    .addOnCanceledListener(() -> {
                                        // 작업 취소

                                        // UI 설정 변경
                                        regNumLayout.setError("작업이 취소되었습니다.");
                                        button.setEnabled(true);
                                        progressBarLayout.setVisibility(View.GONE);
                                        regNumLayout.setVisibility(View.VISIBLE);
                                    }) // cancel-listener end
                                    .addOnSuccessListener(unused -> {
                                        // 작업 성공!

                                        // UI 변경
                                        regNumLayout.setErrorEnabled(false);
                                        regNumEditText.setEnabled(false);

                                        // Dialog 닫기
                                        dialogManagerForAddUser.dismissDialog();

                                        // 화면 전환
                                        startHomeActivity(regNum, firebaseToken);
                                    }) // success-listener end
                                    .addOnFailureListener(e -> {
                                        // 오류 발생

                                        // UI 설정 변경
                                        regNumLayout.setError(e.getMessage());
                                        button.setEnabled(true);
                                        progressBarLayout.setVisibility(View.GONE);
                                        regNumLayout.setVisibility(View.VISIBLE);
                                    }); // fail-listener end
                        }); // listener end
                    } // if end
                }catch (Exception ex) {
                    // 예외 처리
                    ex.printStackTrace();

                    // UI 설정 변경
                    button.setEnabled(true);
                    progressBarLayout.setVisibility(View.GONE);
                    regNumLayout.setVisibility(View.VISIBLE);
                } // try-catch end
            } // if end
        }); // listener end


        isAddUser();
    }



    /**
     * Dialog 필수 입력 사항 UI 상태 확인 및 업데이트
     * @param regNumLayout 등록번호 Layout
     * @param regNumEditText 등록번호 Input
     * @param button Next button
     */
    private boolean checkAddUserForDialog(TextInputLayout regNumLayout,
                                          TextInputEditText regNumEditText,
                                          Button button) {
        // Dialog 입력 상태 가져오기
        boolean isRegNumInput = Objects.requireNonNull(regNumEditText.getText()).toString().length() > 0;

        // 버튼 상태 설정
        button.setEnabled(isRegNumInput);

        // Reg Num Layout 설정
        if (!isRegNumInput)
            regNumLayout.setError("등록번호를 입력해 주세요.");
        else
            regNumLayout.setErrorEnabled(false);

        return isRegNumInput;
    }



    /**
     * 사용자 인증 확인
     */
    private void isAddUser() {
        // 네트워크 연결 확인
        if (!networkChecker.isNetworkConnection(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            finish();
        } // if end

        // Firebase token
        String firebaseToken = FirebaseAuth.getInstance().getUid();
        // Null 확인
        if (firebaseToken != null) {
            // Firebase connection
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("/users/".concat(firebaseToken));
            // 토큰 생성
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                // 사용자 데이터 업데이트
                ref.get()
                        .addOnCanceledListener(() -> {
                            // 작업 취소
                            dialogManagerForAddUser.showDialog();
                        }) // cancel-listener end
                        .addOnSuccessListener(unused -> {
                            // 작업 성공!

                            // Dialog 닫기
                            dialogManagerForAddUser.dismissDialog();

                            // 예외 처리
                            try {
                                // 데이터 추출
                                @SuppressWarnings("unchecked")
                                Map<String, Object> inputValue = (Map<String, Object>) unused.getValue();
                                if (inputValue != null) {
                                    inputValue.put("token", token);
                                    inputValue.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(new Date()));
                                    ref.updateChildren(inputValue);

                                    // 화면 전환
                                    startHomeActivity((String) inputValue.get("key") ,firebaseToken);
                                }else {
                                    dialogManagerForAddUser.showDialog();
                                }
                            }catch (Exception ex) {
                                ex.printStackTrace();

                                dialogManagerForAddUser.showDialog();
                            } // try-catch emd
                        }) // success-listener end
                        .addOnFailureListener(e -> {
                            // 오류 발생
                            dialogManagerForAddUser.showDialog();
                        }); // fail-listener end/
            }); // listener end
        } // if end
    }



    /**
     * 앱 업데이트 확인
     */
    private void isUpdateChecker() {
        // 업데이트 실패 메시지
        final String UPDATE_FAIL_MESSAGE = "업데이트 확인 중 오류가 발생하였습니다.";

        // UI 변경
        taskMessageTextView.setText(getString(R.string.text_check_update));

        // Firebase connection
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("/app-info/version");
        ref.get()
                .addOnCanceledListener(() -> {
                    // 작업 취소

                    // Toast 출력
                    Application.showToast(getApplicationContext(), UPDATE_FAIL_MESSAGE);
                    // App 종료
                    finish();
                }) // cancel-listener end
                .addOnSuccessListener(unused -> {
                    // 작업 성공!

                    // 예외 처리
                    try {
                        // 데이터 추출
                        final String VERSION = (String) unused.getValue();
                        // Null 확인
                        if (VERSION != null) {
                            // 버전 확인
                            if (!updateChecker(VERSION)) {
                                // 업데이트 Dialog 초기화
                                DialogManager dialogManager = new DialogManager(this);
                                dialogManager.setContentView(R.layout.dialog_app_update);
                                dialogManager.setCancelable(false);
                                dialogManager.setBackground(null);
                                dialogManager.setGravity(Gravity.BOTTOM);
                                dialogManager.setDialogSizeWithCustomSize(1);
                                // Update dialog listener 설정

                                // Update dialog 출력
                                dialogManager.showDialog();
                            }else {
                                // 다음 작업 진행
                                checkPermission();
                            } // if-else end
                        }else {
                            // Toast 출력
                            Application.showToast(getApplicationContext(), UPDATE_FAIL_MESSAGE);
                            // App 종료
                            finish();
                        } // if-else end
                    }catch (Exception ex) {
                        ex.printStackTrace();

                        // Toast 출력
                        Application.showToast(getApplicationContext(), UPDATE_FAIL_MESSAGE);
                        // App 종료
                        finish();
                    } // try-catch emd
                }) // success-listener end
                .addOnFailureListener(e -> {
                    // 오류 발생

                    // Toast 출력
                    Application.showToast(getApplicationContext(), UPDATE_FAIL_MESSAGE);
                    // App 종료
                    finish();
                }); // fail-listener end/
    }



    /**
     * 앱 버전 확인
     * @param version 현재 버전
     * @return False -> 업데이트 필요 , True -> 최신 버전
     */
    public boolean updateChecker(String version) {
        return Integer.parseInt(BuildConfig.VERSION_NAME.replace(".", "")) >= Integer.parseInt(version.replace(".", ""));
    }



    private void isAccessCheck() {
        // 확인 실패 메시지
        final String ACCESS_CHECK_FAIL_MESSAGE = "접근 여부 확인 중 오류가 발생하였습니다.";

        // UI 변경
        taskMessageTextView.setText(getString(R.string.text_check_update));

        // Firebase connection
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("/app-info/access");
        ref.get()
                .addOnCanceledListener(() -> {
                    // 작업 취소

                    // Toast 출력
                    Application.showToast(getApplicationContext(), ACCESS_CHECK_FAIL_MESSAGE);
                    // App 종료
                    finish();
                }) // cancel-listener end
                .addOnSuccessListener(unused -> {
                    // 작업 성공!

                    // 예외 처리
                    try {
                        // 데이터 추출
                        final String ACCESS = (String) unused.getValue();
                        // Null 확인
                        if (ACCESS != null) {
                            // 버전 확인
                            if (!ACCESS.equals("true")) {
                                // Access black dialog 초기화
                                DialogManager dialogManager = new DialogManager(this);
                                dialogManager.setContentView(R.layout.dialog_access_block);
                                dialogManager.setCancelable(false);
                                dialogManager.setBackground(null);
                                dialogManager.setGravity(Gravity.BOTTOM);
                                dialogManager.setDialogSizeWithCustomSize(1);
                                // Access black dialog listener 설정
                                dialogManager.dialog.findViewById(R.id.button).setOnClickListener(v -> finish());
                                // Update dialog 출력
                                dialogManager.showDialog();
                            }else {
                                // 다음 작업 진행
                                isUpdateChecker();
                            } // if-else end
                        }else {
                            // Toast 출력
                            Application.showToast(getApplicationContext(), ACCESS_CHECK_FAIL_MESSAGE);
                            // App 종료
                            finish();
                        } // if-else end
                    }catch (Exception ex) {
                        ex.printStackTrace();

                        // Toast 출력
                        Application.showToast(getApplicationContext(), ACCESS_CHECK_FAIL_MESSAGE);
                        // App 종료
                        finish();
                    } // try-catch emd
                }) // success-listener end
                .addOnFailureListener(e -> {
                    // 오류 발생

                    // Toast 출력
                    Application.showToast(getApplicationContext(), ACCESS_CHECK_FAIL_MESSAGE);
                    // App 종료
                    finish();
                }); // fail-listener end/
    }
}
