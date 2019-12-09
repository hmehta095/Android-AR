package com.example.lambtonar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

// extands the arFragment to enable augmented images
public class customAR extends ArFragment {


    @Override
    protected Config getSessionConfiguration(Session session) {

//        pass the session object as a paramter to the constructor of thiis class
        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);

        AugmentedImageDatabase aid = new AugmentedImageDatabase(session);

        // To detect the image
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.image);
//        Now adding this bitmap in augmented image database
        aid.addImage("image",image);   // 1st one is reference and second is bitmap

        config.setAugmentedImageDatabase(aid);

        //configuring the session bycalling this
        this.getArSceneView().setupSession(session);

        return config;
    }

//    removing the hand motion when app starts
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout = (FrameLayout) super.onCreateView(inflater,container,savedInstanceState);

        getPlaneDiscoveryController().hide();
        getPlaneDiscoveryController().setInstructionView(null);

        return frameLayout;

    }

//    @Override
//    public void onAutoFocus(boolean success, Camera camera) {
//
//    }
}
