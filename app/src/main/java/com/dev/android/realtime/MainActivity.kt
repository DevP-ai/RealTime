package com.dev.android.realtime

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.TextureView
import android.widget.ImageView
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    lateinit var textureView: TextureView

    lateinit var cameraManager: CameraManager

    lateinit var handler: Handler
    lateinit var cameraDevice: CameraDevice

    lateinit var bitmap: Bitmap
    lateinit var imageView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Camera Permission
        get_permission()
        //End permission

        //HandlerThread
        val handlerThread=HandlerThread("videoThread")
        handlerThread.start()
        handler= Handler(handlerThread.looper)


        //Texture View
        textureView=findViewById(R.id.textureView)

        textureView.surfaceTextureListener=object:TextureView.SurfaceTextureListener{

            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                open_camera()
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

            }
        }

        cameraManager=getSystemService(Context.CAMERA_SERVICE) as CameraManager



    }

    //Open Camera
    @SuppressLint("MissingPermission")
    fun open_camera(){
        cameraManager.openCamera(cameraManager.cameraIdList[0],object :CameraDevice.StateCallback(){
            override fun onOpened(camera: CameraDevice) {

                cameraDevice=camera

                var surfaceTexture=textureView.surfaceTexture
                var surface=Surface(surfaceTexture)

                var captureRequest=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)

                captureRequest.addTarget(surface)


                cameraDevice.createCaptureSession(listOf(surface),object:CameraCaptureSession.StateCallback(){
                    override fun onConfigured(session: CameraCaptureSession) {
                        session.setRepeatingRequest(captureRequest.build(),null,null)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {

                    }
                },handler)
            }

            override fun onClosed(camera: CameraDevice) {
                super.onClosed(camera)
            }

            override fun onDisconnected(camera: CameraDevice) {

            }

            override fun onError(camera: CameraDevice, error: Int) {

            }
        },handler)
    }



    //Camera Permission
    fun get_permission(){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA),101)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0]!=PackageManager.PERMISSION_GRANTED)
            get_permission()
    }
    //Camera Permission End





}