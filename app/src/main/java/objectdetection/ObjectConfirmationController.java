package objectdetection;

import android.os.CountDownTimer;
import androidx.annotation.Nullable;
import camera.GraphicOverlay;
import settings.PreferenceUtils;

/**
 * Controls the progress of object confirmation before performing additional operation on the
 * detected object.
 */
class ObjectConfirmationController {

    private final CountDownTimer countDownTimer;

    @Nullable
    private Integer objectId = null;
    private float progress = 0;

    /**
     * @param graphicOverlay Used to refresh camera overlay when the confirmation progress updates.
     */
    ObjectConfirmationController(GraphicOverlay graphicOverlay) {
        long confirmationTimeMs = PreferenceUtils.getConfirmationTimeMs(graphicOverlay.getContext());
        countDownTimer =
                new CountDownTimer(confirmationTimeMs, /* countDownInterval= */ 20) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        progress = (float) (confirmationTimeMs - millisUntilFinished) / confirmationTimeMs;
                        graphicOverlay.invalidate();
                    }

                    @Override
                    public void onFinish() {
                        progress = 1;
                    }
                };
    }

    void confirming(Integer objectId) {
        if (objectId.equals(this.objectId)) {
            // Do nothing if it's already in confirming.
            return;
        }

        reset();
        this.objectId = objectId;
        countDownTimer.start();
    }

    boolean isConfirmed() {
        return Float.compare(progress, 1) == 0;
    }

    void reset() {
        countDownTimer.cancel();
        objectId = null;
        progress = 0;
    }

    /** Returns the confirmation progress described as a float value in the range of [0, 1]. */
    float getProgress() {
        return progress;
    }
}
