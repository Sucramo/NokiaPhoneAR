package com.example.nokiaphonerecognizer.tensorflow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.nokiaphonerecognizer.MoreInfoActivity;
import com.example.nokiaphonerecognizer.R;

/**
 * Main {@code Activity} class for the Camera app.
 */
public class CameraActivity extends Activity {
    ImageButton flashButton;
    ImageButton settingsButton;
    Button moreInfoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        flashButton = (ImageButton) findViewById(R.id.flash_button);
        settingsButton = (ImageButton) findViewById(R.id.settings_button);
        moreInfoButton = (Button) findViewById(R.id.more_info);


        if (null == savedInstanceState) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.camera_activity_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> onMenuItemCLick(item));
        popup.show();
    }

    //Handles clicks in the dropdown menu
    public boolean onMenuItemCLick(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings_menu_tf:
                Toast.makeText(CameraActivity.this, "This part is not ready yet", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.admin_login_menu_tf:
                Toast.makeText(CameraActivity.this, "This part is not ready yet", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showPopupFlash(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.flash_settings, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> onMenuItemCLickFlash(item));
        popupMenu.show();
    }

    public boolean onMenuItemCLickFlash(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.flash_off:
                Toast.makeText(CameraActivity.this, "This part is not ready yet", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.flash_on:
                Toast.makeText(CameraActivity.this, "This part is not ready yet", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void moreInfoActivityOpener(View v) {
        startActivity(new Intent(CameraActivity.this, MoreInfoActivity.class));
    }

}
