import 'package:calendar_app/project/new_project.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/components/button_custom.dart';

class ProjectPage extends StatelessWidget {
  ProjectPage({super.key});

  final user = FirebaseAuth.instance.currentUser!;

  void logout() {
    FirebaseAuth.instance.signOut();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        backgroundColor: Colors.white,
        actions: [
          IconButton(
              onPressed: logout,
              icon: const Icon(
                Icons.logout,
                size: 40,
              ))
        ],
      ),
      body: Center(
          child: SingleChildScrollView(
              child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          ButtonCustom(
            text: "New project",
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => NewProjectPage()),
              );
            },
          ),
        ],
      ))),
    );
  }
}
