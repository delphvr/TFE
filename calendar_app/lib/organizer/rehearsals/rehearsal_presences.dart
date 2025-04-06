import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/organizer/participants/participant_element.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class PresencesPage extends StatefulWidget {
  final int rehearsalId;
  final String name;
  final bool isCalendar;

  const PresencesPage({
    super.key,
    required this.rehearsalId,
    required this.name,
    required this.isCalendar,
  });

  @override
  State<PresencesPage> createState() => _PresencesPageState();
}

class _PresencesPageState extends State<PresencesPage> {
  final user = FirebaseAuth.instance.currentUser!;
  late Future<List> usersPresent = Future.value([]);
  late Future<List> usersNotPresent = Future.value([]);

  @override
  void initState() {
    super.initState();
    getUsersPresences(context);
  }

  Future<void> getUsersPresences(BuildContext context) async {
    String url = "";
    if (widget.isCalendar) {
      url =
          '${dotenv.env['API_BASE_URL']}/rehearsals/${widget.rehearsalId}/CPpresences';
    } else {
      url =
          '${dotenv.env['API_BASE_URL']}/rehearsals/${widget.rehearsalId}/presences';
    }
    try {
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final Map<String, dynamic> data =
            json.decode(utf8.decode(response.bodyBytes));

        final List<dynamic> present = data['present'];
        final List<dynamic> notPresent = data['notPresent'];

        setState(() {
          usersPresent = Future.value(present
              .map((item) => {
                    'id': item['id'],
                    'firstName': item['firstName'],
                    'lastName': item['lastName'],
                    'email': item['email'],
                  })
              .toList());

          usersNotPresent = Future.value(notPresent
              .map((item) => {
                    'id': item['id'],
                    'firstName': item['firstName'],
                    'lastName': item['lastName'],
                    'email': item['email'],
                  })
              .toList());
        });
      } else {
        if (context.mounted) {
          Utils.errorMess('Une erreur est survenue',
              'Merci de réessayer plus tard', context);
        }
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess(
            'Une erreur est survenue', 'Merci de réessayer plus tard', context);
      }
      setState(() {
        usersPresent = Future.value([]);
        usersNotPresent = Future.value([]);
      });
    }
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
                    "Participants présent à la répétition \"${widget.name}\" :",
                    textAlign: TextAlign.center,
                    style: const TextStyle(
                      fontSize: 23,
                    ),
                  ),
                  const SizedBox(height: 25),
                  Flexible(
                    child: FutureBuilder<List>(
                      future: usersPresent,
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
                          final users = snapshot.data!;
                          if (users.isEmpty) {
                            return const Text(
                                'Aucun participant ne peut être présent');
                          }

                          return ListView.builder(
                            shrinkWrap: true,
                            physics: const NeverScrollableScrollPhysics(),
                            itemCount: users.length,
                            itemBuilder: (context, index) {
                              return UsersElement(
                                projectId: widget.rehearsalId,
                                userId: users[index]['id'],
                                firstName: users[index]['firstName'],
                                lastName: users[index]['lastName'],
                                email: users[index]['email'],
                                //roles: users[index]['roles'],
                                onUpdate: () => getUsersPresences(context),
                              );
                            },
                          );
                        } else {
                          return const Text(
                              'Aucun participant ne peut être présent');
                        }
                      },
                    ),
                  ),
                  const SizedBox(height: 25),
                  Text(
                    "Participants non présent à la répétition \"${widget.name}\" :",
                    textAlign: TextAlign.center,
                    style: const TextStyle(
                      fontSize: 23,
                    ),
                  ),
                  const SizedBox(height: 25),
                  Flexible(
                    child: FutureBuilder<List>(
                      future: usersNotPresent,
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
                          final users = snapshot.data!;
                          if (users.isEmpty) {
                            return const Text(
                                "Aucun participant n'est pas présent");
                          }

                          return ListView.builder(
                            shrinkWrap: true,
                            physics: const NeverScrollableScrollPhysics(),
                            itemCount: users.length,
                            itemBuilder: (context, index) {
                              return UsersElement(
                                projectId: widget.rehearsalId,
                                userId: users[index]['id'],
                                firstName: users[index]['firstName'],
                                lastName: users[index]['lastName'],
                                email: users[index]['email'],
                                //roles: users[index]['roles'],
                                onUpdate: () => getUsersPresences(context),
                              );
                            },
                          );
                        } else {
                          return const Text(
                              'Aucun participant n\'est pas présent');
                        }
                      },
                    ),
                  ),
                ],
              ),
            )),
        selectedIndex: 1);
  }
}
