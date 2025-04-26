import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/organizer/rehearsals/add_precedence.dart';
import 'package:calendar_app/organizer/rehearsals/precedence_item.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';

/// Page to deal with the precedences relation od the rehearsal with id [rehearsalId].
/// There will be a button to add a precedence relation if possible.
/// There will be the list of rehearsal that need to happend before this rehearsal and the list of rehearsal that needs to happend after this rehearsal.
class RehearsalPrecedencesPage extends StatefulWidget {
  final int projectId;
  final int rehearsalId;
  final String rehearsalName;
  final http.Client? client;
  final FirebaseAuth? auth;

  const RehearsalPrecedencesPage({
    super.key,
    required this.projectId,
    required this.rehearsalId,
    required this.rehearsalName,
    this.client,
    this.auth,
  });

  @override
  State<RehearsalPrecedencesPage> createState() =>
      _RehearsalPrecedencesPageState();
}

class _RehearsalPrecedencesPageState extends State<RehearsalPrecedencesPage> {
  FirebaseAuth get auth => widget.auth ?? FirebaseAuth.instance;
  http.Client get client => widget.client ?? http.Client();
  late final User user;
  late Future<Map<String, List<Map<String, dynamic>>>> precedencesRelations;

  @override
  void initState() {
    super.initState();
    user = auth.currentUser!;
    precedencesRelations = getRehearsalPrecedenceRelations(context);
  }

  /// Get from the backend all the rehearsal that has a sequence constraint with the rehearsal with id [widget.rehearsalId].
  /// [Return] a map with the key: previous, following, notConstraint, constraintByOthers. And as value the list of rehearsal corresponding to the key.
  /// If an error occurs an error message will be displayed.
  Future<Map<String, List<Map<String, dynamic>>>>
      getRehearsalPrecedenceRelations(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/rehearsals/${widget.rehearsalId}/precedences';
    try {
      final response = await client.get(Uri.parse(url));
      if (response.statusCode == 200) {
        final Map<String, dynamic> data =
            json.decode(utf8.decode(response.bodyBytes));
        return {
          'previous': (data['previous'] as List<dynamic>).map((item) {
            return {
              'rehearsalId': item['id'],
              'name': item['name'],
              'date': item['date'],
              'time': item['time'],
              'duration': item['duration'],
              'projectId': item['projectId'],
              'location': item['location'],
            };
          }).toList(),
          'following': (data['following'] as List<dynamic>).map((item) {
            return {
              'rehearsalId': item['id'],
              'name': item['name'],
              'date': item['date'],
              'time': item['time'],
              'duration': item['duration'],
              'projectId': item['projectId'],
              'location': item['location'],
            };
          }).toList(),
          'notConstraint': (data['notConstraint'] as List<dynamic>).map((item) {
            return {
              'rehearsalId': item['id'],
              'name': item['name'],
              'date': item['date'],
              'time': item['time'],
              'duration': item['duration'],
              'projectId': item['projectId'],
              'location': item['location'],
            };
          }).toList(),
          'constraintByOthers':
              (data['constraintByOthers'] as List<dynamic>).map((item) {
            return {
              'rehearsalId': item['id'],
              'name': item['name'],
              'date': item['date'],
              'time': item['time'],
              'duration': item['duration'],
              'projectId': item['projectId'],
              'location': item['location'],
            };
          }).toList(),
        };
      } else {
        if (context.mounted) {
          Utils.errorMess('Une erreur est survenue',
              'Merci de réessayer plus tard', context);
        }
        return {
          'previous': [],
          'following': [],
          'notConstraint': [],
          'constraintByOthers': []
        };
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess(
            'Une erreur est survenue', 'Merci de réessayer plus tard', context);
      }
      return {
        'previous': [],
        'following': [],
        'notConstraint': [],
        'constraintByOthers': []
      };
    }
  }

