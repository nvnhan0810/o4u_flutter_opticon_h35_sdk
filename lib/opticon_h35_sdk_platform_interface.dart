import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'opticon_h35_sdk_method_channel.dart';

abstract class OpticonH35SdkPlatform extends PlatformInterface {
  /// Constructs a OpticonH35SdkPlatform.
  OpticonH35SdkPlatform() : super(token: _token);

  static final Object _token = Object();

  static OpticonH35SdkPlatform _instance = MethodChannelOpticonH35Sdk();

  /// The default instance of [OpticonH35SdkPlatform] to use.
  ///
  /// Defaults to [MethodChannelOpticonH35Sdk].
  static OpticonH35SdkPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [OpticonH35SdkPlatform] when
  /// they register themselves.
  static set instance(OpticonH35SdkPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<bool> connect() {
    throw UnimplementedError('connect() has not been implemented.');
  }

  Future<bool> disconnect() {
    throw UnimplementedError('disconnect() has not been implemented.');
  }

  Future<bool> startScanning() {
    throw UnimplementedError('startScanning() has not been implemented.');
  }

  Future<bool> stopScanning() {
    throw UnimplementedError('stopScanning() has not been implemented.');
  }
}
