package camera;

import java.nio.ByteBuffer;

/** An interface to process the input camera frame and perform detection on it. */
public interface FrameProcessor {

    /** Processes the input frame with the underlying detector. */
    void process(ByteBuffer data, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay);

    /** Stops the underlying detector and release resources. */
    void stop();
}
