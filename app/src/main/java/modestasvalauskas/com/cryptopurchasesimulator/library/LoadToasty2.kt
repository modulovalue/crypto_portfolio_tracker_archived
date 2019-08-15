package modestasvalauskas.com.cryptopurchasesimulator.library

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.nineoldandroids.view.ViewHelper
import com.nineoldandroids.view.ViewPropertyAnimator
import net.steamcrafted.loadtoast.LoadToastView

/**
 * custom implementation of loadtoasty library
 */


class LoadToast2(context: Context) {

    private var mText = ""
    private val mView: LoadToastView
    private val mParentView: ViewGroup
    private var mTranslationY = 0
    private var mShowCalled = false
    private var mToastCanceled = false
    private var mInflated = false
    private var mVisible = false

    init {
        mView = LoadToastView(context)
        mParentView = (context as Activity).window.decorView.findViewById<View>(android.R.id.content) as ViewGroup
        mParentView.addView(mView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        ViewHelper.setAlpha(mView, 0f)
        mParentView.postDelayed({
            ViewHelper.setTranslationX(mView, ((mParentView.width - mView.width) / 2).toFloat())
            ViewHelper.setTranslationY(mView, (-mView.height + mTranslationY).toFloat())
            mInflated = true
            if (!mToastCanceled && mShowCalled) show()
        }, 1)

        mParentView.viewTreeObserver.addOnGlobalLayoutListener { checkZPosition() }
    }

    fun setTranslationY(pixels: Int): LoadToast2 {
        mTranslationY = pixels
        return this
    }

    fun setText(message: String): LoadToast2 {
        mText = message
        mView.setText(mText)
        return this
    }

    fun setTextColor(color: Int): LoadToast2 {
        mView.setTextColor(color)
        return this
    }

    fun setBackgroundColor(color: Int): LoadToast2 {
        mView.setBackgroundColor(color)
        return this
    }

    fun setProgressColor(color: Int): LoadToast2 {
        mView.setProgressColor(color)
        return this
    }

    fun show(): LoadToast2 {
        if (!mInflated) {
            mShowCalled = true
            return this
        }
        mView.show()
        ViewHelper.setTranslationX(mView, ((mParentView.width - mView.width) / 2).toFloat())
        ViewHelper.setAlpha(mView, 0f)
        ViewHelper.setTranslationY(mView, (-mView.height + mTranslationY).toFloat())
        //mView.setVisibility(View.VISIBLE);
        ViewPropertyAnimator.animate(mView).alpha(1f).translationY((25 + mTranslationY).toFloat())
                .setInterpolator(DecelerateInterpolator())
                .setDuration(300).setStartDelay(0).start()

        mVisible = true
        checkZPosition()

        return this
    }

    fun success() {
        if (!mInflated) {
            mToastCanceled = true
            return
        }
        mView.success()
        slideUp()
    }

    fun error() {
        if (!mInflated) {
            mToastCanceled = true
            return
        }
        mView.error()
        slideUp()
    }

    private fun checkZPosition() {
        // If the toast isn't visible, no point in updating all the views
        if (!mVisible) return

        val pos = mParentView.indexOfChild(mView)
        val count = mParentView.childCount
        if (pos != count - 1) {
            (mView.parent as ViewGroup).removeView(mView)
            mParentView.requestLayout()
            mParentView.addView(mView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT))
        }
    }

    private fun slideUp() {
        ViewPropertyAnimator.animate(mView)
                .setStartDelay(1500)
                .translationY((-mView.height + mTranslationY).toFloat())
                .setInterpolator(AccelerateInterpolator())
                .setDuration(300)
                .start()

        mVisible = false
    }

}
