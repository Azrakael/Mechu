package com.example.mechu_project.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechu_project.R;
import com.example.mechu_project.model.Message;
import com.example.mechu_project.ShowDetail;

import java.io.File;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> messageList;
    private Context context;

    // MessageAdapter의 생성자
    public MessageAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
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
        if (message.getSentBy() == Message.SENT_BY_USER) {
            holder.leftChatView.setVisibility(View.GONE);
            holder.rightChatView.setVisibility(View.VISIBLE);
            holder.rightChatTv.setText(message.getMessage());
        } else {
            holder.rightChatView.setVisibility(View.GONE);
            holder.leftChatView.setVisibility(View.VISIBLE);
            holder.leftChatTv.setText(message.getMessage());

            if (message.isMenuItem()) {
                holder.menuRecommendationLayout.setVisibility(View.VISIBLE);
                holder.leftChatTv.setVisibility(View.GONE); // 기존 텍스트 숨기기

                holder.menuNameTextView.setText(message.getMenuName());
                holder.menuCalorieTextView.setText(String.format("%s kcal", message.getCalorie()));
                if (message.getFoodImgPath() != null) {
                    File imgFile = new File(message.getFoodImgPath());
                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        holder.menuImageView.setImageBitmap(bitmap);
                    }
                } else {
                    holder.menuImageView.setImageResource(R.drawable.characterlogo);
                }

                // 메뉴 추천 레이아웃에 대한 클릭 리스너 추가
                holder.menuRecommendationLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ShowDetail.class);
                        intent.putExtra("menuName", message.getMenuName());
                        intent.putExtra("menuCalorie", message.getCalorie());
                        intent.putExtra("menuImage", message.getFoodImgPath());
                        context.startActivity(intent);
                    }
                });

            } else {
                holder.menuRecommendationLayout.setVisibility(View.GONE);
                holder.leftChatTv.setVisibility(View.VISIBLE); // 기존 텍스트 보이기
            }
        }
    }

    // 데이터셋의 크기를 반환하는 메서드
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // 각 데이터 항목에 대한 참조를 제공하는 뷰홀더 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatView, rightChatView, menuRecommendationLayout;
        TextView leftChatTv, rightChatTv, menuNameTextView, menuCalorieTextView;
        ImageView menuImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatView = itemView.findViewById(R.id.leftChatView);
            rightChatView = itemView.findViewById(R.id.rightChatView);
            leftChatTv = itemView.findViewById(R.id.leftChatTv);
            rightChatTv = itemView.findViewById(R.id.rightChatTv);
            menuRecommendationLayout = itemView.findViewById(R.id.menuRecommendationLayout);
            menuNameTextView = itemView.findViewById(R.id.menuNameTextView);
            menuCalorieTextView = itemView.findViewById(R.id.menuCalorieTextView);
            menuImageView = itemView.findViewById(R.id.menuImageView);
        }
    }
}
