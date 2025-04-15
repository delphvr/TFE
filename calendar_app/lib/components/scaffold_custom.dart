import 'package:calendar_app/calendar/calendar.dart';
import 'package:calendar_app/main.dart';
import 'package:calendar_app/organizer/project/project_admin.dart';
import 'package:calendar_app/profil/profil.dart';
import 'package:calendar_app/project/project_user.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class CustomScaffold extends StatefulWidget {
  final Widget body;
  final int selectedIndex;
  final FirebaseAuth? auth;
  final http.Client? client;

  const CustomScaffold({
    super.key,
    required this.body,
    required this.selectedIndex,
    this.auth,
    this.client,
  });

  @override
  State<CustomScaffold> createState() => _CustomScaffoldState();
}

class _CustomScaffoldState extends State<CustomScaffold> {
  int _selectedIndex = 1;

  FirebaseAuth get auth => widget.auth ?? FirebaseAuth.instance;
  http.Client get client => widget.client ?? http.Client();

  @override
  void initState() {
    super.initState();
    _selectedIndex = widget.selectedIndex;
  }

  List<Widget> _pages() {
    return [
      ProjectsUserPage(
        auth: auth,
        client: client,
      ),
      const ProjectOrganizerPage(),
      const CalendarPage(),
      const ProfilPage(),
    ];
  }

  void _navigateBottomBar(int index) {
    setState(() {
      _selectedIndex = index;
    });
    Navigator.of(context).pushAndRemoveUntil(
      MaterialPageRoute(builder: (context) => _pages()[index]),
      (route) => false,
    );
  }

  void logout(Function onLogoutSuccess) async {
    await auth.signOut();
    onLogoutSuccess();
  }

  @override
  Widget build(BuildContext context) {
    return RepaintBoundary(
      child: Scaffold(
        backgroundColor: Colors.white,
        appBar: AppBar(
          backgroundColor: Colors.white,
          actions: [
            IconButton(
              onPressed: () {
                logout(() {
                  Navigator.of(context).pushAndRemoveUntil(
                    MaterialPageRoute(
                        builder: (context) =>
                            MyApp(auth: auth, client: client)),
                    (route) => false,
                  );
                });
              },
              icon: const Icon(
                Icons.logout,
                size: 40,
              ),
            ),
          ],
        ),
        body: widget.body,
        bottomNavigationBar: BottomNavigationBar(
          currentIndex: _selectedIndex,
          onTap: _navigateBottomBar,
          type: BottomNavigationBarType.fixed,
          items: const [
            BottomNavigationBarItem(icon: Icon(Icons.list), label: 'Projets'),
            BottomNavigationBarItem(
                icon: Icon(Icons.assignment), label: 'Organisateurs'),
            BottomNavigationBarItem(
                icon: Icon(Icons.calendar_month), label: 'Calendrier'),
            BottomNavigationBarItem(icon: Icon(Icons.person), label: 'Profil'),
          ],
        ),
      ),
    );
  }
}
