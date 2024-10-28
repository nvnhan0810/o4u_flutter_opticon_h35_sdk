import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'opticon_h35_sdk_platform_interface.dart';

/// An implementation of [OpticonH35SdkPlatform] that uses method channels.
class MethodChannelOpticonH35Sdk extends OpticonH35SdkPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('opticon_h35_sdk');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<bool> connect() async {
    try {
      return await methodChannel.invokeMethod('connect');
    } catch (e) {
      throw PlatformException(
        code: 'CONNECTION_ERROR',
        message: 'Failed to connect to scanner: $e',
      );
    }
  }

  @override
  Future<bool> disconnect() async {
    try {
      return await methodChannel.invokeMethod('disconnect');
    } catch (e) {
      throw PlatformException(
        code: 'DISCONNECTION_ERROR',
        message: 'Failed to disconnect from scanner: $e',
      );
    }
  }

  @override
  Future<bool> startScanning() async {
    try {
      return await methodChannel.invokeMethod('startScanning');
    } catch (e) {
      throw PlatformException(
        code: 'SCANNING_ERROR',
        message: 'Failed to start scanning: $e',
      );
    }
  }

  @override
  Future<bool> stopScanning() async {
    try {
      return await methodChannel.invokeMethod('stopScanning');
    } catch (e) {
      throw PlatformException(
        code: 'SCANNING_ERROR',
        message: 'Failed to stop scanning: $e',
      );
    }
  }
}
