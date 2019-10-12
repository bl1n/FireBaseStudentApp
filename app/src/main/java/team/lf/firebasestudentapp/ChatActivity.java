package team.lf.firebasestudentapp;

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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.List;

public class ChatActivity extends AppCompatActivity  implements EventListener<QuerySnapshot>{
    private static final String TAG = "ChatActivity";

    private EditText mEtMessage;

    private RecyclerView mRecycler;

    private MessagesAdapter mAdapter;
    private ListenerRegistration registration;
    private CollectionReference mReference;


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
        mReference = db.collection("messages");

        Button btnSendMessage = findViewById(R.id.btn_send_message);
        mEtMessage = findViewById(R.id.et_message);
        mAdapter = new MessagesAdapter();

        mRecycler = findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);


        btnSendMessage.setOnClickListener(v -> {
            if (mEtMessage.getText().toString().trim().length() != 0 && user != null) {
                Message message = new Message(user.getDisplayName(), mEtMessage.getText().toString());
                mReference
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

        registerListener();

    }
    public void registerListener(){
        registration = mReference.addSnapshotListener(this);
    }

    public void unregisterListener(){
        registration.remove();
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
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mi_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        unregisterListener();
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

    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.e(TAG, "Listen failed.", e);
            return;
        }
        if (queryDocumentSnapshots != null) {
            List<Message> list = queryDocumentSnapshots.toObjects(Message.class);
            Collections.sort(list, (o1, o2) -> Long.compare(o1.getTimestamp().getSeconds(), o2.getTimestamp().getSeconds()));
            mAdapter.submitList(list);
            mRecycler.smoothScrollToPosition(mAdapter.getItemCount());
        } else {
            Log.d(TAG, "Current data: null");
        }
    }
}
