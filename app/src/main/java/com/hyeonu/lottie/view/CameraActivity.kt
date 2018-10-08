package com.hyeonu.lottie.view

import android.app.Activity
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import com.hyeonu.lottie.R
import com.hyeonu.lottie.databinding.ActivityCameraBinding
import org.jetbrains.anko.toast
import java.io.IOException

class CameraActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private lateinit var binding: ActivityCameraBinding
    private var filePath: Uri? = null
    private var camera: Camera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera)
        binding.surfaceCamera.holder.addCallback(this)

        filePath = intent.extras.getParcelable(MediaStore.EXTRA_OUTPUT)
    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    fun takePicture(view: View) {
        camera?.takePicture({
            toast("촬영을 시작합니다.")
        }, null, { bytes: ByteArray, _ ->
            try {
                if (filePath == null) {
                    throw IllegalArgumentException("file is null")
                }

                var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                bitmap = Bitmap.createBitmap(bitmap,
                        bitmap.width / 3,
                        bitmap.height / 3,
                        bitmap.width / 3,
                        bitmap.height / 3)

                val fos = contentResolver.openOutputStream(filePath)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                bitmap.recycle()
                fos.flush()
                fos.close()
            } catch (e: IOException) {
                Log.e("takePicture", "error: ${e.message}\nstackTrace: ${e.stackTrace}")
                toast("촬영에 실패하였습니다.")
            }

            setResult(Activity.RESULT_OK)
            finish()
        })
    }

    fun autoFocus(view: View) {
        binding.imageCameraShot.isEnabled = false
        camera?.autoFocus { _, _ ->
            binding.imageCameraShot.isEnabled = true
        }
    }

    private fun setCameraDisplayOrientation(activity: Activity,
                                            cameraId: Int, camera: Camera?) {

        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)

        val rotation = activity.windowManager.defaultDisplay.rotation
        var degrees = 0

        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360
        }
        camera?.setDisplayOrientation(result)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        camera = Camera.open()

        val param = camera?.parameters
        if (resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            param?.set("orientation", "portrait")
            param?.setPreviewSize(binding.imageCameraBoundary.width, binding.imageCameraBoundary.height)
            setCameraDisplayOrientation(this, 1, camera)
        }
        camera?.parameters = param
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        camera?.run {
            setPreviewDisplay(binding.surfaceCamera.holder)
            startPreview()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        camera?.run {
            stopPreview()
            release()
        }
    }
}
