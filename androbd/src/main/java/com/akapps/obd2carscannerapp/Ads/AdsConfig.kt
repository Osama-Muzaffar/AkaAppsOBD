package com.quadsquad.savestatus.videodownloader.storysaver.statuskeeper.adsConfigs

import androidx.annotation.Keep

@Keep
data class AdsConfig(
    val splash_intersitial: Boolean = true,
    val splash_banner: Boolean = true,
    val main_banner_simple: Boolean = true,
    val main_banner_collapsible: Boolean = true,
    val main_native: Boolean = true,
    val connect_intersitial: Boolean = true,
    val scanning_intersitial: Boolean = true,
    val realtime_intersitial: Boolean = true,
    val obddata_intersitial: Boolean = true,
    val faultcode_intersitial: Boolean = true,
    val faultcode_details_banner: Boolean = true,
    val help_intersitial: Boolean = true,
    val paired_banner_simple: Boolean = true,
    val paired_banner_collapse: Boolean = true,
    val scanning_banner_simple: Boolean = true,
    val scanning_banner_collapse: Boolean = true,
    val obddata_banner_simple: Boolean = true,
    val obddata_banner_collapse: Boolean = true,
    val faultcodes_banner_simple: Boolean = true,
    val faultcodes_banner_collapse: Boolean = true,
    val realtime_native: Boolean = true,
    val realtime_banner: Boolean = true,
    val language_native: Boolean = true,
    val language_banner_simple: Boolean = true,
    val language_banner_collapsible: Boolean = true,
    val setting_banner_collapsible: Boolean = true,
    val setting_banner: Boolean = true,
    val setting_native: Boolean = true,
)
