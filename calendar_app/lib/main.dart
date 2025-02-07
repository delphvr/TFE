import 'package:flutter/material.dart';
import 'package:calendar_app/auth/auth.dart';
import 'package:firebase_core/firebase_core.dart';
import 'firebase_options.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

void main() async {
  await dotenv.load(fileName: "lib/.env");
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform);
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData(
        colorScheme: const ColorScheme(
          brightness: Brightness.light, 
          primary: Colors.black, // Main color used in buttons, text, etc.
          onPrimary: Colors.white, // Color for text/icons on primary
          secondary: Color(0xFFF2F2F2), // Secondary color for accents
          onSecondary: Colors.black,
          surface: Color(0xFFF2F2F2), // Color for app bars, etc.
          onSurface: Colors.black,
          error: Colors.red,
          onError: Colors.white,
        ),
        useMaterial3: true,
      ),
      home: const Auth(),
    );
  }
}
