package objectdetection;

import android.content.res.Resources;
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
import camera.GraphicOverlay;
import camera.GraphicOverlay.Graphic;
import com.example.nokiaphonerecognizer.R;

/**
 * Draws the detected object info over the camera preview for multiple objects detection mode.
 */
class ObjectGraphicInMultiMode extends Graphic {

    private final DetectedObject object;
    private final ObjectConfirmationController confirmationController;

    private final Paint boxPaint;
    private final Paint scrimPaint;
    private final Paint eraserPaint;
    @ColorInt
    private final int boxGradientStartColor;
    @ColorInt
    private final int boxGradientEndColor;
    private final int boxCornerRadius;
    private final int minBoxLen;

    ObjectGraphicInMultiMode(
            GraphicOverlay overlay,
            DetectedObject object,
            ObjectConfirmationController confirmationController) {
        super(overlay);

        this.object = object;
        this.confirmationController = confirmationController;

        Resources resources = context.getResources();
        boxPaint = new Paint();
        boxPaint.setStyle(Style.STROKE);
        boxPaint.setStrokeWidth(
                resources.getDimensionPixelOffset(
                        confirmationController.isConfirmed()
                                ? R.dimen.bounding_box_confirmed_stroke_width
                                : R.dimen.bounding_box_stroke_width));
        boxPaint.setColor(Color.WHITE);

        boxGradientStartColor = ContextCompat.getColor(context, R.color.bounding_box_gradient_start);
        boxGradientEndColor = ContextCompat.getColor(context, R.color.bounding_box_gradient_end);
        boxCornerRadius = resources.getDimensionPixelOffset(R.dimen.bounding_box_corner_radius);

        scrimPaint = new Paint();
        scrimPaint.setShader(
                new LinearGradient(
                        0,
                        0,
                        overlay.getWidth(),
                        overlay.getHeight(),
                        ContextCompat.getColor(context, R.color.object_confirmed_bg_gradient_start),
                        ContextCompat.getColor(context, R.color.object_confirmed_bg_gradient_end),
                        TileMode.MIRROR));

        eraserPaint = new Paint();
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        minBoxLen =
                resources.getDimensionPixelOffset(R.dimen.object_reticle_outer_ring_stroke_radius) * 2;
    }

    @Override
    public void draw(Canvas canvas) {
        RectF rect = overlay.translateRect(object.getBoundingBox());

        float boxWidth = rect.width() * confirmationController.getProgress();
        float boxHeight = rect.height() * confirmationController.getProgress();
        if (boxWidth < minBoxLen || boxHeight < minBoxLen) {
            // Don't draw the box if its length is too small, otherwise it will intersect with reticle so
            // the UI looks messy.
            return;
        }

        float cx = (rect.left + rect.right) / 2;
        float cy = (rect.top + rect.bottom) / 2;
        rect =
                new RectF(cx - boxWidth / 2f, cy - boxHeight / 2f, cx + boxWidth / 2f, cy + boxHeight / 2f);

        if (confirmationController.isConfirmed()) {
            // Draws the dark background scrim and leaves the object area clear.
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), scrimPaint);
            canvas.drawRoundRect(rect, boxCornerRadius, boxCornerRadius, eraserPaint);
        }

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
                        TileMode.MIRROR));
        canvas.drawRoundRect(rect, boxCornerRadius, boxCornerRadius, boxPaint);
    }
}