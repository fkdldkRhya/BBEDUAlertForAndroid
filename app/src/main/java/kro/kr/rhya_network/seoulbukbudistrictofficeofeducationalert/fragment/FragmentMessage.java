package kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.OpenableColumns;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.R;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.adapter.FileSelectedAdapter;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.adapter.MessageViewPagerAdapter;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.Application;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.DialogManager;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.FileInfoVO;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentMessage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMessage extends Fragment {
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;
    private String mParam5;
    private String mParam6;
    private String mParam7;
    // UI Object
    private TabLayout messageTabLayout;
    private ViewPager2 viewPager2;
    private ImageButton attachmentsButton;
    // Fragment
    private Fragment fragmentMessageSend;
    public Fragment fragmentViewSendMessage;
    public Fragment fragmentViewResponseMessage;
    // Dialog manager
    private DialogManager uploadManager;
    public FileSelectedAdapter fileSelectedAdapter;
    public TextView dialogFileCount;
    // Activity Result Launcher 변수
    private ActivityResultLauncher<Intent> resultLauncher;




    public FragmentMessage() {
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
    public static FragmentMessage newInstance(String param1, String param2, String param3,
                                           String param4, String param5, String param6, String param7) {
        FragmentMessage fragment = new FragmentMessage();
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
            mParam1 = getArguments().getString(Application.FRAGMENT_ARG_PARAM1);
            mParam2 = getArguments().getString(Application.FRAGMENT_ARG_PARAM2);
            mParam3 = getArguments().getString(Application.FRAGMENT_ARG_PARAM3);
            mParam4 = getArguments().getString(Application.FRAGMENT_ARG_PARAM4);
            mParam5 = getArguments().getString(Application.FRAGMENT_ARG_PARAM5);
            mParam6 = getArguments().getString(Application.FRAGMENT_ARG_PARAM6);
            mParam7 = getArguments().getString(Application.FRAGMENT_ARG_PARAM7);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }




    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == -1) {
                        if (result.getData() != null) {

                            if (fileSelectedAdapter.inputValue == null)
                                fileSelectedAdapter.inputValue = new ArrayList<>();

                            if (result.getData().getClipData() != null) {
                                for (int i = 0; i < result.getData().getClipData().getItemCount(); i++) {
                                    ClipData.Item item = result.getData().getClipData().getItemAt(i);
                                    FileInfoVO fileInfoVO = getFilInfo(item.getUri());

                                    if (fileInfoVO == null) {
                                        Application.showToast(requireContext(), "해당 파일은 업로드가 불가능합니다. 최대 20mb");
                                        continue;
                                    }

                                    fileSelectedAdapter.inputValue.add(fileInfoVO);
                                }
                            }else {
                                FileInfoVO fileInfoVO = getFilInfo(result.getData().getData());

                                if (fileInfoVO == null) {
                                    Application.showToast(requireContext(), "해당 파일은 업로드가 불가능합니다. 최대 20mb");
                                }else {
                                    fileSelectedAdapter.inputValue.add(fileInfoVO);
                                }
                            }

                            if (fileSelectedAdapter.inputValue.size() > 0) {
                                dialogFileCount.setVisibility(View.GONE);
                            }else {
                                dialogFileCount.setVisibility(View.VISIBLE);
                            }

                            fileSelectedAdapter.setInputValue(fileSelectedAdapter.inputValue);
                        }else {
                            Application.showToast(requireContext(), "다른 저장소에서 선택해 주세요.");
                        }
                    }
                }); // listener end

        fileSelectedAdapter = new FileSelectedAdapter(this);

        uploadManager = new DialogManager(requireActivity());
        uploadManager.setContentView(R.layout.dialog_file_upload);
        uploadManager.setCancelable(true);
        uploadManager.setBackground(null);
        uploadManager.setGravity(Gravity.BOTTOM);
        uploadManager.setDialogSizeWithCustomSize(1);
        uploadManager.dialog.findViewById(R.id.button1).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            intent.setType("*/*");

            resultLauncher.launch(intent);
        });
        uploadManager.dialog.findViewById(R.id.button2).setOnClickListener(v -> {
            if (fileSelectedAdapter.inputValue != null && fileSelectedAdapter.inputValue.size() > 0) {
                dialogFileCount.setVisibility(View.VISIBLE);
                fileSelectedAdapter.inputValue.clear();
                fileSelectedAdapter.notifyDataSetChanged();
            }
        });
        dialogFileCount = uploadManager.dialog.findViewById(R.id.noItemValue);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        RecyclerView recyclerView = uploadManager.dialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(fileSelectedAdapter);



        // UI object setting
        messageTabLayout = view.findViewById(R.id.messageTabLayout);
        viewPager2 = view.findViewById(R.id.messageViewPager2);
        attachmentsButton = view.findViewById(R.id.attachmentsButton);
        ImageButton moreButton = view.findViewById(R.id.moreButton);


        // Initialize fragment
        // =======================================================================================
        if (fragmentMessageSend == null)
            fragmentMessageSend = FragmentSendMessage.newInstance(
                    mParam1, mParam2, mParam3, mParam4,
                    mParam5, mParam6, mParam7
            );

        if (fragmentViewSendMessage == null)
            fragmentViewSendMessage = FragmentViewSendMessage.newInstance(
                    mParam1, mParam2, mParam3, mParam4,
                    mParam5, mParam6, mParam7
            );

        if (fragmentViewResponseMessage == null)
            fragmentViewResponseMessage = FragmentViewResponseMessage.newInstance(
                    mParam1, mParam2, mParam3, mParam4,
                    mParam5, mParam6, mParam7
            );
        // =======================================================================================

        // Initialize adapter
        MessageViewPagerAdapter messageViewPagerAdapter = new MessageViewPagerAdapter(getChildFragmentManager(), getLifecycle());
        messageViewPagerAdapter.addFragment(fragmentMessageSend);
        messageViewPagerAdapter.addFragment(fragmentViewSendMessage);
        messageViewPagerAdapter.addFragment(fragmentViewResponseMessage);

        // Initialize viewpager
        viewPager2.setUserInputEnabled(false);
        viewPager2.setAdapter(messageViewPagerAdapter);

        // Set tab layout liener
        messageTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: {
                        viewPager2.setCurrentItem(tab.getPosition());
                        attachmentsButton.setVisibility(View.VISIBLE);

                        break;
                    }


                    case 1:
                    case 2: {
                        viewPager2.setCurrentItem(tab.getPosition());
                        attachmentsButton.setVisibility(View.INVISIBLE);

                        break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        Context wrapper = new ContextThemeWrapper(requireActivity(), R.style.RoundPopupMenuStyle);
        PopupMenu popupMenu = new PopupMenu(wrapper, moreButton, Gravity.END);
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    assert menuPopupHelper != null;
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);

                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        requireActivity().getMenuInflater().inflate(R.menu.menu_message_more, popupMenu.getMenu());
        moreButton.setOnClickListener(v -> popupMenu.show());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            //noinspection SwitchStatementWithoutDefaultBranch
            if ("지우기".equals(menuItem.getTitle().toString())) {
                if (messageTabLayout.getSelectedTabPosition() == 0) {
                    ((FragmentSendMessage) fragmentMessageSend).resetInputValue();
                    if (fileSelectedAdapter.inputValue != null && fileSelectedAdapter.inputValue.size() > 0) {
                        dialogFileCount.setVisibility(View.VISIBLE);
                        fileSelectedAdapter.inputValue.clear();
                        fileSelectedAdapter.notifyDataSetChanged();
                    }
                } else if (messageTabLayout.getSelectedTabPosition() == 1) {
                    // 파일 경로 생성
                    StringBuilder sb;
                    sb = new StringBuilder();
                    sb.append(requireActivity().getFilesDir().getAbsolutePath());
                    sb.append(File.separator);
                    sb.append(getString(R.string.path_request_notification));
                    // 파일 제거
                    if (new File(sb.toString()).exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        new File(sb.toString()).delete();
                    }
                    // 변수 변경
                    ((FragmentViewSendMessage) fragmentViewSendMessage).reloadTask();
                }else if (messageTabLayout.getSelectedTabPosition() == 2) {
                    // 파일 경로 생성
                    StringBuilder sb;
                    sb = new StringBuilder();
                    sb.append(requireActivity().getFilesDir().getAbsolutePath());
                    sb.append(File.separator);
                    sb.append(getString(R.string.path_receive_notification));
                    // 파일 제거
                    if (new File(sb.toString()).exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        new File(sb.toString()).delete();
                    }
                    // 변수 변경
                    ((FragmentViewResponseMessage) fragmentViewResponseMessage).reloadTask();
                }
            }

            return false;
        });

        // Set button listener
        attachmentsButton.setOnClickListener(v -> uploadManager.showDialog());
    }



    @SuppressLint("Range")
    public FileInfoVO getFilInfo(Uri uri) {
        FileInfoVO fileInfoVO = new FileInfoVO();

        float size;

        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    fileInfoVO.setName(cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));

                    size = ((float) ((int) (cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE)))) / 1024 / 1024);

                    if (size > 20) {
                        return null;
                    }

                    fileInfoVO.setSize(String.valueOf(size).concat(" mb"));
                    fileInfoVO.setUri(uri);
                }
            }
        }

        if (fileInfoVO.getName() == null) {
            fileInfoVO.setName(uri.getLastPathSegment());
            fileInfoVO.setSize("0");
            fileInfoVO.setUri(uri);
        }

        return fileInfoVO;
    }
}