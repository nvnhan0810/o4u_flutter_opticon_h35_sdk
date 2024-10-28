import 'dart:async';

import 'package:flutter/services.dart';

import 'opticon_h35_sdk_platform_interface.dart';

class OpticonH35Sdk {
  static const MethodChannel _eventChannel =
      MethodChannel('option_h35_sdk_event');

  Future<String?> getPlatformVersion() {
    return OpticonH35SdkPlatform.instance.getPlatformVersion();
  }

  // Stream controllers for different events
  static final StreamController<String> _barcodeController =
      StreamController<String>.broadcast();
  static final StreamController<String> _errorController =
      StreamController<String>.broadcast();

  // Expose streams
  static Stream<String> get barcodeStream => _barcodeController.stream;
  static Stream<String> get errorStream => _errorController.stream;

  static Future<void> initialize() async {
    _eventChannel.setMethodCallHandler(_handleMethodCall);
  }

  static Future<dynamic> _handleMethodCall(MethodCall call) async {
    switch (call.method) {
      case 'onBarcodeScanned':
        _barcodeController.add(call.arguments as String);
        break;
    }
  }

  static Future<bool> connect() async {
    return await OpticonH35SdkPlatform.instance.connect();
  }

  static Future<bool> disconnect() async {
    return await OpticonH35SdkPlatform.instance.disconnect();
  }

  static Future<bool> startScanning() async {
    try {
      return await OpticonH35SdkPlatform.instance.startScanning();
    } catch (e) {
      _errorController.add(e.toString());
      return false;
    }
  }

  static Future<bool> stopScanning() async {
    try {
      return await OpticonH35SdkPlatform.instance.stopScanning();
    } catch (e) {
      _errorController.add(e.toString());
      return false;
    }
  }

  static void dispose() {
    _barcodeController.close();
  }
}
