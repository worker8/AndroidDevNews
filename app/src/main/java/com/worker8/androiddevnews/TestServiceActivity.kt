package com.worker8.androiddevnews

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.worker8.androiddevnews.podcast.PodcastService

class TestServiceActivity : AppCompatActivity() {
    private lateinit var mService: PodcastService
    private var mBound: Boolean = false

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as PodcastService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestServiceScreen {
                onButtonClick()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Bind to LocalService
        Intent(this, PodcastService::class.java).also { intent ->
            intent.putExtra("action", PodcastService.Action.Init("", "", "").name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }

    /** Called when a button is clicked (the button in the layout file attaches to
     * this method with the android:onClick attribute)  */
    private fun onButtonClick() {
        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            val num: Int = mService.randomNumber
            val count = mService.count
            Toast.makeText(this, "number: $num, count: $count", Toast.LENGTH_SHORT).show()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TestServiceScreen(callback: () -> Unit) {
    Text(
        modifier = Modifier.clickable {
            callback()
        },
        text = "BIG ASS TEXT",
        style = TextStyle(
            color = Color.White,
            fontSize = 30.sp,
        )
    )
}
