package kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.R;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.activity.ActivityHome;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.Application;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.DownloadFileVO;

public class DownloadManagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<DownloadFileVO> inputValue;
    private final Activity activity;


    public DownloadManagerAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((DownloadManagerAdapter.ViewHolder) holder).onBind(inputValue.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    @Override
    public int getItemCount() {
        return inputValue == null ? 0 : inputValue.size();
    }



    @SuppressLint("NotifyDataSetChanged")
    public void setInputValue(ArrayList<DownloadFileVO> inputValue) {
        this.inputValue = inputValue;

        notifyDataSetChanged();
    }



    private class ViewHolder extends RecyclerView.ViewHolder {
        // UI Object
        private final TextView downloadFileName;
        private final ImageButton downloadButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // UI object setting
            downloadFileName = itemView.findViewById(R.id.downloadFileName);
            downloadButton = itemView.findViewById(R.id.downloadButton);
        }

        public void onBind(DownloadFileVO downloadFileVO) {
            // 다운로드 파일명 설정
            downloadFileName.setText(downloadFileVO.getName());
            // 다운로드 버튼 클릭 이벤트
            downloadButton.setOnClickListener(view -> {
                try {
                    // 파일 다운로드
                    ((ActivityHome) activity).downloadFileForDownloadManager(downloadFileVO);
                }catch (Exception ex) {
                    // 예외 처리
                    ex.printStackTrace();

                    Application.showToast(activity, "다운로드 중 오류가 발생하였습니다.");
                } // try-catch end
            });
        }
    }
}
