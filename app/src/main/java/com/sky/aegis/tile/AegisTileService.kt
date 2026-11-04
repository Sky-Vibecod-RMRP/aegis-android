package com.sky.aegis.tile

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.sky.aegis.model.ConnectionStatus
import com.sky.aegis.vpn.AegisVpnService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class AegisTileService : TileService() {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onStartListening() {
        super.onStartListening()
        scope.launch {
            AegisVpnService.connectionStatus.collectLatest { status ->
                updateTileState(status)
            }
        }
    }

    private fun updateTileState(status: ConnectionStatus) {
        val tile = qsTile ?: return
        when (status) {
            ConnectionStatus.CONNECTED -> {
                tile.state = Tile.STATE_ACTIVE
                tile.label = "Aegis: Защита"
                tile.subtitle = "Активен"
            }
            ConnectionStatus.CONNECTING -> {
                tile.state = Tile.STATE_UNAVAILABLE
                tile.label = "Aegis: Запуск"
                tile.subtitle = "Подключение..."
            }
            else -> {
                tile.state = Tile.STATE_INACTIVE
                tile.label = "Aegis Shield"
                tile.subtitle = "Отключено"
            }
        }
        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        val current = AegisVpnService.connectionStatus.value
        val intent = Intent(this, AegisVpnService::class.java)

        if (current == ConnectionStatus.CONNECTED) {
            intent.action = AegisVpnService.ACTION_STOP
            startService(intent)
        } else {
            intent.action = AegisVpnService.ACTION_START
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
    }
}
