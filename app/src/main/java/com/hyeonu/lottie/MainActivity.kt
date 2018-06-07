package com.hyeonu.lottie

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.*
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.graphics.Palette
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.hyeonu.lottie.databinding.ActivityMainBinding
import org.jetbrains.anko.*
import org.jetbrains.anko.design.snackbar
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var filePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.fab.setOnClickListener {
            selector("선택하세요.", listOf("Camera", "Gallery"), { dialogInterface, i ->
                when (i) {
                    0 -> {
                        TedPermission.with(this).setPermissionListener(object : PermissionListener {
                            override fun onPermissionGranted() {
                                goToCamera()
                            }

                            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                                snackbar(binding.fab, "$deniedPermissions 권한이 필요합니다.")
                            }
                        })
                                .setDeniedMessage("[설정] > [권한]에서 권한을 허용할 수 있습니다.")
                                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .check()

                    }
                    1 -> {
                        TedPermission
                                .with(this)
                                .setPermissionListener(object : PermissionListener {
                                    override fun onPermissionGranted() {
                                        goToGallery()
                                    }

                                    override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                                        snackbar(binding.fab, "$deniedPermissions 권한이 필요합니다.")
                                    }
                                })
                                .setDeniedMessage("[설정] > [권한]에서 권한을 허용할 수 있습니다.")
                                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                                .check()
                    }
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            GALLERY_REQUEST -> {
                if (data == null || data.data == null) {
                    return
                }
                var bitmap = bringImage(data.data)
                var matrix = Matrix()
                matrix.postRotate(90f)
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                binding.cameraImage.setImageBitmap(bitmap)
                Palette.from(bitmap).generate({
                    binding.lightVibrant.setBackgroundColor(0)
                    binding.darkVibrant.setBackgroundColor(0)
                    binding.lightMuted.setBackgroundColor(0)
                    binding.darkMuted.setBackgroundColor(0)
                    it.lightVibrantSwatch?.rgb?.let { it1 -> binding.lightVibrant.setBackgroundColor(it1) }
                    it.darkVibrantSwatch?.rgb?.let { it1 -> binding.darkVibrant.setBackgroundColor(it1) }
                    it.lightMutedSwatch?.rgb?.let { it1 -> binding.lightMuted.setBackgroundColor(it1) }
                    it.darkMutedSwatch?.rgb?.let { it1 -> binding.darkMuted.setBackgroundColor(it1) }
                    binding.lottie.addValueCallback(KeyPath("**"), LottieProperty.COLOR_FILTER,
                            LottieValueCallback<ColorFilter>(it.darkVibrantSwatch?.rgb?.let { it1 -> SimpleColorFilter(it1) }))
                })
            }
            TAKE_PHOTO_REQUEST -> {
                var bitmap = bringImage(Uri.parse(filePath))
                var matrix = Matrix()
                matrix.postRotate(90f)
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                binding.cameraImage.setImageBitmap(bitmap)
                Palette.from(bitmap).generate({
                    binding.lightVibrant.setBackgroundColor(0)
                    binding.darkVibrant.setBackgroundColor(0)
                    binding.lightMuted.setBackgroundColor(0)
                    binding.darkMuted.setBackgroundColor(0)
                    it.lightVibrantSwatch?.rgb?.let { it1 -> binding.lightVibrant.setBackgroundColor(it1) }
                    it.darkVibrantSwatch?.rgb?.let { it1 -> binding.darkVibrant.setBackgroundColor(it1) }
                    it.lightMutedSwatch?.rgb?.let { it1 -> binding.lightMuted.setBackgroundColor(it1) }
                    it.darkMutedSwatch?.rgb?.let { it1 -> binding.darkMuted.setBackgroundColor(it1) }
                    binding.lottie.addValueCallback(KeyPath("**"), LottieProperty.COLOR_FILTER,
                            LottieValueCallback<ColorFilter>(it.darkVibrantSwatch?.rgb?.let { it1 -> SimpleColorFilter(it1) }))
                })
            }
        }
    }

    fun goToGallery() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "image/*"
//        if (intent.resolveActivity(packageManager) != null) {
//            startActivityForResult(intent, GALLERY_REQUEST)
//        }
        intent = Intent(Intent.ACTION_PICK)
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
        startActivityForResult(intent, GALLERY_REQUEST)
    }

    fun goToCamera() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        val fileUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            filePath = fileUri.toString()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(intent, TAKE_PHOTO_REQUEST)
        }
    }

    fun bringImage(uri: Uri): Bitmap {
        val cursor = contentResolver.query(uri, Array(1) { MediaStore.Images.ImageColumns.DATA },
                null, null, null)
        cursor.moveToFirst()
        val photoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
        cursor.close()

        return BitmapFactory.decodeFile(photoPath)
    }

    companion object {
        private var TAKE_PHOTO_REQUEST = 1
        private var GALLERY_REQUEST = 2
    }
}
