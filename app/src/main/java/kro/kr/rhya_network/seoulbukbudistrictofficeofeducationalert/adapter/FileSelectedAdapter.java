package kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.R;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.fragment.FragmentMessage;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.Application;
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils.FileInfoVO;

public class FileSelectedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<FileInfoVO> inputValue;
    private final Fragment fragment;


    public FileSelectedAdapter(Fragment fragment) {
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_upload, parent, false);
        return new FileSelectedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((FileSelectedAdapter.ViewHolder) holder).onBind(inputValue.get(position));
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
    public void setInputValue(ArrayList<FileInfoVO> inputValue) {
        this.inputValue = inputValue;

        notifyDataSetChanged();
    }


    private class ViewHolder extends RecyclerView.ViewHolder {
        // UI Object
        private final TextView fileName;
        private final TextView fileSize;
        private final ImageButton removeButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // UI object setting
            fileName = itemView.findViewById(R.id.fileName);
            fileSize = itemView.findViewById(R.id.fileSize);
            removeButton = itemView.findViewById(R.id.removeButton);
        }

        public void onBind(FileInfoVO fileInfoVO) {
            fileName.setText(fileInfoVO.getName());
            fileSize.setText(fileInfoVO.getSize());

            removeButton.setOnClickListener(v -> {
                try {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        notifyItemRemoved(pos);
                        inputValue.remove(pos);

                        if (inputValue.size() <= 0) {
                            ((FragmentMessage) fragment).dialogFileCount.setVisibility(View.VISIBLE);
                        }
                    }
                }catch (Exception ex) {
                    ex.printStackTrace();

                    Application.showToast(fragment.requireActivity(), ex.getMessage());
                }
            });
        }
    }
}