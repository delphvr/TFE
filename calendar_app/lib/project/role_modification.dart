import 'package:calendar_app/auth/auth.dart';
import 'package:calendar_app/components/bottom_sheet_selector.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import 'package:multi_select_flutter/multi_select_flutter.dart';

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

class RoleModificationPage extends StatefulWidget {
  final int projectId;
  final int userId;
  final String firstName;
  final String lastName;

  const RoleModificationPage({
    super.key,
    required this.projectId,
    required this.userId,
    required this.firstName,
    required this.lastName,
  });

  @override
  State<RoleModificationPage> createState() => _RoleModificationPageState();
}

class _RoleModificationPageState extends State<RoleModificationPage> {
  final user = FirebaseAuth.instance.currentUser!;

  List<Role> roles = [];
  List<Role> selectedRoles = [];
  List<MultiSelectItem<Role>> items = [];

  @override
  void initState() {
    super.initState();
    _loadRoles();
  }

  //TODO mettre roles dans fichier aux ?
  Future<List<Role>> getRoles() async {
    String url = '${dotenv.env['API_BASE_URL']}/roles';
    try {
      final response = await http.get(Uri.parse(url));

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

  void addUserRole(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/projects/${widget.projectId}/users/${widget.userId}/roles';

    final Map<String, dynamic> requestBody = {
      "roles": selectedRoles.map((p) => p.name).toList(),
    };

    try {
      final response = await http.post(
        Uri.parse(url),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(requestBody),
      );

      print("DEBUGG : ${requestBody}");

      print("DEBUGG : ${response.statusCode}");

      print("DEBUGG : ${response.body}");
      if (response.statusCode == 201) {
        print("DEBUGG : ${response.body}");
        if (mounted) {
          Navigator.pop(context);
        }
      } else {
        Utils.errorMess('Erreur lors de l\'ajout de rôles au participant',
            'Merci de réessayez plus tard.', context);
      }
    } catch (e) {
      Utils.errorMess('Erreur lors de l\'ajout de rôles au participant',
          'Impossible de se connecter au serveur.', context);
    }
  }

  void logout(Function onLogoutSuccess) async {
    await FirebaseAuth.instance.signOut();
    onLogoutSuccess();
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
        heightFactor: 3,
        child: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.all(25.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Text(
                  "Ajout de rôles pour ${widget.lastName} ${widget.firstName}",
                  textAlign: TextAlign.center,
                  style: const TextStyle(
                    fontSize: 27,
                  ),
                ),
                const SizedBox(height: 25),
                BottomSheetSelector<Role>(
                  items: roles,
                  selectedItems: selectedRoles,
                  onSelectionChanged: (selectedList) {
                    setState(() {
                      selectedRoles = selectedList;
                    });
                  },
                  title: "Sélectionnez les rôles à ajouter",
                  buttonLabel: "Valider",
                  itemLabel: (role) => role.name,
                  textfield: "Roles",
                ),
                const SizedBox(height: 10),
                ButtonCustom(
                  text: 'Ajouter',
                  onTap: () {
                    addUserRole(context);
                  },
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
