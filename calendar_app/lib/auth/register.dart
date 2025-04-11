import 'package:calendar_app/components/bottom_sheet_selector.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:multi_select_flutter/multi_select_flutter.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
//source: https://www.youtube.com/watch?v=qlVj-0vpaW0

class Profession {
  final String name;

  Profession({required this.name});

  factory Profession.fromJson(Map<String, dynamic> json) {
    return Profession(
      name: json['profession'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'profession': name,
    };
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) || (other is Profession && other.name == name);

  @override
  int get hashCode => name.hashCode;
}

class Register extends StatefulWidget {
  final Function()? onTap;
  final http.Client? client;
  final FirebaseAuth? auth;
  const Register({super.key, required this.onTap, this.client, this.auth});


  @override
  State<Register> createState() => _RegisterState();
}

class _RegisterState extends State<Register> {
  final emailController = TextEditingController();
  final passwordController = TextEditingController();
  final confirmpasswordController = TextEditingController();
  final firstnameController = TextEditingController();
  final lastnameController = TextEditingController();
  String url = '${dotenv.env['API_BASE_URL']}/users';
  List<Profession> professions = [];
  List<Profession> selectedProfessions = [];
  List<MultiSelectItem<Profession>> items = [];
  http.Client get client => widget.client ?? http.Client();
  FirebaseAuth get auth => widget.auth ?? FirebaseAuth.instance;

  @override
  void initState() {
    super.initState();
    _loadProfessions();
  }

  Future<List<Profession>> getProfessions() async {
    String url = '${dotenv.env['API_BASE_URL']}/professions';
    try {
      final response = await client.get(Uri.parse(url));

      if (response.statusCode == 200) {
        List<dynamic> jsonData = json.decode(utf8.decode(response.bodyBytes));
        return jsonData.isEmpty
            ? []
            : jsonData.map((json) => Profession.fromJson(json)).toList();
      } else if (response.statusCode == 204) {
        // No content
        return [];
      } else {
        throw Exception('Failed to load professions');
      }
    } catch (e) {
      throw Exception('Error: $e');
    }
  }

  Future<void> _loadProfessions() async {
    try {
      final fetchedProfessions = await getProfessions();
      setState(() {
        professions = fetchedProfessions;
        items = professions
            .map((profession) => MultiSelectItem<Profession>(
                  profession,
                  profession.name,
                ))
            .toList();
      });
    } catch (e) {
      return;
    }
  }

  Future<int> pushUserToBackend(
      String email, String firstName, String lastName) async {
    try {
      final response = await client.post(
        Uri.parse(url),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode({
          "firstName": firstName,
          "lastName": lastName,
          "email": email,
          "professions": selectedProfessions.map((p) => p.name).toList(),
        }),
      );

      if (response.statusCode == 201) {
        Map<String, dynamic> parsedJson =
            json.decode(utf8.decode(response.bodyBytes));
        return parsedJson["id"];
      } else if (response.statusCode == 409) {
        if (mounted) {
          Utils.errorMess('Erreur lors de la création du compte',
              "Adresse email déjà utilisé", context);
        }
        return -1;
      } else {
        if (mounted) {
          Utils.errorMess('Erreur lors de la création du compte',
              "Echecs de l'envoie des données au server", context);
        }
        return -1;
      }
    } catch (e) {
      if (mounted) {
        Utils.errorMess('Erreur lors de la création du compte',
            "Echecs de l'envoie des données au server", context);
      }
      return -1;
    }
  }

  int checkInput() {
    if (emailController.text.isEmpty ||
        passwordController.text.isEmpty ||
        confirmpasswordController.text.isEmpty) {
      Utils.errorMess('Erreur lors de la création du compte',
          'Merci de remplir tous les champs', context);
      return -1;
    } else if (!Utils.isValidEmail(emailController.text)) {
      Utils.errorMess(
          'Erreur lors de la création du compte', 'Email non valide', context);
      return -1;
    } else if (passwordController.text.length < 6) {
      Utils.errorMess('Erreur lors de la création du compte',
          'Le mot de passe doit faire au moins 6 caractères.', context);
      return -1;
    } else if (passwordController.text != confirmpasswordController.text) {
      Utils.errorMess('Erreur lors de la création du compte',
          'Les mots de passe ne correspondent pas', context);
      return -1;
    }
    return 1;
  }

  void login() async {
    if (checkInput() == 1) {
      int userId = await pushUserToBackend(
        emailController.text.trim(),
        firstnameController.text.trim(),
        lastnameController.text.trim(),
      );
      if (userId != -1) {
        try {
          await auth.createUserWithEmailAndPassword(
              email: emailController.text, password: passwordController.text);
        } on FirebaseAuthException catch (_) {
          await http.delete(
            Uri.parse("$url/$userId"),
          );
          if (mounted) {
            Utils.errorMess(
              'Erreur lors de la création du compte', "Merci de réessayer plus tard", context);
          }
        }
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        backgroundColor: Colors.white,
        body: Center(
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

                const SizedBox(height: 20),

                TextFieldcustom(
                  key: const Key('firstNameField'),
                  labelText: 'Prénom',
                  controller: firstnameController,
                  obscureText: false,
                  keyboardType: TextInputType.text,
                ),

                const SizedBox(height: 20),

                TextFieldcustom(
                  key: const Key('lastNameField'),
                  labelText: 'Nom de famille',
                  controller: lastnameController,
                  obscureText: false,
                  keyboardType: TextInputType.text,
                ),

                const SizedBox(height: 20),

                //email
                TextFieldcustom(
                  key: const Key('emailField'),
                  labelText: 'Email',
                  controller: emailController,
                  obscureText: false,
                  keyboardType: TextInputType.emailAddress,
                ),

                const SizedBox(height: 20),

                //mot de passe
                TextFieldcustom(
                  key: const Key('passwordField'),
                  labelText: 'Mot de passe',
                  controller: passwordController,
                  obscureText: true,
                  keyboardType: TextInputType.text,
                ),

                const SizedBox(height: 20),

                TextFieldcustom(
                  key: const Key('confirmPasswordField'),
                  labelText: 'Confirmer le mot de passe',
                  controller: confirmpasswordController,
                  obscureText: true,
                  keyboardType: TextInputType.text,
                ),

                const SizedBox(height: 20),

                BottomSheetSelector<Profession>(
                  items: professions,
                  selectedItems: selectedProfessions,
                  onSelectionChanged: (selectedList) {
                    setState(() {
                      selectedProfessions = selectedList;
                    });
                  },
                  title: "Sélectionnez vos professions",
                  buttonLabel: "Valider",
                  itemLabel: (profession) => profession.name,
                  textfield: "Professions",
                ),

                const SizedBox(height: 10),

                //button se connecter
                ButtonCustom(
                  text: 'Créer mon compte',
                  onTap: login,
                ),

                const SizedBox(height: 10),

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
        ));
  }
}
