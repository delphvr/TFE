import 'package:calendar_app/auth/login_or_register.dart';
import 'package:calendar_app/project/project_user.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
//source: https://www.youtube.com/watch?v=_3W-JuIVFlg

//TODO: ceci completement remplacer par main
class Auth extends StatelessWidget {
  //decide between home page and authentification page
  const Auth({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: StreamBuilder<User?>(
          stream: FirebaseAuth.instance.authStateChanges(),
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              return  const ProjectsUserPage();
            } else {
              return const LoginOrRegister();
            }
          }),
    );
  }
}
