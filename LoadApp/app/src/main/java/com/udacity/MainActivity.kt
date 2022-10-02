package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.NotificationCompat
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val NOTIFICATION_ID = 285
        const val NAME = "NAME"
        const val STATUS = "STATUS"
    }

    private var downloadID: Long = 0

    lateinit var notificationManager: NotificationManager

    private lateinit var detailPendingIntent: PendingIntent
    private lateinit var detailIntent: Intent

    private lateinit var customButton: LoadingButton
    private lateinit var motionLayout: MotionLayout

    private lateinit var radioGroup: RadioGroup
    private lateinit var textEdit: EditText

    private val dataPairList = listOf<Pair<String, String>>(
        Pair(
            "Glide - Image Loading Library by BumpTech",
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        ),
        Pair(
            "LoadApp - Current repository by Udacity",
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
        ),
        Pair(
            "Retrofit - Type-safe HTTP client for Android and Java by Square, inc",
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        )
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(successReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        notificationManager = getSystemService(
            NotificationManager::class.java
        ) as NotificationManager

        detailIntent = Intent(applicationContext, DetailActivity::class.java)

        customButton = findViewById<LoadingButton>(R.id.custom_button)
        motionLayout = findViewById<MotionLayout>(R.id.motion_layout)
        radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        textEdit = findViewById<EditText>(R.id.urlText)
        var url = ""
        customButton.progress = 0f

        // setting the strings of the radioGroup items
        for (index in dataPairList.indices) {
            (radioGroup[index] as RadioButton).text = dataPairList[index].first
        }

        customButton.setOnClickListener {
            url = if (textEdit.text.isNotEmpty()) {
                val link = textEdit.text.toString()

                detailIntent.putExtra(NAME, link)
                link
            } else {
                // getting the right index of the checked radio button
                val radioButton = findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
                val radioButtonIndex = radioGroup.indexOfChild(radioButton)

                if (radioButtonIndex == -1) {
                    "-1"
                } else {
                    detailIntent.putExtra(NAME, dataPairList[radioButtonIndex].first)
                    dataPairList[radioButtonIndex].second
                }
            }
            if (url == "-1") {
                Toast.makeText(
                    baseContext,
                    "please select a file to download or type a link",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            Toast.makeText(baseContext, url, Toast.LENGTH_SHORT).show()
            download(url)
            motionLayout.transitionToEnd()
        }

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )
    }

    private fun createChannel(channelID: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                channelID,
                channelName,

                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.apply {
                enableLights(true)
                lightColor = Color.RED
                description = "LoadApp status notification channel"
                enableVibration(true)
            }

            val notificationManager = getSystemService(
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    private fun NotificationManager.sentNotification(
        description: String,
        applicationContext: Context
    ) {

        detailPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            detailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.notification_channel_id)
        )

            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(description)
            .addAction(
                R.drawable.ic_assistant_black_24dp,
                "Details",
                detailPendingIntent
            )

        notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private val successReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {

                getStatusAndSendNotification(id)
            }
        }
    }

    private fun getStatusAndSendNotification(id: Long) {

        motionLayout.setTransitionDuration(300)
        motionLayout.transitionToEnd()


        val query = DownloadManager.Query()
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        query.setFilterById(id)
        val cursor = downloadManager.query(query)

        var status = 0
        if (cursor.moveToFirst()) {
            status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
        }

        when (status) {
            DownloadManager.STATUS_SUCCESSFUL -> {
                detailIntent.putExtra(STATUS, "success")
                notificationManager.sentNotification("download completed", applicationContext)
            }

            DownloadManager.STATUS_FAILED -> {
                detailIntent.putExtra(STATUS, "failed")
                notificationManager.sentNotification("download failed", applicationContext)

            }
        }
    }

    private fun download(URL: String) {

        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

}
