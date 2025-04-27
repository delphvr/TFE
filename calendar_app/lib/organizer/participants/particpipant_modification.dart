import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/organizer/roles/role_and_participant_element.dart';
import 'package:calendar_app/organizer/roles/role_modification.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

/// Page diplaying the information about the participant with id [userId] on the project with id [projectId].
/// There is a button to delete the user from the project and a button to modify it role.
class ParticpipantModificationPage extends StatefulWidget {
  final int projectId;
  final int userId;
  final String firstName;
  final String lastName;
  final String email;
  final FirebaseAuth? auth;
  final http.Client? client;

  const ParticpipantModificationPage({
    super.key,
    required this.projectId,
    required this.userId,
    required this.firstName,
    required this.lastName,
    required this.email,
    this.auth,
    this.client,
  });

  @override
  State<ParticpipantModificationPage> createState() =>
      _ParticpipantModificationPage();
}

class _ParticpipantModificationPage
    extends State<ParticpipantModificationPage> {
  FirebaseAuth get auth => widget.auth ?? FirebaseAuth.instance;
  http.Client get client => widget.client ?? http.Client();
  late final User user;

  late Future<List>? roles;

  @override
  void initState() {
    super.initState();
    user = auth.currentUser!;
    roles = getRoles(context);
  }

  /// Get the list of role assign to the user with id [widget.userId] on the project with id [widget.projectId] from the backend.
  Future<List> getRoles(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/projects/${widget.projectId}/users/${widget.userId}/roles';
    try {
      final response = await client.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        List<String> modifiedRoles = data.map((role) {
          return role == "Organizer" ? "Organisateur" : role as String;
        }).toList();
        return modifiedRoles;
      }
      return [];
    } catch (e) {
      return [];
    }
  }

  /// Update the variable [roles] with the list of role assign to the user with id [widget.userId] on the project with id [widget.projectId].
  void refreshRoles() {
    setState(() {
      roles = getRoles(context);
    });
  }

  void deleteParticipant() async {
    const String errorTitle = 'Erreur lors de la suppression du participant';
    final String url =
        '${dotenv.env['API_BASE_URL']}/projects/${widget.projectId}/users/${widget.userId}';
    try {
      final response = await client.delete(Uri.parse(url));

      if (response.statusCode != 204) {
        if (mounted) {
          Utils.errorMess(errorTitle,
              'Merci de réessayer plus tard', context);
        }
      } else {
        if (mounted) {
          Navigator.pop(context);
        }
      }
    } catch (e) {
      if (mounted) {
        Utils.errorMess(errorTitle,
            'Merci de réessayer plus tard', context);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
      body: Align(
        alignment: Alignment.topCenter,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Text(
              "${widget.lastName} ${widget.firstName}",
              textAlign: TextAlign.center,
              style: const TextStyle(
                fontSize: 30,
              ),
            ),
            const SizedBox(height: 25),
            Align(
              alignment: Alignment.centerLeft,
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 35),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      widget.email,
                      style: const TextStyle(
                        fontSize: 20,
                      ),
                    ),
                    const SizedBox(height: 10),
                    const Text(
                      "Rôles",
                      style: TextStyle(
                        fontSize: 20,
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 10),
            Flexible(
              child: FutureBuilder<List>(
                future: roles,
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return const Center(
                      child: CircularProgressIndicator(),
                    );
                  } else if (snapshot.hasError) {
                    return Center(
                      child: Text("Erreur: ${snapshot.error}"),
                    );
                  } else if (snapshot.hasData) {
                    final roles = snapshot.data!;

                    return ListView.builder(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemCount: roles.length,
                      itemBuilder: (context, index) {
                        return RoleOrParticipantElement(
                          projectId: widget.projectId,
                          userId: widget.userId,
                          name: roles[index],
                          onUpdate: refreshRoles,
                        );
                      },
                    );
                  } else {
                    return const Center(
                      child: Text('Aucun rôle trouvé'),
                    );
                  }
                },
              ),
            ),
            const SizedBox(height: 25),
            ButtonCustom(
              text: 'Modifier les rôles',
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (context) => RoleModificationPage(
                            projectId: widget.projectId,
                            userId: widget.userId,
                            firstName: widget.firstName,
                            lastName: widget.lastName,
                          )),
                ).then((_) {
                  refreshRoles();
                });
              },
            ),
            const SizedBox(height: 25),
            ButtonCustom(
              text: 'Supprimer le participant',
              onTap: () {
                Utils.confirmation(
                    'Action Irrévesible',
                    'Êtes-vous sûre de vouloir supprimer le participant du projet ?',
                    deleteParticipant,
                    context);
              },
            ),
          ],
        ),
      ),
      selectedIndex: 1,
    );
  }
}
