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
import org.jetbrains.anko.support.v4.onUiThread
import java.util.*
import kotlin.concurrent.timerTask

class CompareFragment : Fragment() {

    lateinit var binding: FragmentCompareBinding
    lateinit var lottieDrawable: LottieDrawable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        initView(inflater, container)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        loadImageAsset()

        binding.alphaVideoView.onResume()
        binding.alphaVideoView.start()
    }

    override fun onPause() {
        super.onPause()

        binding.alphaVideoView.onPause()

        lottieDrawable.recycleBitmaps()
    }

    private fun initView(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_compare, container, false)

        binding.alphaVideoView.setVideoFromAssets("fire.mp4")
        // 동영상 재생시 처음 검은색 배경이 보이는 문제로 인해 타이머 설정 - 내부적으로 뷰가 화면에 그려져야 재생되는 구조
//        binding.alphaVideoView.setOnVideoStartedListener {
//            Timer().schedule(timerTask { onUiThread { binding.alphaVideoView.alpha = 1F } }, 300)
//        }
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
