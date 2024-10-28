import 'dart:async';

import 'package:flutter/material.dart';
import 'package:opticon_h35_sdk/opticon_h35_sdk.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _lastBarcode = '';
  String _error = '';
  bool _isConnected = false;
  StreamSubscription? _barcodeSub;
  StreamSubscription? _errorSub;

  @override
  void initState() {
    super.initState();

    initializeApp();
  }

  Future<void> initializeApp() async {
    await OpticonH35Sdk.initialize();
    _barcodeSub = OpticonH35Sdk.barcodeStream.listen((barcode) {
      setState(() => _lastBarcode = barcode);
    });

    _errorSub = OpticonH35Sdk.errorStream.listen((message) {
      setState(() {
        _error = message;
      });
    });

    try {
      await OpticonH35Sdk.connect();

      setState(() {
        _isConnected = true;
      });
    } catch (e) {
      print('Failed to connect: $e');
    }
  }

  @override
  void dispose() {
    super.dispose();
    _barcodeSub?.cancel();
    _errorSub?.cancel();
    OpticonH35Sdk.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('Opticon Scanner')),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                  'Connection status: ${_isConnected ? "Connected" : "Disconnected"}'),
              const SizedBox(height: 20),
              Text('Last scanned barcode: $_lastBarcode'),
              const SizedBox(height: 20),
              Text('Error: $_error'),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: () {
                  OpticonH35Sdk.startScanning();
                },
                child: const Text('Start Scanning'),
              ),
              ElevatedButton(
                onPressed: () => OpticonH35Sdk.stopScanning(),
                child: const Text('Stop Scanning'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
