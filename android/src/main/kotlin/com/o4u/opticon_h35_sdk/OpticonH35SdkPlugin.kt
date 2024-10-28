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

/** OpticonH35SdkPlugin */
class OpticonH35SdkPlugin: FlutterPlugin, MethodCallHandler, BarcodeEventListener {
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

    eventChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "option_h35_sdk_event");
    eventChannel.setStreamHandler(this);
  }

  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scannerManager = ScannerManager.getInstance(this)
        for (info in scannerManager!!.scannerInfoList) {
            //ソフトウェアスキャナ(端末内臓スキャナ)を操作したいとき
            if (info.type == ScannerType.SOFTWARE_SCANNER) {
                scanner = scannerManager!!.getScanner(info)
                break
            }
        }


    }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
        "connect" -> {
            try {
                if (!scanner!!.isConnected) {
                  scanner?.init()
                }
                result.success(true)
            } catch (e: Exception) {
                result.error("CONNECTION_ERROR", e.message, null)
            }
        }
        "disconnect" -> {
            try {
              if (scanner!!.isConnected) {
                scanner?.disconnect()
              }

              result.success(true)
            } catch (e: Exception) {
                result.error("DISCONNECTION_ERROR", e.message, null)
            }
        }
        "startScanning" -> {
            try {
              if (!scanner!!.isConnected) throw Exception("Scanner not connected")
              
              if (!startScan) {
                scanner!!.startScan()
              }

              result.success(true)
            } catch (e: Exception) {
                result.error("SCANNING_ERROR", e.message, null)
            }
        }
        "stopScanning" -> {
            try {
              if (!scanner!!.isConnected) throw Exception("Scanner not connected")
              
              if (startScan) {
                scanner!!.stopScan()
              }

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

  override fun onResume() {
      super.onResume()
      if (scanner != null) {
          scanner!!.addBarcodeEventListener(this)
          Log.d(TAG, "" + scanner!!.init())
      }
  }

  override fun onPause() {
      super.onPause()
      if (scanner != null) {
          scanner!!.deinit()
          scanner!!.removeBarcodeEventListener()
      }
  }

  override fun onDestroy() {
      super.onDestroy()
      if (scanner != null) {
          scanner!!.deinit()
          if (scanner!!.isConnected) scanner!!.removeBarcodeEventListener()
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
    eventChannel.setStreamHandler(null);

    scanner?.disconnect()
    scanner = null
    scannerManager = null
    isScanning = false
  }
}
