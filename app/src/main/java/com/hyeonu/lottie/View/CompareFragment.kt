package com.hyeonu.lottie.View

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieDrawable

import com.hyeonu.lottie.R
import com.hyeonu.lottie.databinding.FragmentCompareBinding

class CompareFragment : Fragment() {

    lateinit var binding: FragmentCompareBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        initView(inflater, container)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.alphaVideoView.onResume()
        binding.alphaVideoView.start()
    }

    override fun onPause() {
        super.onPause()
        binding.alphaVideoView.onPause()
    }

    private fun initView(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_compare, container, false)
        binding.alphaVideoView.setVideoFromAssets("helicopter.mp4")
        loadImageAsset()
    }

    /**
     * 최초 애니메이션 실행시 flicker 현상 발생, 사용하기전 미리 로드
     */
    private fun loadImageAsset() {
        var lottieDrawable = LottieDrawable()
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
