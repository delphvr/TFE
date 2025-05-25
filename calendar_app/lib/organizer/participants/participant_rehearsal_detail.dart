import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/organizer/roles/role_and_participant_element.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

/// Page with the information about the user with id [userId] on the project with id [projectId].
class ParticpipantRehearsalDetailPage extends StatefulWidget {
  final int projectId;
  final int rehearsalId;
  final int userId;
  final String firstName;
  final String lastName;
  final String email;
  final bool organizerPage;

  const ParticpipantRehearsalDetailPage({
    super.key,
    required this.projectId,
    required this.rehearsalId,
    required this.userId,
    required this.firstName,
    required this.lastName,
    required this.email,
    required this.organizerPage,
  });

  @override
  State<ParticpipantRehearsalDetailPage> createState() =>
      _ParticpipantRehearsalDetailPage();
}

class _ParticpipantRehearsalDetailPage
    extends State<ParticpipantRehearsalDetailPage> {
  final user = FirebaseAuth.instance.currentUser!;

  late Future<List>? roles;

  @override
  void initState() {
    super.initState();
    roles = getRoles(context);
  }

  /// Get the list of roles the user with id [widget.userId] is assigned to on the project with id [widget.projectId]
  Future<List> getRoles(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/projects/${widget.projectId}/users/${widget.userId}/roles';
    try {
      final response = await http.get(Uri.parse(url));

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

  /// Update the variable [roles] with the list
  void refreshRoles() {
    setState(() {
      roles = getRoles(context);
    });
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
            ],
          ),
        ),
        selectedIndex: widget.organizerPage ? 1 : 0);
  }
}
