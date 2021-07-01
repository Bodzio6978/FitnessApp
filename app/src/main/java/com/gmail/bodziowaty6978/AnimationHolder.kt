package com.gmail.bodziowaty6978

import android.animation.Animator
import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.animation.addListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
object AnimationHolder {

    var context: Context? = null
    lateinit var set : Animator


    fun setAnimationContext(context: Context){
        this.context = context
    }
    fun setUpAnimation(){
        set = AnimatorInflater.loadAnimator(context, R.animator.notification)
                .apply {
                    addListener(onRepeat = {
                        it.pause()
                        GlobalScope.launch(Dispatchers.Main) {
                            delay(2000L)
                            it.resume()
                        }

                    })
                }
    }
}