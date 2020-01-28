package com.trinity.countly

import android.app.Activity
import android.content.Context
import ly.count.android.sdk.Countly
import java.util.HashMap

object Control {

    var event: String = ""
    lateinit var segmentation: HashMap<String, String>
    var unicKey: String = ""
    var countlyControlAcsess: CountlyControlAccess? = null

    fun build(event: String, unicKey: String = ""): Control {
        this.event = event
        segmentation = HashMap()
        this.unicKey = unicKey
        return this
    }

    fun addSuccess() {
        segmentation["Total"] = "Sucesso"
    }

    fun addError() {
        segmentation["Total"] = "Erro"
    }

    fun addSegment(key: String = "", value: String): Control {
        segmentation[key(key)] = value
        return this
    }

    fun addSegment(key: String = "", value: List<String>) {
        value.forEach {
            segmentation[key(key)] = it
        }
    }

    fun accessToScreen(value: String) {
        countlyControlAcsess = CountlyControlAccess(value)
        countlyControlAcsess!!.accessToScreen()
    }

    fun concludeScreenFlow() {
        countlyControlAcsess!!.conclude()
    }

    fun unfinishedScreenFlow() {
        countlyControlAcsess!!.unfinished()
    }

    private fun key(key: String) = if (unicKey.isEmpty()) key else unicKey

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