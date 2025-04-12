import 'package:calendar_app/auth/login.dart';
import 'package:calendar_app/auth/register.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

//source: https://www.youtube.com/watch?v=qlVj-0vpaW0

class LoginOrRegister extends StatefulWidget {
  final FirebaseAuth auth;
  final http.Client client;

  const LoginOrRegister({
    super.key,
    required this.auth,
    required this.client,
  });

  @override
  State<LoginOrRegister> createState() => _LoginOrRegisterState();
}

class _LoginOrRegisterState extends State<LoginOrRegister> {
  bool showLoginPage = true;

  void togglePages() {
    setState(() {
      showLoginPage = !showLoginPage;
    });
  }

  @override
  Widget build(BuildContext context) {
    if (showLoginPage) {
      return LoginScreen(
        onTap: togglePages,
      );
    } else {
      return Register(
        onTap: togglePages,
        auth: widget.auth,
        client: widget.client,
      );
    }
  }
}
