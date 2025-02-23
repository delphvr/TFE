import 'package:calendar_app/auth/login_or_register.dart';
import 'package:calendar_app/home.dart';
import 'package:calendar_app/organizer/project/project_admin.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

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
              return FutureBuilder(
                  future: SharedPreferences.getInstance(),
                  builder: (context, snapshotb) {
                    if (snapshotb.hasData) {
                      SharedPreferences prefs = snapshotb.data!;
                      bool? isOrganize = prefs.getBool("isOrganizer");
                      if (isOrganize == null) {
                        return HomePage();
                      }
                      if (isOrganize == true) {
                        return const ProjectOrganizerPage();
                      } else if (isOrganize == false) {
                        return HomePage();
                      }
                      return HomePage();
                    } else {
                      return const Center(
                        child: CircularProgressIndicator(),
                      );
                    }
                  });
            } else {
              return const LoginOrRegister();
            }
          }),
    );
  }
}
