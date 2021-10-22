package com.gmail.bodziowaty6978.customviews

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.animation.addListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.NotificationViewBinding
import com.gmail.bodziowaty6978.singleton.NotificationText
import com.gmail.bodziowaty6978.singleton.Strings
import kotlinx.coroutines.*

@DelicateCoroutinesApi
class NotificationView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs), LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private var isRunning = false

    var binding: NotificationViewBinding = NotificationViewBinding.inflate(LayoutInflater.from(context))
    var set : Animator

    init {
        addView(binding.root)

        set = AnimatorInflater.loadAnimator(context, R.animator.notification)
            .apply {
                addListener(onRepeat = {
                    it.pause()
                    GlobalScope.launch(Dispatchers.Main) {
                        delay(3400)
                        it.resume()
                    }

                })
                addListener(onEnd = {
                    this@NotificationView.isRunning = false
                })
            }

        set.setTarget(this)

        val attributes = context.obtainStyledAttributes(attrs,R.styleable.NotificationView)

        binding.tvNotificationView.text = attributes.getString(R.styleable.NotificationView_text)

        attributes.recycle()

        NotificationText.text.observe(this, {
            when (it) {
                "username" -> binding.tvNotificationView.text = Strings.get(R.string.please_enter_your_username)
                "length" -> binding.tvNotificationView.text = Strings.get(R.string.username_length_notification)
                "exists" -> binding.tvNotificationView.text = Strings.get(R.string.username_exists_notification)
                "fields" -> binding.tvNotificationView.text = Strings.get(R.string.please_make_sure_all_fields_are_filled_in_correctly)
                "match" -> binding.tvNotificationView.text = Strings.get(R.string.please_make_sure_both_passwords_are_the_same)
                else -> binding.tvNotificationView.text = it
            }

        })

        NotificationText.state.observe(this,{
            if(it&&!isRunning){
                isRunning=true
                startAnimation()
                GlobalScope.launch(Dispatchers.Main) {
                    delay(1)
                    NotificationText.state.value = false

                }
            }
        })
    }


    fun startAnimation(){
        set.start()
    }

    fun setText(text:String){
        binding.tvNotificationView.text = text
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }
}