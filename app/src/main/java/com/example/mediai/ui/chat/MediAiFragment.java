package com.example.mediai.ui.chat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediai.R;
import com.example.mediai.data.local.DatabaseHelper;
import com.example.mediai.data.local.dao.ChatMessageDao;
import com.example.mediai.data.model.ChatMessage;
import com.example.mediai.data.remote.AiApiClient;
import com.example.mediai.data.remote.AiProvider;
import com.example.mediai.data.remote.AiRepositoryRemote;
import com.example.mediai.data.repository.AiRepository;
import com.example.mediai.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MediAiFragment extends Fragment {

    private RecyclerView rvChat;
    private EditText etMessage;
    private Button btnSend;
    private TextView tvTyping;
    private ChatAdapter adapter;
    private AiRepository aiRepository;
    private SessionManager sessionManager;
    private List<ChatMessage> messages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medi_ai, container, false);

        sessionManager = new SessionManager(requireContext());
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(requireContext());
        ChatMessageDao chatMessageDao = new ChatMessageDao(dbHelper);
        aiRepository = new AiRepository(chatMessageDao);

        initViews(view);
        loadChatHistory();

        return view;
    }

    private void initViews(View view) {
        rvChat = view.findViewById(R.id.rv_chat);
        etMessage = view.findViewById(R.id.et_message);
        btnSend = view.findViewById(R.id.btn_send);
        tvTyping = view.findViewById(R.id.tv_typing);

        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadChatHistory() {
        long userId = sessionManager.getUserId();
        messages = aiRepository.getRecentChatHistory(userId, 50);
        if (messages == null) {
            messages = new ArrayList<>();
        }
        adapter = new ChatAdapter(messages);
        rvChat.setAdapter(adapter);
        if (messages.size() > 0) {
            rvChat.smoothScrollToPosition(messages.size() - 1);
        }
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) {
            return;
        }

        etMessage.setText("");

        // Save user message
        long userId = sessionManager.getUserId();
        aiRepository.saveMessage(userId, "user", text, "DEMO");

        // Update adapter
        messages.add(new ChatMessage(0, userId, "user", text, System.currentTimeMillis() / 1000, "DEMO"));
        adapter.notifyItemInserted(messages.size() - 1);
        rvChat.smoothScrollToPosition(messages.size() - 1);

        // Show typing indicator
        tvTyping.setVisibility(View.VISIBLE);

        // Process message
        processMessage(text);
    }

    private void processMessage(String userMessage) {
        long userId = sessionManager.getUserId();
        String provider = sessionManager.getAiProvider();
        String apiKey = sessionManager.getApiKey();

        String response;
        boolean isDemo = provider.equals("DEMO") || apiKey.isEmpty();

        if (isDemo) {
            response = AiRepositoryRemote.getDemoResponse(userMessage);
            addResponse(response, "DEMO");
        } else {
            new ApiCallTask().execute(userMessage, apiKey, provider);
        }
    }

    private void addResponse(String response, String provider) {
        tvTyping.setVisibility(View.GONE);
        long userId = sessionManager.getUserId();

        aiRepository.saveMessage(userId, "assistant", response, provider);
        messages.add(new ChatMessage(0, userId, "assistant", response, System.currentTimeMillis() / 1000, provider));
        adapter.notifyItemInserted(messages.size() - 1);
        rvChat.smoothScrollToPosition(messages.size() - 1);
    }

    private class ApiCallTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String userMessage = params[0];
            String apiKey = params[1];
            String providerStr = params[2];
            AiProvider provider = AiProvider.fromString(providerStr);
            return AiApiClient.callApi(userMessage, apiKey, provider);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                addResponse(result, sessionManager.getAiProvider());
            } else {
                tvTyping.setVisibility(View.GONE);
                Toast.makeText(getContext(), R.string.chat_error_network, Toast.LENGTH_SHORT).show();
            }
        }
    }
}