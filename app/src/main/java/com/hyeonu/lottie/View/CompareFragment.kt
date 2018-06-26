package com.hyeonu.lottie.View

import android.databinding.DataBindingUtil
import android.graphics.drawable.Animatable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieDrawable
import com.facebook.common.util.UriUtil
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.imagepipeline.image.ImageInfo

import com.hyeonu.lottie.R
import com.hyeonu.lottie.databinding.FragmentCompareBinding

class CompareFragment : Fragment() {

    lateinit var binding: FragmentCompareBinding
    lateinit var lottieDrawable: LottieDrawable
    var mAnimatable: Animatable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        initView(inflater, container)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

//        loadImageAsset()

        binding.alphaVideoView.onResume()
        binding.alphaVideoView2.onResume()
    }

    override fun onPause() {
        super.onPause()

        binding.alphaVideoView.onPause()
        binding.alphaVideoView2.onPause()

//        lottieDrawable.recycleBitmaps()
    }

    private fun initView(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_compare, container, false)

        binding.alphaVideoView.setVideoFromAssets("vr.mp4")
        binding.alphaVideoView2.setVideoFromAssets("fire.mp4")

        binding.frescoImage.controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.Builder().scheme(UriUtil.LOCAL_ASSET_SCHEME).path("vr.gif").build())
                .setAutoPlayAnimations(false)
                .setControllerListener(object : BaseControllerListener<ImageInfo>() {
                    override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                        mAnimatable = animatable?: null
                    }
                })
                .build()

        binding.videoBtn.setOnClickListener {
            binding.alphaVideoView.start()
            binding.alphaVideoView2.start()
            mAnimatable?.stop()
        }

        binding.frescoBtn.setOnClickListener {
            mAnimatable?.start()
            binding.alphaVideoView.pause()
            binding.alphaVideoView2.pause()
        }
    }

    /**
     * Lottie 로 Image 파일을 애니메이션 실행시 최초 실행에서 flicker 현상 발생, 애니메이션 전 미리 로드
     */
    private fun loadImageAsset() {
        lottieDrawable = LottieDrawable()
        binding.lottieImage.setImageDrawable(lottieDrawable)
        lottieDrawable.setImagesAssetsFolder("images/")
        LottieComposition.Factory.fromRawFile(context, R.raw.yonghee) { composition ->
            run {
                lottieDrawable.composition = composition

                for (i in 0..29) {
                    lottieDrawable.getImageAsset("image_$i")
                }

                lottieDrawable.repeatCount = LottieDrawable.INFINITE
                lottieDrawable.playAnimation()
            }
        }
    }
}
