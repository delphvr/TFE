import 'package:calendar_app/components/bottom_sheet_selector.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';
import 'package:calendar_app/organizer/roles/role_modification.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:multi_select_flutter/multi_select_flutter.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

/// Page to add a participant to the project
class AddParticipant extends StatefulWidget {
  final int projectId;
  final String projectName;
  final FirebaseAuth? auth;
  final http.Client? client;

  const AddParticipant({
    super.key,
    required this.projectId,
    required this.projectName,
    this.auth,
    this.client,
  });

  @override
  State<AddParticipant> createState() => _AddParticipant();
}

class _AddParticipant extends State<AddParticipant> {
  FirebaseAuth get auth => widget.auth ?? FirebaseAuth.instance;
  http.Client get client => widget.client ?? http.Client();
  late final User user;

  final emailController = TextEditingController();
  List<Role> roles = [];
  List<Role> selectedRoles = [];
  List<MultiSelectItem<Role>> items = [];

  @override
  void initState() {
    super.initState();
    user = auth.currentUser!;
    _loadRoles();
  }

  /// Get the list of possible roles from the backend.
  /// If an error occurs, display an error message and returns an empty list.
  Future<List<Role>> getRoles() async {
    String url = '${dotenv.env['API_BASE_URL']}/roles';
    try {
      final response = await client.get(Uri.parse(url));

      if (response.statusCode == 200) {
        List<dynamic> jsonData = json.decode(utf8.decode(response.bodyBytes));
        List<Role> res = jsonData.isEmpty
            ? []
            : jsonData.map((json) => Role.fromJson(json)).toList();
        res.removeWhere((role) => role.name == "Non défini");
        return res;
      } else if (response.statusCode == 204) {
        return [];
      } else {
        if (mounted) {
          Utils.errorMess('Une erreur est survenue',
              'Merci de réessayer plus tard', context);
        }
        return [];
      }
    } catch (e) {
      if (mounted) {
        Utils.errorMess(
            'Une erreur est survenue', 'Merci de réessayer plus tard', context);
      }
      return [];
    }
  }

  /// Updates the variable [roles] and [items] with the list of possible roles asked to the backend.
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
      return;
    }
  }

  /// Check the the email input is correct.
  /// [Return] -1 if the email is empty or is not a valid format, 1 otherwise.
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

  /// Add a user to the project.
  /// If an error occurs an error message will be displayed.
  void addUserToProject(BuildContext context) async {
    if (checkInput() == 1) {
      final String url = '${dotenv.env['API_BASE_URL']}/userProjects';

      final Map<String, dynamic> requestBody = {
        "userEmail": emailController.text,
        "projectId": widget.projectId,
        "roles": selectedRoles
            .map((p) => p.name == "Organisateur" ? "Organizer" : p.name)
            .toList(),
      };

      try {
        final response = await client.post(
          Uri.parse(url),
          headers: {"Content-Type": "application/json"},
          body: jsonEncode(requestBody),
        );

        if (response.statusCode == 201) {
          if (context.mounted) {
            Navigator.pop(context);
          }
        } else if (response.statusCode == 404) {
          //TODO ajouter l'utilisateur et l'inviter a ce faire un compte
          if (context.mounted) {
            Utils.errorMess(
                'TODO',
                'Utilisateur existe pas. Lui envoyer une invitation ?',
                context);
          }
        } else {
          if (context.mounted) {
            Utils.errorMess('Erreur lors de l\'ajout du participant',
                'Merci de réessayez plus tard.', context);
          }
        }
      } catch (e) {
        if (context.mounted) {
          Utils.errorMess('Erreur lors de l\'ajout du participant',
              'Impossible de se connecter au serveur.', context);
        }
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
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
                    key: const Key('emailField'),
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
                    title: "Sélectionnez les rôles",
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
        ),
        selectedIndex: 1);
  }
}
