package kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.R;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.adapter.MessageRequestLogAdapter;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.Application;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.DialogManager;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.RequestMessageVO;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.RhyaAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentViewSendMessage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentViewSendMessage extends Fragment {
    // UI Object
    public TextView count;
    // Reload checker
    public boolean isReload = true;
    // Adapter
    private MessageRequestLogAdapter messageRequestLogAdapter;




    public FragmentViewSendMessage() {
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
    public static FragmentViewSendMessage newInstance(String param1, String param2, String param3,
                                                      String param4, String param5, String param6, String param7) {
        FragmentViewSendMessage fragment = new FragmentViewSendMessage();
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
        return inflater.inflate(R.layout.fragment_view_send_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        count = view.findViewById(R.id.count);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);


        swipeRefreshLayout.setOnRefreshListener(() -> {
            reloadTask();
            swipeRefreshLayout.setRefreshing(false);
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
        messageRequestLogAdapter = new MessageRequestLogAdapter(this);
        recyclerView.setAdapter(messageRequestLogAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        isReload = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isReload) {
            isReload = false;

            reloadTask();
        }
    }



    public void reloadTask() {
        // ????????? ?????? Dialog ??????
        DialogManager dialogManager = new DialogManager(requireActivity());
        dialogManager.setContentView(R.layout.dialog_loading);
        dialogManager.setCancelable(false);
        dialogManager.setBackground(null);
        dialogManager.setGravity(Gravity.BOTTOM);
        dialogManager.setDialogSizeWithCustomSize(1);
        NumberProgressBar numberProgressBar = dialogManager.dialog.findViewById(R.id.numberProgressBar);
        ((TextView) dialogManager.dialog.findViewById(R.id.title)).setText("????????? ?????? ???...");
        ((TextView) dialogManager.dialog.findViewById(R.id.message)).setText("????????? ?????? ????????? ????????? ????????? ?????????. ?????? ????????? ????????? ????????? ?????? ????????????.");

        // ????????? ??????
        new RhyaAsyncTask<String, String>() {
            private ArrayList<RequestMessageVO> requestMessageVOS;

            @Override
            protected void onPreExecute() {
                requestMessageVOS = new ArrayList<>();
                dialogManager.showDialog();
            }

            @Override
            protected String doInBackground(String arg) {
                try {
                    // ?????? ?????? ??????
                    StringBuilder sb;
                    sb = new StringBuilder();
                    sb.append(requireActivity().getFilesDir().getAbsolutePath());
                    sb.append(File.separator);
                    sb.append(getString(R.string.path_request_notification));
                    // ?????? ??????, ??????
                    final String FILE_NAME = sb.toString();
                    sb.setLength(0);
                    if (new File(FILE_NAME).exists()) {
                        // ?????? ??????
                        FileReader fr = new FileReader(FILE_NAME);
                        BufferedReader buf = new BufferedReader(fr);
                        String readLine;

                        // ?????? ??????
                        while ((readLine = buf.readLine()) != null) {
                            sb.append(readLine);
                        } // while end

                        // Stream ??????
                        buf.close();
                        fr.close();

                        JSONArray jsonArray = new JSONArray(sb.toString());
                        numberProgressBar.setMax(jsonArray.length());
                        for (int i = 0; i < jsonArray.length(); i ++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            RequestMessageVO requestMessageVO = new RequestMessageVO();
                            requestMessageVO.setTitle(object.getString("notification-title"));
                            requestMessageVO.setMessage(object.getString("notification-message"));
                            requestMessageVO.setFile(object.getString("notification-files"));
                            requestMessageVO.setError(object.getString("fail"));
                            requestMessageVO.setDate(object.getString("date"));

                            requestMessageVOS.add(requestMessageVO);

                            numberProgressBar.incrementProgressBy(1);
                        }
                    }

                    return "success";
                }catch (Exception ex) {
                    ex.printStackTrace();

                    return ex.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                dialogManager.dismissDialog();

                if (!result.equals("success")) {
                    Application.showToast(requireContext(), result);
                }

                count.setText(String.valueOf(requestMessageVOS.size()).concat("???"));
                messageRequestLogAdapter.setInputValue(requestMessageVOS);
            }
        }.execute(null);
    }


    public void saveFileData(ArrayList<RequestMessageVO> requestMessageVOS) {
        // ????????? ?????? Dialog ??????
        DialogManager dialogManager = new DialogManager(requireActivity());
        dialogManager.setContentView(R.layout.dialog_loading);
        dialogManager.setCancelable(false);
        dialogManager.setBackground(null);
        dialogManager.setGravity(Gravity.BOTTOM);
        dialogManager.setDialogSizeWithCustomSize(1);
        NumberProgressBar numberProgressBar = dialogManager.dialog.findViewById(R.id.numberProgressBar);
        ((TextView) dialogManager.dialog.findViewById(R.id.title)).setText("?????? ?????? ?????? ???...");
        ((TextView) dialogManager.dialog.findViewById(R.id.message)).setText("????????? ?????? ????????? ????????? ????????? ?????????. ?????? ????????? ????????? ????????? ?????? ????????????.");
        dialogManager.showDialog();

        // ????????? ??????
        new RhyaAsyncTask<String, String>() {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected String doInBackground(String arg) {
                try {
                    // ?????? ?????? ??????
                    StringBuilder sb;
                    sb = new StringBuilder();
                    sb.append(requireActivity().getFilesDir().getAbsolutePath());
                    sb.append(File.separator);
                    sb.append(getString(R.string.path_request_notification));

                    numberProgressBar.setMax(requestMessageVOS.size());

                    JSONArray jsonArray = new JSONArray();
                    for (RequestMessageVO requestMessageVO : requestMessageVOS) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("notification-title", requestMessageVO.getTitle());
                        jsonObject.put("notification-message", requestMessageVO.getMessage());
                        jsonObject.put("notification-files", requestMessageVO.getFile());
                        jsonObject.put("date", requestMessageVO.getDate());
                        jsonObject.put("fail", requestMessageVO.getError());

                        jsonArray.put(jsonObject);

                        numberProgressBar.incrementProgressBy(1);
                    }

                    // ?????? ??????
                    FileWriter fw = new FileWriter(sb.toString(), false);
                    BufferedWriter buf = new BufferedWriter(fw);
                    buf.append(jsonArray.toString());

                    // Stream ??????
                    buf.close();
                    fw.close();
                }catch (Exception ex) {
                    ex.printStackTrace();

                    return ex.getMessage();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    Application.showToast(requireContext(), result);
                }

                dialogManager.dismissDialog();
            }
        }.execute(null);
    }
}