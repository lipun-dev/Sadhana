package com.example.pomodora.services

import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.pomodora.view.utils.WarningOverlayUI

class ComposeLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    fun onCreate() {
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    fun onStart() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    fun onResume() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
}


class OverlayHelper(private val context: Context ) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: ComposeView? = null
    private var lifecycleOwner: ComposeLifecycleOwner? = null



    fun showOverlay(onGiveUpClicked: () -> Unit) {
        if (overlayView != null) return // Already showing

        // Or pass context if needed

        // 1. Initialize Custom Lifecycle
        lifecycleOwner = ComposeLifecycleOwner()
        lifecycleOwner?.onCreate()

        // 2. Create ComposeView and attach Lifecycle
        overlayView = ComposeView(context).apply {
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)

            setContent {
                WarningOverlayUI(
                    onGiveUp = onGiveUpClicked
                )
            }
        }

        // 3. Define Window Params
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, // Dims background app
            PixelFormat.TRANSLUCENT
        ).apply {
            screenBrightness = 1.0f
        }

        // 4. Add to Window and Resume Lifecycle
        try {
            windowManager?.addView(overlayView, params)
            lifecycleOwner?.onStart()
            lifecycleOwner?.onResume()
        } catch (e: Exception) {
            Log.e("OverlayHelper", "Error showing overlay: ${e.message}")
        }
    }

    fun hideOverlay() {
        if (overlayView != null) {
            lifecycleOwner?.onDestroy() // Clean up lifecycle
            try {
                windowManager?.removeView(overlayView)
            } catch (e: Exception) {
                // View might not be attached
            }
            overlayView = null
            lifecycleOwner = null
        }
    }
}