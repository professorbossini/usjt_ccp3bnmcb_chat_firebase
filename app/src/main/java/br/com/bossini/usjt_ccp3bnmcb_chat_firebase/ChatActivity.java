package br.com.bossini.usjt_ccp3bnmcb_chat_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mensagensRecyclerView;
    private ChatAdapter adapter;
    private List <Mensagem> mensagens;
    private EditText mensagemEditText;

    private CollectionReference mMsgsReference;
    private FirebaseUser fireUser;

    private void setupFirebase(){
        mMsgsReference =
                FirebaseFirestore.getInstance().collection(
                        "mensagens"
                );
        fireUser = FirebaseAuth.getInstance().getCurrentUser();
        mMsgsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                mensagens.clear();
                for (DocumentSnapshot document :
                        queryDocumentSnapshots.getDocuments()){
                    Mensagem m = document.toObject(Mensagem.class);
                    mensagens.add(m);
                }
                Collections.sort(mensagens);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setupRecyclerView (){
        mensagensRecyclerView = findViewById(R.id.mensagensRecyclerView);
        mensagens = new ArrayList<>();
        adapter = new ChatAdapter (this, mensagens);
        mensagensRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );
        mensagensRecyclerView.setAdapter(adapter);
    }

    private void setupViews (){
        mensagemEditText =
                findViewById(R.id.mensagemEditText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setupRecyclerView();
        setupViews();
        setupFirebase();
    }

    public void enviarMensagem (View v){
        String texto = mensagemEditText.getText().toString();
        Mensagem m = new Mensagem (fireUser.getEmail(), new Date(), texto);
        mMsgsReference.add(m);
        mensagemEditText.setText("");
        Toast.makeText(
                this,
                getString(R.string.msg_enviada),
                Toast.LENGTH_SHORT
        ).show();
    }

}



class ChatAdapter extends RecyclerView.Adapter <ChatViewHolder>{

    private Context context;
    private List<Mensagem> mensagens;

    public ChatAdapter(Context context, List<Mensagem> mensagens) {
        this.context = context;
        this.mensagens = mensagens;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View raiz = inflater.inflate(
                R.layout.list_item,
                parent,
                false
        );
        return new ChatViewHolder(raiz);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Mensagem m = mensagens.get(position);
        holder.mensagemTextView.setText(
                m.getTexto()
        );
        holder.dataNomeTextView.setText(
                context.getString(
                        R.string.data_nome,
                        DateHelper.format(m.getData()),
                        m.getUsuario()
                )
        );
    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }
}

class ChatViewHolder extends RecyclerView.ViewHolder {

    TextView dataNomeTextView;
    TextView mensagemTextView;

    public ChatViewHolder (View raiz){
        super (raiz);
        dataNomeTextView =
                raiz.findViewById(R.id.dataNomeTextView);
        mensagemTextView =
                raiz.findViewById(R.id.mensagemTextView);
    }
}