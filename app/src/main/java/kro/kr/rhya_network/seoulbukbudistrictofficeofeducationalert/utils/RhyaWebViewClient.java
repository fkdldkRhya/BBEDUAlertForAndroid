package kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils;

import android.app.Activity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.constraintlayout.widget.ConstraintLayout;

public class RhyaWebViewClient extends WebViewClient {
    // UI Object
    private final ConstraintLayout progressBarLayout;
    // Activity
    private final Activity activity;




    /**
     * 생성자
     * @param progressBarLayout 로딩 프로그레스바 View
     * @param activity Activity
     */
    public RhyaWebViewClient(ConstraintLayout progressBarLayout, Activity activity) {
        this.progressBarLayout = progressBarLayout;
        this.activity = activity;
    }



    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // Https 를 Http 로 변경
        url = url.replace("https://", "http://");

        // UI setting
        view.setVisibility(View.INVISIBLE);
        progressBarLayout.setVisibility(View.VISIBLE);

        // 서울특별시북부지원교육청 도메인 확인
        final String MAIN_DOMAIN = "http://bbedu.sen.go.kr";
        if (!url.contains(MAIN_DOMAIN)) {
            // Toast 출력
            Application.showToast(activity, "해당 페이지는 웹브라우저를 이용해 주세요.");

            // UI setting
            view.setVisibility(View.VISIBLE);
            progressBarLayout.setVisibility(View.GONE);

            return true;
        } // if end

        return false;
    }



    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        // UI setting
        view.setVisibility(View.VISIBLE);
        progressBarLayout.setVisibility(View.GONE);
    }
}
