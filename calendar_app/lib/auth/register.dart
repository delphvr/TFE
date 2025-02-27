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
  String url = '${dotenv.env['API_BASE_URL']}/users';
  List<Profession> professions = [];
  List<Profession> selectedProfessions = [];
  List<MultiSelectItem<Profession>> items = [];

  @override
  void initState() {
    super.initState();
    _loadProfessions();
  }

  Future<List<Profession>> getProfessions() async {
    String url = '${dotenv.env['API_BASE_URL']}/professions';
    try {
      final response = await http.get(Uri.parse(url));

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
      print("Error loading professions: $e");
    }
  }

  Future<int> pushUserToBackend(
      String email, String firstName, String lastName) async {
    try {
      final response = await http.post(
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
          Navigator.pop(context);
        }
        if (mounted) {
          Utils.errorMess('Erreur lors de la création du compte',
              "Adresse email déjà utilisé", context);
        }
        return -1;
      } else {
        if (mounted) {
          Navigator.pop(context);
        }
        if (mounted) {
          Utils.errorMess('Erreur lors de la création du compte',
              "Echecs de l'envoie des données au server", context);
        }
        return -1;
      }
    } catch (e) {
      if (mounted) {
        Navigator.pop(context);
      }
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
      if (mounted) {
        Navigator.pop(context);
      }
      Utils.errorMess('Erreur lors de la création du compte',
          'Merci de remplir tous les champs', context);
      return -1;
    } else if (!Utils.isValidEmail(emailController.text)) {
      if (mounted) {
        Navigator.pop(context);
      }
      Utils.errorMess(
          'Erreur lors de la création du compte', 'Email non valide', context);
      return -1;
    } else if (passwordController.text.length < 6) {
      if (mounted) {
        Navigator.pop(context);
      }
      Utils.errorMess('Erreur lors de la création du compte',
          'Le mot de passe doit faire au moins 6 caractères.', context);
      return -1;
    } else if (passwordController.text != confirmpasswordController.text) {
      if (mounted) {
        Navigator.pop(context);
      }
      Utils.errorMess('Erreur lors de la création du compte',
          'Les mots de passe ne correspondent pas', context);
      return -1;
    }
    return 1;
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
    if (checkInput() == 1) {
      int userId = await pushUserToBackend(
        emailController.text.trim(),
        firstnameController.text.trim(),
        lastnameController.text.trim(),
      );
      if (userId != -1) {
        try {
          await FirebaseAuth.instance.createUserWithEmailAndPassword(
              email: emailController.text, password: passwordController.text);
          if (mounted) {
            Navigator.pop(context);
          }
        } on FirebaseAuthException catch (e) {
          await http.delete(
            Uri.parse("$url/$userId"),
          );
          if (mounted) {
            Navigator.pop(context);
            Utils.errorMess(
              'Erreur lors de la création du compte', e.code, context);
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
                  labelText: 'Prénom',
                  controller: firstnameController,
                  obscureText: false,
                  keyboardType: TextInputType.text,
                ),

                const SizedBox(height: 20),

                TextFieldcustom(
                  labelText: 'Nom de famille',
                  controller: lastnameController,
                  obscureText: false,
                  keyboardType: TextInputType.text,
                ),

                const SizedBox(height: 20),

                //email
                TextFieldcustom(
                  labelText: 'Email',
                  controller: emailController,
                  obscureText: false,
                  keyboardType: TextInputType.emailAddress,
                ),

                const SizedBox(height: 20),

                //mot de passe
                TextFieldcustom(
                  labelText: 'Mot de passe',
                  controller: passwordController,
                  obscureText: true,
                  keyboardType: TextInputType.text,
                ),

                const SizedBox(height: 20),

                TextFieldcustom(
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