  /// Updates the variable [precedencesRelations] with the sequence constraint on the rehearsal with id [widget.rehearsalId], retreived from the backend.
  void refreshRehearsalsPrecedence() {
    setState(() {
      precedencesRelations = getRehearsalPrecedenceRelations(context);
    });
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
      body: SingleChildScrollView(
        child: Align(
          alignment: Alignment.topCenter,
          child: Padding(
            padding: const EdgeInsets.all(40.0),
            child: Column(
              children: [
                const Text(
                  'Gestion des précédences',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    fontSize: 25,
                  ),
                ),
                const SizedBox(height: 20),
                FutureBuilder<Map<String, List<Map<String, dynamic>>>>(
                  future: precedencesRelations,
                  builder: (context, snapshot) {
                    if (snapshot.connectionState == ConnectionState.waiting) {
                      return const Center(child: CircularProgressIndicator());
                    } else if (snapshot.hasError) {
                      return Center(child: Text("Erreur: ${snapshot.error}"));
                    } else if (!snapshot.hasData) {
                      return const Center(child: Text('Aucune donnée trouvée'));
                    }

                    final data = snapshot.data!;
                    final previousRehearsals = data['previous'] ?? [];
                    final followingRehearsals = data['following'] ?? [];
                    final notConstraintRehearsals = data['notConstraint'] ?? [];

                    return Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        if (notConstraintRehearsals.isNotEmpty) ...[
                          ButtonCustom(
                            text: "Ajouter une précédence",
                            onTap: () {
                              Navigator.push(
                                context,
                                MaterialPageRoute(
                                  builder: (context) => AddPrecedencePage(
                                    projectId: widget.projectId,
                                    rehearsalId: widget.rehearsalId,
                                    rehearsalName: widget.rehearsalName,
                                    rehearsals: notConstraintRehearsals,
                                  ),
                                ),
                              ).then((_) {
                                refreshRehearsalsPrecedence();
                              });
                            },
                          ),
                          const SizedBox(height: 20),
                        ],
                        if (previousRehearsals.isNotEmpty) ...[
                          Text(
                            "Répétitions qui doivent précèder la répétition \"${widget.rehearsalName}\" :",
                            style: const TextStyle(
                              fontSize: 17,
                            ),
                          ),
                          const SizedBox(height: 10),
                          ListView.builder(
                            shrinkWrap: true,
                            physics: const ClampingScrollPhysics(),
                            itemCount: previousRehearsals.length,
                            itemBuilder: (context, index) {
                              final rehearsal = previousRehearsals[index];
                              return PrecedenceRehearsalElement(
                                name: rehearsal['name'],
                                rehearsalId: widget.rehearsalId,
                                current: widget.rehearsalId,
                                previous: rehearsal['rehearsalId'],
                                onUpdate: refreshRehearsalsPrecedence,
                              );
                            },
                          ),
                          const SizedBox(height: 20),
                        ],
                        if (followingRehearsals.isNotEmpty) ...[
                          Text(
                            "Répétitions qui doivent avoir lieu après la répétition \"${widget.rehearsalName}\" :",
                            style: const TextStyle(
                              fontSize: 17,
                            ),
                          ),
                          const SizedBox(height: 10),
                          ListView.builder(
                            shrinkWrap: true,
                            physics: const ClampingScrollPhysics(),
                            itemCount: followingRehearsals.length,
                            itemBuilder: (context, index) {
                              final rehearsal = followingRehearsals[index];
                              return PrecedenceRehearsalElement(
                                name: rehearsal['name'],
                                rehearsalId: widget.rehearsalId,
                                previous: widget.rehearsalId,
                                current: rehearsal['rehearsalId'],
                                onUpdate: refreshRehearsalsPrecedence,
                              );
                            },
                          ),
                        ],
                      ],
                    );
                  },
                ),
                const SizedBox(height: 10),
              ],
            ),
          ),
        ),
      ),
      selectedIndex: 1,
    );
  }
}
