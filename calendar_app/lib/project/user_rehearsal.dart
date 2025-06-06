import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/organizer/rehearsals/rehearsal_element.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

/// See the information of the user rehearsals when not on organizer page.
class UserRehearsalPage extends StatefulWidget {
  final int projectId;
  final String projectName;

  const UserRehearsalPage({
    super.key,
    required this.projectId,
    required this.projectName,
  });

  @override
  State<UserRehearsalPage> createState() => _UserRehearsalPageState();
}

class _UserRehearsalPageState extends State<UserRehearsalPage> {
  final user = FirebaseAuth.instance.currentUser!;
  late Future<List>? rehearsals;

  @override
  void initState() {
    super.initState();
    rehearsals = getRehearsals(context);
  }

  /// Get the list of rehearsal of the user for the project with id [widget.projectId].
  Future<List> getRehearsals(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/users/${user.email}/projects/${widget.projectId}/rehearsals';
    try {
      final response = await http.get(Uri.parse(url));
      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        final List<dynamic> userRehearsals = data.map((item) {
          return {
            'id': item['id'],
            'name': item['name'],
            'description': item['description'],
            'date': item['date'],
            'time': item['time'],
            'duration': item['duration'],
            'projectId': item['projectId'],
            'location': item['location'],
            'participantsIds': item['participantsIds']
          };
        }).toList();
        return userRehearsals;
      }
      return [];
    } catch (e) {
      return [];
    }
  }

  /// Update the variable [rehearsals] with the list of rehearsal the user is asign to in the project with id [widget.projectId].
  void refreshRehearsals() {
    setState(() {
      rehearsals = getRehearsals(context);
    });
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
        body: Align(
            alignment: Alignment.topCenter,
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 17.0),
              child: Column(
                children: [
                  Text(
                    "Vos répétitions pour le projet ${widget.projectName}",
                    textAlign: TextAlign.center,
                    style: const TextStyle(
                      fontSize: 27,
                    ),
                  ),
                  const SizedBox(height: 25),
                  Flexible(
                    child: FutureBuilder<List>(
                      future: rehearsals,
                      builder: (context, snapshot) {
                        if (snapshot.connectionState ==
                            ConnectionState.waiting) {
                          return const Center(
                            child: CircularProgressIndicator(),
                          );
                        } else if (snapshot.hasError) {
                          return Center(
                            child: Text("Erreur: ${snapshot.error}"),
                          );
                        } else if (snapshot.hasData) {
                          final rehearsals = snapshot.data!;
                          if (rehearsals.isEmpty) {
                            return const Text(
                              'Vous n\'êtes assigné à aucune répétition dans ce projet.',
                              textAlign: TextAlign.center,
                              style: TextStyle(
                                fontSize: 20,
                              ),
                            );
                          }
                          return ListView.builder(
                            shrinkWrap: true,
                            physics: const NeverScrollableScrollPhysics(),
                            itemCount: rehearsals.length,
                            itemBuilder: (context, index) {
                              return RehearsalElement(
                                rehearsalId: rehearsals[index]['id'],
                                name: rehearsals[index]['name'],
                                description: rehearsals[index]['description'],
                                date: Utils.formatDateString(
                                    rehearsals[index]['date']),
                                time: rehearsals[index]['time'],
                                duration: rehearsals[index]['duration'],
                                projectId: rehearsals[index]['projectId'],
                                location: rehearsals[index]['location'],
                                participantsIds: rehearsals[index]
                                    ['participantsIds'],
                                organizerPage: false,
                                onUpdate: refreshRehearsals,
                              );
                            },
                          );
                        } else {
                          return const Center(
                            child: Text('Aucune répétition trouvé'),
                          );
                        }
                      },
                    ),
                  ),
                ],
              ),
            )),
        selectedIndex: 0);
  }
}
