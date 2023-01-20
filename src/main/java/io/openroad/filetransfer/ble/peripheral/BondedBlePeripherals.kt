package io.openroad.filetransfer.ble.peripheral

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.annotation.RequiresPermission
import com.adafruit.glider.utils.LogUtils
import io.openroad.filetransfer.ble.utils.BleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Created by Antonio García (antonio@openroad.es)
 */


class BondedBlePeripherals(
    private val context: Context
) {
    data class Data(var name: String?, val address: String)

    // Data - Internal
    private val log by LogUtils()
    private val _peripheralsData = MutableStateFlow(getPeripheralsData())

    // Data - Public
    val peripheralsData = _peripheralsData.asStateFlow()

    private fun getPeripheralsData(): List<Data> {
        return try {
            val bondedDevices = BleManager.getBondedPeripherals(context)
            bondedDevices?.toList()?.map { Data(name = it.name, address = it.address) }
                ?: emptyList()
        } catch (e: SecurityException) {
            log.warning("bondedDevices security exception: $e")
            emptyList()
        }
    }

    // region Actions
    fun add(name: String?, address: String) {
        // The bonded peripheral list is maintained internally by Android but it takes some time until it refreshes. So add it manually

        // If already exist that address, remove it and add it to update the name
        val peripherals = _peripheralsData.value.toMutableList()
        val existingPeripheral = peripherals.firstOrNull { it.address == address }

        // Continue if not exist or the name has changed
        if (existingPeripheral == null || existingPeripheral.name != name) {

            // If the name has changed, remove it to add it with the new name
            if (existingPeripheral != null) {
                peripherals.remove(existingPeripheral)
            }

            peripherals.add(Data(name, address))
            _peripheralsData.update { peripherals }
        }
    }

    fun remove(address: String) {
        try {
            val bondedDevices = BleManager.getBondedPeripherals(context)
            val peripheralData = bondedDevices?.firstOrNull { it.address == address }
            peripheralData?.let {
                removeBondedDevice(it)

                // The bonded peripheral list is maintained internally by Android but it takes some time until it refreshes. So remove it manually
                val peripherals = _peripheralsData.value.toMutableList()
                val existingPeripheral = peripherals.firstOrNull { it.address == address }
                if (existingPeripheral != null) {
                    peripherals.remove(existingPeripheral)
                    _peripheralsData.update { peripherals }
                }
            }
        } catch (e: SecurityException) {
            log.warning("remove bonded devices security exception: $e")
        }
    }

    fun refresh() {
        _peripheralsData.update { getPeripheralsData() }
    }

    fun clear() {
        BleManager.removeAllBondedPeripheralInfo(context)
        refresh()
    }

    private fun removeBondedDevice(device: BluetoothDevice) {
        BleManager.removeBondedPeripheralInfo(device)
        refresh()
    }
    // endregion
}