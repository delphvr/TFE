import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:multi_select_flutter/multi_select_flutter.dart';
//source: https://www.youtube.com/watch?v=qlVj-0vpaW0

class Profession {
  final String name;

  Profession({required this.name});

  factory Profession.fromJson(Map<String, dynamic> json) {
    return Profession(
      name: json['profession'],
    );
  }
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
  bool isOrganizer = false;
  String url =
      'http://192.168.129.6:8080/api/users'; //TODO: recup dans un .env?
  List<Profession> professions = [];
  List<Profession> selectedProfessions = [];
  List<MultiSelectItem<Profession>> items = [];

  @override
  void initState() {
    super.initState();
    _loadProfessions();
  }

  Future<List<Profession>> getProfessions() async {
    const String url = 'http://192.168.129.6:8080/api/professions';
    try {
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        List<dynamic> jsonData = json.decode(response.body);
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
          "isOrganizer": isOrganizer,
          "professions": [],
        }),
      );

      if (response.statusCode == 201) {
        print(response.body);
        Map<String, dynamic> parsedJson = json.decode(response.body);
        return parsedJson["id"];
      } else if (response.statusCode == 409) {
        if (mounted) {
          Navigator.pop(context);
        }
        errorMess("Adresse email déjà utilisé");
        return -1;
      } else {
        //print(response.statusCode);
        if (mounted) {
          Navigator.pop(context);
        }
        errorMess("Echecs de l'envoie des données au server");
        return -1;
      }
    } catch (e) {
      //print(e);
      if (mounted) {
        Navigator.pop(context);
      }
      errorMess("Echecs de l'envoie des données au server");
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
      errorMess('Merci de remplir tous les champs');
      return -1;
    } else if (!isValidEmail(emailController.text)) {
      if (mounted) {
        Navigator.pop(context);
      }
      errorMess('Email non Valide');
      return -1;
    } else if (passwordController.text.length < 6) {
      if (mounted) {
        Navigator.pop(context);
      }
      errorMess('Le mot de passe doit faire au moins 6 caractères.');
      return -1;
    } else if (passwordController.text != confirmpasswordController.text) {
      if (mounted) {
        Navigator.pop(context);
      }
      errorMess('Les mots de passe ne correspondent pas');
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
          final response = await http.delete(
            Uri.parse("$url/$userId"),
          );
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

  void _showBottomSheet() async {
    List<Profession> tempSelected = List.from(selectedProfessions);

    await showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (BuildContext context) {
        return StatefulBuilder(
          builder: (BuildContext context, StateSetter setStateInModal) {
            return Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const SizedBox(height: 10),
                const Text(
                  "Selectionez vos professions",
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
                const Divider(),
                Expanded(
                  child: ListView.builder(
                    shrinkWrap: true,
                    itemCount: professions.length,
                    itemBuilder: (BuildContext context, int index) {
                      final profession = professions[index];
                      return CheckboxListTile(
                        title: Text(profession.name),
                        value: tempSelected.contains(profession),
                        onChanged: (bool? isSelected) {
                          setStateInModal(() {
                            if (isSelected == true) {
                              tempSelected.add(profession);
                            } else {
                              tempSelected.remove(profession);
                            }
                          });
                        },
                      );
                    },
                  ),
                ),
                ElevatedButton(
                  onPressed: () {
                    Navigator.pop(context);
                    setState(() {
                      selectedProfessions = tempSelected;
                    });
                  },
                  child: const Text("Valider"),
                ),
                const SizedBox(height: 10),
              ],
            );
          },
        );
      },
    );
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
                ),

                const SizedBox(height: 20),

                TextFieldcustom(
                  labelText: 'Nom de famille',
                  controller: lastnameController,
                  obscureText: false,
                ),

                const SizedBox(height: 20),

                //email
                TextFieldcustom(
                  labelText: 'Email',
                  controller: emailController,
                  obscureText: false,
                ),

                const SizedBox(height: 20),

                //mot de passe
                TextFieldcustom(
                  labelText: 'Mot de passe',
                  controller: passwordController,
                  obscureText: true,
                ),

                const SizedBox(height: 20),

                TextFieldcustom(
                  labelText: 'Confirmer le mot de passe',
                  controller: confirmpasswordController,
                  obscureText: true,
                ),

                const SizedBox(height: 20),

                //Done with the help of chatgpt
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // Dropdown Button
                    SizedBox(
                      width: 250,
                      child: ElevatedButton(
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFFF2F2F2),
                          foregroundColor: Colors.black,
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(6),
                            side:
                                const BorderSide(color: Colors.black, width: 1),
                          ),
                          padding: const EdgeInsets.symmetric(
                              horizontal: 16, vertical: 12),
                        ),
                        onPressed: _showBottomSheet,
                        child: const Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text(
                              "Professions",
                              style: TextStyle(
                                fontSize: 16,
                                fontWeight: FontWeight
                                    .normal, // Move this inside TextStyle
                              ),
                            ),
                            Icon(Icons.arrow_drop_down),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(height: 5),

                    // Display selected professions below the button
                    Wrap(
                      spacing: 8.0,
                      runSpacing: 4.0,
                      children: selectedProfessions
                          .map((profession) => Chip(
                                label: Text(profession.name),
                                onDeleted: () {
                                  setState(() {
                                    selectedProfessions.remove(profession);
                                  });
                                },
                              ))
                          .toList(),
                    ),
                  ],
                ),

                const SizedBox(height: 10),

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

                const SizedBox(height: 15),

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
