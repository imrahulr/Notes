package developers.sd.notes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class FullScreenImage extends Activity {

    private TouchImageView touchImageView;
    private Button btnClose;
    private String imageFileString, imageBytes;
    private byte[] imageByte;
    private int type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fullscreen_image);
        touchImageView = (TouchImageView) findViewById(R.id.image_full_screen);
        btnClose = (Button) findViewById(R.id.btnClose);

        Intent i = getIntent();
        type = i.getIntExtra("Type", 0);
        if (type == 1) {
            imageFileString = i.getStringExtra("CameraFile");
            Bitmap bitmap = PictureUtils.getScaledBitmap(imageFileString, this);
            touchImageView.setImageBitmap(bitmap);
        }
        if (type == 2) {
            imageBytes = i.getStringExtra("GalleryPic");
            Bitmap bitmap1 = PictureUtils.getScaledBitmap(imageBytes, this);
            touchImageView.setImageBitmap(bitmap1);
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /*public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context context) {
            gestureDetector = new GestureDetector(context, new GestureListener());
        }
        public void onSwipeLeft(){
        }
        public void onSwipeRight() {
        }
        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
            private static final int SWIPE_DISTANCE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();
                if (Math.abs(distanceX)>Math.abs(distanceY) && Math.abs(distanceX)>SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX)>SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceX > 0) { onSwipeRight(); }
                    else { onSwipeLeft(); }
                    return true;
                }
                return false;
            }
        }*/
    //}
}
