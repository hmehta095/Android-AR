package com.example.lambtonar;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    private ExternalTexture texture;
    private MediaPlayer mediaPlayer;
    private customAR arFragment;
    private Scene scene;
    private ModelRenderable renderable;
    private boolean isImageDetected = false;


//    @Override
//    public boolean onTouchEvent(final MotionEvent event)
//    {
//        if (event.getAction() == MotionEvent.ACTION_UP)
//        {
//            float x = event.getX();
//            float y = event.getY();
//            float touchMajor = event.getTouchMajor();
//            float touchMinor = event.getTouchMinor();
//
//            Rect touchRect = new Rect((int)(x - touchMajor / 2), (int)(y - touchMinor / 2), (int)(x + touchMajor / 2), (int)(y + touchMinor / 2));
//
//            this.submitFocusAreaRect(touchRect);
//        }
//        return true;
//    }
//
//    private void submitFocusAreaRect(final Rect touchRect)
//    {
//        Camera.Parameters cameraParameters = camera.getParameters();
//
//        if (cameraParameters.getMaxNumFocusAreas() == 0)
//        {
//            return;
//        }
//
//        // Convert from View's width and height to +/- 1000
//
//        Rect focusArea = new Rect();
//
//        focusArea.set(touchRect.left * 2000 ,
//                touchRect.top * 2000 ,
//                touchRect.right * 2000 ,
//                touchRect.bottom * 2000 );
//
//        // Submit focus area to camera
//
//        ArrayList<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
//        focusAreas.add(new Camera.Area(focusArea, 1000));
//
//        cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//        cameraParameters.setFocusAreas(focusAreas);
//        camera.setParameters(cameraParameters);
//
//        // Start the autofocus operation
//
//        camera.autoFocus(arFragment);
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        texture = new ExternalTexture();

        mediaPlayer = MediaPlayer.create(this,R.raw.video);
//        at this point our media player has been set to external texture
        mediaPlayer.setSurface(texture.getSurface());
        mediaPlayer.setLooping(true);

//        building the model on which we are playing the video
        ModelRenderable
                .builder()
                .setSource(this, Uri.parse("video_screen.sfb"))
                .build()
                .thenAccept(modelRenderable -> {
                    modelRenderable.getMaterial().setExternalTexture("videoTexture",texture); // our media has been attached to the model renderable
//                    to filter out the croma key color from the video
                    modelRenderable.getMaterial().setFloat4("keyColor",new Color(0.01843f,1f,0.098f));
                    renderable = modelRenderable;
                });


//        refer the arFragment object to customAR fragment that is in activity xml file
        arFragment = (customAR) getSupportFragmentManager().findFragmentById(R.id.arFragment);

//        refering the scene object to the scene fragment ar of scene view
        scene = arFragment.getArSceneView().getScene();

//        adding the scene to the update scene listener
        scene.addOnUpdateListener(this::onUpdate);


    }

    private void onUpdate(FrameTime frameTime) {
//       if its gets true i am playing the video on detected image once
        if(isImageDetected)
            return;
//          refering to the arscene view
            Frame frame = arFragment.getArSceneView().getArFrame();

//            create a collection of augmented images and geting the trackable of aumented image class from the frame
        Collection<AugmentedImage> augmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage image:augmentedImages){
            if(image.getTrackingState() == TrackingState.TRACKING){
                if (image.getName().equals("image")){
                    isImageDetected = true;

                    playVideo(image.createAnchor(image.getCenterPose()),image.getExtentX(),image.getExtentZ());
                    break;
                }
            }
        }
    }

    private void playVideo(Anchor anchor, float extentX, float extentZ) {
    mediaPlayer.start();
        AnchorNode anchorNode = new AnchorNode(anchor);

        texture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
            anchorNode.setRenderable(renderable);
            texture.getSurfaceTexture().setOnFrameAvailableListener(null);
        });

        anchorNode.setWorldScale(new Vector3(extentX,1f,extentZ));
        scene.addChild(anchorNode);

    }
}
