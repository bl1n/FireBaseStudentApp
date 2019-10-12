package team.lf.firebasestudentapp;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

class MessageViewHolder extends RecyclerView.ViewHolder {
    private TextView mUserName;
    private TextView mDate;
    private TextView mText;

    MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        mUserName = itemView.findViewById(R.id.tvItemUserName);
        mDate = itemView.findViewById(R.id.tvItemDate);
        mText = itemView.findViewById(R.id.tvItemText);
    }

    void bind(Message message) {

        mUserName.setText(message.getUserName());
        mDate.setText(Utils.getTime(message.getTimestamp().toDate()));
        mText.setText(message.getText());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null && user.getDisplayName()!=null && user.getDisplayName().equals(message.getUserName())){
            ((CardView)itemView.findViewById(R.id.card)).setCardBackgroundColor(itemView.getResources().getColor(R.color.primary_light));
        }
    }
}
