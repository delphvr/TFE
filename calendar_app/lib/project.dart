import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

class ProjectPage extends StatelessWidget{
  ProjectPage({super.key});

  final user = FirebaseAuth.instance.currentUser!;

  void logout(){
    FirebaseAuth.instance.signOut();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(actions: [IconButton(onPressed: logout, icon: const Icon(Icons.logout, size: 40,))],),
      body: Center(
        child: Text('Bienvenue organizer (${user.email!})'),
      ),
    );
  }
}
