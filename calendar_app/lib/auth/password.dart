import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

class PasswordForgottenPage extends StatefulWidget {
  const PasswordForgottenPage({super.key});

  @override
  State<PasswordForgottenPage> createState() => _PasswordForgottenPageState();
}

class _PasswordForgottenPageState extends State<PasswordForgottenPage> {
  final emailController = TextEditingController();

  void sendMail() async {
    final String email = emailController.text;
    if (emailController.text.isEmpty) {
      Utils.errorMess('Une erreur est survenue',
          'Merci de remplir tous le champs', context);
    } else if (!Utils.isValidEmail(emailController.text)) {
      Utils.errorMess('Erreur de connexion', 'Email non valide.', context);
    } else {
      await FirebaseAuth.instance.sendPasswordResetEmail(email: email);
      if (mounted) {
        Utils.errorMess(
            'Email envoyé', 'Un email de récupération à été envoyé, si un compte existe avec cette adresse email.', context);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        backgroundColor: Colors.white,
        appBar: AppBar(
          backgroundColor: Colors.white,
          actions: const [],
        ),
        body: Center(
          child: SingleChildScrollView(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Text(
                  'Mot de passe oublié',
                  style: TextStyle(
                    fontSize: 30,
                  ),
                ),
                const SizedBox(height: 20),
                TextFieldcustom(
                  labelText: 'Email',
                  controller: emailController,
                  obscureText: false,
                  keyboardType: TextInputType.emailAddress,
                ),
                const SizedBox(height: 20),
                ButtonCustom(
                  text: 'Récupérer',
                  onTap: sendMail,
                ),
              ],
            ),
          ),
        ));
  }
}
