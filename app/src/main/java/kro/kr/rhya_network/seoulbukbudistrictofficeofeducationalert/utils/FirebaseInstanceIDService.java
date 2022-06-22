package kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.R;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.activity.ActivitySplash;

public class FirebaseInstanceIDService extends FirebaseMessagingService {
    // 랜덤 정수 생성
    private final AtomicInteger atomicInteger = new AtomicInteger(0);
    // Notification 전용 변수
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder notificationCompat;
    private NotificationChannel channel;
    // Notification 데이터 JSON 키
    private final String JSON_KEY_NOTIFICATION_TITLE = "notification-title";
    private final String JSON_KEY_NOTIFICATION_MESSAGE = "notification-message";
    private final String JSON_KEY_NOTIFICATION_PRIORITY = "notification-priority";




    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        // Firebase connection
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("/users/".concat(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())));
        // Firebase Real-Time base 데이터 변경
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            ref.get()
                    .addOnSuccessListener(unused -> {
                        // 작업 성공!
                        @SuppressWarnings("unchecked")
                        Map<String, Object> inputValue = (Map<String, Object>) unused.getValue();

                        if (inputValue != null) {
                            inputValue.put("token", token);
                            inputValue.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(new Date()));
                            ref.updateChildren(inputValue);
                        }
                    }); // success-listener end
        }); // listener end
    }



    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // 메시지 수신 확인
        if (remoteMessage.getData().size() > 0) {
            showNotification(remoteMessage);
        }
    }


    /**
     * Notification 출력
     * @param remoteMessage RemoteMessage
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    private void showNotification(@NonNull RemoteMessage remoteMessage) {
        // Notification 채널 정보 [ ID, Name ]
        final String CHANNEL_ID = getString(R.string.notification_channel_id);
        final String CHANNEL_NAME = getString(R.string.notification_channel_name);
        RhyaSharedPreferences rhyaSharedPreferences = new RhyaSharedPreferences(getApplicationContext());
        if (!rhyaSharedPreferences.getStringNoAES("_IS_NOTICE_SETTING_").equals("true")) {
            return;
        }

        // 예외 처리
        try {
            // NULL 확인
            if (notificationManager == null)
                // NotificationManagerCompat
                notificationManager = NotificationManagerCompat.from(getApplicationContext());

            // Android 버전 확인
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                    // NULL 확인
                    if (channel == null)
                        channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                    // Channel 설정
                    notificationManager.createNotificationChannel(channel);
                } // if end
            } // if end

            // Notification 클릭 이벤트 설정
            Intent mainIntent = new Intent(getApplicationContext(), ActivitySplash.class);
            mainIntent.setAction(Intent.ACTION_MAIN);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
                pendingIntent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE);
            else
                pendingIntent = PendingIntent.getBroadcast(this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // NULL 확인
            if (notificationCompat == null)
                // Notification builder Channel 설정
                notificationCompat = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            // Notification builder 데이터
            final String title = Objects.requireNonNull(remoteMessage.getData()).get(JSON_KEY_NOTIFICATION_TITLE);
            final String message = Objects.requireNonNull(Objects.requireNonNull(remoteMessage.getData()).get(JSON_KEY_NOTIFICATION_MESSAGE)).replace("#</br>#", System.lineSeparator());
            final int priority = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get(JSON_KEY_NOTIFICATION_PRIORITY)));
            final String MAIN_NOTIFICATION_GROUP = "RHYA_MAIN_NOTIFICATION_GROUP";
            // Notification builder 데이터 설정
            notificationCompat
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setShowWhen(true)
                    .setGroup(MAIN_NOTIFICATION_GROUP)
                    .setSmallIcon(R.drawable.ic_logo_sbdooe_window)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            // Notification 생성
            Notification notification = notificationCompat.build();
            notificationManager.notify(getRandomNotificationID(), notification);

            // 예외 처리
            try {
                // 메시지 저장
                receiveNotificationDataSave(title, message, priority);
                rhyaSharedPreferences.setStringNoAES("_RELOAD_REQUEST_MESSAGE_", "true");
            }catch (Exception ex) {
                ex.printStackTrace();
            } // try-catch end
        }catch (Exception ex) {
            ex.printStackTrace();

            // 예외 처리
            Application.showToast(getApplicationContext(), ex.getMessage());
        } // try-catch end
    }



    /**
     * 정수형 난수 발생 - 알림 ID
     * @return int
     */
    private int getRandomNotificationID() {
        return atomicInteger.incrementAndGet();
    }



    /**
     * 전송 받은 Notification 데이터 저장
     *
     * @param title Notification title
     * @param message Notification message
     * @param priority Notification priority
     * @throws JSONException JSONException
     * @throws IOException IOException
     */
    private void receiveNotificationDataSave(String title, String message, int priority) throws JSONException, IOException {
        // 날자 형식
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        final int MAX_JSON_ARRAY_INDEX = 90;
        // 파일 경로 생성
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(getFilesDir().getAbsolutePath());
        sb.append(File.separator);
        sb.append(getString(R.string.path_receive_notification));
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
        jsonObject.put(JSON_KEY_NOTIFICATION_PRIORITY, priority);
        jsonObject.put("date", dateFormat.format(new Date()));
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
}