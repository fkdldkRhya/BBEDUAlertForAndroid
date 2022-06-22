package kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.activity;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.R;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.adapter.HomeViewPagerApdater;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.fragment.FragmentHome;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.fragment.FragmentMessage;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.fragment.FragmentSetting;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.lib.om_iammert_library_readablebottombar.ReadableBottomBar;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.Application;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.DownloadFileVO;

public class ActivityHome extends AppCompatActivity {
    // UI Object
    private ViewPager2 mainViewPager;
    // ViewPager fragment
    public Fragment homeFragment;
    public Fragment messageFragment;
    public Fragment settingFragment;
    // ViewPager index
    private int viewPagerIndex = 0;
    // 뒤로가기 버튼 시간
    private long backBtnTime = 0;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // UI object setting
        mainViewPager = findViewById(R.id.mainViewPager);
        ReadableBottomBar mainTabLayout = findViewById(R.id.mainTabLayout);

        // 인자 추출
        Intent getIntent = getIntent();
        final String PARAM_1_FOUNDER = getIntent.getStringExtra(Application.FRAGMENT_ARG_PARAM1);
        final String PARAM_2_NAME = getIntent.getStringExtra(Application.FRAGMENT_ARG_PARAM2);
        final String PARAM_3_TYPE = getIntent.getStringExtra(Application.FRAGMENT_ARG_PARAM3);
        final String PARAM_4_REG_KEY = getIntent.getStringExtra(Application.FRAGMENT_ARG_PARAM4);
        final String PARAM_5_TOKEN = getIntent.getStringExtra(Application.FRAGMENT_ARG_PARAM5);
        final String PARAM_6_PRIVATE_KEY = getIntent.getStringExtra(Application.FRAGMENT_ARG_PARAM6);
        final String PARAM_7_DECRYPT_KEY = getIntent.getStringExtra(Application.FRAGMENT_ARG_PARAM7);

        // Initialize fragment
        // =======================================================================================
        if (homeFragment == null)
            homeFragment = FragmentHome.newInstance(
                    PARAM_1_FOUNDER, PARAM_2_NAME, PARAM_3_TYPE, PARAM_4_REG_KEY,
                    PARAM_5_TOKEN, PARAM_6_PRIVATE_KEY, PARAM_7_DECRYPT_KEY
            ); // Home fragment
        if (messageFragment == null)
            messageFragment = FragmentMessage.newInstance(
                    PARAM_1_FOUNDER, PARAM_2_NAME, PARAM_3_TYPE, PARAM_4_REG_KEY,
                    PARAM_5_TOKEN, PARAM_6_PRIVATE_KEY, PARAM_7_DECRYPT_KEY
            ); // Message fragment
        if (settingFragment == null)
            settingFragment = FragmentSetting.newInstance(
                    PARAM_1_FOUNDER, PARAM_2_NAME, PARAM_3_TYPE, PARAM_4_REG_KEY,
                    PARAM_5_TOKEN, PARAM_6_PRIVATE_KEY, PARAM_7_DECRYPT_KEY
            ); // Setting fragment
        // =======================================================================================

        // Initialize adapter
        HomeViewPagerApdater homeViewPagerApdater = new HomeViewPagerApdater(getSupportFragmentManager(), getLifecycle());
        homeViewPagerApdater.addFragment(homeFragment);
        homeViewPagerApdater.addFragment(messageFragment);
        homeViewPagerApdater.addFragment(settingFragment);

        // Initialize viewpager
        mainViewPager.setUserInputEnabled(false);
        mainViewPager.setAdapter(homeViewPagerApdater);

        // Initialize Tab Layout
        mainTabLayout.setOnItemSelectListener(index -> {
            mainViewPager.setCurrentItem(index, false);
            viewPagerIndex = index;
        });
    }


    @Override
    public void onBackPressed() {
        switch (viewPagerIndex) {
            case 0: {
                if (homeFragment.isAdded() && ((FragmentHome) homeFragment).webView.canGoBack()) {
                    ((FragmentHome) homeFragment).webView.goBack();
                }else {
                    backButtonPressAndExitEvent();
                } // if-else end

                break;
            } // case-2 end

            case 1:
            case 2: {
                backButtonPressAndExitEvent();

                break;
            } // case-1 end
        } // switch end
    }



    /**
     * 뒤로가기 버튼 종료 이벤트
     */
    private void backButtonPressAndExitEvent() {
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



    /**
     * 파일 다운로드 관리자
     * @param downloadFileVO 파일 정보 VO
     */
    public void downloadFileForDownloadManager(DownloadFileVO downloadFileVO) {
        // Download manager request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadFileVO.getUrl()));
        // Request setting
        request.setDescription("다운로드 중...");
        request.setTitle(downloadFileVO.getName());
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(1);
        request.setVisibleInDownloadsUi(true);
        request.setAllowedOverMetered(true);
        request.setAllowedOverRoaming(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadFileVO.getName());
        // 파일 다운로드 진행
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(request);

        // Show toast message
        Application.showToast(getApplicationContext(), "파일을 다운로드합니다.");
    }
}
