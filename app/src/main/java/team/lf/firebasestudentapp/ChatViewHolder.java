package team.lf.firebasestudentapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class ChatViewHolder extends RecyclerView.ViewHolder {
    private TextView mUserName;
    private TextView mDate;
    private TextView mText;

    ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        mUserName = itemView.findViewById(R.id.tvItemUserName);
        mDate = itemView.findViewById(R.id.tvItemDate);
        mText = itemView.findViewById(R.id.tvItemText);
    }

    void bind(Message message) {
        mUserName.setText(message.getUserName());
//            mDate.setText(message.getTimestamp().toString());
        mText.setText(message.getText());
    }
}
