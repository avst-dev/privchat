package com.example.kodingapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;

import org.json.JSONObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class MainActivity extends Activity {
    
    private String currentUsername = "";
    private String encodedPhotoBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII="; 
    private final String historyFileName = "chat_history_secure.txt";
    
    private WebSocketClient webSocketClient;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatMessage> chatList;
    private ListView lvChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLoginScreen();
    }

    private void showLoginScreen() {
        setContentView(R.layout.activity_login);

        final EditText etServerUrl = (EditText) findViewById(R.id.etServerUrl);
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        Button btnConnect = (Button) findViewById(R.id.btnConnect);
        Button btnSelectPhoto = (Button) findViewById(R.id.btnSelectPhoto);

        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 101);
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverUrl = etServerUrl.getText().toString().trim();
                currentUsername = etUsername.getText().toString().trim();

                if (serverUrl.isEmpty() || currentUsername.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Harap isi semua kolom!", Toast.LENGTH_SHORT).show();
                    return;
                }
                connectToCustomApi(serverUrl);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            InputStream imageStream = null;
            try {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    
                    Bitmap scaled = Bitmap.createScaledBitmap(selectedImage, 120, 120, true);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    scaled.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] b = baos.toByteArray();
                    
                    encodedPhotoBase64 = Base64.encodeToString(b, Base64.DEFAULT);
                    Toast.makeText(this, "Foto Profil Berhasil Diproses!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Gagal memproses foto", Toast.LENGTH_SHORT).show();
            } finally {
                if (imageStream != null) {
                    try {
                        imageStream.close();
                    } catch (Exception ignored) {}
                }
            }
        }
    }

    private void connectToCustomApi(String url) {
        try {
            URI uri = new URI(url);
            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showChatScreen();
                        }
                    });
                }

                @Override
                public void onMessage(final String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            prosesPesanMasuk(message);
                        }
                    });
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showLoginScreen();
                        }
                    });
                }

                @Override
                public void onError(Exception ex) { 
                    ex.printStackTrace(); 
                }
            };
            webSocketClient.connect();
        } catch (Exception e) {
            Toast.makeText(this, "URL API bermasalah!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showChatScreen() {
        setContentView(R.layout.activity_main);

        lvChats = (ListView) findViewById(R.id.lvChats);
        final EditText etMessage = (EditText) findViewById(R.id.etMessage);
        Button btnSend = (Button) findViewById(R.id.btnSend);

        chatList = new ArrayList<ChatMessage>();
        chatAdapter = new ChatAdapter(this, chatList);
        lvChats.setAdapter(chatAdapter);

        muatRiwayatChatLokal();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etMessage.getText().toString().trim();
                if (!text.isEmpty()) {
                    kirimPesanKeApi(text);
                    etMessage.setText("");
                }
            }
        });
    }

    private void kirimPesanKeApi(String isiPesan) {
        try {
            JSONObject json = new JSONObject();
            json.put("pengirim", currentUsername);
            json.put("foto", encodedPhotoBase64);
            json.put("pesan", isiPesan);
            if (webSocketClient != null && webSocketClient.isOpen()) {
                webSocketClient.send(json.toString());
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    private void prosesPesanMasuk(String rawJson) {
        try {
            JSONObject json = new JSONObject(rawJson);
            String nama = json.getString("pengirim");
            String pesanText = json.getString("pesan");
            String fotoBase64 = json.getString("foto");
            
            boolean isMe = nama.equals(currentUsername);

            ChatMessage msg = new ChatMessage(nama, pesanText, fotoBase64, isMe);
            chatList.add(msg);
            chatAdapter.notifyDataSetChanged();

            String barisSimpan = nama + "|" + fotoBase64 + "|" + pesanText;
            simpanChatKeMemoriInternal(barisSimpan);
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    private void simpanChatKeMemoriInternal(String teksData) {
        BufferedWriter writer = null;
        try {
            FileOutputStream fos = openFileOutput(historyFileName, MODE_APPEND);
            writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(teksData);
            writer.newLine();
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ignored) {}
            }
        }
    }

    private void muatRiwayatChatLokal() {
        BufferedReader reader = null;
        try {
            FileInputStream fis = openFileInput(historyFileName);
            reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] part = line.split("\\|");
                if (part.length >= 3) {
                    boolean isMe = part[0].equals(currentUsername);
                    chatList.add(new ChatMessage(part[0], part[2], part[1], isMe));
                }
            }
            chatAdapter.notifyDataSetChanged();
        } catch (FileNotFoundException e) {
            // Abaikan jika berkas history belum ada
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {}
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}