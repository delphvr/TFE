import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/organizer/participants/participants.dart';
import 'package:calendar_app/organizer/project/calendar_proposition.dart';
import 'package:calendar_app/organizer/rehearsals/rehearsals.dart';
import 'package:calendar_app/organizer/project/project_modification.dart';
import 'package:calendar_app/organizer/roles/role_and_participant_element.dart';
import 'package:calendar_app/project/user_rehearsal.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class ProjectDetailsPage extends StatefulWidget {
  final int id;
  final bool organizerPage;

  const ProjectDetailsPage({
    super.key,
    required this.id,
    required this.organizerPage,
  });

  @override
  State<ProjectDetailsPage> createState() => _ProjectDetailsPage();
}

class _ProjectDetailsPage extends State<ProjectDetailsPage> {
  final user = FirebaseAuth.instance.currentUser!;

  String? name;
  String? description;
  String? beginningDate;
  String? endingDate;
  Future<List>? users;
  late Future<List>? roles;

  @override
  void initState() {
    super.initState();
    users = getUsersOnProject(context);
    getProjectData(context);
    roles = getRoles(context);
  }

  Future<List> getRoles(BuildContext context) async {
    final email = user.email;
    final String url =
        '${dotenv.env['API_BASE_URL']}/projects/${widget.id}/users/roles?email=$email';

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

  void refreshRoles() {
    setState(() {
      roles = getRoles(context);
    });
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
        if (context.mounted) {
          Utils.errorMess('Une erreur c\'est produite',
              'Merci de réessayer plus tard.', context);
        }
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess('Une erreur c\'est produite',
            'Merci de réessayer plus tard.', context);
      }
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
        if (mounted) {
          Utils.errorMess('Erreur lors de la suppression du project',
              'Merci de réessayer plus tard', context);
        }
      } else {
        if (mounted) {
          Navigator.pop(context);
        }
      }
    } catch (e) {
      if (mounted) {
        Utils.errorMess('Erreur lors de la suppression du project',
            'Merci de réessayer plus tard', context);
      }
    }
  }

  void deleteParticipant() async {
    final email = user.email;
    final String url =
        '${dotenv.env['API_BASE_URL']}/projects/${widget.id}/users?email=$email';
    try {
      final response = await http.delete(Uri.parse(url));

      if(response.statusCode == 400){
        if (mounted) {
          Utils.errorMess('Une erreur est survenue',
              'Au moins un organisateur dois rester présent sur le projet.', context);
        }
      }else if (response.statusCode != 204) {
        if (mounted) {
          Utils.errorMess('Une erreur est survenue',
              'Merci de réessayer plus tard', context);
        }
      } else {
        if (mounted) {
          Navigator.pop(context);
        }
      }
    } catch (e) {
      if (mounted) {
        Utils.errorMess('Une erreur est survenue',
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
                        'Date de début: ${beginningDate != null ? Utils.formatDateString(beginningDate) : "-"}',
                        style: const TextStyle(
                          fontSize: 20,
                        ),
                      ),
                      const SizedBox(height: 10),
                      Text(
                        'Date de fin: ${endingDate != null ? Utils.formatDateString(endingDate) : "-"}',
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
                      if (context.mounted) {
                        users = getUsersOnProject(context);
                        getProjectData(context);
                      }
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
                const SizedBox(height: 25),
                ButtonCustom(
                  text: "Calculer l'horaire",
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (context) => CalendarPropositionPage(projectId: widget.id)),
                    );
                  },
                ),
                const SizedBox(height: 40),
                ButtonCustom(
                  text: 'Supprimer le projet',
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
                ButtonCustom(
                  text: 'Voir mes répétitions',
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => UserRehearsalPage(
                          projectId: widget.id,
                          projectName: name!,
                        ),
                      ),
                    );
                  },
                ),
                const SizedBox(height: 20),
                ButtonCustom(
                  text: 'Me retirer du projet',
                  onTap: () {
                Utils.confirmation(
                    'Action Irrévesible',
                    'Êtes-vous sûre de vouloir vous rétirer du projet ?',
                    deleteParticipant,
                    context);
              },
                ),
                const SizedBox(height: 20),
                const Align(
                  alignment: Alignment.centerLeft,
                  child: Padding(
                    padding: EdgeInsets.symmetric(horizontal: 35),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          "Mes rôles :",
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
                  //TODO: same as in participant_madification, put in aux file ?
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
                              projectId: widget.id,
                              userId: 0,
                              name: roles[index],
                              onUpdate: null,
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
              ]
            ],
          )),
      selectedIndex: widget.organizerPage ? 1 : 0,
    );
  }
}
