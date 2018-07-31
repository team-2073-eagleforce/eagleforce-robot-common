package com.team2073.common.svc.camera;

import com.google.inject.Inject;
import com.google.inject.Provider;
import edu.wpi.cscore.UsbCamera;

/**
 * @author pbriggs
 */
public class CameraRecoveryService {
    private final Provider<UsbCamera> cameraProvider;
    private UsbCamera camera;
    private boolean cameraHasBeenUnplugged;

    @Inject
    public CameraRecoveryService(Provider<UsbCamera> cameraProvider) {
        this.cameraProvider = cameraProvider;
        camera = cameraProvider.get();
    }

    public void keepCameraAlive() {
        if (!camera.isConnected()) {
            cameraHasBeenUnplugged = true;
        } else if (cameraHasBeenUnplugged) {
            camera = cameraProvider.get();
            cameraHasBeenUnplugged = false;
        }
    }
}
