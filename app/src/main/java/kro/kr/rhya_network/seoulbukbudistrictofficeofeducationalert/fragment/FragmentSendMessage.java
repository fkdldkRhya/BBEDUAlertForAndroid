package kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.R;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.activity.ActivityHome;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.Application;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.DialogManager;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.FileInfoVO;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.RhyaAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSendMessage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSendMessage extends Fragment {
    private String mParam1;
    private String mParam2;
    private String mParam4;
    private String mParam5;
    private String mParam6;
    // UI Object
    private TextInputLayout messageTitleLayout;
    private TextInputEditText messageTitle;
    private EditText messageEditText;




    public FragmentSendMessage() {
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
    public static FragmentSendMessage newInstance(String param1, String param2, String param3,
                                                  String param4, String param5, String param6, String param7) {
        FragmentSendMessage fragment = new FragmentSendMessage();
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
            // TODO: Rename and change types of parameters
            mParam1 = getArguments().getString(Application.FRAGMENT_ARG_PARAM1);
            mParam2 = getArguments().getString(Application.FRAGMENT_ARG_PARAM2);
            mParam4 = getArguments().getString(Application.FRAGMENT_ARG_PARAM4);
            mParam5 = getArguments().getString(Application.FRAGMENT_ARG_PARAM5);
            mParam6 = getArguments().getString(Application.FRAGMENT_ARG_PARAM6);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // UI object setting
        messageTitleLayout = view.findViewById(R.id.messageTitleLayout);
        messageTitle = view.findViewById(R.id.messageTitle);
        messageEditText = view.findViewById(R.id.messageEditText);
        Button messageSendButton = view.findViewById(R.id.messageSendButton);
        TextView name = view.findViewById(R.id.name);
        TextView type = view.findViewById(R.id.type);

        name.setText(mParam2);
        type.setText(mParam1);

        // Title editText listener
        messageTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                messageTitleLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Send button listener
        messageSendButton.setOnClickListener(this::onClick);
    }



    private void onClick(View v) {
        // 데이터 Null 확인
        final Editable title = messageTitle.getText();
        final Editable message = messageEditText.getText();
        if (title != null && message != null) {
            // 데이터 추출
            final String inputTitle = title.toString();
            final String inputMessage = message.toString().replace(System.lineSeparator(), "#</br>#");

            // 제목 입력 확인
            if (inputTitle.replace(" ", "").length() <= 0) {
                messageTitleLayout.setError("제목을 입력해 주세요.");
                return;
            }

            // 내용 입력 확인
            if (inputMessage.replace(" ", "").length() <= 0) {
                Application.showToast(requireContext(), "메시지 내용을 입력해 주세요.");
                return;
            }

            // 메시지 전송 Dialog 표시
            DialogManager dialogManager = new DialogManager(requireActivity());
            dialogManager.setContentView(R.layout.dialog_loading);
            dialogManager.setCancelable(false);
            dialogManager.setBackground(null);
            dialogManager.setGravity(Gravity.BOTTOM);
            dialogManager.setDialogSizeWithCustomSize(1);
            NumberProgressBar numberProgressBar = dialogManager.dialog.findViewById(R.id.numberProgressBar);
            ((TextView) dialogManager.dialog.findViewById(R.id.title)).setText("메시지 전송 중...");
            ((TextView) dialogManager.dialog.findViewById(R.id.message)).setText("작업이 끝날 때까지 잠시만 기다려 주세요. 해당 작업은 시간이 소요될 수도 있습니다.");
            // 비동기 작업 진행
            new RhyaAsyncTask<String, String>() {
                private ArrayList<FileInfoVO> fileInfoVOS;
                private int temp = 0;
                private String fileList = null;
                private StringBuilder failNameBuilder;
                private StringBuilder failLogBuilder;
                private String fileNameList = null;

                @Override
                protected void onPreExecute() {
                    fileInfoVOS = ((FragmentMessage) (((ActivityHome) requireActivity()).messageFragment)).fileSelectedAdapter.inputValue;
                    failLogBuilder = new StringBuilder();
                    failNameBuilder = new StringBuilder();

                    dialogManager.showDialog();
                }

                @Override
                protected String doInBackground(String arg) {
                    try {
                        Random random = new Random();

                        while (numberProgressBar.getProgress() <= 30) {
                            int randomInt = random.nextInt(4);

                            //noinspection BusyWait
                            Thread.sleep(50);
                            numberProgressBar.incrementProgressBy(randomInt);
                        }

                        if (fileInfoVOS != null) {
                            StringBuilder rootBuilder;
                            rootBuilder = new StringBuilder();
                            final String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(new Date());
                            for (int index = 0; index < fileInfoVOS.size(); index++) {
                                FileInfoVO fileInfoVO = fileInfoVOS.get(index);

                                StringBuilder stringBuilder;
                                stringBuilder = new StringBuilder();
                                UUID uuid1 = UUID.randomUUID();
                                UUID uuid2 = UUID.randomUUID();
                                stringBuilder.append(uuid1.toString());
                                stringBuilder.append("-rhya-privatekey-Qgt2vsVqdcmDG38t-");
                                stringBuilder.append(uuid2.toString());
                                // SHA 512 암호화
                                MessageDigest md = MessageDigest.getInstance("SHA-256");
                                md.update(stringBuilder.toString().getBytes());
                                String hexToken = String.format("%064x", new BigInteger(1, md.digest()));
                                // Firebase 작업 처리
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference ref1 = database.getReference("/files/".concat(hexToken));
                                fileInfoVO.setUid(hexToken);
                                // File 데이터
                                Map<String, String> inputValueForFile = new HashMap<>();
                                inputValueForFile.put("name", fileInfoVO.getName());
                                inputValueForFile.put("owner", mParam5);
                                inputValueForFile.put("date", date);
                                ref1.setValue(inputValueForFile)
                                        .addOnCanceledListener(() -> {
                                            // 작업 취소

                                            temp = temp + 1;
                                            failLogBuilder.append("[ERROR 0002] 파일 정보 생성 실패! ---> ");
                                            failLogBuilder.append(fileInfoVO.getName());
                                            failLogBuilder.append(System.lineSeparator());
                                        }) // cancel-listener end
                                        .addOnSuccessListener(unused -> {
                                            // 작업 성공!

                                            // UI 변경
                                            temp = temp + 1;
                                        }) // success-listener end
                                        .addOnFailureListener(e -> {
                                            // 오류 발생

                                            temp = temp + 1;
                                            failLogBuilder.append("[ERROR 0001] 파일 정보 생성 실패! ---> ");
                                            failLogBuilder.append(fileInfoVO.getName());
                                            failLogBuilder.append(System.lineSeparator());
                                        }); // fail-listener end

                                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                StorageReference rootRef = firebaseStorage.getReference("users/".concat(hexToken));
                                UploadTask uploadTask = rootRef.putFile(fileInfoVO.getUri());
                                uploadTask
                                        .addOnCanceledListener(() -> {
                                            // 작업 취소

                                            temp = temp + 1;
                                            failLogBuilder.append("[ERROR 0004] 파일 업로드 실패! ---> ");
                                            failLogBuilder.append(fileInfoVO.getName());
                                            failLogBuilder.append(System.lineSeparator());
                                        }) // cancel-listener end
                                        .addOnSuccessListener(unused -> {
                                            // 작업 성공!

                                            temp = temp + 1;
                                        }) // success-listener end
                                        .addOnFailureListener(e -> {
                                            // 오류 발생

                                            temp = temp + 1;
                                            failLogBuilder.append("[ERROR 0003] 파일 업로드 실패! ---> ");
                                            failLogBuilder.append(fileInfoVO.getName());
                                            failLogBuilder.append(System.lineSeparator());
                                        }); // fail-listener end

                                rootBuilder.append(",");
                                rootBuilder.append(fileInfoVO.getUid());

                                failNameBuilder.append("dFsND@mhW!B-48AW");
                                failNameBuilder.append(fileInfoVO.getName());
                            }

                            if (rootBuilder.toString().length() > 0)
                                fileList = rootBuilder.substring(1);
                            if (failNameBuilder.toString().length() > 0)
                                fileNameList = failNameBuilder.substring(16);

                            if (fileInfoVOS.size() > 0) {
                                // 파일 업로드 완료 감지
                                int timeOutChecker = 0;
                                while (true) {
                                    if (temp >= fileInfoVOS.size() * 2) {
                                        while (numberProgressBar.getProgress() <= 90) {
                                            int randomInt = random.nextInt(10);

                                            //noinspection BusyWait
                                            Thread.sleep(40);
                                            numberProgressBar.incrementProgressBy(randomInt);
                                        }

                                        break;
                                    }

                                    //noinspection BusyWait
                                    Thread.sleep(100);

                                    timeOutChecker++;
                                    if (timeOutChecker >= 600) {
                                        numberProgressBar.setProgress(100);
                                        failLogBuilder.append("[ERROR 0005] 파일 업로드 실패! ---> Time out!");
                                        failLogBuilder.append(System.lineSeparator());

                                        if (fileInfoVOS == null || fileInfoVOS.size() <= 0) {
                                            sendNotificationDataSave(inputTitle, inputMessage, "NoFile", failLogBuilder);
                                        }else if (fileInfoVOS.size() == 1) {
                                            sendNotificationDataSave(inputTitle, inputMessage, fileInfoVOS.get(0).getName(), failLogBuilder);
                                        }else {
                                            sendNotificationDataSave(inputTitle, inputMessage, fileNameList, failLogBuilder);
                                        }

                                        return "timeout";
                                    }
                                }
                            }else {
                                while (numberProgressBar.getProgress() <= 90) {
                                    int randomInt = random.nextInt(10);

                                    //noinspection BusyWait
                                    Thread.sleep(40);
                                    numberProgressBar.incrementProgressBy(randomInt);
                                }
                            }
                        }else {
                            while (numberProgressBar.getProgress() <= 90) {
                                int randomInt = random.nextInt(10);

                                //noinspection BusyWait
                                Thread.sleep(40);
                                numberProgressBar.incrementProgressBy(randomInt);
                            }
                        }

                        if (fileInfoVOS == null || fileInfoVOS.size() <= 0) {
                            sendNotificationDataSave(inputTitle, inputMessage, "NoFile", failLogBuilder);
                        }else if (fileInfoVOS.size() == 1) {
                            sendNotificationDataSave(inputTitle, inputMessage, fileInfoVOS.get(0).getName(), failLogBuilder);
                        }else {
                            sendNotificationDataSave(inputTitle, inputMessage, fileNameList, failLogBuilder);
                        }

                        return "";
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(String result) {
                    if (result == null) {
                        Toast.makeText(requireContext(), "메시지 전송 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                    }else if (result.equals("timeout")) {
                        Toast.makeText(requireContext(), "Time out! 6000ms", Toast.LENGTH_SHORT).show();

                        dialogManager.dismissDialog();

                        return;
                    }

                    try {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        StringBuilder stringBuilder;
                        stringBuilder = new StringBuilder();

                        // 메시지 경로 생성
                        stringBuilder.append("/messages/");
                        stringBuilder.append(mParam4.replace("-" + mParam6, ""));
                        stringBuilder.append("/");
                        stringBuilder.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(new Date()));
                        // Firebase 작업 처리
                        DatabaseReference ref2 = database.getReference(stringBuilder.toString());
                        Map<String, String> inputValue = new HashMap<>();
                        inputValue.put("account", mParam5);
                        inputValue.put("title", inputTitle);
                        inputValue.put("message", inputMessage);
                        // 파일 선택 확인
                        if (fileInfoVOS == null || fileInfoVOS.size() <= 0) {
                            inputValue.put("file", "NoFile");
                        }else if (fileInfoVOS.size() == 1) {
                            inputValue.put("file", fileInfoVOS.get(0).getUid());
                        }else {
                            inputValue.put("file", fileList);
                        }
                        // 데이터 저장
                        ref2.setValue(inputValue)
                                .addOnCanceledListener(() -> {
                                    // 작업 취소

                                    // Dialog 닫기
                                    dialogManager.dismissDialog();

                                    // Toast 메시지 출력
                                    Application.showToast(requireContext(), "메시지 전송 중 알 수 없는 오류 발생!");

                                    errorMessageShow(failLogBuilder);
                                }) // cancel-listener end
                                .addOnSuccessListener(unused -> {
                                    // 작업 성공!

                                    // UI 변경
                                    numberProgressBar.setProgress(100);

                                    // Dialog 닫기
                                    dialogManager.dismissDialog();

                                    // Toast 메시지 출력
                                    if (result != null)
                                        Application.showToast(requireContext(), "메시지 전송 성공!");

                                    ((FragmentViewSendMessage) ((FragmentMessage) ((ActivityHome) requireActivity()).messageFragment).fragmentViewSendMessage).isReload = true;

                                    errorMessageShow(failLogBuilder);
                                }) // success-listener end
                                .addOnFailureListener(e -> {
                                    // 오류 발생

                                    // Dialog 닫기
                                    dialogManager.dismissDialog();

                                    // Toast 메시지 출력
                                    Application.showToast(requireContext(), e.getMessage());

                                    errorMessageShow(failLogBuilder);
                                }); // fail-listener end
                    }catch (Exception ex) {
                        ex.printStackTrace();

                        dialogManager.dismissDialog();

                        Toast.makeText(requireContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute(null);
        }
    }



    private void errorMessageShow(StringBuilder sb) {
        if (sb.toString().length() > 0) {
            DialogManager dialogManager = new DialogManager(requireActivity());
            dialogManager.setContentView(R.layout.dialog_upload_result);
            dialogManager.setCancelable(true);
            dialogManager.setBackground(null);
            dialogManager.setGravity(Gravity.BOTTOM);
            dialogManager.setDialogSizeWithCustomSize(1);
            ((EditText) dialogManager.dialog.findViewById(R.id.messageEditText)).setText(sb.toString());
            dialogManager.dialog.findViewById(R.id.button).setOnClickListener(v -> dialogManager.dismissDialog());
            dialogManager.showDialog();
        }
    }



    private void sendNotificationDataSave(String title, String message, String files, StringBuilder fail) throws JSONException, IOException {
        final String JSON_KEY_NOTIFICATION_TITLE = "notification-title";
        final String JSON_KEY_NOTIFICATION_MESSAGE = "notification-message";
        final String JSON_KEY_NOTIFICATION_FILES = "notification-files";
        // 날자 형식
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        final int MAX_JSON_ARRAY_INDEX = 90;
        // 파일 경로 생성
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(requireActivity().getFilesDir().getAbsolutePath());
        sb.append(File.separator);
        sb.append(getString(R.string.path_request_notification));
        // 파일 이름, 경로
        final String FILE_NAME = sb.toString();
        // StringBuilder 초기화
        sb.setLength(0);
        // 파일 생성 여부 확인
        JSONArray rootArray;
        if (new File(FILE_NAME).exists()) {
            // 파일 읽기
            FileReader fr = new FileReader(FILE_NAME);
            BufferedReader buf = new BufferedReader(fr);
            String readLine;

            // 파일 읽기
            while ((readLine = buf.readLine()) != null) {
                sb.append(readLine);
            } // while end

            // Stream 닫기
            buf.close();
            fr.close();

            // JSON 데이터 설정
            rootArray = new JSONArray(sb.toString());
        }else {
            rootArray = new JSONArray();
        } // if-else end
        // 파일 내용 생성
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_KEY_NOTIFICATION_TITLE, title);
        jsonObject.put(JSON_KEY_NOTIFICATION_MESSAGE, message);
        jsonObject.put(JSON_KEY_NOTIFICATION_FILES, files);
        jsonObject.put("date", dateFormat.format(new Date()));
        if (fail.toString().length() > 0) {
            jsonObject.put("fail", fail.toString().replace(System.lineSeparator(), "#</br>#"));
        }else {
            jsonObject.put("fail", "NoFail");
        }
        rootArray.put(jsonObject);
        // 아이템 개수 확인
        if (rootArray.length() > MAX_JSON_ARRAY_INDEX) {
            // 90 개로 아이템 개수 조정
            ArrayList<Integer> removeIndex = new ArrayList<>();
            // 대상 index 추가
            for (int index = 0; index < rootArray.length(); index++) {
                // 아이템 개수 확인
                if (rootArray.length() - removeIndex.size() <= MAX_JSON_ARRAY_INDEX) {
                    break;
                }

                // 제거 대상 index 추가
                removeIndex.add(index);
            } // for end

            // Index 제거
            for (int remove = 0; remove < removeIndex.size(); remove++) {
                rootArray.remove(remove);
            }
        } // if end
        // 파일 생성
        FileWriter fw = new FileWriter(FILE_NAME, false);
        BufferedWriter buf = new BufferedWriter(fw);
        buf.append(rootArray.toString());

        // Stream 닫기
        buf.close();
        fw.close();
    }



    public void resetInputValue() {
        messageTitle.setText("");
        messageEditText.setText("");
    }
}