package com.keysersoze.yumyard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.keysersoze.yumyard.presentation.navigation.NavGraph
import com.keysersoze.yumyard.presentation.screens.update.UpdateRequiredScreen
import com.keysersoze.yumyard.ui.theme.YumPurple
import com.keysersoze.yumyard.ui.theme.YumYardTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK can post notifications.
        } else {

        }
    }

    private lateinit var appUpdateManager: AppUpdateManager
    private var updateRequired by mutableStateOf(false)
    private var pendingUpdateInfo: AppUpdateInfo? = null

    private val updateFlowLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            checkForUpdate()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(YumPurple.toArgb())
        )

        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkForUpdate()

        setContent {
            YumYardTheme {
                val navController = rememberNavController()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    if (updateRequired) {
                        UpdateRequiredScreen(onUpdate = { startImmediateUpdate() })
                    } else {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .systemBarsPadding()
                        ) {
                            NavGraph(navController = navController)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .windowInsetsTopHeight(WindowInsets.statusBars)
                            .background(YumPurple)
                    )
                }
            }
        }

        askNotificationPermission()
    }

    override fun onResume() {
        super.onResume()
        checkForUpdate()
    }

    private fun checkForUpdate() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { info ->
                val inProgress =
                    info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                val available =
                    info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                if ((available || inProgress) && info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    pendingUpdateInfo = info
                    updateRequired = true
                    if (inProgress) startImmediateUpdate()
                } else {
                    updateRequired = false
                }
            }
    }

    private fun startImmediateUpdate() {
        val info = pendingUpdateInfo ?: return
        appUpdateManager.startUpdateFlowForResult(
            info,
            updateFlowLauncher,
            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
        )
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

}