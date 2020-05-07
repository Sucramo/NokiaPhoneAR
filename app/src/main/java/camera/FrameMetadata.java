package camera;

/** Metadata info of a camera frame. */
public class FrameMetadata {

    public final int width;
    public final int height;
    public final int rotation;


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRotation() {
        return rotation;
    }


    public FrameMetadata(int width, int height, int rotation) {
        this.width = width;
        this.height = height;
        this.rotation = rotation;

    }

    /** Builder of {@link FrameMetadata}. */
    public static class Builder {

        public int width;
        public int height;
        public int rotation;


        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setRotation(int rotation) {
            this.rotation = rotation;
            return this;
        }


        public FrameMetadata build() {
            return new FrameMetadata(width, height, rotation);
        }
    }
}