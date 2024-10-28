package com.o4u.opticon_h35_sdk

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import com.opticon.scannersdk.scanner.*
import com.opticon.settings.readoption.ReadOption
import android.os.Bundle
import android.util.Log
import android.content.Context


/** OpticonH35SdkPlugin */
class OpticonH35SdkPlugin: FlutterPlugin, MethodCallHandler, BarcodeEventListener {
    val TAG = "H35_SDK"
    private lateinit var channel : MethodChannel
    private lateinit var eventChannel: MethodChannel;
    private lateinit var context: Context
    var scanner: Scanner? = null
    var scannerManager: ScannerManager? = null
    var isScanning: Boolean = false

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "opticon_h35_sdk")
        channel.setMethodCallHandler(this)

        eventChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "option_h35_sdk_event");
        eventChannel.setMethodCallHandler(this);
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method != "connect" && scanner == null) {
            result.error("SCANNER_ERROR", "Not init scanner", null);
            return;
        }

        when (call.method) {
            "connect" -> {
                try {
                    scannerManager = ScannerManager.getInstance(context)

                    Log.d(TAG, "Trigger")
                    for (info in scannerManager!!.scannerInfoList) {
                        Log.d(TAG, info.toString())
                        Log.d(TAG, info?.type.toString())
                        //ソフトウェアスキャナ(端末内臓スキャナ)を操作したいとき
                        if (info.type == ScannerType.SOFTWARE_SCANNER) {
                            Log.d(TAG, "Create Scanner")
                            scanner = scannerManager!!.getScanner(info)
                            break
                        }
                    }

                    scanner?.init()

                    result.success(true)
                } catch (e: Exception) {
                    Log.d(TAG, "$e")
                    result.error("CONNECTION_ERROR", e.message, null)
                }
            }
            "disconnect" -> {
                try {
                if (scanner!!.isConnected) {
                    scanner?.deinit()
                    scanner?.removeBarcodeEventListener()
                }

                result.success(true)
                } catch (e: Exception) {
                    result.error("DISCONNECTION_ERROR", e.message, null)
                }
            }
            "startScanning" -> {
                try {
                    Log.d(TAG, scanner.toString())
                    if (!scanner!!.isConnected) throw Exception("Scanner not connected")
                    
                    scanner!!.startScan()

                    result.success(true)
                } catch (e: Exception) {
                    Log.d(TAG, "$e")
                    result.error("SCANNING_ERROR", e.message, null)
                }
            }
            "stopScanning" -> {
                try {
                    if (!scanner!!.isConnected) throw Exception("Scanner not connected")
                    
                    scanner!!.stopScan()

                    result.success(true)
                } catch (e: Exception) {
                    result.error("SCANNING_ERROR", e.message, null)
                }
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    //BarcodeEventListener
    override fun onReadData(readData: ReadData) {
        //読み取りデータを取得した際に呼ばれます
        Log.d(TAG, "onReadData:" + readData.text)
        Log.d(TAG, "onReadData:length:" + readData.rawData.size)
        Log.d(TAG, "onReadData:codeId:" + readData.codeID)

        val data = readData.text
        
        eventChannel.invokeMethod("onBarcodeScanned", data)
    }

    override fun onTimeout() {
        //読み取りを開始し、バーコードを読み取らずに時間が経過した際に呼ばれます
        Log.d(TAG, "onTimeout")
    }

    override fun onDecodeStart() {
        //読み取り開始時に呼ばれます
        isScanning = true
        Log.d(TAG, "onDecodeStart")
    }

    override fun onDecodeStop() {
        //読み取りの可否にかかわらず、読み取り終了時に呼ばれます
        isScanning = false
        Log.d(TAG, "onDecodeStop")
    }

    override fun onConnect() {
        //スキャナとの接続が開始された際に呼ばれます
        Log.d(TAG, "onConnect")
        changeIntentSettings()
    }

    override fun onDisconnect() {
        //スキャナとの接続が終了した際に呼ばれます
        Log.d(TAG, "onDisconnect")
    }

    private fun changeIntentSettings() {
        val settings = scanner!!.settings
        if (settings != null) {
            settings.softwareScanner.h35.wedge.intentIsEnable = true
            settings.softwareScanner.h35.wedge.intentAction = "com.opticon.decode.action"
            settings.softwareScanner.h35.wedge.intentCategory = "com.opticon.decode.category"
            settings.softwareScanner.h35.wedge.intentBarcodeType = "com.opticon.decode.barcode_type"
            settings.softwareScanner.h35.wedge.intentBarcodeData = "com.opticon.decode.barcode_data"
            settings.softwareScanner.h35.wedge.intentPackageName = "com.example.scannersdksample"
            scanner!!.settings = settings
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        eventChannel.setMethodCallHandler(null);

        scanner?.deinit()
        scanner?.removeBarcodeEventListener()
        scanner = null
        scannerManager = null
        isScanning = false
    }
}
