import 'package:flutter_test/flutter_test.dart';
import 'package:opticon_h35_sdk/opticon_h35_sdk.dart';
import 'package:opticon_h35_sdk/opticon_h35_sdk_platform_interface.dart';
import 'package:opticon_h35_sdk/opticon_h35_sdk_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockOpticonH35SdkPlatform
    with MockPlatformInterfaceMixin
    implements OpticonH35SdkPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final OpticonH35SdkPlatform initialPlatform = OpticonH35SdkPlatform.instance;

  test('$MethodChannelOpticonH35Sdk is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelOpticonH35Sdk>());
  });

  test('getPlatformVersion', () async {
    OpticonH35Sdk opticonH35SdkPlugin = OpticonH35Sdk();
    MockOpticonH35SdkPlatform fakePlatform = MockOpticonH35SdkPlatform();
    OpticonH35SdkPlatform.instance = fakePlatform;

    expect(await opticonH35SdkPlugin.getPlatformVersion(), '42');
  });
}
