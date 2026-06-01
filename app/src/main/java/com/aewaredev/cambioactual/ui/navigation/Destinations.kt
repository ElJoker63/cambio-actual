package com.aewaredev.cambioactual.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data object Market : Destination
    
    @Serializable
    data object Crypto : Destination
    
    @Serializable
    data object SMS : Destination
    
    @Serializable
    data object Converter : Destination

    @Serializable
    data class Detail(val code: String) : Destination
}
