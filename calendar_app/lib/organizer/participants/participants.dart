import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/organizer/participants/add_participant.dart';
import 'package:calendar_app/organizer/participants/participant_element.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

/// page to see all the participant of a project. 
/// Plus a button to get to the page to add a participant.
class ParticipantsPage extends StatefulWidget {
  final int projectId;
  final String name;
  final bool organizerPage;
  final FirebaseAuth? auth;
  final http.Client? client;

  const ParticipantsPage({
    super.key,
    required this.projectId,
    required this.name,
    this.organizerPage = true,
    this.auth,
    this.client,
  });

  @override
  State<ParticipantsPage> createState() => _ParticipantsPage();
}

class _ParticipantsPage extends State<ParticipantsPage> {
  FirebaseAuth get auth => widget.auth ?? FirebaseAuth.instance;
  http.Client get client => widget.client ?? http.Client();
  late final User user;
  late Future<List>? users;

  @override
  void initState() {
    super.initState();
    user = auth.currentUser!;
    users = getUsersOnProject(context);
  }

  /// Get the list of participants on the project.
  /// If an error occurs return an empty list.
  Future<List> getUsersOnProject(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/userProjects/${widget.projectId}';
    try {
      final response = await client.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        final List<dynamic> userProjects = data.map((item) {
          return {
            'id': item['id'],
            'firstName': item['firstName'],
            'lastName': item['lastName'],
            'email': item['email'],
          };
        }).toList();
        return userProjects;
      }
      return [];
    } catch (e) {
      return [];
    }
  }

  /// Updates the variable [users] with the list of users that are on the project, get its from the backend.
  void refreshUsers() {
    setState(() {
      users = getUsersOnProject(context);
    });
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
        body: Align(
            alignment: Alignment.topCenter,
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 25),
              child: Column(
                children: [
                  Text(
                    "Participants sur le projet ${widget.name}",
                    textAlign: TextAlign.center,
                    style: const TextStyle(
                      fontSize: 27,
                    ),
                  ),
                  const SizedBox(height: 25),
                  ButtonCustom(
                    text: 'Ajouter un participant',
                    onTap: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => AddParticipant(
                            projectId: widget.projectId,
                            projectName: widget.name,
                            client: widget.client,
                            auth: widget.auth,
                          ),
                        ),
                      ).then((_) {
                        setState(() {
                          users = getUsersOnProject(context);
                        });
                      });
                    },
                  ),
                  const SizedBox(height: 25),
                  Flexible(
                    child: FutureBuilder<List>(
                      future: users,
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
                          final users = snapshot.data!;
              
                          return ListView.builder(
                            shrinkWrap: true,
                            physics: const NeverScrollableScrollPhysics(),
                            itemCount: users.length,
                            itemBuilder: (context, index) {
                              return UsersElement(
                                projectId: widget.projectId,
                                userId: users[index]['id'],
                                firstName: users[index]['firstName'],
                                lastName: users[index]['lastName'],
                                email: users[index]['email'],
                                //roles: users[index]['roles'],
                                organizerPage: widget.organizerPage,
                                onUpdate: refreshUsers,
                              );
                            },
                          );
                        } else {
                          return const Center(
                            child: Text('Aucun participant trouvé'),
                          );
                        }
                      },
                    ),
                  ),
                ],
              ),
            )),
        selectedIndex:  widget.organizerPage ? 1 : 0);
  }
}
