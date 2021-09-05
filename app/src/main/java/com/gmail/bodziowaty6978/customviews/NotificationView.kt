package com.gmail.bodziowaty6978.customviews

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.animation.addListener
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.NotificationViewBinding
import kotlinx.coroutines.*

@DelicateCoroutinesApi
class NotificationView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    var binding: NotificationViewBinding = NotificationViewBinding.inflate(LayoutInflater.from(context))
    var set : Animator

    init {
        addView(binding.root)

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

        set.setTarget(this)

        val attributes = context.obtainStyledAttributes(attrs,R.styleable.NotificationView)

        binding.notificationViewText.text = attributes.getString(R.styleable.NotificationView_text)

        attributes.recycle()
    }

    fun startAnimation(){
        set.start()
    }

    fun setText(text:String){
        binding.notificationViewText.text = text
    }



}