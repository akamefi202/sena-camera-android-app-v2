package com.sena.senacamera.Function.CameraAction;

import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.Presenter.PreviewPresenter;
import com.sena.senacamera.SdkApi.CameraAction;
import com.sena.senacamera.SdkApi.CameraProperties;
import com.sena.senacamera.ui.ExtendComponent.ZoomView;

/**
 * Created by zhang yanhu C001012 on 2015/12/29 09:42.
 */
public class ZoomInOut{
    private  float lastZoomRate = 1.0f;
    private static ZoomInOut zoomInOut;
    private ZoomCompletedListener zoomCompletedListener;
    private CameraAction cameraAction;
    private CameraProperties cameraProperties;

    public ZoomInOut() {
        cameraAction = CameraManager.getInstance().getCurCamera().getCameraAction();
        cameraProperties = CameraManager.getInstance().getCurCamera().getCameraProperties();
    }

    public void zoomIn(){
        if(lastZoomRate <= ZoomView.MAX_VALUE) {
            cameraAction.zoomIn();
        }
        lastZoomRate = cameraProperties.getCurrentZoomRatio();
        //zoomCompletedListener.onCompleted(lastZoomRate);
    }
    public void zoomOut(){
        if(lastZoomRate >= ZoomView.MIN_VALUE) {
            cameraAction.zoomOut();
        }

        lastZoomRate = cameraProperties.getCurrentZoomRatio();
       // zoomCompletedListener.onCompleted(lastZoomRate);
    }

    public void startZoomInOutThread(final PreviewPresenter presenter){
        new Thread(new Runnable() {
            @Override
            public void run() {
                zoom(presenter);
            }
        }).start();
    }


    private void zoom(PreviewPresenter presenter) {
        int maxZoomCount = 50;
        lastZoomRate = cameraProperties.getCurrentZoomRatio();
        float curProgress = presenter.getZoomViewProgress();
        if (lastZoomRate > curProgress) {
            while (lastZoomRate > (presenter.getZoomViewProgress()) && lastZoomRate > ZoomView.MIN_VALUE && maxZoomCount-- > 0) {
                cameraAction.zoomOut();
                lastZoomRate = cameraProperties.getCurrentZoomRatio();
            }
        } else {
            while (lastZoomRate < (presenter.getZoomViewProgress()) && lastZoomRate < ZoomView.MAX_VALUE && maxZoomCount-- > 0) {
                cameraAction.zoomIn();
                lastZoomRate = cameraProperties.getCurrentZoomRatio();
            }
        }
        zoomCompletedListener.onCompleted(lastZoomRate);
    }

    public interface ZoomCompletedListener{
        void onCompleted(float currentZoomRate);
    };

    public void addZoomCompletedListener(ZoomCompletedListener zoomCompletedListener){
        this.zoomCompletedListener = zoomCompletedListener;
    }
}
