import 'package:calendar_app/auth/auth.dart';
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

class Role {
  final String name;

  Role({required this.name});

  factory Role.fromJson(Map<String, dynamic> json) {
    String name = json['role'] == "Organizer" ? "Orgnaisateur" : json['role'];
    return Role(
      name: name,
    );
  }
}

class AddParticipant extends StatefulWidget {
  final int projectId;
  final String projectName;

  const AddParticipant(
      {super.key, required this.projectId, required this.projectName});

  @override
  State<AddParticipant> createState() => _AddParticipant();
}

class _AddParticipant extends State<AddParticipant> {
  final user = FirebaseAuth.instance.currentUser!;

  final emailController = TextEditingController();
  List<Role> roles = [];
  List<Role> selectedRoles = [];
  List<MultiSelectItem<Role>> items = [];

  @override
  void initState() {
    super.initState();
    _loadRoles();
  }

  void logout(Function onLogoutSuccess) async {
    await FirebaseAuth.instance.signOut();
    onLogoutSuccess();
  }

  Future<List<Role>> getRoles() async {
    String url = '${dotenv.env['API_BASE_URL']}/roles';
    try {
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        List<dynamic> jsonData = json.decode(utf8.decode(response.bodyBytes));
        return jsonData.isEmpty
            ? []
            : jsonData.map((json) => Role.fromJson(json)).toList();
      } else if (response.statusCode == 204) {
        return [];
      } else {
        throw Exception('Failed to load roles');
      }
    } catch (e) {
      throw Exception('Error: $e');
    }
  }

  Future<void> _loadRoles() async {
    try {
      final fetchedRoles = await getRoles();
      setState(() {
        roles = fetchedRoles;
        items = roles
            .map((role) => MultiSelectItem<Role>(
                  role,
                  role.name,
                ))
            .toList();
      });
    } catch (e) {
      print("Error loading Roles: $e");
    }
  }

  int checkInput() {
    if (emailController.text.isEmpty) {
      Utils.errorMess('Erreur lors de l\'ajout du participant',
          'Merci de remplir tous les champs', context);
      return -1;
    } else if (!Utils.isValidEmail(emailController.text)) {
      Utils.errorMess('Erreur lors de l\'ajout du participant',
          'Email non valide', context);
      return -1;
    }
    return 1;
  }

  void addUserToProject(BuildContext context) async {
    if (checkInput() == 1) {
      final String url = '${dotenv.env['API_BASE_URL']}/userProjects';

      final Map<String, dynamic> requestBody = {
        "userEmail": emailController.text,
        "projectId": widget.projectId,
        "roles": selectedRoles.map((p) => p.name).toList(),
      };

      try {
        final response = await http.post(
          Uri.parse(url),
          headers: {"Content-Type": "application/json"},
          body: jsonEncode(requestBody),
        );

        if (response.statusCode == 201) {
          if (mounted) {
            Navigator.pop(context);
          }
        } else if (response.statusCode == 404) {
          //TODO ajouter l'utilisateur et l'inviter a ce faire un compte
          Utils.errorMess('TODO',
              'Utilisateur existe pas. Lui envoyer une invitation ?', context);
        } else {
          Utils.errorMess('Erreur lors de l\'ajout du participant',
              'Merci de réessayez plus tard.', context);
        }
      } catch (e) {
        Utils.errorMess('Erreur lors de l\'ajout du participant',
            'Impossible de se connecter au serveur.', context);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        backgroundColor: Colors.white,
        appBar: AppBar(
          backgroundColor: Colors.white,
          actions: [
            IconButton(
              onPressed: () {
                logout(() {
                  Navigator.of(context).pushAndRemoveUntil(
                    MaterialPageRoute(builder: (context) => const Auth()),
                    (route) => false,
                  );
                });
              },
              icon: const Icon(
                Icons.logout,
                size: 40,
              ),
            ),
          ],
        ),
        body: Center(
          child: SingleChildScrollView(
            child: Padding(
              padding: const EdgeInsets.all(25.0),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    'Ajout d\'un paticipant au projet ${widget.projectName}',
                    textAlign: TextAlign.center,
                    style: const TextStyle(
                      fontSize: 27,
                    ),
                  ),
                  const SizedBox(height: 20),
                  TextFieldcustom(
                    labelText: 'Email*',
                    controller: emailController,
                    obscureText: false,
                    keyboardType: TextInputType.emailAddress,
                  ),
                  const SizedBox(height: 20),
                  BottomSheetSelector<Role>(
                    items: roles,
                    selectedItems: selectedRoles,
                    onSelectionChanged: (selectedList) {
                      setState(() {
                        selectedRoles = selectedList;
                      });
                    },
                    title: "Sélectionnez vos rôles",
                    buttonLabel: "Valider",
                    itemLabel: (role) => role.name,
                    textfield: "Roles",
                  ),
                  const SizedBox(height: 10),
                  ButtonCustom(
                    text: 'Ajouter',
                    onTap: () => addUserToProject(context),
                  ),
                  const SizedBox(height: 10),
                ],
              ),
            ),
          ),
        ));
  }
}
