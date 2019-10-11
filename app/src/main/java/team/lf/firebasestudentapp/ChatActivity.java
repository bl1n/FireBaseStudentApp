package team.lf.firebasestudentapp;

import android.accessibilityservice.FingerprintGestureController;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private TextView mUserNameTextView;
    private EditText mEtMessage;
    private Button mBtnSendMessage;

    private RecyclerView mRecycler;

//    private ChatAdapter mAdapter;
    private MessagesAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            MainActivity.start(this);
            finish();
        }
        setContentView(R.layout.activity_chat);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mBtnSendMessage = findViewById(R.id.btn_send_message);
        mEtMessage = findViewById(R.id.et_message);
        mAdapter = new MessagesAdapter();

        mRecycler = findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);

        mBtnSendMessage.setOnClickListener(v -> {
            if (mEtMessage.getText().toString().trim().length() != 0 && user != null) {
                Message message = new Message(user.getDisplayName(), mEtMessage.getText().toString());
                db.collection("messages")
                        .add(message)
                        .addOnSuccessListener(documentReference -> {
                            Log.d(TAG, "message is sent");
                            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "onCreate:  " + e.getMessage());
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

            } else {
                mEtMessage.setError("Некорректный текст");
            }
            mEtMessage.setText("");
            hideKeyboard(this);
        });

        updateAdapter(db);

//        db.collection("messages")
//                .addSnapshotListener((snapshot, e) -> {
//                    if (e != null) {
//                        Log.w(TAG, "Listen failed.", e);
//                        return;
//                    }
//                    if (snapshot != null) {
//                        Log.d(TAG, "Current data: " + snapshot.getDocumentChanges());
//                        for (DocumentChange documentChange:snapshot.getDocumentChanges()){
//                            Message message = documentChange.getDocument().toObject(Message.class);
//                            mAdapter.addMessage(message);
//                        }
//
//                    } else {
//                        Log.d(TAG, "Current data: null");
//                    }
//
//                });



//        mUserNameTextView = findViewById(R.id.tv_username);
//        if (user != null) {
//            mUserNameTextView.setText(user.getDisplayName());
//        }
//        Timestamp timestamp = snapshot.getTimestamp("created_at");
//        java.util.Date date = timestamp.toDate();
    }

    private void updateAdapter(FirebaseFirestore db) {
//        mAdapter.clear();
        db.collection("messages")
                .get()
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "onCreate: task");
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Message message = document.toObject(Message.class);
                            mAdapter.addMessage(message);
                        }
                    } else {
                        Log.e(TAG, "Error getting documents.", task.getException());
                    }
                });
        Log.d(TAG, "onCreate: task1");

    }


    public static void start(Context context) {
        if (context != null) {
            Intent intent = new Intent(context, ChatActivity.class);
            context.startActivity(intent);
        } else {
            throw new IllegalArgumentException("Context can't be null");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_logout: {
                logout();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        AuthUI.getInstance()
                .signOut(getApplicationContext())
                .addOnCompleteListener(task -> {
                    MainActivity.start(getApplicationContext());
                    finish();
                });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
