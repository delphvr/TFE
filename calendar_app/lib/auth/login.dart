import 'package:calendar_app/auth/password.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
//source: https://www.youtube.com/watch?v=Dh-cTQJgM-Q

class LoginScreen extends StatefulWidget {
  final Function()? onTap;
  const LoginScreen({super.key, required this.onTap});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final emailController = TextEditingController();

  final passwordController = TextEditingController();

  void login() async {
    //TODO check that the user exist in the db?
    try {
      if (emailController.text.isEmpty || passwordController.text.isEmpty) {
        Utils.errorMess(
            'Erreur de connexion', 'Merci de remplir tous les champs', context);
      } else if (!Utils.isValidEmail(emailController.text)) {
        Utils.errorMess('Erreur de connexion', 'Email non valide.', context);
      } else {
        await FirebaseAuth.instance.signInWithEmailAndPassword(
          email: emailController.text,
          password: passwordController.text,
        );
        return;
      }
    } on FirebaseAuthException catch (e) {
      if (e.code == 'invalid-credential') {
        Utils.errorMess(
            'Erreur de connexion', 'Email ou mot de passe incorect', context);
      } else {
        Utils.errorMess('Erreur de connexion', e.code, context);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        backgroundColor: Colors.white,
        body: SafeArea(
          child: Center(
            child: SingleChildScrollView(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  //const SizedBox(height:50),

                  //S identifier
                  const Text(
                    'S\'identifier',
                    style: TextStyle(
                      fontSize: 30,
                    ),
                  ),

                  const SizedBox(height: 50),

                  //email
                  TextFieldcustom(
                    labelText: 'Email',
                    controller: emailController,
                    obscureText: false,
                    keyboardType: TextInputType.emailAddress,
                  ),

                  const SizedBox(height: 25),

                  //mot de passe
                  TextFieldcustom(
                    labelText: 'Mot de passe',
                    controller: passwordController,
                    obscureText: true,
                    keyboardType: TextInputType.text,
                  ),

                  const SizedBox(height: 15),

                  GestureDetector(
                    onTap: () {
                      Navigator.push(
                          context,
                          MaterialPageRoute(
                              builder: (context) =>
                                  const PasswordForgottenPage()));
                    },
                    child: const Text(
                      'Mot de passe oublié ?',
                      style: TextStyle(
                        fontSize: 15.0,
                      ),
                    ),
                  ),

                  const SizedBox(height: 25),

                  //button se connecter
                  ButtonCustom(
                    text: 'Se connecter',
                    onTap: login,
                  ),

                  const SizedBox(height: 25),

                  //creer un compte
                  GestureDetector(
                      onTap: widget.onTap,
                      child: const Text(
                        'Créer un compte',
                        style: TextStyle(
                          fontSize: 20.0,
                        ),
                      )),
                ],
              ),
            ),
          ),
        ));
  }
}
