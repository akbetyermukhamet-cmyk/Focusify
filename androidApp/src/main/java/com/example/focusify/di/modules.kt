package com.example.focusify.di

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.create
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel
import com.example.focusify.R
import com.example.focusify.data.AppDatabase
import com.example.focusify.data.AppPreferenceRepository
import com.example.focusify.data.AppStatRepository
import com.example.focusify.data.AuthRepository
import com.example.focusify.data.FirebaseAuthRepository
import com.example.focusify.data.PreferenceRepository
import com.example.focusify.data.StatRepository
import com.example.focusify.data.StateRepository
import com.example.focusify.data.TaskRepository
import com.example.focusify.service.ServiceHelper
import com.example.focusify.service.addTimerActions
import com.example.focusify.ui.onboarding.AuthViewModel
import com.example.focusify.ui.settingsScreen.screens.backupRestore.viewModel.BackupRestoreViewModel
import com.example.focusify.ui.settingsScreen.viewModel.SettingsViewModel
import com.example.focusify.ui.statsScreen.viewModel.StatsViewModel
import com.example.focusify.ui.timerScreen.viewModel.TimerViewModel

val dbModule = module {
    single<AppDatabase> { create(::createDatabase) }
    single { get<AppDatabase>().preferenceDao() }
    single { get<AppDatabase>().statDao() }
    single { get<AppDatabase>().systemDao() }
    single { get<AppDatabase>().taskDao() }
    single { get<AppDatabase>().projectDao() }
    single { get<AppDatabase>().tagDao() }
}

val servicesModule = module {
    single<CoroutineDispatcher> { Dispatchers.IO }

    single<AppInfo> { create(::createAppInfo) }
    single<AppStatRepository>() bind StatRepository::class
    single<AppPreferenceRepository>() bind PreferenceRepository::class
    single<TaskRepository>()
    single<StateRepository>()
    single<ServiceHelper>()

    single { NotificationManagerCompat.from(get()) }
    single { create(::createNotificationManager) }
    single { create(::createNotificationCompatBuilder) }

    single<TimerStateHolder>()
    single<ActivityCallbacks>()

    // Firebase
    single { FirebaseAuth.getInstance() }
    single<AuthRepository> { FirebaseAuthRepository(get()) }
}

val viewModels = module {
    viewModel<BackupRestoreViewModel>()
    viewModel<TimerViewModel>()
    viewModel<SettingsViewModel>()
    viewModel<StatsViewModel>()
    viewModel<AuthViewModel>()
}

private fun createDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "app_database"
    ).build()
}

private fun createAppInfo(context: Context): AppInfo {
    val debug = context.packageName.endsWith(".debug")

    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName ?: "-"
    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode
    } else {
        0L
    }

    return AppInfo(debug, versionName, versionCode)
}

private fun createNotificationManager(context: Context): NotificationManager {
    return context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
}

private fun createNotificationCompatBuilder(context: Context): NotificationCompat.Builder {
    return NotificationCompat.Builder(context, "timer")
        .setSmallIcon(R.drawable.tomato_logo_notification)
        .setColor(Color.Red.toArgb())
        .setContentIntent(
            PendingIntent.getActivity(
                context,
                0,
                context.packageManager.getLaunchIntentForPackage(context.packageName),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        .addTimerActions(context, R.drawable.play, context.getString(R.string.start))
        .setShowWhen(true)
        .setSilent(true)
        .setOngoing(true)
        .setRequestPromotedOngoing(true)
        .setVisibility(VISIBILITY_PUBLIC)
        .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
}
