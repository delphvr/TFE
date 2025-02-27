import 'package:calendar_app/auth/login_or_register.dart';
import 'package:calendar_app/project/project_user.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:firebase_core/firebase_core.dart';
import 'firebase_options.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

//Source: https://gist.github.com/bicasoftware/d222e76e81d367f947f89d006a10165b
class SadPageTransition extends PageTransitionsBuilder {
  @override
  Widget buildTransitions<T>(
      PageRoute<T> route,
      BuildContext context,
      Animation<double> animation,
      Animation<double> secondaryAnimation,
      Widget child) {
    return child;
  }
}

void main() async {
  await dotenv.load(fileName: "lib/.env");
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform);
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool isUserLoggedIn = false; 

  @override
  void initState() {
    super.initState();
    
    FirebaseAuth.instance.authStateChanges().listen((User? user) {
      setState(() {
        isUserLoggedIn = user != null;
      });
    });
  }

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
        pageTransitionsTheme: PageTransitionsTheme(
      builders: {
        TargetPlatform.android: SadPageTransition()
      }
    )
      ),
      home: isUserLoggedIn
          ? const ProjectsUserPage()
          : const LoginOrRegister(),
    );
  }
}

