import 'package:calendar_app/auth/login_or_register.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/home.dart';
import 'package:calendar_app/project/project_admin.dart';
import 'package:firebase_core/firebase_core.dart';
import 'firebase_options.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:shared_preferences/shared_preferences.dart';


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
  int _selectedIndex = 0;
  bool? isOrganizer;
  bool isUserLoggedIn = false; 

  @override
  void initState() {
    super.initState();
    
    FirebaseAuth.instance.authStateChanges().listen((User? user) {
      setState(() {
        isUserLoggedIn = user != null;
      });
    });

    SharedPreferences.getInstance().then((prefs) {
      setState(() {
        isOrganizer = prefs.getBool("isOrganizer");
      });
    });
  }

  void _navigateBottomBar(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  List<Widget> _pages() {
    return [
      HomePage(),
      if (isOrganizer == true) const ProjectPage(),
      HomePage(),
    ];
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
      ),
      home: isUserLoggedIn
          ? Scaffold(
              body: _pages()[_selectedIndex],
              bottomNavigationBar: BottomNavigationBar(
                currentIndex: _selectedIndex,
                onTap: _navigateBottomBar,
                type: BottomNavigationBarType.fixed,
                items: const [
                  BottomNavigationBarItem(icon: Icon(Icons.home), label: 'Home'),
                  BottomNavigationBarItem(
                      icon: Icon(Icons.assignment), label: 'Organisateurs'),
                  BottomNavigationBarItem(icon: Icon(Icons.person), label: 'Profil'),
                ],
              ),
            )
          : const LoginOrRegister(),
    );
  }
}

