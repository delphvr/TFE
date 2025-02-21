import 'package:calendar_app/auth/auth.dart';
import 'package:calendar_app/project/participants/participant_element.dart';
import 'package:calendar_app/project/rehearsals/rehearsal_modification.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class RehearsalDetailsPage extends StatefulWidget {
  final int projectId;
  final int rehearsalId;
  final String name;
  final String? description;
  final String? date;
  final String? duration;
  final List participantsIds;

  const RehearsalDetailsPage({
    super.key,
    required this.projectId,
    required this.rehearsalId,
    required this.name,
    required this.description,
    required this.date,
    required this.duration,
    required this.participantsIds,
  });

  @override
  State<RehearsalDetailsPage> createState() => _RehearsalDetailsPage();
}

class _RehearsalDetailsPage extends State<RehearsalDetailsPage> {
  final user = FirebaseAuth.instance.currentUser!;

  late String name;
  late String? description;
  late String? date;
  late String? duration;
  late List participantsIds;
  late Future<List>? users;

  @override
  void initState() {
    super.initState();
    name = widget.name;
    description = widget.description;
    date = widget.date;
    duration = widget.duration;
    participantsIds = widget.participantsIds;
    users = getUsersOnReharsal(context);
    getRehearsal();
  }

  Future<List> getUsersOnReharsal(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/userProjects/${widget.projectId}';
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

  void refreshUsers() {
    setState(() {
      users = getUsersOnReharsal(context);
    });
  }

  void logout(Function onLogoutSuccess) async {
    await FirebaseAuth.instance.signOut();
    onLogoutSuccess();
  }

  void deleteRehearsal() async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/rehearsals/${widget.rehearsalId}';
    try {
      final response = await http.delete(Uri.parse(url));

      if (response.statusCode != 204) {
        Utils.errorMess('Erreur lors de la suppression de la répétition',
            'Merci de réessayer plus tard', context);
      } else {
        Navigator.pop(context);
      }
    } catch (e) {
      Utils.errorMess('Erreur lors de la suppression de la répétition',
          'Merci de réessayer plus tard', context);
    }
  }

  void getRehearsal() async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/rehearsals/${widget.rehearsalId}';
    try {
      final response = await http.get(Uri.parse(url));
      if (response.statusCode == 200) {
        final Map<String, dynamic> data =
            json.decode(utf8.decode(response.bodyBytes));
        setState(() {
          name = data['name'];
          description = data['description'];
          date = data['date'];
          duration = data['duration'];
        });
        //TODO get participants
      } else {
        print("response.statusCode: ${response.statusCode}");
        print("response.body: ${response.body}");
        Utils.errorMess(
            'Une erreur est survenue', 'Merci de réessayer plus tard', context);
      }
    } catch (e) {
      print("e: $e");
      Utils.errorMess(
          'Une erreur est survenue', 'Merci de réessayer plus tard', context);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        backgroundColor: Colors.white,
        actions: [
          //TODO in utils ?
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
                name,
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
                        'Date: ${date != null ? Utils.formatDate(date) : "-"}',
                        style: const TextStyle(
                          fontSize: 20,
                        ),
                      ),
                      const SizedBox(height: 10),
                      Text(
                        'Durée: ${duration != null ? Utils.formatDuration(duration!) : "-"}',
                        style: const TextStyle(
                          fontSize: 20,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 25),
              ButtonCustom(
                text: 'Modifier',
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => RehearsalModificationPage(
                        rehearsalId: widget.rehearsalId,
                        projectId: widget.projectId,
                        name: name,
                        description: description,
                        date: date,
                        duration: duration,
                        participantsIds: participantsIds,
                      ),
                    ),
                  ).then((_) {
                    getRehearsal();
                  });
                },
              ),
              const SizedBox(height: 20),
              ButtonCustom(
                //TODO
                text: 'Ajouter des participant',
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => RehearsalModificationPage(
                        rehearsalId: widget.rehearsalId,
                        projectId: widget.projectId,
                        name: name,
                        description: description,
                        date: date,
                        duration: duration,
                        participantsIds: participantsIds,
                      ),
                    ),
                  ).then((updatedProject) {
                    if (updatedProject != null) {
                      setState(() {
                        getRehearsal();
                      });
                    }
                  });
                },
              ),
              const SizedBox(height: 25),
              //TODO: modif pour fit comme role élément plutot

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
              const SizedBox(height: 40),
              ButtonCustom(
                text: 'Suprimmer la répétition',
                onTap: () {
                  Utils.confirmation(
                      'Action Irrévesible',
                      'Êtes-vous sûre de vouloir supprimer la répétition ?',
                      deleteRehearsal,
                      context);
                },
              ),
            ],
          )),
    );
  }
}
