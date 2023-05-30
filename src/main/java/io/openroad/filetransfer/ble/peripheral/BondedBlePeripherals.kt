package io.openroad.filetransfer.ble.peripheral

import kotlinx.coroutines.flow.StateFlow

interface BondedBlePeripherals {
    data class Data(var name: String?, val address: String)

    val peripheralsData: StateFlow<List<Data>>

    fun add(name: String?, address: String)
    fun remove(address: String)
    fun refresh()
}
