package com.example.mistareas.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mistareas.R;
import com.example.mistareas.adapters.MessageAdapter;
import com.example.mistareas.adapters.MyPostsAdapter;
import com.example.mistareas.models.Chat;
import com.example.mistareas.models.Message;
import com.example.mistareas.models.User;
import com.example.mistareas.providers.AuthProvider;
import com.example.mistareas.providers.ChatsProvider;
import com.example.mistareas.providers.MessageProvider;
import com.example.mistareas.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUser1;
    String mExtraIdUser2;
    String mExtraIdChat;

    ChatsProvider mChatsProvider;
    MessageProvider mMessageProvider;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;

    EditText mEditTextMessage;
    ImageView mImageViewSendMessage;

    CircleImageView mCircleImageProfile;
    ImageView mImageViewUsername;
    TextView mTextViewRelativeTime;
    TextView mTextViewUsername;
    ImageView mImageViewBack;
    RecyclerView mRecyclerViewMessage;

    LinearLayoutManager mLinearLayoutManager;

    MessageAdapter mAdapter;

    View mActionBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatsProvider = new ChatsProvider();
        mMessageProvider = new MessageProvider();
        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();

        mEditTextMessage = findViewById(R.id.editTextMessage);
        mImageViewSendMessage = findViewById(R.id.imageViewSendMessage);
        mRecyclerViewMessage = findViewById(R.id.recyclerViewMessage);

        mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerViewMessage.setLayoutManager(mLinearLayoutManager);

        mExtraIdUser1 = getIntent().getStringExtra("idUser1");
        mExtraIdUser2 = getIntent().getStringExtra("idUser2");
        mExtraIdChat = getIntent().getStringExtra("idChat");

        showCustomToolbar(R.layout.custom_chat_toolbar);

        mImageViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        checkIfChatExists();
    }

    @Override
    public void onStart(){
        super.onStart();
        if(mAdapter != null){
            mAdapter.startListening();
        }

    }

    @Override
    public void onStop(){
        super.onStop();
        mAdapter.stopListening();
    }

    //Obtiene los mensaje de la base de datos
    private void getMessageChat(){
        Query query = mMessageProvider.getMessageByChat(mExtraIdChat);
        FirestoreRecyclerOptions<Message> options =
                new FirestoreRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .build();
        mAdapter = new MessageAdapter(options, ChatActivity.this);
        mRecyclerViewMessage.setAdapter(mAdapter);
        mAdapter.startListening();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateViewed();
                int nMessages = mAdapter.getItemCount();
                int lastMessagePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                //Siempre abre el chat en el último mensaje
                if(lastMessagePosition == -1 || (positionStart >= (nMessages -1) && lastMessagePosition == (positionStart - 1))){
                    mRecyclerViewMessage.scrollToPosition(positionStart);
                }
            }
        });
    }

    //Envía un mensaje a la base de datos y actualiza el chat en tiempo real
    private void sendMessage() {
        String textMessage = mEditTextMessage.getText().toString();
        if(!textMessage.isEmpty()){
            Message message = new Message();
            message.setIdChat(mExtraIdChat);
            if(mAuthProvider.getUid().equals(mExtraIdUser1)){
                message.setIdSender(mExtraIdUser1);
                message.setIdReceiver(mExtraIdUser2);
            }else{
                message.setIdSender(mExtraIdUser2);
                message.setIdReceiver(mExtraIdUser1);
            }
            message.setTimestamp(new Date().getTime());
            message.setViewed(false);
            message.setMessage(textMessage);

            mMessageProvider.create(message).addOnCompleteListener((task -> {
                if(task.isSuccessful()){
                    mEditTextMessage.setText("");
                    mAdapter.notifyDataSetChanged();
                }
            }));
        }
    }

    //Método para mostrar una toolbar personalizada que nos indique con quién estamos chateando
    private void showCustomToolbar(int resource) {
        Toolbar toolbar = findViewById(R.id.action_bar_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarView = inflater.inflate(resource, null);
        actionBar.setCustomView(mActionBarView);
        mCircleImageProfile = mActionBarView.findViewById(R.id.circleImageProfile);
        mTextViewUsername = mActionBarView.findViewById(R.id.textViewUserName);
        mImageViewBack = mActionBarView.findViewById(R.id.imageViewBack);

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getUserInfo();

    }

    //Nos permite recopilar la información (nombre y foto de perfil) de la persona con la que estamos chateando
    private void getUserInfo() {
        String idUserInfo = "";
        if (mAuthProvider.getUid().equals(mExtraIdUser1)){
            idUserInfo = mExtraIdUser2;
        }else{
            idUserInfo = mExtraIdUser1;
        }
        mUsersProvider.getUser(idUserInfo).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        mTextViewUsername.setText(username);
                    }
                    if(documentSnapshot.contains("image_profile")){
                        String imageProfile = documentSnapshot.getString("image_profile");
                        if(imageProfile != null){
                            if(!imageProfile.equals("")){
                                Picasso.with(ChatActivity.this).load(imageProfile).into(mCircleImageProfile);
                            }
                        }
                    }
                }
            }
        });
    }

    //Comprueba si ya existe un chat creado con el usuario. Si lo hay, lo abre y recoge todos los mensajes de la base de datos. Si no lo hay, crea uno nuevo.
    private void checkIfChatExists(){
        mChatsProvider.getChatbyUser1AndUser2(mExtraIdUser1, mExtraIdUser2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if(size == 0){
                    createChat();
                }else{
                    mExtraIdChat = queryDocumentSnapshots.getDocuments().get(0).getId();
                    getMessageChat();
                    updateViewed();
                }
            }
        });
    }

    //Comprueba si un mensaje ha sido visto
    private void updateViewed() {
        String idSender = "";

        if(mAuthProvider.getUid().equals(mExtraIdUser1)){
            idSender = mExtraIdUser2;
        }else{
            idSender = mExtraIdUser1;
        }

        mMessageProvider.getMessageByChatAndSender(mExtraIdChat, idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                    mMessageProvider.updateViewed(document.getId(), true);
                }
            }
        });

    }

    //Crea un nuevo chat entre dos usuariuos si no existe aún en la base de datos. La ID de un chat es la concatenación
    // de las ids de los dos usuarios que lo comparten.
    private void createChat() {
        Chat chat = new Chat();
        chat.setIdUser1(mExtraIdUser1);
        chat.setIdUser2(mExtraIdUser2);
        chat.setWriting(false);
        chat.setTimestamp(new Date().getTime());
        chat.setId(mExtraIdUser1 + mExtraIdUser2);

        ArrayList<String> arrayIds = new ArrayList<>();
        arrayIds.add(mExtraIdUser1);
        arrayIds.add(mExtraIdUser2);
        chat.setArrayIds(arrayIds);
        mChatsProvider.create(chat);
        mExtraIdChat = chat.getId();
        getMessageChat();
    }
}