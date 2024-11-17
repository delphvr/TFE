import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
//source: https://www.youtube.com/watch?v=qlVj-0vpaW0

class Register extends StatefulWidget {
  final Function()? onTap;
  const Register({super.key, required this.onTap});

  @override
  State<Register> createState() => _RegisterState();
}

class _RegisterState extends State<Register> {
  final emailController = TextEditingController();
  final passwordController = TextEditingController();
  final confirmpasswordController = TextEditingController();
  final firstnameController = TextEditingController();
  final lastnameController = TextEditingController();
  bool isOrganizer = false;

  Future<int> pushUserToBackend(
      String email, String firstName, String lastName) async {
    const String url = 'http://192.168.0.169:8080/api/users';

    try {
      final response = await http.post(
        Uri.parse(url),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode({
          "firstName": firstName,
          "lastName": lastName,
          "email": email,
          "isOrganizer": isOrganizer,
          "professions": [],
        }),
      );

      if (response.statusCode == 201) {
        return 0;
      } else {
        print(response.statusCode);
        return -1;
      }
    } catch (e) {
      print(e);
      return -1;
    }
  }

  void login() async {
    showDialog(
      context: context,
      builder: (context) {
        return const Center(
          child: CircularProgressIndicator(),
        );
      },
    );
    //TODO: les check if dans une fonction aux?
    if (!isValidEmail(emailController.text)) {
      if (mounted) {
        Navigator.pop(context);
      }
      errorMess('Email non Valide');
    } else if (emailController.text.isEmpty ||
        passwordController.text.isEmpty ||
        confirmpasswordController.text.isEmpty) {
      if (mounted) {
        Navigator.pop(context);
      }
      errorMess('Merci de remplir tous les champs');
    } else if (passwordController.text.length < 6) {
      if (mounted) {
        Navigator.pop(context);
      }
      errorMess('Le mot de passe doit faire au moins 6 caractères.');
    } else if (passwordController.text != confirmpasswordController.text) {
      if (mounted) {
        Navigator.pop(context);
      }
      errorMess('Les mots de passe ne correspondent pas');
    } else {
      int error = await pushUserToBackend(
        emailController.text,
        firstnameController.text,
        lastnameController.text,
      );
      //TODO: if erreur car email alredy in use put better error message indication
      //TODO: if erreur firebase dois rolback les changement à la db
      if (error == -1) {
        if (mounted) {
          Navigator.pop(context);
        }
        errorMess("Echecs de l'envoie des données au backend");
      } else {
        try {
          await FirebaseAuth.instance.createUserWithEmailAndPassword(
              email: emailController.text, password: passwordController.text);

          if (mounted) {
            Navigator.pop(context);
          }
        } on FirebaseAuthException catch (e) {
          if (mounted) {
            Navigator.pop(context);
          }
          errorMess(e.code);
        }
      }
    }
  }

  //Source: https://stackoverflow.com/questions/16800540/how-should-i-check-if-the-input-is-an-email-address-in-flutter
  bool isValidEmail(String email) {
    return RegExp(
            r'^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$')
        .hasMatch(email);
  }

  void errorMess(String message) {
    print(message);
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
            title: const Text('Erreur lors de la création du compte'),
            content: Text(message),
            actions: [
              TextButton(
                onPressed: () {
                  Navigator.pop(context);
                },
                child: const Text('OK'),
              ),
            ]);
      },
    );
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
                    'Créer un Compte',
                    style: TextStyle(
                      fontSize: 30,
                    ),
                  ),

                  const SizedBox(height: 50),

                  TextFieldcustom(
                    labelText: 'Prénom',
                    controller: firstnameController,
                    obscureText: false,
                  ),

                  const SizedBox(height: 25),

                  TextFieldcustom(
                    labelText: 'Nom de famille',
                    controller: lastnameController,
                    obscureText: false,
                  ),

                  const SizedBox(height: 25),

                  //email
                  TextFieldcustom(
                    labelText: 'Email',
                    controller: emailController,
                    obscureText: false,
                  ),

                  const SizedBox(height: 25),

                  //mot de passe
                  TextFieldcustom(
                    labelText: 'Mot de passe',
                    controller: passwordController,
                    obscureText: true,
                  ),

                  const SizedBox(height: 25),

                  TextFieldcustom(
                    labelText: 'Confirmer le mot de passe',
                    controller: confirmpasswordController,
                    obscureText: true,
                  ),

                  const SizedBox(height: 25),

                  const Text(
                    'Êtes-vous organisateur ?',
                    style: TextStyle(fontSize: 18),
                  ),
                  const SizedBox(height: 10),

                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Row(
                        children: [
                          Radio<bool>(
                            value: true,
                            groupValue: isOrganizer,
                            onChanged: (bool? value) {
                              setState(() {
                                isOrganizer = value!;
                              });
                            },
                          ),
                          const Text(
                            'Oui',
                            style: TextStyle(fontSize: 18),
                          ),
                        ],
                      ),
                      Row(
                        children: [
                          Radio<bool>(
                            value: false,
                            groupValue: isOrganizer,
                            onChanged: (bool? value) {
                              setState(() {
                                isOrganizer = value!;
                              });
                            },
                          ),
                          const Text(
                            'Non',
                            style: TextStyle(fontSize: 18),
                          ),
                        ],
                      ),
                    ],
                  ),

                  const SizedBox(height: 25),

                  //button se connecter
                  ButtonCustom(
                    text: 'Créer mon compte',
                    onTap: login,
                  ),

                  const SizedBox(height: 25),

                  //creer un compte
                  GestureDetector(
                      onTap: widget.onTap,
                      child: const Text('A déjà un compte, se connecter',
                          style: TextStyle(
                            fontSize: 17.0,
                          ))),
                ],
              ),
            ),
          ),
        ));
  }
}
