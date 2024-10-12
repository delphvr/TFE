import 'package:calendar_app/auth/login.dart';
import 'package:calendar_app/auth/register.dart';
import 'package:flutter/material.dart';
//source: https://www.youtube.com/watch?v=qlVj-0vpaW0

class LoginOrRegister extends StatefulWidget {
  const LoginOrRegister({super.key});

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
    if (showLoginPage){
      return LoginScreen(onTap: togglePages,);
    } else {
      return Register(onTap: togglePages,);
    }
  }
}