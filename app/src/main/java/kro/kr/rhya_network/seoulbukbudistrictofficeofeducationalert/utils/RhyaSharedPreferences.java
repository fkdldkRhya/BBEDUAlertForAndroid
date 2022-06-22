package kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class RhyaSharedPreferences {
    /*
     * SharedPreferences 저장 정보
     */
    public final String SHARED_PREFERENCES_APP_NAME = "RHYA.Network_PREFERENCES"; // 설정 저장 이름
    // String 기본 반환 데이터
    public final String DEFAULT_RETURN_STRING_VALUE = "<Null>";
    // Context
    private Context context;

    /**
     * 생성자 - 초기화 작업
     */
    public RhyaSharedPreferences(Context context) {
        this.context = context;
    }



    /**
     * SharedPreferences (No Encrypt) [ String ] 설정
     * @param key 설정 이름
     * @param value 설정 데이터
     */
    public void setStringNoAES(String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_APP_NAME,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }
    /**
     * SharedPreferences (No Decrypt) [ String ] 가져오기
     * @param key 설정 이름
     * @return 설정 데이터
     */
    public String getStringNoAES(String key) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_APP_NAME,
                Context.MODE_PRIVATE);
        return prefs.getString(key, DEFAULT_RETURN_STRING_VALUE);
    }
    /**
     * SharedPreferences [ String ] 제거
     * @param key 설정 이름
     */
    public void removeString(String key) {

        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_APP_NAME,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }
}
