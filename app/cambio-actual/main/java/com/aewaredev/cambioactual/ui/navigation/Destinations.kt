package com.aewaredev.cambioactual.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data object Market : Destination
    
    @Serializable
    data object Crypto : Destination

    @Serializable
    data object Marketplace : Destination
    
    @Serializable
    data object SMS : Destination
    
    @Serializable
    data object Converter : Destination

    @Serializable
    data class Detail(val code: String) : Destination

    @Serializable
    data class MarketplaceDetail(val postId: Int) : Destination

    @Serializable
    data object CreatePost : Destination

    @Serializable
    data object Profile : Destination

    @Serializable
    data object Login : Destination

    @Serializable
    data object Register : Destination

    @Serializable
    data object Verification : Destination

    @Serializable
    data object MyPosts : Destination

    @Serializable
    data class Ratings(val userId: Int) : Destination

    @Serializable
    data class PublicProfile(val userId: Int) : Destination
}
