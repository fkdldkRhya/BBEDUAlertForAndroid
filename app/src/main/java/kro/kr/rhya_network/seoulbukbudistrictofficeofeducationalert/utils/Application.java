package kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils;

import android.content.Context;
import android.widget.Toast;

public class Application extends android.app.Application {
    // ===================================================================
    // Toast Manager
    // ===================================================================
    // Toast 변수
    private static Toast sToast;

    /**
     * Toast message 생성
     * @param context Context
     * @param message Toast message
     */
    public static void showToast(Context context, String message) {
        if (sToast == null)
            sToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        else
            sToast.setText(message);

        sToast.show();
    }
    // ===================================================================


    // Fragment 공용 인자 이름
    // ===================================================================
    public static final String FRAGMENT_ARG_PARAM1 = "founder";
    public static final String FRAGMENT_ARG_PARAM2 = "name";
    public static final String FRAGMENT_ARG_PARAM3 = "type";
    public static final String FRAGMENT_ARG_PARAM4 = "regKey";
    public static final String FRAGMENT_ARG_PARAM5 = "token";
    public static final String FRAGMENT_ARG_PARAM6 = "private-key";
    public static final String FRAGMENT_ARG_PARAM7 = "decryptRegKey";
    // ===================================================================
}
