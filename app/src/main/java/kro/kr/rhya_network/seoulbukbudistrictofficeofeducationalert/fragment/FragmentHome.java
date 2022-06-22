package kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.R;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.adapter.DownloadManagerAdapter;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.Application;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.DialogManager;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.DownloadFileVO;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.RhyaAsyncTask;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.RhyaWebViewClient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment {
    // UI Object
    public WebView webView;
    private ConstraintLayout progressBarLayout;




    public FragmentHome() {
        webView = null;
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
    public static FragmentHome newInstance(String param1, String param2, String param3,
                                           String param4, String param5, String param6, String param7) {
        FragmentHome fragment = new FragmentHome();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // UI object setting
        webView = view.findViewById(R.id.webView);
        progressBarLayout = view.findViewById(R.id.progressBarLayout);
        // UI default setting
        webView.setVisibility(View.INVISIBLE);
        progressBarLayout.setVisibility(View.VISIBLE);

        // WebView setting
        webViewSetting();
    }



    /**
     * WebView 기본 설정
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void webViewSetting() {
        // WebView 기본 설정
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setTextZoom(100);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setBuiltInZoomControls(true);
        // Custom webView client 설정
        webView.setWebViewClient(new RhyaWebViewClient(progressBarLayout, requireActivity()));
        // Download listener 설정
        webView.setDownloadListener((s, s1, s2, s3, l) -> {
            try {
                // 비동기 작업
                new RhyaAsyncTask<String, String>() {
                    // Utils
                    private DownloadManagerAdapter downloadManagerAdapter;
                    private DialogManager dialogManager;
                    private ArrayList<DownloadFileVO> downloadFileVOS; // 다운로드 파일 데이터
                    // UI Object
                    private ConstraintLayout progressBarLayout;
                    private RecyclerView recyclerView;
                    private TextView noItemValue;


                    @Override
                    protected void onPreExecute() {
                        // Dialog manager 초기화
                        dialogManager = new DialogManager(getActivity());
                        dialogManager.setContentView(R.layout.dialog_download_file);
                        dialogManager.setCancelable(true);
                        dialogManager.setBackground(null);
                        dialogManager.setGravity(Gravity.BOTTOM);
                        dialogManager.setDialogSizeWithCustomSize(1);

                        // Adapter 초기화
                        downloadManagerAdapter = new DownloadManagerAdapter(requireActivity());

                        // UI object setting
                        progressBarLayout = dialogManager.dialog.findViewById(R.id.progressBarLayout);
                        recyclerView = dialogManager.dialog.findViewById(R.id.recyclerView);
                        noItemValue = dialogManager.dialog.findViewById(R.id.noItemValue);
                        // UI setting
                        progressBarLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        noItemValue.setVisibility(View.INVISIBLE);
                        // Set button listener
                        dialogManager.dialog.findViewById(R.id.button).setOnClickListener(view -> dialogManager.dismissDialog());

                        // Show dialog
                        dialogManager.showDialog();
                    }

                    @Override
                    protected String doInBackground(String arg) {
                        try {
                            // 다운로드 가능한 파일 리스트 설정
                            downloadFileVOS = urlLoadHtmlForDownloadJavaScript(arg);
                        }catch (Exception ex) {
                            // 예외 처리
                            ex.printStackTrace();
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        // Adapter setting
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                        recyclerView.setAdapter(downloadManagerAdapter);
                        recyclerView.setLayoutManager(linearLayoutManager);

                        // UI setting
                        progressBarLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        if (downloadFileVOS.size() <= 0)
                            noItemValue.setVisibility(View.VISIBLE);

                        // Set value
                        downloadManagerAdapter.setInputValue(downloadFileVOS);
                    }
                }.execute(webView.getUrl()); // Async end
            }catch (Exception ex) {
                // 에외 처리
                ex.printStackTrace();
            } // try-catch end
        }); // listener end
        // URL 로딩
        webView.loadUrl("https://bbedu.sen.go.kr/CMS/adminfo/adminfo03/adminfo0301/index.html");
    }



    /**
     * URL 기반 Parser / 다운로드 파일 URL 생성
     * @param url Base URL
     * @return 다운로드 파일 URL 리스트
     */
    private ArrayList<DownloadFileVO> urlLoadHtmlForDownloadJavaScript(String url) throws IOException {
        // JSON <a href=""> Tag 파싱
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");

        // 반환 데이터 설정
        ArrayList<DownloadFileVO> downloadFileURL = new ArrayList<>();

        // StringBuilder
        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder();

        // 파싱 기본 데이터
        final String FILE_DOWNLOAD_BASE_URL_1 = "http://bbedu.sen.go.kr/CMS/fileDownload.do";
        final String FILE_DOWNLOAD_BASE_URL_2 = "http://bbedu.sen.go.kr/FUS2/fileDownload.do";
        final String JAVASCRIPT_FILE_DOWNLOAD_1 = "javascript:filedownload";
        final String JAVASCRIPT_FILE_DOWNLOAD_2 = "javascript:filedownload2";
        final String JAVASCRIPT_REPLACE_ITEM_1 = "javascript:fileDownLoad('','";
        final String JAVASCRIPT_REPLACE_ITEM_2 = "');";
        final String JAVASCRIPT_REPLACE_ITEM_3 = "javascript:fileDownLoad2('','";
        final String JAVASCRIPT_REPLACE_VALUE = "";
        final String JAVASCRIPT_SPLIT_ITEM_1 = "','";
        final String FILE_DOWNLOAD_PARAMETER_1 = "?fileName=";
        final String FILE_DOWNLOAD_PARAMETER_2 = "&orifileName=";
        final String FILE_DOWNLOAD_PARAMETER_3 = "&subPath=";
        final String URL_ENCODE = "UTF-8";
        // 파싱 데이터 변경
        for (int i = 0; i < links.size(); i ++) {
            // 파일 다운로드 JavaScript 추출
            String script = links.get(i).attr("href");

            // JavaScript 확인
            if (script.toLowerCase().contains(JAVASCRIPT_FILE_DOWNLOAD_1)) {
                // 불필요한 데이터 제거
                script = script.replace(JAVASCRIPT_REPLACE_ITEM_1, JAVASCRIPT_REPLACE_VALUE);
                script = script.replace(JAVASCRIPT_REPLACE_ITEM_2, JAVASCRIPT_REPLACE_VALUE);
                String[] split = script.split(JAVASCRIPT_SPLIT_ITEM_1);
                // 파일 다운로드 URL 생성
                stringBuilder.append(FILE_DOWNLOAD_BASE_URL_1);
                stringBuilder.append(FILE_DOWNLOAD_PARAMETER_1);
                stringBuilder.append(URLEncoder.encode(split[0], URL_ENCODE));
                stringBuilder.append(FILE_DOWNLOAD_PARAMETER_2);
                stringBuilder.append(URLEncoder.encode(split[0], URL_ENCODE));
                stringBuilder.append(FILE_DOWNLOAD_PARAMETER_3);
                stringBuilder.append(URLEncoder.encode(split[1], URL_ENCODE));
                // 파일 다운로드 데이터 VO 생성
                DownloadFileVO downloadFileVO = new DownloadFileVO(split[0], stringBuilder.toString());
                downloadFileURL.add(downloadFileVO);
            }else if (script.toLowerCase().contains(JAVASCRIPT_FILE_DOWNLOAD_2)) {
                // 불필요한 데이터 제거
                script = script.replace(JAVASCRIPT_REPLACE_ITEM_3, JAVASCRIPT_REPLACE_VALUE);
                script = script.replace(JAVASCRIPT_REPLACE_ITEM_2, JAVASCRIPT_REPLACE_VALUE);
                String[] split = script.split(JAVASCRIPT_SPLIT_ITEM_1);
                // 파일 다운로드 URL 생성
                stringBuilder.append(FILE_DOWNLOAD_BASE_URL_2);
                stringBuilder.append(FILE_DOWNLOAD_PARAMETER_1);
                stringBuilder.append(URLEncoder.encode(split[0], URL_ENCODE));
                stringBuilder.append(FILE_DOWNLOAD_PARAMETER_2);
                stringBuilder.append(URLEncoder.encode(split[0], URL_ENCODE));
                stringBuilder.append(FILE_DOWNLOAD_PARAMETER_3);
                stringBuilder.append(URLEncoder.encode(split[1], URL_ENCODE));
                // 파일 다운로드 데이터 VO 생성
                DownloadFileVO downloadFileVO = new DownloadFileVO(split[0], stringBuilder.toString());
                downloadFileURL.add(downloadFileVO);
            } // else-if end

            // StringBuilder 초기화
            stringBuilder.setLength(0);
        } // for end

        return downloadFileURL;
    }
}