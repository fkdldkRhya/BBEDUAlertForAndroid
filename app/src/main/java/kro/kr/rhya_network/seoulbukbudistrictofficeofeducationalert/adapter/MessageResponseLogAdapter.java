package kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.adapter;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.R;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.fragment.FragmentViewResponseMessage;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.fragment.FragmentViewSendMessage;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.Application;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.DialogManager;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.ResponseMessageVO;

public class MessageResponseLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ResponseMessageVO> inputValue;
    private final Fragment fragment;


    public MessageResponseLogAdapter(Fragment fragment) {
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_message_log, parent, false);
        return new MessageResponseLogAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MessageResponseLogAdapter.ViewHolder) holder).onBind(inputValue.get(position));
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
    public void setInputValue(ArrayList<ResponseMessageVO> inputValue) {
        this.inputValue = inputValue;

        notifyDataSetChanged();
    }



    private class ViewHolder extends RecyclerView.ViewHolder {
        // UI Object
        private final TextView date;
        private final TextView title;
        private final ImageButton removeButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // UI object setting
            date = itemView.findViewById(R.id.date);
            title = itemView.findViewById(R.id.title);
            removeButton = itemView.findViewById(R.id.removeButton);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    ResponseMessageVO requestMessageVO = inputValue.get(pos);

                    try {
                        // 메시지 전송 Dialog 표시
                        DialogManager dialogManager = new DialogManager(fragment.getActivity());
                        dialogManager.setContentView(R.layout.dialog_message_info_for_response);
                        dialogManager.setCancelable(true);
                        dialogManager.setBackground(null);
                        dialogManager.setGravity(Gravity.BOTTOM);
                        dialogManager.setDialogSizeWithCustomSize(1);
                        ((TextView) dialogManager.dialog.findViewById(R.id.title)).setText(requestMessageVO.getTitle());
                        ((TextView) dialogManager.dialog.findViewById(R.id.message)).setText(requestMessageVO.getMessage().replace("#</br>#", System.lineSeparator()));

                        ((Button) dialogManager.dialog.findViewById(R.id.button)).setOnClickListener(v1 -> dialogManager.dismissDialog());

                        dialogManager.showDialog();
                    }catch (Exception ex) {
                        ex.printStackTrace();

                        Application.showToast(fragment.getContext(), ex.getMessage());
                    }
                }
            });
        }

        public void onBind(ResponseMessageVO downloadFileVO) {
            date.setText(downloadFileVO.getDate());
            title.setText("제목: ".concat(downloadFileVO.getTitle()));

            // 다운로드 버튼 클릭 이벤트
            removeButton.setOnClickListener(view -> {
                try {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        notifyItemRemoved(pos);
                        inputValue.remove(pos);

                        ((FragmentViewResponseMessage) fragment).count.setText(String.valueOf(inputValue.size()).concat("개"));

                        // 파일 저장
                        ((FragmentViewResponseMessage) fragment).saveFileData(inputValue);
                    }
                }catch (Exception ex) {
                    ex.printStackTrace();

                    Application.showToast(fragment.requireActivity(), ex.getMessage());
                }
            });
        }
    }
}
