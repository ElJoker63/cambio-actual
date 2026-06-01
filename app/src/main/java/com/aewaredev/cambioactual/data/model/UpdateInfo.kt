package com.aewaredev.cambioactual.data.model

data class UpdateInfo(
    val versionName: String,
    val versionCode: Long,
    val releaseNotes: String,
    val apkUrl: String
)
