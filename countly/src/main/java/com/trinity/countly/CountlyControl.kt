package com.trinity.countly

import android.app.Activity
import android.content.Context
import ly.count.android.sdk.Countly
import ly.count.android.sdk.DeviceId
import java.util.HashMap

object CountlyControl {

    var event: String = ""
    lateinit var segmentation: HashMap<String, String>
    var unicKey: String = ""
    var countlyControlAcsess: CountlyControlAccess? = null

    @JvmStatic
    fun build(event: String, unicKey: String = ""): CountlyControl {
        this.event = event
        segmentation = HashMap()
        this.unicKey = unicKey
        return this
    }

    @JvmStatic
    fun addSuccess() {
        segmentation["Total"] = "Sucesso"
    }

    @JvmStatic
    fun addError() {
        segmentation["Total"] = "Erro"
    }

    @JvmStatic
    fun addSegment(key: String = "", value: String): CountlyControl {
        segmentation[key(key)] = value
        return this
    }

    @JvmStatic
    fun addSegment(key: String = "", value: List<String>) {
        value.forEach {
            segmentation[key(key)] = it
        }
    }

    @JvmStatic
    fun accessToScreen(value: String) {
        countlyControlAcsess = CountlyControlAccess(value)
        countlyControlAcsess!!.accessToScreen()
    }

    @JvmStatic
    fun concludeScreenFlow() {
        countlyControlAcsess!!.conclude()
    }

    @JvmStatic
    fun unfinishedScreenFlow() {
        countlyControlAcsess!!.unfinished()
    }

    private fun key(key: String) = if (unicKey.isEmpty()) key else unicKey

    @JvmStatic
    fun send(clear: Boolean = false) {
        try {
            Countly.sharedInstance().recordEvent(event, segmentation, 1)
            if (clear) {
                this.event = ""
                segmentation = HashMap()
                unicKey = ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun start(
        context: Context,
        url: String,
        key: String,
        enableCrashReporting: Boolean = false,
        isLoggingEnabled: Boolean = false,
        isHttpPostForced: Boolean = false
    ) {
        init(context, url, key, enableCrashReporting, isLoggingEnabled, isHttpPostForced)
        Countly.sharedInstance().onStart(context as Activity)
    }

    @JvmStatic
    fun init(
        context: Context,
        url: String,
        key: String
    ) {
        Countly.sharedInstance().init(context, url, key)
    }

    @JvmStatic
    fun init(
        context: Context,
        url: String,
        key: String,
        deviceId: String
    ) {
        Countly.sharedInstance().init(context, url, key, deviceId)
    }

    @JvmStatic
    fun init(
        context: Context,
        url: String,
        key: String,
        enableCrashReporting: Boolean = false,
        isLoggingEnabled: Boolean = false,
        isHttpPostForced: Boolean = false
    ) {
        Countly.sharedInstance().init(context, url, key)
        Countly.sharedInstance().isLoggingEnabled = isLoggingEnabled
        if (enableCrashReporting)
            Countly.sharedInstance().enableCrashReporting()
        Countly.sharedInstance().isHttpPostForced = isHttpPostForced
    }

    @JvmStatic
    fun init(
        context: Context,
        url: String,
        key: String,
        deviceId: String,
        enableCrashReporting: Boolean = false,
        isLoggingEnabled: Boolean = false,
        isHttpPostForced: Boolean = false
    ) {
        Countly.sharedInstance().init(context, url, key, deviceId)
        Countly.sharedInstance().isLoggingEnabled = isLoggingEnabled
        if (enableCrashReporting)
            Countly.sharedInstance().enableCrashReporting()
        Countly.sharedInstance().isHttpPostForced = isHttpPostForced
    }

    @JvmStatic
    fun countly(): Countly = Countly.sharedInstance()

    class CountlyControlAccess(var value: String) {
        lateinit var segmentation: HashMap<String, String>

        fun accessToScreen() {
            segmentation = HashMap()
            segmentation["Inicializada"] = value
            send()
        }

        fun conclude() {
            segmentation["Concluído"] = value
            send()
        }

        fun unfinished() {
            segmentation["Não concluído"] = value
            send()
        }

        private fun send() {
            Countly.sharedInstance().recordEvent("Fluxo de Tela", segmentation, 1)
        }
    }
}