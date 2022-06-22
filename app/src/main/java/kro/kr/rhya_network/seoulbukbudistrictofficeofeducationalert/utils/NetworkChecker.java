package kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

public class NetworkChecker {
    public boolean isNetworkConnection(Context context) {
        // 네트워크 연결 상태 확인하기 위한 ConnectivityManager 객체 생성
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            // 기기가 마시멜로우 버전인 Andorid 6 이상인 경우
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                // 활성화된 네트워크의 상태를 표현하는 객체
                NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());

                if (nc != null) {
                    if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    }else {
                        return false;
                    }
                }else {
                    return false;
                }
            }else {
                // 기기 버전이 마시멜로우 버전보다 아래인 경우
                // getActiveNetworkInfo -> API level 29에 디플리케이트 됨
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    // 연결된 네트워크 확인
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        return true;
                    }else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        return true;
                    }else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }else {
            return false;
        }
    }
}
