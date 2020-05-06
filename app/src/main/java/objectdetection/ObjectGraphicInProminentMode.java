package objectdetection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import camera.GraphicOverlay;
import camera.GraphicOverlay.Graphic;
import com.example.nokiaphonerecognizer.R;

/**
 * Draws the detected object info over the camera preview for prominent object detection mode.
 */
class ObjectGraphicInProminentMode extends Graphic {

    private final FirebaseVisionObject object;
    private final ObjectConfirmationController confirmationController;

    private final Paint scrimPaint;
    private final Paint eraserPaint;
    private final Paint boxPaint;
    @ColorInt
    private final int boxGradientStartColor;
    @ColorInt
    private final int boxGradientEndColor;
    private final int boxCornerRadius;

    ObjectGraphicInProminentMode(
            GraphicOverlay overlay,
            FirebaseVisionObject object,
            ObjectConfirmationController confirmationController) {
        super(overlay);

        this.object = object;
        this.confirmationController = confirmationController;

        scrimPaint = new Paint();
        // Sets up a gradient background color at vertical.
        if (confirmationController.isConfirmed()) {
            scrimPaint.setShader(
                    new LinearGradient(
                            0,
                            0,
                            overlay.getWidth(),
                            overlay.getHeight(),
                            ContextCompat.getColor(context, R.color.object_confirmed_bg_gradient_start),
                            ContextCompat.getColor(context, R.color.object_confirmed_bg_gradient_end),
                            TileMode.CLAMP));
        } else {
            scrimPaint.setShader(
                    new LinearGradient(
                            0,
                            0,
                            overlay.getWidth(),
                            overlay.getHeight(),
                            ContextCompat.getColor(context, R.color.object_detected_bg_gradient_start),
                            ContextCompat.getColor(context, R.color.object_detected_bg_gradient_end),
                            TileMode.CLAMP));
        }

        eraserPaint = new Paint();
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        boxPaint = new Paint();
        boxPaint.setStyle(Style.STROKE);
        boxPaint.setStrokeWidth(
                context
                        .getResources()
                        .getDimensionPixelOffset(
                                confirmationController.isConfirmed()
                                        ? R.dimen.bounding_box_confirmed_stroke_width
                                        : R.dimen.bounding_box_stroke_width));
        boxPaint.setColor(Color.WHITE);

        boxGradientStartColor = ContextCompat.getColor(context, R.color.bounding_box_gradient_start);
        boxGradientEndColor = ContextCompat.getColor(context, R.color.bounding_box_gradient_end);
        boxCornerRadius =
                context.getResources().getDimensionPixelOffset(R.dimen.bounding_box_corner_radius);
    }

    @Override
    public void draw(Canvas canvas) {
        RectF rect = overlay.translateRect(object.getBoundingBox());

        // Draws the dark background scrim and leaves the object area clear.
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), scrimPaint);
        canvas.drawRoundRect(rect, boxCornerRadius, boxCornerRadius, eraserPaint);

        // Draws the bounding box with a gradient border color at vertical.
        boxPaint.setShader(
                confirmationController.isConfirmed()
                        ? null
                        : new LinearGradient(
                        rect.left,
                        rect.top,
                        rect.left,
                        rect.bottom,
                        boxGradientStartColor,
                        boxGradientEndColor,
                        TileMode.CLAMP));
        canvas.drawRoundRect(rect, boxCornerRadius, boxCornerRadius, boxPaint);
    }
}