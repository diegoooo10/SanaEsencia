package com.example.sanaescencia.testutil

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

/**
 * Sustituye [Dispatchers.Main] durante un test de Kotest/JUnit 5.
 *
 * Con [UnconfinedTestDispatcher] los `viewModelScope.launch` se ejecutan de
 * forma eager hasta el primer punto de suspension, de modo que los estados de
 * los ViewModel quedan actualizados sin necesidad de avanzar relojes virtuales.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherController(
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) {

    fun install() {
        Dispatchers.setMain(dispatcher)
    }

    fun reset() {
        Dispatchers.resetMain()
    }
}
