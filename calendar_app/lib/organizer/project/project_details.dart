import 'package:calendar_app/auth/auth.dart';
import 'package:calendar_app/organizer/participants/participants.dart';
import 'package:calendar_app/organizer/rehearsals/rehearsals.dart';
import 'package:calendar_app/organizer/project/project_modification.dart';
import 'package:calendar_app/project/user_rehearsal.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class ProjectDetailsOrganizerPage extends StatefulWidget {
  final int id;
  final bool organizerPage;

  const ProjectDetailsOrganizerPage({
    super.key,
    required this.id,
    required this.organizerPage,
  });

  @override
  State<ProjectDetailsOrganizerPage> createState() => _ProjectDetailsPage();
}

class _ProjectDetailsPage extends State<ProjectDetailsOrganizerPage> {
  final user = FirebaseAuth.instance.currentUser!;

  String? name;
  String? description;
  String? beginningDate;
  String? endingDate;
  Future<List>? users;

  @override
  void initState() {
    super.initState();
    users = getUsersOnProject(context);
    getProjectData(context);
  }

  Future<List> getUsersOnProject(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/userProjects/${widget.id}';
    try {
      final response = await http.get(Uri.parse(url));

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

  //TODO: return the value instead and go in aux file ?
  void getProjectData(BuildContext context) async {
    final String url = '${dotenv.env['API_BASE_URL']}/projects/${widget.id}';
    try {
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final Map<String, dynamic> data =
            json.decode(utf8.decode(response.bodyBytes));
        setState(() {
          name = data['name'];
          description = data['description'];
          beginningDate = data['beginningDate'];
          endingDate = data['endingDate'];
        });
      } else {
        Utils.errorMess('Une erreur c\'est produite',
            'Merci de réessayer plus tard.', context);
      }
    } catch (e) {
      Utils.errorMess('Une erreur c\'est produite',
          'Merci de réessayer plus tard.', context);
    }
  }

  void refreshUsers() {
    setState(() {
      users = getUsersOnProject(context);
    });
  }

  void logout(Function onLogoutSuccess) async {
    await FirebaseAuth.instance.signOut();
    onLogoutSuccess();
  }

  void deleteProject() async {
    final String url = '${dotenv.env['API_BASE_URL']}/projects/${widget.id}';
    try {
      final response = await http.delete(Uri.parse(url));

      if (response.statusCode != 204) {
        Utils.errorMess('Erreur lors de la suppression du project',
            'Merci de réessayer plus tard', context);
      } else {
        Navigator.pop(context);
      }
    } catch (e) {
      Utils.errorMess('Erreur lors de la suppression du project',
          'Merci de réessayer plus tard', context);
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
      body: Align(
          alignment: Alignment.topCenter,
          child: Column(
            children: [
              Text(
                name ?? '',
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
                        "Description: ${description != null && description != '' ? description : "-"}",
                        style: const TextStyle(
                          fontSize: 20,
                        ),
                      ),
                      const SizedBox(height: 10),
                      Text(
                        'Date de début: ${beginningDate != null ? Utils.formatDate(beginningDate) : "-"}',
                        style: const TextStyle(
                          fontSize: 20,
                        ),
                      ),
                      const SizedBox(height: 10),
                      Text(
                        'Date de fin: ${endingDate != null ? Utils.formatDate(endingDate) : "-"}',
                        style: const TextStyle(
                          fontSize: 20,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 25),
              if (widget.organizerPage) ...[
                ButtonCustom(
                  text: 'Modifier',
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => UpdateProjectPage(
                          id: widget.id,
                          name: name!,
                          description: description,
                          beginningDate: beginningDate,
                          endingDate: endingDate,
                        ),
                      ),
                    ).then((_) {
                      users = getUsersOnProject(context);
                      getProjectData(context);
                    });
                  },
                ),
                const SizedBox(height: 25),
                ButtonCustom(
                  text: "Voir les participants",
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (context) => ParticipantsPage(
                                id: widget.id,
                                name: name!,
                              )),
                    );
                  },
                ),
                const SizedBox(height: 25),
                ButtonCustom(
                  text: "Voir les répétitions",
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (context) => RehearsalPage(
                                projectId: widget.id,
                                projectName: name!,
                              )),
                    );
                  },
                ),
                const SizedBox(height: 40),
                ButtonCustom(
                  text: 'Suprimmer le projet',
                  onTap: () {
                    Utils.confirmation(
                        'Action Irrévesible',
                        'Êtes-vous sûre de vouloir supprimer le projet ?',
                        deleteProject,
                        context);
                  },
                ),
              ],
              if (!widget.organizerPage) ...[
                //TODO afficher ces roles
                ButtonCustom(
                  text: 'Voir mes répétitions',
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => UserRehearsalPage(projectId: widget.id, projectName: name!,),
                      ),
                    );
                  },
                ),
              ]
            ],
          )),
    );
  }
}
