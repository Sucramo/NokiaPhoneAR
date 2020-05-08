/* Copyright 2017 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.example.nokiaphonerecognizer.poets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.example.nokiaphonerecognizer.MainActivity;
import com.example.nokiaphonerecognizer.R;

/** Main {@code Activity} class for the Camera app. */
public class CameraActivity extends Activity {
    ImageButton flashButton;
    ImageButton settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

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
            case R.id.main_acti_menu:
                startActivity(new Intent(CameraActivity.this, MainActivity.class));
                return true;
            case R.id.settings_menu_tf:
                Toast.makeText(CameraActivity.this, "This part is not ready yet", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.admin_login_menu_tf:
                Toast.makeText(CameraActivity.this, "This part is not ready yet", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
