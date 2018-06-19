package com.hyeonu.lottie.View

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.graphics.Palette
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.hyeonu.lottie.Model.Colors

import com.hyeonu.lottie.R
import com.hyeonu.lottie.databinding.FragmentMainBinding
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.selector

class MainFragment : Fragment() {

    lateinit var binding: FragmentMainBinding
    private var filePath: String? = null
    private var colors: Colors = Colors()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main)
//        var viewModel = ViewModelProviders.of(this).get(ImageViewModel::class.java)
        binding.item = colors
        binding.fab.setOnClickListener {
            selector("선택하세요.", listOf("Camera", "Gallery", "Init")) { _, i ->
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

                    2 -> {
                        init()
                    }
                }
            }
        }

        var lottieDrawable = LottieDrawable()
        binding.lottieImage.setImageDrawable(lottieDrawable)
        lottieDrawable.setImagesAssetsFolder("images/")
        LottieComposition.Factory.fromRawFile(this, R.raw.yonghee) { composition ->
            run {
                lottieDrawable.composition = composition

                lottieDrawable.getImageAsset("image_0")
                lottieDrawable.getImageAsset("image_1")
                lottieDrawable.getImageAsset("image_2")
                lottieDrawable.getImageAsset("image_3")
                lottieDrawable.getImageAsset("image_4")
                lottieDrawable.getImageAsset("image_5")
                lottieDrawable.getImageAsset("image_6")
                lottieDrawable.getImageAsset("image_7")
                lottieDrawable.getImageAsset("image_8")
                lottieDrawable.getImageAsset("image_9")
                lottieDrawable.getImageAsset("image_10")
                lottieDrawable.getImageAsset("image_11")
                lottieDrawable.getImageAsset("image_12")
                lottieDrawable.getImageAsset("image_13")
                lottieDrawable.getImageAsset("image_14")
                lottieDrawable.getImageAsset("image_15")
                lottieDrawable.getImageAsset("image_16")
                lottieDrawable.getImageAsset("image_17")
                lottieDrawable.getImageAsset("image_18")
                lottieDrawable.getImageAsset("image_19")
                lottieDrawable.getImageAsset("image_20")
                lottieDrawable.getImageAsset("image_21")
                lottieDrawable.getImageAsset("image_22")
                lottieDrawable.getImageAsset("image_23")
                lottieDrawable.getImageAsset("image_24")
                lottieDrawable.getImageAsset("image_25")
                lottieDrawable.getImageAsset("image_26")
                lottieDrawable.getImageAsset("image_27")
                lottieDrawable.getImageAsset("image_28")
                lottieDrawable.getImageAsset("image_29")

                lottieDrawable.repeatCount = LottieDrawable.INFINITE
                lottieDrawable.playAnimation()
            }
        }

        binding.alphaVideoView.setVideoFromAssets("helicopter.mp4")
        return inflater.inflate(R.layout.fragment_main, container, false)
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
                var bitmap = getImageFromUri(data.data)
                bindImage(rotateImageFor90Degree(bitmap))
            }

            TAKE_PHOTO_REQUEST -> {
                var bitmap = getImageFromUri(Uri.parse(filePath))
                bindImage(rotateImageFor90Degree(bitmap))
            }
        }
    }

    fun goToGallery() {
        var intent = Intent(Intent.ACTION_PICK)
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

    private fun getImageFromUri(uri: Uri): Bitmap {
        val cursor = contentResolver.query(uri, Array(1) { MediaStore.Images.ImageColumns.DATA },
                null, null, null)
        cursor.moveToFirst()
        val photoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
        cursor.close()

        return BitmapFactory.decodeFile(photoPath)
    }

    private fun rotateImageFor90Degree(bitmap: Bitmap): Bitmap {
        var matrix = Matrix()
        matrix.postRotate(90f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun bindImage(bitmap: Bitmap) {
        binding.cameraImage.setImageBitmap(bitmap)
        Palette.from(bitmap).generate {
            colors.lightVibrant = it.lightVibrantSwatch?.rgb ?: 0
            colors.lightVibrantTextColor = it.lightVibrantSwatch?.bodyTextColor ?: 0
            colors.vibrant = it.vibrantSwatch?.rgb ?: 0
            colors.vibrantTextColor = it.vibrantSwatch?.bodyTextColor ?: 0
            colors.darkVibrant = it.darkVibrantSwatch?.rgb ?: 0
            colors.darkVibrantTextColor = it.darkVibrantSwatch?.bodyTextColor ?: 0
            colors.lightMuted = it.lightMutedSwatch?.rgb ?: 0
            colors.lightMutedTextColor = it.lightMutedSwatch?.bodyTextColor ?: 0
            colors.muted = it.mutedSwatch?.rgb ?: 0
            colors.mutedTextColor = it.mutedSwatch?.bodyTextColor ?: 0
            colors.darkMuted = it.darkMutedSwatch?.rgb ?: 0
            colors.darkMutedTextColor = it.darkMutedSwatch?.bodyTextColor ?: 0
            colors.colorFilter = it.vibrantSwatch?.rgb ?: it.mutedSwatch?.rgb ?: 0
            binding.lottie.addValueCallback(KeyPath("**"), LottieProperty.COLOR_FILTER,
                    LottieValueCallback<ColorFilter>(SimpleColorFilter(colors.colorFilter)))
            binding.item = colors
        }
    }

    private fun init() {
        binding.item = Colors()
        binding.cameraImage.setImageBitmap(null)
        binding.lottie.addValueCallback(KeyPath("**"), LottieProperty.COLOR_FILTER,
                LottieValueCallback<ColorFilter>(SimpleColorFilter(0)))
    }

    companion object {
        private var TAKE_PHOTO_REQUEST = 1
        private var GALLERY_REQUEST = 2
    }
}
