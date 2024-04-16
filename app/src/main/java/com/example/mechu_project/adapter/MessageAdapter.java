package com.example.mechu_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechu_project.R;
import com.example.mechu_project.model.Message;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> messageList;

    // MessageAdapter의 생성자
    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    // ViewHolder 객체를 생성하여 반환하는 메서드
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chat_item, parent, false);
        return new ViewHolder(view);
    }

    // ViewHolder에 데이터를 바인딩하는 메서드
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if(message.getSentBy() == Message.SENT_BY_USER){
            holder.leftChatView.setVisibility(View.GONE);
            holder.rightChatView.setVisibility(View.VISIBLE);
            holder.rightChatTv.setText(message.getMessage());
        } else {
            holder.rightChatView.setVisibility(View.GONE);
            holder.leftChatView.setVisibility(View.VISIBLE);
            holder.leftChatTv.setText(message.getMessage());
        }
    }

    // 데이터셋의 크기를 반환하는 메서드
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // 각 데이터 항목에 대한 참조를 제공하는 뷰홀더 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatView, rightChatView;
        TextView leftChatTv, rightChatTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatView = itemView.findViewById(R.id.leftChatView);
            rightChatView = itemView.findViewById(R.id.rightChatView);
            leftChatTv = itemView.findViewById(R.id.leftChatTv);
            rightChatTv = itemView.findViewById(R.id.rightChatTv);
        }
    }
}