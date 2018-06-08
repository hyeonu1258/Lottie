package com.hyeonu.lottie.View

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.ContentValues
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.hyeonu.lottie.R
import com.hyeonu.lottie.ViewModel.ImageViewModel
import com.hyeonu.lottie.databinding.ActivityMainBinding
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.selector
import org.jetbrains.anko.textColor
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var filePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ViewModelProviders.of(this).get(ImageViewModel.class);
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
                        TedPermission.with(this).setPermissionListener(object : PermissionListener {
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
                bindImage(rotation90(bitmap))
            }

            TAKE_PHOTO_REQUEST -> {
                var bitmap = bringImage(Uri.parse(filePath))
                bindImage(rotation90(bitmap))
            }
        }
    }

    fun goToGallery() {
        intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
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

    fun rotation90(bitmap: Bitmap): Bitmap {
        var matrix = Matrix()
        matrix.postRotate(90f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun bindImage(bitmap: Bitmap) {
        binding.cameraImage.setImageBitmap(bitmap)
        Palette.from(bitmap).generate({
            binding.lightVibrant.backgroundColor = it.lightVibrantSwatch?.rgb ?: 0
            binding.lightVibrant.textColor = it.lightVibrantSwatch?.bodyTextColor ?: 0
            binding.vibrant.backgroundColor = it.vibrantSwatch?.rgb ?: 0
            binding.vibrant.textColor = it.vibrantSwatch?.bodyTextColor ?: 0
            binding.darkVibrant.backgroundColor = it.darkVibrantSwatch?.rgb ?: 0
            binding.darkVibrant.textColor = it.darkVibrantSwatch?.bodyTextColor ?: 0
            binding.lightMuted.backgroundColor = it.lightMutedSwatch?.rgb ?: 0
            binding.lightMuted.textColor = it.lightMutedSwatch?.bodyTextColor ?: 0
            binding.muted.backgroundColor = it.mutedSwatch?.rgb ?: 0
            binding.muted.textColor = it.mutedSwatch?.bodyTextColor ?: 0
            binding.darkMuted.backgroundColor = it.darkMutedSwatch?.rgb ?: 0
            binding.darkMuted.textColor = it.darkMutedSwatch?.bodyTextColor ?: 0

            binding.lottie.addValueCallback(KeyPath("**"), LottieProperty.COLOR_FILTER,
                    LottieValueCallback<ColorFilter>(SimpleColorFilter(
                            it.vibrantSwatch?.rgb ?: it.mutedSwatch?.rgb ?: 0)))
        })
    }

    companion object {
        private var TAKE_PHOTO_REQUEST = 1
        private var GALLERY_REQUEST = 2
    }
}
