package io.halogen.astrim.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.util.Objects;

import io.halogen.astrim.R;
import io.halogen.astrim.About;
import io.halogen.astrim.util.Utilities;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    int heightInPixels;
    DrawerLayout smenu;
    EmojiPopup emojiPopup;
    RecyclerView chatView;
    LinearLayout entryView;
    ViewAdapter chatAdapter;
    EmojiEditText emojiEditText;
    ActionBarDrawerToggle menuToggle;
    Utilities utils = new Utilities();
    final int MIN_KEYBOARD_HEIGHT_PX = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        EmojiManager.install(new GoogleEmojiProvider());
        setContentView(R.layout.activity_chat);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        smenu = findViewById(R.id.slidingMenu);
        ActionBarDrawerToggle menuToggle = new ActionBarDrawerToggle(this, smenu, R.string.drawer_open, R.string.drawer_close);
        smenu.addDrawerListener(menuToggle);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        menuToggle.syncState();
        ImageButton mOpener = findViewById(R.id.menuOpen);
        mOpener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smenu.openDrawer(GravityCompat.START);
            }
        });
        NavigationView navVw = findViewById(R.id.navView);
        navVw.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.srvinfo:
                        Toast.makeText(ChatActivity.this, "Server Info", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.settings:
                        Toast.makeText(ChatActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.about:
                        Intent intent = new Intent(getApplicationContext(), About.class);
                        startActivity(intent);
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

        emojiEditText = findViewById(R.id.chatEntry);
        emojiPopup = EmojiPopup.Builder.fromRootView(smenu).build(emojiEditText);

        smenu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private final Rect windowVisibleDisplayFrame = new Rect();
            private int lastVisibleDecorViewHeight;

            @Override
            public void onGlobalLayout() {
                smenu.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
                final int visibleDecorViewHeight = windowVisibleDisplayFrame.height();
                if (lastVisibleDecorViewHeight != 0) {
                    if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                        int currentKeyboardHeight = smenu.getHeight() - windowVisibleDisplayFrame.bottom;
                        onKeyboardShown(currentKeyboardHeight);
                    } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                        onKeyboardHidden();
                    }
                }
                lastVisibleDecorViewHeight = visibleDecorViewHeight;
            }
        });

        chatView = findViewById(R.id.chatWidget);
        chatView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ViewAdapter();
        chatView.setAdapter(chatAdapter);

        entryView = findViewById(R.id.inputSection);
        if(utils.isNavBarPresent(this)){
            heightInPixels = utils.getNavBarHeight(this) + 15;
            chatView.setPadding(0,0,0,heightInPixels);
            entryView.setPadding(0,0,0,heightInPixels);
        }
        else{
            chatView.setPadding(0,0,0,15);
            entryView.setPadding(0,0,0,15);
        }
        //test
        chatAdapter.addItem(new MessageItem("$SRV", "Server Test will be down for maintenance at 8:30pm",
                "now", "grey"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(menuToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if(smenu.isDrawerOpen(GravityCompat.START)){
            smenu.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    public void toggleEmojiKeyBoard(View view){
        if(emojiPopup.isShowing()){
            emojiPopup.dismiss();
        }
        else{
            emojiPopup.toggle();
        }
    }

    public void onKeyboardShown(int keyboardHeight){
        entryView = findViewById(R.id.inputSection);
        float scale = getResources().getDisplayMetrics().density;
        int toPixels = (int) (35 * scale + 0.5f);
        int padding = (int) (53 * scale + 0.5f);
        int finalHeight = (keyboardHeight - 25) - toPixels;
        if(utils.isNavBarPresent(this)){
            chatView.setPadding(0,0,0,finalHeight + padding);
            entryView.setPadding(0,0,0,finalHeight + padding);
        }
        else{
            chatView.setPadding(0,0,0,finalHeight + padding);
            entryView.setPadding(0,0,0,finalHeight + padding);
        }
    }

    public void onKeyboardHidden(){
        entryView = findViewById(R.id.inputSection);
        if(utils.isNavBarPresent(this)){
            chatView.setPadding(0,0,0,heightInPixels);
            entryView.setPadding(0,0,0,heightInPixels);
        }
        else{
            chatView.setPadding(0,0,0,15);
            entryView.setPadding(0,0,0,15);
        }
    }

    public void onSend(View view) {
        String cTime = utils.getCurrentTime();
        TextView mWidget = findViewById(R.id.chatEntry);
        String message = mWidget.getText().toString();
        int msgLen = message.length();
        if (msgLen > 0) {
            if (msgLen < 2) {//1 char
                message += "              ";//14 spaces
            }
            else if (msgLen < 6) {//5 char
                message += "          ";//10 spaces
            }
            else if (msgLen < 10) {//9 char
                message += "      ";//6 spaces
            }
            chatAdapter.addItem(new MessageItem("$ME!", message, cTime, "sent"));
            utils.playTune(this, 2);
            //test
            chatAdapter.addItem(new MessageItem("MrEcho", message, cTime, "rcvd"));
            mWidget.setText("");
            chatView.scrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }
}