package settings;

import android.hardware.Camera;
import android.os.Bundle;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import camera.CameraSizePair;
import camera.CameraSource;
import com.example.nokiaphonerecognizer.R;
import utils.Utils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.nokiaphonerecognizer.R;


public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        setUpRearCameraPreviewSizePreference();
    }

    private void setUpRearCameraPreviewSizePreference() {
        ListPreference previewSizePreference =
                (ListPreference) findPreference(getString(R.string.pref_key_rear_camera_preview_size));
        if (previewSizePreference == null) {
            return;
        }

        Camera camera = null;
        try {
            camera = Camera.open(CameraSource.CAMERA_FACING_BACK);
            List<CameraSizePair> previewSizeList = Utils.generateValidPreviewSizeList(camera);
            String[] previewSizeStringValues = new String[previewSizeList.size()];
            Map<String, String> previewToPictureSizeStringMap = new HashMap<>();
            for (int i = 0; i < previewSizeList.size(); i++) {
                CameraSizePair sizePair = previewSizeList.get(i);
                previewSizeStringValues[i] = sizePair.preview.toString();
                if (sizePair.picture != null) {
                    previewToPictureSizeStringMap.put(
                            sizePair.preview.toString(), sizePair.picture.toString());
                }
            }
            previewSizePreference.setEntries(previewSizeStringValues);
            previewSizePreference.setEntryValues(previewSizeStringValues);
            previewSizePreference.setSummary(previewSizePreference.getEntry());
            previewSizePreference.setOnPreferenceChangeListener(
                    (preference, newValue) -> {
                        String newPreviewSizeStringValue = (String) newValue;
                        previewSizePreference.setSummary(newPreviewSizeStringValue);
                        PreferenceUtils.saveStringPreference(
                                getActivity(),
                                R.string.pref_key_rear_camera_picture_size,
                                previewToPictureSizeStringMap.get(newPreviewSizeStringValue));
                        return true;
                    });

        } catch (Exception e) {
            // If there's no camera for the given camera id, hide the corresponding preference.
            if (previewSizePreference.getParent() != null) {
                previewSizePreference.getParent().removePreference(previewSizePreference);
            }
        } finally {
            if (camera != null) {
                camera.release();
            }
        }
    }
}
