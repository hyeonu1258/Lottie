package com.hyeonu.lottie.view

import android.Manifest
import android.app.Activity
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
import android.support.v4.app.Fragment
import android.support.v7.graphics.Palette
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.hyeonu.lottie.model.Colors

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
        initView(inflater, container)

        return binding.root
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
                var bitmap = getImageFromUri(data.data) ?: return
                bindImage(rotateImageFor90Degree(bitmap))
            }

            TAKE_PHOTO_REQUEST -> {
                var bitmap = getImageFromUri(Uri.parse(filePath)) ?: return
                bindImage(rotateImageFor90Degree(bitmap))
            }
        }
    }

    private fun initView(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.item = colors
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.fab.setOnClickListener {
            selector("선택하세요.", listOf("Camera", "Gallery", "Init")) { _, i ->
                when (i) {
                    0 -> {
                        TedPermission.with(context).setPermissionListener(object : PermissionListener {
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
                        TedPermission.with(context).setPermissionListener(object : PermissionListener {
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
    }

    private fun goToCamera() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        val fileUri = context?.contentResolver
                ?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: return
        filePath = fileUri.toString()
        val intent = Intent(activity, CameraActivity::class.java)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, TAKE_PHOTO_REQUEST)
    }

    private fun goToGallery() {
        var intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, GALLERY_REQUEST)
    }

    private fun init() {
        binding.item = Colors()
        binding.cameraImage.setImageBitmap(null)
        binding.lottie.addValueCallback(KeyPath("**"), LottieProperty.COLOR_FILTER,
                LottieValueCallback<ColorFilter>(SimpleColorFilter(0)))
    }

    private fun getImageFromUri(uri: Uri): Bitmap? {
        val cursor = context?.contentResolver?.query(uri, Array(1)
        { MediaStore.Images.ImageColumns.DATA }, null, null, null)
                ?: return null
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

    companion object {
        private var TAKE_PHOTO_REQUEST = 1
        private var GALLERY_REQUEST = 2
    }
}
