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
    // Activity Result Launcher ??????
    private ActivityResultLauncher<Intent> resultLauncher;
    // Dialog Manager
    private DialogManager dialogManagerForAddUser;
    // Firebase Aut ??????
    private FirebaseAuth mFirebaseAuth;
    // ???????????? ?????? ??????
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


        // Activity result launcher ??????
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // ?????? ??????
                        isGoogleLoginIntentRun = false;

                        Intent intent = result.getData();
                        // ????????? ??????
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                        handleSignInResult(task);
                    }else {
                        // ?????? ??????
                        isGoogleLoginIntentRun = false;

                        // ???????????? ?????? ??????
                        if (networkChecker.isNetworkConnection(getApplicationContext())) {
                            // Google firebase login request
                            googleSignTaskRequest();
                        }else {
                            Toast.makeText(getApplicationContext(), "????????? ????????? ?????? ??? ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                            finish();
                        } // if-else end
                    } // if-else end
                }); // listener end

        // Google firebase login ?????? ?????? ??????
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
                    // ?????? ??????
                    isDelayedChecker = true;

                    // UI ?????? ??????
                    appLogoConstraintLayout.setVisibility(View.VISIBLE);
                    progressBarLayout.setVisibility(View.VISIBLE);

                    // ?????? ?????? ??????
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
            // ??????
            finish();
        }else {
            backBtnTime = curTime;
            Toast.makeText(getApplicationContext(), "'??????' ????????? ?????? ??? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
        } // if-else end
    }


    @Override
    protected void onStart() {
        super.onStart();

        // ?????? ?????? ??????
        if (isDelayedChecker) {
            isAccessCheck();
        } // if end
    }



    /**
     * ?????? ?????? ?????? ?????? ??????
     */
    private void nextTask() {
        googleSignTaskRequest();
    }



    /**
     * Home activity ??? ??????
     */
    private void startHomeActivity(String regKey, String token) {
        if (!isFinishing() && !isHomeActivityRun) {
            // ?????? ??????
            isHomeActivityRun = true;

            // UI ??????
            taskMessageTextView.setText(getString(R.string.text_welcome));

            // ????????? ????????????
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference getPrivateKeyRef = database.getReference("/private-key/key");
            getPrivateKeyRef.get()
                    .addOnCanceledListener(() -> {
                        // ?????? ??????
                        dialogManagerForAddUser.showDialog();
                    }) // cancel-listener end
                    .addOnSuccessListener(unused -> {
                        // ?????? ??????!

                        // ?????? ??????
                        try {
                            // ????????? ??????
                            final String privateKey = (String) unused.getValue();
                            if (privateKey != null) {
                                // Reg key ??????
                                final String decryptRegKey = regKey.replace("-".concat(privateKey), "");
                                DatabaseReference getMainDataRef = database.getReference("/reg-key/".concat(decryptRegKey));
                                // ????????? ????????????
                                getMainDataRef.get()
                                        .addOnCanceledListener(() -> {
                                            // ?????? ??????
                                            dialogManagerForAddUser.showDialog();
                                        }) // cancel-listener end
                                        .addOnSuccessListener(unused2 -> {
                                            // ?????? ??????!

                                            // ?????? ??????
                                            try {
                                                // ????????? ??????
                                                @SuppressWarnings("unchecked")
                                                Map<String, Object> inputValue = (Map<String, Object>) unused2.getValue();
                                                if (inputValue != null) {
                                                    // ?????????
                                                    final String FOUNDER = (String) inputValue.get("founder");
                                                    final String NAME = (String) inputValue.get("name");
                                                    final String TYPE = (String) inputValue.get("type");

                                                    // Topics ?????????
                                                    final String TOPICS_NAME_1 = "Topics1";
                                                    final String TOPICS_NAME_2 = "Topics2";

                                                    // Topics ??????
                                                    if (TYPE != null) {
                                                        if (TYPE.equals(TOPICS_NAME_1)) {
                                                            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPICS_NAME_2);
                                                            FirebaseMessaging.getInstance().subscribeToTopic(TOPICS_NAME_1);
                                                        }else if (TYPE.equals(TOPICS_NAME_2)) {
                                                            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPICS_NAME_1);
                                                            FirebaseMessaging.getInstance().subscribeToTopic(TOPICS_NAME_2);
                                                        }
                                                    }

                                                    // ?????? ??????
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
                                            // ?????? ??????
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
                        // ?????? ??????
                        dialogManagerForAddUser.showDialog();
                    }); // fail-listener end/
        } // if end
    }



    /**
     * Google firebase ????????? ??????
     */
    private void googleSignTaskRequest() {
        // ?????? ??????
        if(!isGoogleLoginIntentRun) {
            // UI ??????
            taskMessageTextView.setText(getString(R.string.text_check_login));

            // ?????? ??????
            isGoogleLoginIntentRun = true;

            // ????????? ?????? Intent ??????
            Intent intent = mGoogleSignInClient.getSignInIntent();
            resultLauncher.launch(intent);
        } // if end
    }



    /**
     * Google firebase ????????? ?????? ??????
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

            // ?????? ????????? ?????????
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
                        // ????????? ?????? ??????
                        userAddChecker();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }); // listener end
    }



    /**
     * ??????????????? ?????? ?????? ??????
     */
    private void checkPermission() {
        // UI ??????
        taskMessageTextView.setText(getString(R.string.text_check_permission));

        // Android API ?????? ??????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            TedPermission.create()
                    .setGotoSettingButton(false)
                    .setPermissionListener(permissionListener)
                    // Scoped Storage ??????
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



    // Permission ?????? ?????? ?????????
    private final PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            // ?????? ?????????
            nextTask();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            // ?????? ????????? - ?????? ????????? ?????? ??? ??????
            Toast.makeText(getApplicationContext(), "?????? ????????? ?????? ????????? ???????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }; // listener end



    /**
     * ????????? ?????? ??????
     */
    private void userAddChecker() {
        // UI object setting
        final ConstraintLayout progressBarLayout = dialogManagerForAddUser.dialog.findViewById(R.id.progressBarLayout);
        final TextInputLayout regNumLayout = dialogManagerForAddUser.dialog.findViewById(R.id.regNumLayout);
        final TextInputEditText regNumEditText = dialogManagerForAddUser.dialog.findViewById(R.id.regNumEditText);
        final Button button = dialogManagerForAddUser.dialog.findViewById(R.id.button);

        // UI ??????
        checkAddUserForDialog(regNumLayout, regNumEditText, button);
        taskMessageTextView.setText(getString(R.string.text_verifying_user_authentication));

        // EditText changed listener ??????
        regNumEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // UI ??????
                checkAddUserForDialog(regNumLayout, regNumEditText, button);
            }
        }); // listener end
        // Button listener
        button.setOnClickListener(v -> {
            // ???????????? ?????? ??????
            if (!networkChecker.isNetworkConnection(getApplicationContext())) {
                // Toast ??????
                Application.showToast(getApplicationContext(), "????????? ????????? ?????? ??? ?????? ????????? ?????????.");

                // ??????
                return;
            } // if end

            // ????????? ?????? ??????
            if (checkAddUserForDialog(regNumLayout, regNumEditText, button)) {
                // UI ?????? ??????
                button.setEnabled(false);
                progressBarLayout.setVisibility(View.VISIBLE);
                regNumLayout.setVisibility(View.GONE);

                // ?????? ??????
                try {
                    // ????????? ??????
                    String regNum = Objects.requireNonNull(regNumEditText.getText()).toString();
                    // Firebase token
                    String firebaseToken = FirebaseAuth.getInstance().getUid();
                    // Null ??????
                    if (firebaseToken != null) {
                        // Firebase connection
                        String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference("/users/".concat(firebaseToken));
                        // ?????? ??????
                        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                            Map<String, String> inputValue = new HashMap<>();
                            inputValue.put("token", token);
                            inputValue.put("key", regNum);
                            inputValue.put("email", email);
                            inputValue.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(new Date()));
                            // ????????? ????????? ??????
                            ref.setValue(inputValue)
                                    .addOnCanceledListener(() -> {
                                        // ?????? ??????

                                        // UI ?????? ??????
                                        regNumLayout.setError("????????? ?????????????????????.");
                                        button.setEnabled(true);
                                        progressBarLayout.setVisibility(View.GONE);
                                        regNumLayout.setVisibility(View.VISIBLE);
                                    }) // cancel-listener end
                                    .addOnSuccessListener(unused -> {
                                        // ?????? ??????!

                                        // UI ??????
                                        regNumLayout.setErrorEnabled(false);
                                        regNumEditText.setEnabled(false);

                                        // Dialog ??????
                                        dialogManagerForAddUser.dismissDialog();

                                        // ?????? ??????
                                        startHomeActivity(regNum, firebaseToken);
                                    }) // success-listener end
                                    .addOnFailureListener(e -> {
                                        // ?????? ??????

                                        // UI ?????? ??????
                                        regNumLayout.setError(e.getMessage());
                                        button.setEnabled(true);
                                        progressBarLayout.setVisibility(View.GONE);
                                        regNumLayout.setVisibility(View.VISIBLE);
                                    }); // fail-listener end
                        }); // listener end
                    } // if end
                }catch (Exception ex) {
                    // ?????? ??????
                    ex.printStackTrace();

                    // UI ?????? ??????
                    button.setEnabled(true);
                    progressBarLayout.setVisibility(View.GONE);
                    regNumLayout.setVisibility(View.VISIBLE);
                } // try-catch end
            } // if end
        }); // listener end


        isAddUser();
    }



    /**
     * Dialog ?????? ?????? ?????? UI ?????? ?????? ??? ????????????
     * @param regNumLayout ???????????? Layout
     * @param regNumEditText ???????????? Input
     * @param button Next button
     */
    private boolean checkAddUserForDialog(TextInputLayout regNumLayout,
                                          TextInputEditText regNumEditText,
                                          Button button) {
        // Dialog ?????? ?????? ????????????
        boolean isRegNumInput = Objects.requireNonNull(regNumEditText.getText()).toString().length() > 0;

        // ?????? ?????? ??????
        button.setEnabled(isRegNumInput);

        // Reg Num Layout ??????
        if (!isRegNumInput)
            regNumLayout.setError("??????????????? ????????? ?????????.");
        else
            regNumLayout.setErrorEnabled(false);

        return isRegNumInput;
    }



    /**
     * ????????? ?????? ??????
     */
    private void isAddUser() {
        // ???????????? ?????? ??????
        if (!networkChecker.isNetworkConnection(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "????????? ????????? ?????? ??? ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
            finish();
        } // if end

        // Firebase token
        String firebaseToken = FirebaseAuth.getInstance().getUid();
        // Null ??????
        if (firebaseToken != null) {
            // Firebase connection
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("/users/".concat(firebaseToken));
            // ?????? ??????
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                // ????????? ????????? ????????????
                ref.get()
                        .addOnCanceledListener(() -> {
                            // ?????? ??????
                            dialogManagerForAddUser.showDialog();
                        }) // cancel-listener end
                        .addOnSuccessListener(unused -> {
                            // ?????? ??????!

                            // Dialog ??????
                            dialogManagerForAddUser.dismissDialog();

                            // ?????? ??????
                            try {
                                // ????????? ??????
                                @SuppressWarnings("unchecked")
                                Map<String, Object> inputValue = (Map<String, Object>) unused.getValue();
                                if (inputValue != null) {
                                    inputValue.put("token", token);
                                    inputValue.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(new Date()));
                                    ref.updateChildren(inputValue);

                                    // ?????? ??????
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
                            // ?????? ??????
                            dialogManagerForAddUser.showDialog();
                        }); // fail-listener end/
            }); // listener end
        } // if end
    }



    /**
     * ??? ???????????? ??????
     */
    private void isUpdateChecker() {
        // ???????????? ?????? ?????????
        final String UPDATE_FAIL_MESSAGE = "???????????? ?????? ??? ????????? ?????????????????????.";

        // UI ??????
        taskMessageTextView.setText(getString(R.string.text_check_update));

        // Firebase connection
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("/app-info/version");
        ref.get()
                .addOnCanceledListener(() -> {
                    // ?????? ??????

                    // Toast ??????
                    Application.showToast(getApplicationContext(), UPDATE_FAIL_MESSAGE);
                    // App ??????
                    finish();
                }) // cancel-listener end
                .addOnSuccessListener(unused -> {
                    // ?????? ??????!

                    // ?????? ??????
                    try {
                        // ????????? ??????
                        final String VERSION = (String) unused.getValue();
                        // Null ??????
                        if (VERSION != null) {
                            // ?????? ??????
                            if (!updateChecker(VERSION)) {
                                // ???????????? Dialog ?????????
                                DialogManager dialogManager = new DialogManager(this);
                                dialogManager.setContentView(R.layout.dialog_app_update);
                                dialogManager.setCancelable(false);
                                dialogManager.setBackground(null);
                                dialogManager.setGravity(Gravity.BOTTOM);
                                dialogManager.setDialogSizeWithCustomSize(1);
                                // Update dialog listener ??????

                                // Update dialog ??????
                                dialogManager.showDialog();
                            }else {
                                // ?????? ?????? ??????
                                checkPermission();
                            } // if-else end
                        }else {
                            // Toast ??????
                            Application.showToast(getApplicationContext(), UPDATE_FAIL_MESSAGE);
                            // App ??????
                            finish();
                        } // if-else end
                    }catch (Exception ex) {
                        ex.printStackTrace();

                        // Toast ??????
                        Application.showToast(getApplicationContext(), UPDATE_FAIL_MESSAGE);
                        // App ??????
                        finish();
                    } // try-catch emd
                }) // success-listener end
                .addOnFailureListener(e -> {
                    // ?????? ??????

                    // Toast ??????
                    Application.showToast(getApplicationContext(), UPDATE_FAIL_MESSAGE);
                    // App ??????
                    finish();
                }); // fail-listener end/
    }



    /**
     * ??? ?????? ??????
     * @param version ?????? ??????
     * @return False -> ???????????? ?????? , True -> ?????? ??????
     */
    public boolean updateChecker(String version) {
        return Integer.parseInt(BuildConfig.VERSION_NAME.replace(".", "")) >= Integer.parseInt(version.replace(".", ""));
    }



    private void isAccessCheck() {
        // ?????? ?????? ?????????
        final String ACCESS_CHECK_FAIL_MESSAGE = "?????? ?????? ?????? ??? ????????? ?????????????????????.";

        // UI ??????
        taskMessageTextView.setText(getString(R.string.text_check_update));

        // Firebase connection
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("/app-info/access");
        ref.get()
                .addOnCanceledListener(() -> {
                    // ?????? ??????

                    // Toast ??????
                    Application.showToast(getApplicationContext(), ACCESS_CHECK_FAIL_MESSAGE);
                    // App ??????
                    finish();
                }) // cancel-listener end
                .addOnSuccessListener(unused -> {
                    // ?????? ??????!

                    // ?????? ??????
                    try {
                        // ????????? ??????
                        final String ACCESS = (String) unused.getValue();
                        // Null ??????
                        if (ACCESS != null) {
                            // ?????? ??????
                            if (!ACCESS.equals("true")) {
                                // Access black dialog ?????????
                                DialogManager dialogManager = new DialogManager(this);
                                dialogManager.setContentView(R.layout.dialog_access_block);
                                dialogManager.setCancelable(false);
                                dialogManager.setBackground(null);
                                dialogManager.setGravity(Gravity.BOTTOM);
                                dialogManager.setDialogSizeWithCustomSize(1);
                                // Access black dialog listener ??????
                                dialogManager.dialog.findViewById(R.id.button).setOnClickListener(v -> finish());
                                // Update dialog ??????
                                dialogManager.showDialog();
                            }else {
                                // ?????? ?????? ??????
                                isUpdateChecker();
                            } // if-else end
                        }else {
                            // Toast ??????
                            Application.showToast(getApplicationContext(), ACCESS_CHECK_FAIL_MESSAGE);
                            // App ??????
                            finish();
                        } // if-else end
                    }catch (Exception ex) {
                        ex.printStackTrace();

                        // Toast ??????
                        Application.showToast(getApplicationContext(), ACCESS_CHECK_FAIL_MESSAGE);
                        // App ??????
                        finish();
                    } // try-catch emd
                }) // success-listener end
                .addOnFailureListener(e -> {
                    // ?????? ??????

                    // Toast ??????
                    Application.showToast(getApplicationContext(), ACCESS_CHECK_FAIL_MESSAGE);
                    // App ??????
                    finish();
                }); // fail-listener end/
    }
}
