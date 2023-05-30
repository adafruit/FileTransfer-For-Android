package io.openroad.filetransfer.ble.peripheral

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Created by Antonio García (antonio@openroad.es)
 */

class BondedBlePeripheralsFake() : BondedBlePeripherals {
    private val _peripheralsData = MutableStateFlow(getPeripheralsData())
    override val peripheralsData = _peripheralsData.asStateFlow()

    private fun getPeripheralsData(): List<BondedBlePeripherals.Data> {
        return emptyList()
    }

    override fun add(name: String?, address: String) {
    }

    override fun remove(address: String) {
    }

    override fun refresh() {
    }
}