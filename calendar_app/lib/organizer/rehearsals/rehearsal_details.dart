import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/organizer/rehearsals/rehearsal_modification.dart';
import 'package:calendar_app/organizer/rehearsals/rehearsal_participant_element.dart';
import 'package:calendar_app/organizer/rehearsals/rehearsal_precedence_relatoins.dart';
import 'package:calendar_app/organizer/rehearsals/rehearsal_presences.dart';
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
  final String? time;
  final String? duration;
  final List participantsIds;
  final bool organizerPage;

  const RehearsalDetailsPage({
    super.key,
    required this.projectId,
    required this.rehearsalId,
    required this.name,
    required this.description,
    required this.date,
    required this.time,
    required this.duration,
    required this.participantsIds, //TODO delete ?
    required this.organizerPage,
  });

  @override
  State<RehearsalDetailsPage> createState() => _RehearsalDetailsPage();
}

class _RehearsalDetailsPage extends State<RehearsalDetailsPage> {
  final user = FirebaseAuth.instance.currentUser!;

  late String name;
  late String? description;
  late String? date;
  late String? time;
  late String? duration;
  late List participantsIds;
  late String? location;
  late Future<List>? users;

  @override
  void initState() {
    super.initState();
    name = widget.name;
    description = widget.description;
    date = widget.date;
    time = widget.time;
    duration = widget.duration;
    participantsIds = widget.participantsIds;
    users = getUsersOnRehearsal(context);
    location = null;
    getRehearsal();
  }

  Future<List> getUsersOnRehearsal(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/rehearsals/${widget.rehearsalId}/participants';
    try {
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        final List<dynamic> userRehearsal = data.map((item) {
          return {
            'id': item['id'],
            'firstName': item['firstName'],
            'lastName': item['lastName'],
            'email': item['email'],
          };
        }).toList();
        setState(() {
          participantsIds = data.map<int>((item) {
            return item['id'] as int;
          }).toList();
        });
        return userRehearsal;
      }
      return [];
    } catch (e) {
      return [];
    }
  }

  void refreshUsers() {
    setState(() {
      users = getUsersOnRehearsal(context);
    });
  }

  void deleteRehearsal() async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/rehearsals/${widget.rehearsalId}';
    try {
      final response = await http.delete(Uri.parse(url));

      if (response.statusCode != 204) {
        if (mounted) {
          Utils.errorMess('Erreur lors de la suppression de la répétition',
              'Merci de réessayer plus tard', context);
        }
      } else {
        if (mounted) {
          Navigator.pop(context);
        }
      }
    } catch (e) {
      if (mounted) {
        Utils.errorMess('Erreur lors de la suppression de la répétition',
            'Merci de réessayer plus tard', context);
      }
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
          time = data['time'];
          duration = data['duration'];
          location = data['location'];
        });
      } else {
        if (mounted) {
          Utils.errorMess('Une erreur est survenue',
              'Merci de réessayer plus tard', context);
        }
      }
    } catch (e) {
      if (mounted) {
        Utils.errorMess(
            'Une erreur est survenue', 'Merci de réessayer plus tard', context);
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
                          'Date: ${date != null ? Utils.formatDateString(date) : "-"} ${time != null ? Utils.formatTimeString(time) : ""}',
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
                        const SizedBox(height: 10),
                        Text(
                          'Lieu: ${location != null && location != '' ? location : "-"}',
                          style: const TextStyle(
                            fontSize: 20,
                          ),
                        ),
                        const SizedBox(height: 10),
                        Text(
                          'Participants: ${participantsIds.isEmpty ? "-" : ""}',
                          style: const TextStyle(
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
                            return ParticipantElement(
                              projectId: widget.projectId,
                              rehearsalId: widget.rehearsalId,
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
                if (widget.organizerPage) ...[
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
                            time: time,
                            duration: duration,
                            participantsIds: participantsIds,
                            location: location,
                          ),
                        ),
                      ).then((_) {
                        getRehearsal();
                        refreshUsers();
                      });
                    },
                  ),
                  const SizedBox(height: 20),
                  ButtonCustom(
                    text: 'Ordre des répétitions',
                    onTap: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => RehearsalPrecedencesPage(
                            rehearsalId: widget.rehearsalId,
                            projectId: widget.projectId,
                            rehearsalName: name,
                          ),
                        ),
                      ).then((_) {
                        getRehearsal();
                        refreshUsers();
                      });
                    },
                  ),
                  const SizedBox(height: 20),
                  if (date != null && time != null)
                    ButtonCustom(
                      text: 'Afficher les présences',
                      onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => PresencesPage(
                              rehearsalId: widget.rehearsalId,
                              projectId: widget.projectId,
                              name: name,
                              isCalendar: false,
                            ),
                          ),
                        ).then((_) {
                          getRehearsal();
                          refreshUsers();
                        });
                      },
                    ),
                  const SizedBox(height: 20),
                  ButtonCustom(
                    text: 'Supprimer la répétition',
                    onTap: () {
                      Utils.confirmation(
                          'Action Irrévesible',
                          'Êtes-vous sûre de vouloir supprimer la répétition ?',
                          deleteRehearsal,
                          context);
                    },
                  ),
                ],
              ],
            )),
        selectedIndex: widget.organizerPage ? 1 : 0);
  }
}
