import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:opticon_h35_sdk/opticon_h35_sdk_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelOpticonH35Sdk platform = MethodChannelOpticonH35Sdk();
  const MethodChannel channel = MethodChannel('opticon_h35_sdk');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
