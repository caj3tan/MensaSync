package com.mensasync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mensasync.model.TableModelImpl
import com.mensasync.storage.LocalStorageImpl
import com.mensasync.sync.SyncServiceImpl
import com.mensasync.viewmodel.MensaViewModel
import com.mensasync.ui.MensaScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tableModel = TableModelImpl()
        val storage = LocalStorageImpl(this)
        val sync = SyncServiceImpl { json ->
            // optional: Callback, wenn Daten empfangen wurden
        }

        val viewModel = MensaViewModel(tableModel, storage, sync)

        setContent {
            MensaScreen(viewModel)
        }
    }
}